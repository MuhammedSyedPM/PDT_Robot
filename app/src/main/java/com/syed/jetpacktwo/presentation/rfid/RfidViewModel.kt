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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.first
import javax.inject.Inject

import com.syed.jetpacktwo.data.repository.SyncRepository
import com.syed.jetpacktwo.data.model.StockTakeResponse
import com.syed.jetpacktwo.data.local.PreferenceManager

@HiltViewModel
class RfidViewModel @Inject constructor(
    private val rfidRepository: RfidRepository,
    private val scannedTagDao: com.syed.jetpacktwo.data.local.db.ScannedTagDao,
    private val syncRepository: SyncRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    private val _uploadResult = MutableStateFlow<Result<StockTakeResponse>?>(null)
    val uploadResult: StateFlow<Result<StockTakeResponse>?> = _uploadResult.asStateFlow()

    val readerStatus: StateFlow<ReaderStatus> = rfidRepository.readerStatus
    val scannerSpec: StateFlow<String> = MutableStateFlow(rfidRepository.getSavedScannerSpec()).asStateFlow()

    private val _tagReads = MutableStateFlow<List<TagReadEvent>>(emptyList())
    val tagReads: StateFlow<List<TagReadEvent>> = _tagReads.asStateFlow()

    private val _barcodeScans = MutableStateFlow<List<BarcodeEvent>>(emptyList())
    val barcodeScans: StateFlow<List<BarcodeEvent>> = _barcodeScans.asStateFlow()

    val totalScannedCount: StateFlow<Int> = scannedTagDao.getTagCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val existingTagEpcs: StateFlow<Set<String>> = scannedTagDao.getAllTags()
        .map { tags -> tags.map { it.epc }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val configuredDeviceName: StateFlow<String> = preferenceManager.deviceName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Select Device")

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

        viewModelScope.launch {
            preferenceManager.epcFilter.collect { filter ->
                rfidRepository.setEpcFilter(filter)
            }
        }
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
    fun saveCurrentTags() {
        val currentTags = _tagReads.value // Capture immediately to avoid race condition
        viewModelScope.launch {
            val schedulerId = preferenceManager.schedulerId.first().ifEmpty { "1" }
            val tagsToSave = currentTags
                .distinctBy { it.epc }
                .map { com.syed.jetpacktwo.data.local.db.ScannedTag(epc = it.epc, schedulerId = schedulerId) }
            
            if (tagsToSave.isNotEmpty()) {
                scannedTagDao.insertTags(tagsToSave)
                Log.d("RfidViewModel", "Saved ${tagsToSave.size} tags with scheduler $schedulerId")
            }
        }
    }

    fun clearAllTags() {
        viewModelScope.launch {
            scannedTagDao.clearAll()
            Log.d("RfidViewModel", "Cleared all tags from database")
        }
    }

    fun uploadTags() {
        viewModelScope.launch {
            _isUploading.value = true
            _uploadResult.value = null
            val result = syncRepository.uploadInventory()
            _uploadResult.value = result
            _isUploading.value = false
        }
    }

    fun resetUploadResult() {
        _uploadResult.value = null
    }

    fun dispose() = rfidRepository.dispose()
}
