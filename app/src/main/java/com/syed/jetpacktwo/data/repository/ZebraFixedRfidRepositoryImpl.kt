package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.domain.repository.RfidRepository
import com.syed.jetpacktwo.util.EpcParser
import com.technowave.techno_rfid.Beeper
import com.zebra.rfid.api3.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZebraFixedRfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository, RfidEventsListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val prefs = context.getSharedPreferences("zebrafixed_prefs", Context.MODE_PRIVATE)
    private val _readerStatus = MutableStateFlow(ReaderStatus("Disconnected", Color.RED, false))
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private val _traceData = MutableSharedFlow<TracedTagInfo>(extraBufferCapacity = 16)
    private var isTracing = false
    var isFound = false

    private var reader: RFIDReader? = null
    private var antennaInfo: AntennaInfo? = null
    private var tagsCount = 0
    private var isInventoryRunning = false
    private var currentEpcFilter = ""
    private var currentTracingEpc: String? = null
    private var lastTraceTime: Long = 0
    private var traceWatchdogJob: Job? = null
    private var locationBeepJob: Job? = null

    private var isConnecting = false

    init {
        Beeper.init(context)
        Beeper.setEnabled(false) // Disabled as per user request
    }

    override fun connectToReader(scannerSpec: String) {
        val ipAddress = scannerSpec.ifBlank { getSavedScannerSpec() }
        if (ipAddress.isBlank()) return
        
        if (isConnecting) {
            Log.d("ZebraFixedRepo", "Already connecting, skipping request for $ipAddress")
            return
        }
        
        if (reader?.isConnected == true && reader?.hostName?.trim()?.equals(ipAddress.trim(), ignoreCase = true) == true) {
            Log.d("ZebraFixedRepo", "Already connected to $ipAddress")
            _readerStatus.value = ReaderStatus("Connected: $ipAddress", Color.GREEN, true)
            return
        }

        scope.launch {
            isConnecting = true
            try {
                _readerStatus.value = ReaderStatus("Preparing Connection...", Color.GRAY, false, true)
                
                if (reader != null) {
                    try {
                        if (reader?.isConnected == true) {
                            reader?.Actions?.Inventory?.stop()
                            delay(200)
                            reader?.disconnect()
                        }
                        reader?.Events?.removeEventsListener(this@ZebraFixedRfidRepositoryImpl)
                    } catch (_: Exception) {}
                    reader = null
                    delay(500)
                }

                _readerStatus.value = ReaderStatus("Connecting to $ipAddress...", Color.GRAY, false, true)
                
                // FIXED READER INITIALIZATION
                val newReader = RFIDReader(ipAddress, 5084, 1000)
                
                var retryCount = 0
                var connected = false
                while (retryCount < 3 && !connected) {
                    try {
                        if (!newReader.isConnected) {
                            newReader.connect()
                        }
                        connected = true
                    } catch (e: Exception) {
                        retryCount++
                        Log.w("ZebraFixedRepo", "Connect attempt $retryCount failed: ${e.message}")
                        try { newReader.disconnect() } catch(_: Exception) {}
                        delay(2000) 
                        if (retryCount == 3) throw e
                    }
                }
                
                if (newReader.isConnected) {
                    reader = newReader
                    Log.d("ZebraFixedRepo", "Connected successfully to $ipAddress")
                    
                    var waitCount = 0
                    while (!newReader.isCapabilitiesReceived && waitCount < 50) {
                        delay(100)
                        waitCount++
                    }
                    
                    configureReader(newReader)
                    prefs.edit().putString("last_reader_ip", ipAddress).apply()
                    _readerStatus.value = ReaderStatus("Connected: $ipAddress", Color.GREEN, true)
                } else {
                    _readerStatus.value = ReaderStatus("Connection Failed", Color.RED, false)
                }
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Connect Error", e)
                val errorMsg = when {
                    e.message?.contains("Region") == true -> "Error: Region Not Set"
                    e is OperationFailureException -> "Error: ${e.results}"
                    else -> "Error: ${e.message ?: "Unknown Connection Error"}"
                }
                _readerStatus.value = ReaderStatus(errorMsg, Color.RED, false)
            } finally {
                isConnecting = false
            }
        }
    }

    private fun configureReader(reader: RFIDReader) {
        try {
            reader.Events.addEventsListener(this)
            reader.Events.setHandheldEvent(true)
            reader.Events.setTagReadEvent(true)
            reader.Events.setAttachTagDataWithReadEvent(true)
            reader.Events.setReaderDisconnectEvent(true)
            reader.Events.setInventoryStartEvent(true)
            reader.Events.setInventoryStopEvent(true)
            reader.Events.setBatteryEvent(true)

            val numAntennas = if (reader.isCapabilitiesReceived) reader.ReaderCapabilities.numAntennaSupported else 1
            val antennas = ShortArray(numAntennas) { (it + 1).toShort() }
            antennaInfo = AntennaInfo(antennas)
            
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE)
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE)
            
            reader.Config.setStartTrigger(triggerInfo.StartTrigger)
            reader.Config.setStopTrigger(triggerInfo.StopTrigger)
            reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            
            if (reader.isCapabilitiesReceived) {
                val powerLevels = reader.ReaderCapabilities.transmitPowerLevelValues
                if (powerLevels.isNotEmpty()) {
                    for (i in 1..numAntennas) {
                        try {
                            val config = reader.Config.Antennas.getAntennaRfConfig(i)
                            config.transmitPowerIndex = powerLevels.size - 1
                            reader.Config.Antennas.setAntennaRfConfig(i, config)
                        } catch (_: Exception) {}
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("ZebraFixedRepo", "Config error", e)
        }
    }

    override fun disconnectReader() {
        scope.launch {
            try {
                reader?.let { r ->
                    try {
                        if (isInventoryRunning) r.Actions.Inventory.stop()
                        delay(100)
                    } catch (_: Exception) {}
                    
                    try {
                        r.Events.removeEventsListener(this@ZebraFixedRfidRepositoryImpl)
                    } catch (_: Exception) {}
                    
                    try {
                        r.disconnect()
                    } catch (_: Exception) {}
                    
                    Log.d("ZebraFixedRepo", "Reader disconnected")
                }
                reader = null
                isInventoryRunning = false
                isTracing = false
                _readerStatus.value = ReaderStatus("Disconnected", Color.RED, false)
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Disconnect Error", e)
            }
        }
    }

    override fun startReader() {
        if (isInventoryRunning) return
        scope.launch {
            try {
                reader?.let { r ->
                    if (!r.isConnected) {
                        Log.e("ZebraFixedRepo", "Start failed: Reader not connected")
                        return@launch
                    }
                    if (isTracing) return@launch
                    
                    try {
                        r.Actions.Inventory.stop()
                        delay(200) 
                    } catch (_: Exception) {}

                    tagsCount = 0
                    try {
                        r.Actions.purgeTags()
                    } catch (e: Exception) {
                        Log.e("ZebraFixedRepo", "Purge failed: ${e.message}")
                    }

                    r.Actions.Inventory.perform(null, null, antennaInfo)
                    isInventoryRunning = true
                    Log.d("ZebraFixedRepo", "Inventory Started Successfully")
                }
            } catch (e: OperationFailureException) {
                Log.e("ZebraFixedRepo", "Start Error (OpFailure): ${e.results}")
                isInventoryRunning = false
                if (e.results == RFIDResults.RFID_API_SUCCESS) return@launch 
                scope.launch {
                    try {
                        reader?.Actions?.Inventory?.perform(null, null, null)
                        isInventoryRunning = true
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Start Error: ${e.message}")
                isInventoryRunning = false
            }
        }
    }

    override fun stopReader() {
        scope.launch {
            try {
                if (isTracing) {
                    stopTrace()
                } else {
                    reader?.Actions?.Inventory?.stop()
                    isInventoryRunning = false
                    Log.d("ZebraFixedRepo", "Inventory Stopped")
                }
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Stop Error: ${e.message}")
                isInventoryRunning = false
            }
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {}

    override fun getSavedScannerSpec(): String = prefs.getString("last_reader_ip", "") ?: ""

    override fun launchDeviceList(activity: Activity) {
        // UI uses ZebraFixedConfigDialog instead
    }

    override fun setEpcFilter(filter: String) {
        currentEpcFilter = filter
        Log.d("ZebraFixedRepo", "EPC Filter updated to: $filter")
    }

    override fun startTagTracing(epc: String) {
        if (epc.isBlank()) return
        scope.launch {
            try {
                reader?.let { r ->
                    if (isInventoryRunning) {
                        r.Actions.Inventory.stop()
                        delay(200)
                    }
                    
                    isTracing = true
                    currentTracingEpc = epc.replace(" ", "").uppercase()
                    
                    try {
                        r.Actions.PreFilters.deleteAll()
                        val pf = r.Actions.PreFilters.PreFilter()
                        pf.antennaID = 0.toShort()
                        pf.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
                        pf.setTagPattern(currentTracingEpc)
                        pf.tagPatternBitCount = currentTracingEpc!!.length * 4
                        pf.bitOffset = 32
                        pf.filterAction = FILTER_ACTION.FILTER_ACTION_DEFAULT
                        r.Actions.PreFilters.add(pf)
                    } catch (e: Exception) {
                        Log.e("ZebraFixedRepo", "Filter Error", e)
                    }
                    
                    optimizeForLocationing(r)
                    
                    r.Actions.Inventory.perform(null, null, null)
                    isInventoryRunning = true
                    
                    lastTraceTime = System.currentTimeMillis()
                    startTraceWatchdog()
                }
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Trace Start Error", e)
            }
        }
    }

    private fun startTraceWatchdog() {
        traceWatchdogJob?.cancel()
        traceWatchdogJob = scope.launch {
            while (isTracing) {
                delay(500)
                if (System.currentTimeMillis() - lastTraceTime > 3000) {
                    currentTracingEpc?.let { epc ->
                        if(!isFound)
                        _traceData.tryEmit(TracedTagInfo(epc, 0, EpcParser.extractOrdinal(epc)))
                    }
                }
            }
        }
    }

    override fun stopTrace() {
        scope.launch {
            try {
                traceWatchdogJob?.cancel()
                reader?.let { r ->
                    r.Actions.Inventory.stop()
                    delay(200)
                    r.Actions.PreFilters.deleteAll()
                    
                    isTracing = false
                    currentTracingEpc = null
                    isInventoryRunning = false
                    
                    restoreAfterLocationing(r)
                }
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Trace Stop Error", e)
            }
        }
    }

    private fun optimizeForLocationing(reader: RFIDReader) {
        try {
            val numAntennas = reader.ReaderCapabilities.numAntennaSupported
            for (i in 1..numAntennas) {
                val config = reader.Config.Antennas.getSingulationControl(i)
                config.session = SESSION.SESSION_S0
                config.tagPopulation = 30
                reader.Config.Antennas.setSingulationControl(i, config)
                
                val powerLevels = reader.ReaderCapabilities.transmitPowerLevelValues
                if (powerLevels.isNotEmpty()) {
                    val rfConfig = reader.Config.Antennas.getAntennaRfConfig(i)
                    rfConfig.transmitPowerIndex = powerLevels.size - 1
                    reader.Config.Antennas.setAntennaRfConfig(i, rfConfig)
                }
            }
        } catch (e: Exception) {}
    }

    private fun restoreAfterLocationing(reader: RFIDReader) {
        try {
            val numAntennas = reader.ReaderCapabilities.numAntennaSupported
            for (i in 1..numAntennas) {
                val config = reader.Config.Antennas.getSingulationControl(i)
                config.session = SESSION.SESSION_S2
                config.tagPopulation = 100
                reader.Config.Antennas.setSingulationControl(i, config)
            }
        } catch (e: Exception) {}
    }

    override fun getTraceData(): Flow<TracedTagInfo> = _traceData.asSharedFlow()
    override fun isTracingTag(): Boolean = isTracing

    override fun eventReadNotify(readEvent: RfidReadEvents?) {
        val tags = reader?.Actions?.getReadTags(100) ?: return
        tags.forEach { tag ->
            val epc = tag.tagID
            if (isTracing) {
                if(epc.trim() == currentTracingEpc?.trim()) {
                    isFound = true
                    lastTraceTime = System.currentTimeMillis()
                    val rssi = tag.peakRSSI.toInt()
                    val proximity = calculateProximityFromRssi(rssi)
                    val ordinal = EpcParser.extractOrdinal(epc)
                    _traceData.tryEmit(TracedTagInfo(epc, proximity, ordinal))
                } else {
                    isFound = false
                }
            } else {
                if (epc.length == 24) {
                    if (currentEpcFilter.isEmpty() || epc.startsWith(currentEpcFilter, ignoreCase = true) || epc.startsWith("C", ignoreCase = true)) {
                        tagsCount++
                        _tagReadEvents.tryEmit(TagReadEvent(epc, tagsCount))
                    }
                }
            }
        }
    }

    private fun calculateProximityFromRssi(rssi: Int): Int {
        val maxRssi = -35
        val minRssi = -75
        return when {
            rssi >= maxRssi -> 100
            rssi <= minRssi -> 0
            else -> ((rssi - minRssi) * 100) / (maxRssi - minRssi)
        }
    }

    override fun eventStatusNotify(eventData: RfidStatusEvents) {
        val statusEvent = eventData.StatusEventData
        if (statusEvent.statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            val triggerEvent = statusEvent.HandheldTriggerEventData
            if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                // startReader()
            } else if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                // stopReader()
            }
        } else if (statusEvent.statusEventType == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            isInventoryRunning = false
            isTracing = false
            _readerStatus.value = ReaderStatus("Zebra Fixed Disconnected", Color.RED, false)
        } else if (statusEvent.statusEventType == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            val batteryData = statusEvent.BatteryData
            _readerStatus.value = _readerStatus.value.copy(batteryLevel = batteryData.level)
        }
    }

    override fun switchHardware(type: String) {}
    override fun getCurrentType(): String = "ZEBRA FIXED"

    override fun launchPowerSettings(activity: Activity) {
        android.widget.Toast.makeText(activity, "Power settings not configured for Fixed Reader", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun setPowerLevel(level: Int) {}
    override fun getPowerLevel(): Int = 300

    override fun dispose() {
        scope.launch {
            try {
                disconnectReader()
                reader?.Dispose()
                reader = null
            } catch (e: Exception) {
                Log.e("ZebraFixedRepo", "Dispose Error", e)
            }
        }
    }
}
