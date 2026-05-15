package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.syed.jetpacktwo.TraceTagController
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.util.EpcParser
import com.syed.jetpacktwo.domain.repository.RfidRepository
import com.technowave.techno_rfid.Beeper
import com.technowave.techno_rfid.NordicId.AccBarcodeResult
import com.technowave.techno_rfid.NordicId.AccBarcodeResultListener
import com.technowave.techno_rfid.NurApi.BleScanner
import com.technowave.techno_rfid.NurApi.NurApiAutoConnectTransport
import com.technowave.techno_rfid.NurApi.NurDeviceListActivity
import com.technowave.techno_rfid.NurApi.NurDeviceSpec
import com.technowave.techno_rfid.NordicId.AccessoryExtension
import com.technowave.techno_rfid.NordicId.NurApi
import com.technowave.techno_rfid.NordicId.NurApiErrors
import com.technowave.techno_rfid.NordicId.NurApiListener
import com.technowave.techno_rfid.NordicId.NurEventIOChange
import com.technowave.techno_rfid.NordicId.NurEventInventory
import com.technowave.techno_rfid.NordicId.NurTagStorage
import com.technowave.techno_rfid.NordicId.NurRespReaderInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

private const val NORDIC_PREF = "nordic_pref"
private const val SCANNER_ID = "scanner_id"
private const val TRIGGER_SOURCE = 100

@Singleton
class RfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository, NurApiListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val prefs = context.getSharedPreferences(NORDIC_PREF, Context.MODE_PRIVATE)

    private val _readerStatus = MutableStateFlow(
        ReaderStatus(status = "Disconnected!", statusColor = Color.RED, isConnected = false)
    )
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private var readingEnabled = true
    private var mTriggerDown = false
    private var mTagsAddedCounter = 0
    private val rfidDataCaptured = mutableSetOf<String>()
    private var mScanning = false
    private var mAiming = false
    private var currentEpcFilter = ""

    private lateinit var mNurApi: NurApi
    private lateinit var mAccExt: AccessoryExtension
    private var hAcTr: NurApiAutoConnectTransport? = null
    private val mTagStorage = NurTagStorage()
    /** Full scanner spec (e.g. type=BLE;addr=...;name=...) used for reconnect. Must save this, not just address. */
    private var lastUsedScannerSpec: String = ""

    private var traceTagController: TraceTagController? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    private val mBarcodeResult = AccBarcodeResultListener { accBarcodeResult: AccBarcodeResult ->
        when {
            accBarcodeResult.status == NurApiErrors.NO_TAG -> {
                Beeper.beep(Beeper.FAIL)
                mScanning = false
            }
            accBarcodeResult.status == NurApiErrors.NOT_READY -> {}
            accBarcodeResult.status == NurApiErrors.HW_MISMATCH -> {
                Beeper.beep(Beeper.FAIL)
                mScanning = false
            }
            accBarcodeResult.status != NurApiErrors.NUR_SUCCESS -> {
                Beeper.beep(Beeper.FAIL)
                mScanning = false
            }
            else -> {
                scope.launch {
                    _barcodeEvents.tryEmit(BarcodeEvent(accBarcodeResult.strBarcode.trim()))
                }
                Beeper.beep(Beeper.BEEP_100MS)
                try {
                    mAccExt.beepAsync(100)
                    val config = mAccExt.config
                    if (config != null && config.hasVibrator()) {
                        mAccExt.vibrate(200)
                    }
                } catch (_: Exception) {}
                mScanning = false
            }
        }
    }

    init {
        Beeper.init(context)
        Beeper.setEnabled(true)
        BleScanner.init(context)
        mNurApi = NurApi()
        mAccExt = AccessoryExtension(mNurApi)
        mNurApi.listener = this
        mAccExt.registerBarcodeResultListener(mBarcodeResult)
    }

    override fun getSavedScannerSpec(): String =
        prefs.getString(SCANNER_ID, "").orEmpty()

    override fun launchDeviceList(activity: Activity) {
        try {
            NurDeviceListActivity.startDeviceRequest(activity, mNurApi)
        } catch (e: Exception) {
            updateStatus("Device list error: ${e.message}", Color.RED, _readerStatus.value.isConnected)
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {
        readingEnabled = enabled
    }

    override fun connectToReader(scannerSpec: String) {
        if (scannerSpec.isBlank()) {
            updateStatus("No scanner selected", Color.RED, false)
            return
        }
        lastUsedScannerSpec = scannerSpec
        
        // Comprehensive cleanup before new connection
        disconnectReader()
        try {
            mNurApi.disconnect() // Explicit API reset
        } catch (_: Exception) {}

        val spec = try { NurDeviceSpec(scannerSpec) } catch (e: Exception) { null }
        val isUsb = spec?.type?.equals("USB", ignoreCase = true) == true

        if (isUsb) {
            // USB connection can be immediate
            try {
                hAcTr = NurDeviceSpec.createAutoConnectTransport(context, mNurApi, spec!!)
                hAcTr?.setAddress("USB")
                showConnecting("USB")
            } catch (e: Exception) {
                updateStatus(e.message ?: "USB Error", Color.RED, false)
            }
        } else {
            // 2s delay allows BT stack to settle (prevents GATT 133)
            mainHandler.postDelayed({
                try {
                    val currentSpec = NurDeviceSpec(scannerSpec)
                    hAcTr = NurDeviceSpec.createAutoConnectTransport(context, mNurApi, currentSpec)
                    val strAddress = currentSpec.address
                    hAcTr?.setAddress(strAddress)
                    showConnecting(strAddress)
                } catch (e: Exception) {
                    Log.d("RfidRepositoryImpl", "Connection error: ${e.message}")
                    updateStatus(e.message ?: "Error", Color.RED, false)
                }
            }, 2000)
        }
    }

    override fun disconnectReader() {
        try {
            hAcTr?.dispose()
            hAcTr = null
            if (mNurApi.isConnected) {
                mNurApi.disconnect()
            }
        } catch (e: Exception) {
            Log.e("RfidRepositoryImpl", "Disconnect error: ${e.message}")
        }
    }

    override fun startReader() {
        if (mTriggerDown) return
        try {
            // Performance Tuning for Nordic
            mNurApi.clearIdBuffer()
            try {
                mNurApi.storage.clear() // Clear internal buffer to keep memory light
            } catch (_: Exception) {}
            
            rfidDataCaptured.clear() // Reset duplicate filter for new session
            
            mNurApi.startInventoryStream()
            mTriggerDown = true
            mTagsAddedCounter = 0
        } catch (ex: Exception) {
            updateStatus(ex.message ?: "Error", Color.RED, _readerStatus.value.isConnected)
        }
    }

    override fun stopReader() {
        try {
            if (mNurApi.isInventoryStreamRunning) mNurApi.stopInventoryStream()
            mTriggerDown = false
        } catch (ex: Exception) {
            updateStatus(ex.message ?: "Error", Color.RED, _readerStatus.value.isConnected)
        }
    }

    override fun startTagTracing(epc: String) {
        if (epc.isBlank()) return
        val controller = traceTagController ?: return
        if (controller.isTracingTag) {
            stopTrace()
            return
        }
        try {
            mNurApi.storage.clear()
            mTagStorage.clear()
            controller.setTagTrace(epc)
            controller.startTagTrace(epc)
        } catch (e: Exception) {
            Log.d("RfidRepositoryImpl", "startTagTracing error: ${e.message}")
        }
    }

    override fun stopTrace() {
        traceTagController?.stopTagTrace()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getTraceData(): Flow<TracedTagInfo> = callbackFlow {
        val controller = traceTagController ?: run {
            awaitClose { }
            return@callbackFlow
        }
        controller.setListener(object : TraceTagController.TraceTagListener {
            override fun traceTagEvent(data: TraceTagController.TracedTagInfo) {
                val epcHex = if (data.epc != null) NurApi.byteArrayToHexString(data.epc) else ""
                val ordinal = EpcParser.extractOrdinal(epcHex)
                trySend(TracedTagInfo(epcHex = epcHex, scaledRssi = data.scaledRssi, ordinal = ordinal))
            }
            override fun readerDisconnected() {}
            override fun readerConnected() {}
            override fun IOChangeEvent(event: com.technowave.techno_rfid.NordicId.NurEventIOChange) {}
        })
        awaitClose { }
    }

    override fun isTracingTag(): Boolean = traceTagController?.isTracingTag == true

    private fun showConnecting(address: String) {
        updateStatus("Connecting to $address", Color.RED, false, true)
    }

    private fun updateStatus(status: String, color: Int, isConnected: Boolean, isConnecting: Boolean = false) {
        _readerStatus.value = ReaderStatus(status, color, isConnected, isConnecting)
    }

    override fun logEvent(p0: Int, p1: String?) {}

    override fun connectedEvent() {
        try {
            val isAccessorySupported = mAccExt.isSupported
            val statusText = if (isAccessorySupported) {
                try {
                    "Connected to ${mAccExt.config.name}"
                } catch (e: Exception) {
                    "Connected (accessory)"
                }
            } else {
                val ri: NurRespReaderInfo = mNurApi.readerInfo
                "Connected to ${ri.name}"
            }
            updateStatus(statusText, Color.GREEN, true, false)
            if (lastUsedScannerSpec.isNotEmpty()) {
                prefs.edit().putString(SCANNER_ID, lastUsedScannerSpec).apply()
            }
            traceTagController = TraceTagController(mNurApi)
        } catch (ex: Exception) {
            updateStatus(ex.message ?: "Error", Color.RED, true)
        }
    }

    override fun disconnectedEvent() {
        traceTagController?.stopTagTrace()
        traceTagController = null
        updateStatus("Reader disconnected", Color.RED, false)
    }

    override fun bootEvent(p0: String?) {}

    @Synchronized
    override fun inventoryStreamEvent(event: NurEventInventory) {
        if (event.stopped) {
            try {
                mNurApi.startInventoryStream()
            } catch (_: Exception) {}
            return
        }

        scope.launch {
            try {
                val tagStorage = mNurApi.storage
                for (i in 0 until tagStorage.size()) {
                    val tag = tagStorage.get(i)
                    val epcString = NurApi.byteArrayToHexString(tag.epc)
                    
                    // Filter 24 digit tags
                    if (epcString.length != 24) continue
                    
                    // Apply EPC Prefix Filter if active
                    if (currentEpcFilter.isNotEmpty() && !epcString.startsWith(currentEpcFilter, ignoreCase = true)) {
                        continue
                    }
                    
                    // Filter duplicates in this session
                    if (rfidDataCaptured.contains(epcString.trim())) continue
                    rfidDataCaptured.add(epcString.trim())

                    Log.d("RfidRepositoryImpl", "Read tag: $epcString")
                    if (readingEnabled) {
                        _tagReadEvents.tryEmit(
                            TagReadEvent(epcString, rfidDataCaptured.size)
                        )
                    }
                }
            } catch (ex: Exception) {
                Log.e("RfidRepositoryImpl", "RFID read error: ${ex.message}")
            }
        }
    }

    override fun IOChangeEvent(event: NurEventIOChange) {
        try {
            if (event.source == TRIGGER_SOURCE && event.direction == 1) {
                if (mScanning) {
                    mAccExt.cancelBarcodeAsync()
                } else {
                    mAiming = true
                    mAccExt.imagerAIM(mAiming)
                }
            } else if (event.source == TRIGGER_SOURCE && event.direction == 0) {
                if (mScanning) {
                    mScanning = false
                    return
                }
                mAiming = false
                mAccExt.imagerAIM(mAiming)
                mAccExt.readBarcodeAsync(5000)
                mScanning = true
            }
        } catch (ex: Exception) {
            // ignore
        }
    }

    override fun traceTagEvent(p0: com.technowave.techno_rfid.NordicId.NurEventTraceTag?) {}
    override fun triggeredReadEvent(p0: com.technowave.techno_rfid.NordicId.NurEventTriggeredRead?) {}
    override fun frequencyHopEvent(p0: com.technowave.techno_rfid.NordicId.NurEventFrequencyHop?) {}
    override fun debugMessageEvent(p0: String?) {}
    override fun inventoryExtendedStreamEvent(p0: NurEventInventory?) {}
    override fun programmingProgressEvent(p0: com.technowave.techno_rfid.NordicId.NurEventProgrammingProgress?) {}
    override fun deviceSearchEvent(p0: com.technowave.techno_rfid.NordicId.NurEventDeviceInfo?) {}
    override fun clientConnectedEvent(p0: com.technowave.techno_rfid.NordicId.NurEventClientInfo?) {}
    override fun clientDisconnectedEvent(p0: com.technowave.techno_rfid.NordicId.NurEventClientInfo?) {}
    override fun nxpEasAlarmEvent(p0: com.technowave.techno_rfid.NordicId.NurEventNxpAlarm?) {}
    override fun epcEnumEvent(p0: com.technowave.techno_rfid.NordicId.NurEventEpcEnum?) {}
    override fun autotuneEvent(p0: com.technowave.techno_rfid.NordicId.NurEventAutotune?) {}
    override fun tagTrackingScanEvent(p0: com.technowave.techno_rfid.NordicId.NurEventTagTrackingData?) {}
    override fun tagTrackingChangeEvent(p0: com.technowave.techno_rfid.NordicId.NurEventTagTrackingChange?) {}

    override fun switchHardware(type: String) { /* Master handles this */ }
    override fun getCurrentType(): String = "NORDIC"

    override fun dispose() {
        if (::mNurApi.isInitialized) mNurApi.disconnect()
    }

    override fun setEpcFilter(filter: String) {
        currentEpcFilter = filter
        Log.d("NordicRepo", "EPC Filter updated to: $filter")
    }
}
