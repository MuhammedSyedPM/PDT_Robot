package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import com.rscja.deviceapi.RFIDWithUHFUART
import com.rscja.deviceapi.entity.InventoryParameter
import com.rscja.deviceapi.entity.UHFTAGInfo
import com.rscja.deviceapi.interfaces.IUHF
import com.rscja.deviceapi.interfaces.IUHFInventoryCallback
import com.rscja.deviceapi.interfaces.IUHFLocationCallback
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.util.EpcParser
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
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChainwayRfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _readerStatus = MutableStateFlow(
        ReaderStatus(status = "Disconnected!", statusColor = Color.RED, isConnected = false)
    )
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private var mReader: RFIDWithUHFUART? = null
    private var readingEnabled = true
    private var isInventoryRunning = false
    private val rfidDataCaptured = mutableSetOf<String>()
    private var tagsCount = 0
    private var currentEpcFilter = ""

    init {
        // Initialize Chainway Reader
        try {
            mReader = RFIDWithUHFUART.getInstance()
        } catch (e: Exception) {
            Log.e("ChainwayRepo", "Failed to get instance: ${e.message}")
        }
    }

    override fun connectToReader(scannerSpec: String) {
        // For Chainway, "connecting" means initializing the hardware
        scope.launch {
            try {
                _readerStatus.value = ReaderStatus("Connecting to Chainway...", Color.GRAY, false, true)
                if (mReader == null) mReader = RFIDWithUHFUART.getInstance()
                val result = mReader?.init(context) ?: false
                if (result) {
                    val version = mReader?.version ?: ""
                    val model = android.os.Build.MODEL
                    val deviceName = when {
                        model.contains("C66", ignoreCase = true) -> "Chainway C66"
                        model.contains("C72", ignoreCase = true) -> "Chainway C72"
                        model.contains("C61", ignoreCase = true) -> "Chainway C61"
                        model.contains("C71", ignoreCase = true) -> "Chainway C71"
                        else -> "Chainway Handheld ($model)"
                    }
                    Log.d("ChainwayRepo", "Init Success. Model: $model, Module: $version")
                    // Set default power to 30
                    mReader?.setPower(30)
                    _readerStatus.value = ReaderStatus(deviceName, Color.GREEN, true, false)
                } else {
                    Log.e("ChainwayRepo", "Init Failed")
                    _readerStatus.value = ReaderStatus("Init Failed", Color.RED, false, false)
                }
            } catch (e: Exception) {
                Log.e("ChainwayRepo", "Connection error", e)
                _readerStatus.value = ReaderStatus("Error: ${e.message}", Color.RED, false, false)
            }
        }
    }

    override fun disconnectReader() {
        mReader?.free()
        _readerStatus.value = ReaderStatus("Disconnected", Color.RED, false)
    }

    override fun startReader() {
        if (isInventoryRunning) return
        mReader?.let { reader ->
            rfidDataCaptured.clear()
            tagsCount = 0
            reader.setInventoryCallback(object : IUHFInventoryCallback {
                override fun callback(info: UHFTAGInfo?) {
                    Log.d("ChainwayRepo", "Callback received: info=$info")
                    info?.let { tag ->
                        val epc = tag.getEPC() ?: ""
                        Log.d("ChainwayRepo", "Tag EPC: $epc, RSSI: ${tag.getRssi()}")
                        if (epc.isNotEmpty() && epc.length == 24 && !rfidDataCaptured.contains(epc)) {
                            if (currentEpcFilter.isEmpty() || epc.startsWith(currentEpcFilter, ignoreCase = true)) {
                                rfidDataCaptured.add(epc)
                                tagsCount++
                                Log.d("ChainwayRepo", "Emitting tag: $epc (Total: $tagsCount)")
                                scope.launch {
                                    _tagReadEvents.emit(TagReadEvent(epc, tagsCount))
                                }
                            }
                        }
                    }
                }
            })
            val param = InventoryParameter()
            param.setResultData(InventoryParameter.ResultData()) // Ensure result data is initialized
            if (reader.startInventoryTag(param)) {
                isInventoryRunning = true
                Log.d("ChainwayRepo", "Inventory Started with Callback")
            } else {
                Log.e("ChainwayRepo", "Failed to start inventory tag method")
            }
        }
    }

    override fun stopReader() {
        if (mReader?.stopInventory() == true) {
            isInventoryRunning = false
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {
        readingEnabled = enabled
    }

    override fun getSavedScannerSpec(): String = "internal_chainway"

    override fun launchDeviceList(activity: Activity) {
        // No device list for internal hardware, or just show a message
        _readerStatus.value = ReaderStatus("Using Internal UHF", Color.GREEN, _readerStatus.value.isConnected)
    }

    private val _traceData = MutableSharedFlow<TracedTagInfo>(extraBufferCapacity = 16)
    private var isTracing = false

    override fun startTagTracing(epc: String) {
        if (epc.isBlank()) return
        mReader?.let { reader ->
            // Bank 1 is EPC, 32 is the start pointer for EPC data
            val result = reader.startLocation(context, epc, IUHF.Bank_EPC, 32, object : IUHFLocationCallback {
                override fun getLocationValue(value: Int, valid: Boolean) {
                    if (valid) {
                        scope.launch {
                            val ordinal = EpcParser.extractOrdinal(epc)
                            _traceData.emit(TracedTagInfo(epcHex = epc, scaledRssi = value, ordinal = ordinal))
                        }
                    }else
                    {
                        scope.launch {
                            val ordinal = EpcParser.extractOrdinal(epc)
                            _traceData.emit(TracedTagInfo(epcHex = epc, scaledRssi = 0, ordinal = ordinal))
                        }
                    }
                }
            })
            if (result) {
                isTracing = true
                Log.d("ChainwayRepo", "Tag tracing started for $epc")
            } else {
                Log.e("ChainwayRepo", "Failed to start tag tracing")
            }
        }
    }

    override fun stopTrace() {
        mReader?.stopLocation()
        isTracing = false
    }

    override fun getTraceData(): Flow<TracedTagInfo> = _traceData.asSharedFlow()

    override fun isTracingTag(): Boolean = isTracing

    override fun switchHardware(type: String) { /* Master handles this */ }
    override fun getCurrentType(): String = "CHAINWAY"

    override fun dispose() {
        disconnectReader()
        mReader = null
    }

    override fun setEpcFilter(filter: String) {
        currentEpcFilter = filter
        Log.d("ChainwayRepo", "EPC Filter updated to: $filter")
    }
}
