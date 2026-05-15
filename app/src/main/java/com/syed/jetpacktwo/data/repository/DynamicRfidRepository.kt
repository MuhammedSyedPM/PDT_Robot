package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.content.Context
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.domain.repository.RfidRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class DynamicRfidRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val nordicRepo: RfidRepositoryImpl,
    private val chainwayRepo: ChainwayRfidRepositoryImpl,
    private val zebraRepo: ZebraRfidRepositoryImpl
) : RfidRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val prefs = context.getSharedPreferences("hardware_prefs", Context.MODE_PRIVATE)

    private val _currentType = MutableStateFlow(
        prefs.getString("hardware_type", null) ?: getDefaultHardwareType()
    )
    
    private fun getDefaultHardwareType(): String {
        val manufacturer = android.os.Build.MANUFACTURER
        return when {
            manufacturer.contains("Zebra", ignoreCase = true) -> "ZEBRA"
            manufacturer.contains("Chainway", ignoreCase = true) || 
            manufacturer.contains("C5", ignoreCase = true) -> "CHAINWAY"
            else -> "NORDIC"
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override val readerStatus: StateFlow<ReaderStatus> = _currentType
        .flatMapLatest { type ->
            getRepoForType(type).readerStatus
        }
        .stateIn(scope, SharingStarted.Eagerly, ReaderStatus("Initializing", 0, false))

    @OptIn(ExperimentalCoroutinesApi::class)
    override val tagReadEvents: SharedFlow<TagReadEvent> = _currentType
        .flatMapLatest { type ->
            getRepoForType(type).tagReadEvents
        }
        .shareIn(scope, SharingStarted.Eagerly, 64)

    @OptIn(ExperimentalCoroutinesApi::class)
    override val barcodeEvents: SharedFlow<BarcodeEvent> = _currentType
        .flatMapLatest { type ->
            getRepoForType(type).barcodeEvents
        }
        .shareIn(scope, SharingStarted.Eagerly, 16)

    private fun getRepoForType(type: String): RfidRepository {
        return when (type.uppercase()) {
            "ZEBRA" -> zebraRepo
            "CHAINWAY" -> chainwayRepo
            "NORDIC" -> nordicRepo
            else -> nordicRepo
        }
    }

    override fun switchHardware(type: String) {
        scope.launch {
            try {
                getRepoForType(_currentType.value).disconnectReader()
                _currentType.value = type
                prefs.edit { putString("hardware_type", type) }
                
                // Automatically connect to the new hardware's last saved reader
                val newRepo = getRepoForType(type)
                val savedSpec = newRepo.getSavedScannerSpec()
                if (savedSpec.isNotEmpty()) {
                    newRepo.connectToReader(savedSpec)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getCurrentType(): String = _currentType.value

    private val activeRepo: RfidRepository
        get() = getRepoForType(_currentType.value)

    override fun connectToReader(scannerSpec: String) = activeRepo.connectToReader(scannerSpec)
    override fun disconnectReader() = activeRepo.disconnectReader()
    override fun startReader() = activeRepo.startReader()
    override fun stopReader() = activeRepo.stopReader()
    override fun setReadingEnabled(enabled: Boolean) = activeRepo.setReadingEnabled(enabled)
    override fun getSavedScannerSpec(): String = activeRepo.getSavedScannerSpec()
    override fun launchDeviceList(activity: Activity) = activeRepo.launchDeviceList(activity)
    override fun startTagTracing(epc: String) = activeRepo.startTagTracing(epc)
    override fun stopTrace() = activeRepo.stopTrace()
    override fun getTraceData(): Flow<TracedTagInfo> = activeRepo.getTraceData()
    override fun isTracingTag(): Boolean = activeRepo.isTracingTag()

    override fun dispose() {
        nordicRepo.dispose()
        chainwayRepo.dispose()
        zebraRepo.dispose()
    }

    override fun setEpcFilter(filter: String) {
        nordicRepo.setEpcFilter(filter)
        chainwayRepo.setEpcFilter(filter)
        zebraRepo.setEpcFilter(filter)
    }
}
