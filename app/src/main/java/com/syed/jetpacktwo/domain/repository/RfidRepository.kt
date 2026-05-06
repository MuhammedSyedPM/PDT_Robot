package com.syed.jetpacktwo.domain.repository

import android.app.Activity
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

/**
 * Domain contract for RFID reader operations.
 * Clean architecture: UI and ViewModels depend only on this interface.
 */
interface RfidRepository {

    /** Current reader status (connection state, status text, color). */
    val readerStatus: StateFlow<ReaderStatus>

    /** Stream of tag read events (EPC + inventory count). */
    val tagReadEvents: Flow<TagReadEvent>

    /** Stream of barcode scan results. */
    val barcodeEvents: Flow<BarcodeEvent>

    /** Connect to reader using the given scanner spec (e.g. from device list). */
    fun connectToReader(scannerSpec: String)

    /** Disconnect the current reader. */
    fun disconnectReader()

    /** Start continuous inventory stream (trigger down). */
    fun startReader()

    /** Stop inventory stream (trigger up). */
    fun stopReader()

    /** Whether reading is enabled (can be toggled by UI). */
    fun setReadingEnabled(enabled: Boolean)

    /** Last saved scanner spec (e.g. for reconnecting). */
    fun getSavedScannerSpec(): String

    /** Launch the system device picker so the user can select an RFID reader. Result spec should be passed to connectToReader. */
    fun launchDeviceList(activity: Activity)

    /** Start tracing a tag by EPC (hex string). Emit updates via getTraceData(). */
    fun startTagTracing(epc: String)

    /** Stop tag tracing. */
    fun stopTrace()

    /** Stream of trace updates (scaledRssi etc.) while tracing. */
    fun getTraceData(): Flow<TracedTagInfo>

    /** Whether a tag trace is currently running. */
    fun isTracingTag(): Boolean

    /** Switch the active hardware implementation at runtime. */
    fun switchHardware(type: String)

    /** Get the identifier of the currently active hardware type. */
    fun getCurrentType(): String

    /** Release all hardware resources. Called when app is destroyed. */
    fun dispose()

    /** Apply an EPC prefix filter. Only tags starting with this prefix will be scanned. Empty = no filter. */
    fun setEpcFilter(filter: String)
}
