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
import android.app.AlertDialog
import android.widget.Toast
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZebraRfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository, RfidEventsListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val prefs = context.getSharedPreferences("zebra_prefs", Context.MODE_PRIVATE)
    private val _readerStatus = MutableStateFlow(ReaderStatus("Disconnected", Color.RED, false))
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private val _traceData = MutableSharedFlow<TracedTagInfo>(extraBufferCapacity = 16)
    private var isTracing = false
    var isFound=false

    private var readers: Readers? = null
    private var reader: RFIDReader? = null
    private var antennaInfo: AntennaInfo? = null
    private var tagsCount = 0
    private var isInventoryRunning = false
    private var isConnected = false
    private var readingEnabled = true
    private var currentEpcFilter = ""
    private var currentTracingEpc: String? = null
    private var lastTraceTime: Long = 0
    private var traceWatchdogJob: Job? = null
    private var locationBeepJob: Job? = null

    private var isConnecting = false

    init {
        Beeper.init(context)
        Beeper.setEnabled(true)
        // We will not auto-connect in init anymore to avoid conflicts with MainActivity's connectLastSaved
        // But we can pre-initialize the readers object
        scope.launch {
            try {
                if (readers == null) {
                    readers = Readers(context, ENUM_TRANSPORT.ALL)
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Initial Readers setup error", e)
            }
        }
    }

    private fun initializeZebra() {
        // This method is now empty as we handle it in connectToReader or launchDeviceList
    }

    override fun connectToReader(scannerSpec: String) {
        if (scannerSpec.isBlank()) return
        if (isConnecting) {
            Log.d("ZebraRepo", "Already connecting, skipping request for $scannerSpec")
            return
        }
        
        // If already connected to the requested reader, don't reconnect
        if (reader?.isConnected == true && reader?.hostName?.trim()?.equals(scannerSpec.trim(), ignoreCase = true) == true) {
            Log.d("ZebraRepo", "Already connected to $scannerSpec")
            _readerStatus.value = ReaderStatus("Connected: $scannerSpec", Color.GREEN, true)
            return
        }

        scope.launch {
            isConnecting = true
            try {
                _readerStatus.value = ReaderStatus("Preparing Connection...", Color.GRAY, false, true)
                
                // If we have an existing reader object, ensure it's fully cleaned up first
                if (reader != null) {
                    try {
                        if (reader?.isConnected == true) {
                            reader?.Actions?.Inventory?.stop()
                            delay(200)
                            reader?.disconnect()
                        }
                        reader?.Events?.removeEventsListener(this@ZebraRfidRepositoryImpl)
                    } catch (_: Exception) {}
                    reader = null
                    delay(500) // Give BT stack a moment to settle
                }

                if (readers == null) {
                    try {
                        readers = Readers(context, ENUM_TRANSPORT.ALL)
                        delay(500)
                    } catch (e: Exception) {
                        Log.e("ZebraRepo", "Failed to init Readers", e)
                        _readerStatus.value = ReaderStatus("SDK Init Failed", Color.RED, false)
                        return@launch
                    }
                }
                
                var device: ReaderDevice? = null
                _readerStatus.value = ReaderStatus("Searching for $scannerSpec...", Color.GRAY, false, true)
                
                // Try to find the device with retries
                for (i in 1..3) {
                    val availableReaders = readers?.GetAvailableRFIDReaderList()
                    device = availableReaders?.find { it.name.trim().equals(scannerSpec.trim(), ignoreCase = true) }
                    if (device != null) break
                    Log.d("ZebraRepo", "Search attempt $i: device '$scannerSpec' not found")
                    delay(1000)
                }
                
                if (device != null) {
                    _readerStatus.value = ReaderStatus("Connecting to ${device.name}...", Color.GRAY, false, true)
                    val newReader = device.rfidReader
                    newReader?.let { r ->
                        var retryCount = 0
                        var connected = false
                        while (retryCount < 3 && !connected) {
                            try {
                                if (!r.isConnected) {
                                    r.connect()
                                }
                                connected = true
                            } catch (e: Exception) {
                                retryCount++
                                Log.w("ZebraRepo", "Connect attempt $retryCount failed: ${e.message}")
                                try { r.disconnect() } catch(_: Exception) {}
                                delay(2000) // Longer delay on retry
                                if (retryCount == 3) throw e
                            }
                        }
                        
                        if (r.isConnected) {
                            reader = r
                            Log.d("ZebraRepo", "Connected successfully to ${device.name}")
                            
                            // Wait for reader to be ready/capabilities received
                            var waitCount = 0
                            while (!r.isCapabilitiesReceived && waitCount < 50) {
                                delay(100)
                                waitCount++
                            }
                            
                            configureReader(r)
                            // Save this reader as last used
                            prefs.edit().putString("last_reader", scannerSpec).apply()
                            _readerStatus.value = ReaderStatus("Connected: ${device.name}", Color.GREEN, true)
                        } else {
                            _readerStatus.value = ReaderStatus("Connection Failed", Color.RED, false)
                        }
                    }
                } else {
                    _readerStatus.value = ReaderStatus("Device $scannerSpec Not Found", Color.RED, false)
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Connect Error", e)
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
            reader.Events.setReaderDisconnectEvent(true)

            // Setup antenna info for all supported antennas
            val numAntennas = if (reader.isCapabilitiesReceived) reader.ReaderCapabilities.numAntennaSupported else 1
            val antennas = ShortArray(numAntennas) { (it + 1).toShort() }
            antennaInfo = AntennaInfo(antennas)
            
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE)
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE)
            
            reader.Config.setStartTrigger(triggerInfo.StartTrigger)
            reader.Config.setStopTrigger(triggerInfo.StopTrigger)
            reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            
            // Set max power for all antennas
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
            Log.e("ZebraRepo", "Config error", e)
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
                        r.Events.removeEventsListener(this@ZebraRfidRepositoryImpl)
                    } catch (_: Exception) {}
                    
                    try {
                        r.disconnect()
                    } catch (_: Exception) {}
                    
                    Log.d("ZebraRepo", "Reader disconnected and listeners removed")
                }
                reader = null
                isInventoryRunning = false
                isTracing = false
                _readerStatus.value = ReaderStatus("Disconnected", Color.RED, false)
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Disconnect Error", e)
            }
        }
    }

    override fun startReader() {
        if (isInventoryRunning) return
        scope.launch {
            try {
                reader?.let { r ->
                    if (!r.isConnected) {
                        Log.e("ZebraRepo", "Start failed: Reader not connected")
                        return@launch
                    }
                    if (isTracing) return@launch
                    
                    // Ensure previous operations are stopped
                    try {
                        r.Actions.Inventory.stop()
                        delay(200) 
                    } catch (_: Exception) {}

                    tagsCount = 0
                    try {
                        r.Actions.purgeTags()
                    } catch (e: Exception) {
                        Log.e("ZebraRepo", "Purge failed: ${e.message}")
                    }

                    r.Actions.Inventory.perform(null, null, antennaInfo)
                    isInventoryRunning = true
                    Log.d("ZebraRepo", "Inventory Started Successfully")
                }
            } catch (e: OperationFailureException) {
                Log.e("ZebraRepo", "Start Error (OpFailure): ${e.results} - ${e.vendorMessage}")
                isInventoryRunning = false
                // If it fails, try without antennaInfo as a fallback
                if (e.results == RFIDResults.RFID_API_SUCCESS) return@launch 
                scope.launch {
                    try {
                        reader?.Actions?.Inventory?.perform(null, null, null)
                        isInventoryRunning = true
                    } catch (_: Exception) {}
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Start Error: ${e.message}")
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
                    Log.d("ZebraRepo", "Inventory Stopped")
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Stop Error: ${e.message}")
                isInventoryRunning = false
            }
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {
        // Implementation depends on app state
    }

    override fun getSavedScannerSpec(): String = prefs.getString("last_reader", "") ?: ""

    override fun launchDeviceList(activity: Activity) {
        scope.launch(Dispatchers.Main) {
            try {
                if (readers == null) {
                    readers = Readers(context, ENUM_TRANSPORT.ALL)
                }
                
                val availableReaders = readers?.GetAvailableRFIDReaderList()
                if (availableReaders.isNullOrEmpty()) {
                    Toast.makeText(activity, "No paired Zebra readers found. Please pair in Android BT Settings.", Toast.LENGTH_LONG).show()
                    _readerStatus.value = ReaderStatus("No paired readers", Color.RED, false)
                } else {
                    val readerNames = availableReaders.map { it.name }.toTypedArray()
                    AlertDialog.Builder(activity)
                        .setTitle("Select Zebra Reader")
                        .setItems(readerNames) { _, which ->
                            // Force disconnect old device before connecting new one
                            scope.launch {
                                disconnectReader()
                                delay(1000) // Wait for cleanup
                                connectToReader(readerNames[which])
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Error launching device list", e)
                Toast.makeText(activity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setEpcFilter(filter: String) {
        currentEpcFilter = filter
        Log.d("ZebraRepo", "EPC Filter updated to: $filter")
    }

    override fun startTagTracing(epc: String) {
        if (epc.isBlank()) return
        scope.launch {
            try {
                reader?.let { r ->
                    // 1. Stop any current activity
                    if (isInventoryRunning) {
                        r.Actions.Inventory.stop()
                        delay(200)
                    }
                    
                    isTracing = true
                    currentTracingEpc = epc.replace(" ", "").uppercase()
                    
                    // 2. Apply Pre-Filter
                    try {
                        r.Actions.PreFilters.deleteAll()
                        val pf = r.Actions.PreFilters.PreFilter()
                        pf.antennaID = 0.toShort()
                        pf.memoryBank = MEMORY_BANK.MEMORY_BANK_EPC
                        pf.setTagPattern(currentTracingEpc)
                        pf.tagPatternBitCount = currentTracingEpc!!.length * 4
                        pf.bitOffset = 32 // Skip CRC and PC
                        pf.filterAction = FILTER_ACTION.FILTER_ACTION_DEFAULT
                        r.Actions.PreFilters.add(pf)
                    } catch (e: Exception) {
                        Log.e("ZebraRepo", "Filter Error", e)
                    }
                    
                    // 3. Optimize for high-speed tracking (Session S0)
                    optimizeForLocationing(r)
                    
                    // 4. Start standard inventory (which is now filtered)
                    r.Actions.Inventory.perform(null, null, null)
                    isInventoryRunning = true
                    
                    lastTraceTime = System.currentTimeMillis()
                    startTraceWatchdog()
                    
                    Log.d("ZebraRepo", "Tracing (Filtered Inventory) Started for $currentTracingEpc")
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Trace Start Error", e)
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
                    Log.d("ZebraRepo", "Tracing Stopped and Filters Cleared")
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Trace Stop Error", e)
            }
        }
    }

    private fun optimizeForLocationing(reader: RFIDReader) {
        try {
            val numAntennas = reader.ReaderCapabilities.numAntennaSupported
            for (i in 1..numAntennas) {
                // Session S0 is best for locating because tags respond continuously
                val config = reader.Config.Antennas.getSingulationControl(i)
                config.session = SESSION.SESSION_S0
                config.tagPopulation = 30
                reader.Config.Antennas.setSingulationControl(i, config)
                
                // Set max power for better range during locate
                val powerLevels = reader.ReaderCapabilities.transmitPowerLevelValues
                if (powerLevels.isNotEmpty()) {
                    val rfConfig = reader.Config.Antennas.getAntennaRfConfig(i)
                    rfConfig.transmitPowerIndex = powerLevels.size - 1
                    reader.Config.Antennas.setAntennaRfConfig(i, rfConfig)
                }
            }
        } catch (e: Exception) {
            Log.e("ZebraRepo", "Optimize Error: ${e.message}")
        }
    }

    private fun restoreAfterLocationing(reader: RFIDReader) {
        try {
            val numAntennas = reader.ReaderCapabilities.numAntennaSupported
            for (i in 1..numAntennas) {
                // Restore Session S2 for standard inventory
                val config = reader.Config.Antennas.getSingulationControl(i)
                config.session = SESSION.SESSION_S2
                config.tagPopulation = 100
                reader.Config.Antennas.setSingulationControl(i, config)
            }
        } catch (e: Exception) {
            Log.e("ZebraRepo", "Restore Error: ${e.message}")
        }
    }

    override fun getTraceData(): Flow<TracedTagInfo> = _traceData.asSharedFlow()

    override fun isTracingTag(): Boolean = isTracing

    override fun eventReadNotify(readEvent: RfidReadEvents?) {
        val tags = reader?.Actions?.getReadTags(100) ?: return

        tags.forEach { tag ->
            val epc = tag.tagID
            if (isTracing) {
                Log.d("ZebraRepo search", "Tag Read: $epc (Antenna: ${tag.antennaID}, RSSI: ${tag.peakRSSI})")
                if(epc.trim()==currentTracingEpc?.trim()) {
                    isFound=true
                    lastTraceTime = System.currentTimeMillis()
                    // In filtered inventory, the reader only sends back the target tag
                    val rssi = tag.peakRSSI.toInt()
                    val proximity = calculateProximityFromRssi(rssi)
                    val ordinal = EpcParser.extractOrdinal(epc)
                    _traceData.tryEmit(TracedTagInfo(epc, proximity, ordinal))
                }else
                {
                    isFound=false
                }

            } else {
                Log.d("ZebraRepo", "Tag Read: $epc (Antenna: ${tag.antennaID}, RSSI: ${tag.peakRSSI})")
                // Only process 24-digit RFID tags that match the filter (if any)
                if (epc.length == 24) {
                    if (currentEpcFilter.isEmpty() || epc.startsWith(currentEpcFilter, ignoreCase = true)) {
                        tagsCount++
                        _tagReadEvents.tryEmit(TagReadEvent(epc, tagsCount))
                    }
                }
            }
        }
    }

    private fun calculateProximityFromRssi(rssi: Int): Int {
        // Zebra RSSI typically ranges from -30 (very close) to -80 (far)
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
        Log.d("ZebraRepo", "Status Event: ${statusEvent.statusEventType}")
        
        if (statusEvent.statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            val triggerEvent = statusEvent.HandheldTriggerEventData
            if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                startReader()
            } else if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                stopReader()
            }
        } else if (statusEvent.statusEventType == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            isInventoryRunning = false
            isTracing = false
            _readerStatus.value = ReaderStatus("Zebra Disconnected", Color.RED, false)
            Log.w("ZebraRepo", "Reader Disconnected Event")
        } else if (statusEvent.statusEventType == STATUS_EVENT_TYPE.BATTERY_EVENT) {
            val batteryData = statusEvent.BatteryData
            Log.d("ZebraRepo", "Battery Event: ${batteryData.level}% (Charging: ${batteryData.charging})")
            _readerStatus.value = _readerStatus.value.copy(batteryLevel = batteryData.level)
        }
    }

    private var lastProximity = 0
    private fun startLocationBeep() {
        locationBeepJob?.cancel()
        locationBeepJob = scope.launch {
            while (isActive) {
                if (isTracing && lastProximity > 0) {
                    Beeper.beep(Beeper.BEEP_100MS)
                    val delayTime = when {
                        lastProximity > 90 -> 50L
                        lastProximity > 70 -> 150L
                        lastProximity > 50 -> 300L
                        lastProximity > 30 -> 600L
                        else -> 1000L
                    }
                    delay(delayTime)
                } else {
                    delay(500)
                }
            }
        }
        
        // Listen to proximity updates to set lastProximity
        scope.launch {
            _traceData.collect { info ->
                lastProximity = info.scaledRssi
            }
        }
    }

    private fun stopLocationBeep() {
        locationBeepJob?.cancel()
        locationBeepJob = null
        lastProximity = 0
    }

    override fun switchHardware(type: String) {}
    override fun getCurrentType(): String = "ZEBRA"

    override fun dispose() {
        scope.launch {
            try {
                disconnectReader()
                reader?.Dispose()
                reader = null
                readers?.Dispose()
                readers = null
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Dispose Error", e)
            }
        }
    }
}
