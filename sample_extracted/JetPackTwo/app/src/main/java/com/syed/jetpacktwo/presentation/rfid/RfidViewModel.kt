package com.syed.jetpacktwo.presentation.rfid

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.domain.repository.RfidRepository
import kotlinx.coroutines.flow.Flow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class RfidViewModel @Inject constructor(
    private val rfidRepository: RfidRepository
) : ViewModel() {

    val readerStatus: StateFlow<ReaderStatus> = rfidRepository.readerStatus

    private val _tagReads = MutableStateFlow<List<TagReadEvent>>(emptyList())
    val tagReads: StateFlow<List<TagReadEvent>> = _tagReads.asStateFlow()

    private val _barcodeScans = MutableStateFlow<List<BarcodeEvent>>(emptyList())
    val barcodeScans: StateFlow<List<BarcodeEvent>> = _barcodeScans.asStateFlow()

    init {
        rfidRepository.tagReadEvents
            .onEach { event ->
                _tagReads.update { list -> list + event }
            }
            .catch { }
            .launchIn(viewModelScope)

        rfidRepository.barcodeEvents
            .onEach { event ->
                _barcodeScans.update { list -> list + event }
            }
            .catch { }
            .launchIn(viewModelScope)
    }

    fun connect(scannerSpec: String) {
        rfidRepository.connectToReader(scannerSpec)
    }

    fun connectLastSaved() {
        val spec = rfidRepository.getSavedScannerSpec()
        Log.d("MSD", "Last saved scanner spec: $spec")
        if (spec.isNotEmpty()) rfidRepository.connectToReader(spec)
    }

    fun disconnect() = rfidRepository.disconnectReader()
    fun startReader() = rfidRepository.startReader()
    fun stopReader() = rfidRepository.stopReader()
    fun setReadingEnabled(enabled: Boolean) = rfidRepository.setReadingEnabled(enabled)
    fun clearTagReads() { _tagReads.value = emptyList() }
    fun clearBarcodeScans() { _barcodeScans.value = emptyList() }
    fun launchDeviceList(activity: Activity) = rfidRepository.launchDeviceList(activity)

    fun startTagTracing(epc: String) = rfidRepository.startTagTracing(epc)
    fun stopTrace() = rfidRepository.stopTrace()
    fun getTraceData(): Flow<TracedTagInfo> = rfidRepository.getTraceData()
    fun isTracingTag(): Boolean = rfidRepository.isTracingTag()

    fun switchHardware(type: String) = rfidRepository.switchHardware(type)
    fun getCurrentHardwareType(): String = rfidRepository.getCurrentType()
}
