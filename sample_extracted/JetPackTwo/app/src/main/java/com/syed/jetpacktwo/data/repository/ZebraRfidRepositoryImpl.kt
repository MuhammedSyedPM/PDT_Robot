package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.zebra.rfid.api3.*
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.domain.repository.RfidRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import android.widget.Toast
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ZebraRfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository, RfidEventsListener {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val prefs = context.getSharedPreferences("zebra_prefs", Context.MODE_PRIVATE)

    private val _readerStatus = MutableStateFlow(
        ReaderStatus(status = "Idle", statusColor = Color.GRAY, isConnected = false)
    )
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private val _traceData = MutableSharedFlow<TracedTagInfo>(extraBufferCapacity = 16)
    private var isTracing = false

    private var readers: Readers? = null
    private var reader: RFIDReader? = null
    private var tagsCount = 0
    private var isInventoryRunning = false


    override fun connectToReader(scannerSpec: String) {
        scope.launch(Dispatchers.IO) {
            try {
                if (readers == null) {
                    try {
                        readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
                    } catch (e: Exception) {
                        Log.e("ZebraRepo", "Failed to init readers: ${e.message}")
                    }
                }
                
                _readerStatus.value = ReaderStatus("Connecting to Zebra...", Color.GRAY, false, true)
                val availableReaders = readers?.GetAvailableRFIDReaderList()
                if (availableReaders.isNullOrEmpty()) {
                    _readerStatus.value = ReaderStatus("No Zebra Readers found", Color.RED, false, false)
                    return@launch
                }

                // If spec is provided, try to find matching reader, otherwise take the first one
                val device = if (scannerSpec.isNotEmpty()) {
                    availableReaders.find { it.name == scannerSpec } ?: availableReaders[0]
                } else {
                    availableReaders[0]
                }

                reader = device.rfidReader
                reader?.let { r ->
                    try {
                        // Even if it says disconnected, try a "Safe Connect"
                        if (r.isConnected) {
                            Log.d("ZebraRepo", "Reader already connected, resetting...")
                            r.disconnect()
                        }
                        
                        r.connect()
                        configureReader(r)
                        // Save reader name for auto-connect
                        prefs.edit().putString("last_reader_name", r.hostName).apply()
                        
                        _readerStatus.value = ReaderStatus("Zebra Connected: ${r.hostName}", Color.GREEN, true, false)
                        Log.d("ZebraRepo", "Success: Zebra Connected")
                    } catch (e: Exception) {
                        Log.e("ZebraRepo", "Connection failed, trying alternate...", e)
                        _readerStatus.value = ReaderStatus("Connection Failed: ${e.message}", Color.RED, false, false)
                    }
                }
            } catch (e: Exception) {
                Log.e("ZebraRepo", "Connection error", e)
                _readerStatus.value = ReaderStatus("Error: ${e.message}", Color.RED, false)
            }
        }
    }

    private fun configureReader(reader: RFIDReader) {
        try {
            reader.Events.addEventsListener(this)
            reader.Events.setHandheldEvent(true)
            reader.Events.setTagReadEvent(true)
            reader.Events.setAttachTagDataWithReadEvent(true)
            
            val triggerInfo = TriggerInfo()
            triggerInfo.StartTrigger.setTriggerType(START_TRIGGER_TYPE.START_TRIGGER_TYPE_IMMEDIATE)
            triggerInfo.StopTrigger.setTriggerType(STOP_TRIGGER_TYPE.STOP_TRIGGER_TYPE_IMMEDIATE)
            
            reader.Config.setStartTrigger(triggerInfo.StartTrigger)
            reader.Config.setStopTrigger(triggerInfo.StopTrigger)
            reader.Config.setTriggerMode(ENUM_TRIGGER_MODE.RFID_MODE, true)
            
            // Set max power
            val powerLevels = reader.ReaderCapabilities.transmitPowerLevelValues
            if (powerLevels.isNotEmpty()) {
                val config = reader.Config.Antennas.getAntennaRfConfig(1)
                config.transmitPowerIndex = powerLevels.size - 1
                reader.Config.Antennas.setAntennaRfConfig(1, config)
            }
        } catch (e: Exception) {
            Log.e("ZebraRepo", "Config error", e)
        }
    }

    override fun disconnectReader() {
        try {
            reader?.disconnect()
            reader = null
            _readerStatus.value = ReaderStatus("Disconnected", Color.RED, false)
        } catch (e: Exception) {
            Log.e("ZebraRepo", "Disconnect error: ${e.message}")
        }
    }

    override fun getSavedScannerSpec(): String = prefs.getString("last_reader_name", "").orEmpty()

    private var currentTracingEpc: String? = null
    private var lastTraceSeenTime: Long = 0

    override fun startReader() {
        if (isInventoryRunning) return
        scope.launch(Dispatchers.IO) {
            try {
                reader?.let { r ->
                    if (r.isConnected) {
                        tagsCount = 0
                        r.Actions.Inventory.perform()
                        isInventoryRunning = true
                        Log.d("ZebraRepo", "Inventory Started (Tracing=$isTracing)")
                        
                        // Start a watchdog timer for tracing to reset values if tag is lost
                        if (isTracing) {
                            startTracingWatchdog()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.d("ZebraRepo", "Start Error: ${e.message}")
            }
        }
    }

    private fun startTracingWatchdog() {
        scope.launch {
            while (isTracing && isInventoryRunning) {
                kotlinx.coroutines.delay(1000) // Check every 1s
                val timeSinceLastSeen = System.currentTimeMillis() - lastTraceSeenTime
                if (timeSinceLastSeen > 2500 && lastTraceSeenTime > 0) {
                    // Tag lost for more than 3s, reset proximity to 0
                    currentTracingEpc?.let { epc ->
                        _traceData.emit(TracedTagInfo(epcHex = epc, scaledRssi = 0))
                        Log.d("ZebraRepo", "Tag Lost (3s): Resetting proximity to 0")
                        lastTraceSeenTime = 0 // Reset to avoid constant firing
                    }
                }
            }
        }
    }

    override fun stopReader() {
        scope.launch(Dispatchers.IO) {
            try {
                reader?.Actions?.Inventory?.stop()
                isInventoryRunning = false
                Log.d("ZebraRepo", "Inventory Stopped")
            } catch (e: Exception) {
                Log.d("ZebraRepo", "Stop Error: ${e.message}")
            }
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {
        // Not used for Zebra in this basic impl
    }

    override fun launchDeviceList(activity: Activity) {
        scope.launch(Dispatchers.Main) {
            try {
                if (readers == null) {
                    readers = Readers(context, ENUM_TRANSPORT.BLUETOOTH)
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
                            connectToReader(readerNames[which])
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

    override fun startTagTracing(epc: String) {
        if (epc.isBlank()) return
        Log.d("ZebraRepo", "Starting Trace for $epc")
        currentTracingEpc = epc
        isTracing = true
        startReader()
    }

    override fun stopTrace() {
        Log.d("ZebraRepo", "Stopping Trace...")
        isTracing = false
        currentTracingEpc = null
        stopReader()
        // Force an extra stop for safety to ensure module is quiet
        scope.launch(Dispatchers.IO) {
            try {
                reader?.Actions?.Inventory?.stop()
                reader?.Actions?.TagLocationing?.Stop()
            } catch (e: Exception) {
                Log.d("ZebraRepo", "Safety Stop Error: ${e.message}")
            }
        }
    }

    override fun getTraceData(): Flow<TracedTagInfo> = _traceData.asSharedFlow()

    override fun isTracingTag(): Boolean = isTracing

    // Zebra Callbacks
    override fun eventReadNotify(readEvent: RfidReadEvents?) {
        val tags = reader?.Actions?.getReadTags(100) ?: return
        
        tags.forEach { tag ->
            val epc = tag.tagID
            if (epc != null) {
                if (isTracing) {
                    // Filter: Only report for the specific tag we are searching for
                    if (epc.equals(currentTracingEpc, ignoreCase = true)) {
                        lastTraceSeenTime = System.currentTimeMillis()
                        val rssi = tag.peakRSSI.toInt()
                        // Scale RSSI (-80 far to -40 near) to 0..100
                        val proximity = ((rssi + 80) * 2.5).toInt().coerceIn(0, 100)
                        Log.d("ZebraRepo", "Searching $epc: RSSI=$rssi, Match=$proximity")
                        scope.launch { _traceData.emit(TracedTagInfo(epcHex = epc, scaledRssi = proximity)) }
                    }
                } else {
                    tagsCount++
                    scope.launch { _tagReadEvents.emit(TagReadEvent(epc, tagsCount)) }
                }
            }
        }
    }

    override fun eventStatusNotify(statusEvent: RfidStatusEvents?) {
        val eventData = statusEvent?.StatusEventData ?: return
        Log.d("ZebraRepo", "Status Event: ${eventData.statusEventType}")
        
        if (eventData.statusEventType == STATUS_EVENT_TYPE.HANDHELD_TRIGGER_EVENT) {
            val triggerEvent = eventData.HandheldTriggerEventData
            if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_PRESSED) {
                Log.d("ZebraRepo", "Trigger Pressed")
                startReader() // In Zebra, startReader handles inventory or starts locationing
            } else if (triggerEvent.handheldEvent == HANDHELD_TRIGGER_EVENT_TYPE.HANDHELD_TRIGGER_RELEASED) {
                Log.d("ZebraRepo", "Trigger Released")
                stopReader()
            }
        } else if (eventData.statusEventType == STATUS_EVENT_TYPE.DISCONNECTION_EVENT) {
            _readerStatus.value = ReaderStatus("Zebra Disconnected", Color.RED, false)
        }
    }

    override fun switchHardware(type: String) { /* Master handles this */ }
    override fun getCurrentType(): String = "ZEBRA"
}
