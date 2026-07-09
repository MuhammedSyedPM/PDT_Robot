package com.syed.jetpacktwo.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.impinj.octane.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.syed.jetpacktwo.domain.model.BarcodeEvent
import com.syed.jetpacktwo.domain.model.ReaderStatus
import com.syed.jetpacktwo.domain.model.TagReadEvent
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.domain.repository.RfidRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

data class AntennaConfig(
    var antennaPort: Int, 
    var txPower: Double, 
    var rxSensitivity: Double, 
    var isEnabled: Boolean
)

data class ImpinjConfig(
    var readerAddress: String = "", 
    var antennaConfig: List<AntennaConfig> = emptyList()
)

@Singleton
class ImpinjRfidRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : RfidRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs = context.getSharedPreferences("impinj_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    var currentConfig: ImpinjConfig
        private set
        
    init {
        val configStr = prefs.getString("impinj_config", "")
        if (configStr.isNullOrEmpty()) {
            val defaultConfig = (1..4).map { AntennaConfig(it, 30.0, -70.0, true) }
            currentConfig = ImpinjConfig("", defaultConfig)
            saveConfig(currentConfig)
        } else {
            currentConfig = gson.fromJson(configStr, ImpinjConfig::class.java)
        }
    }

    fun saveConfig(config: ImpinjConfig) {
        currentConfig = config
        prefs.edit { putString("impinj_config", gson.toJson(config)) }
    }

    private val _readerStatus = MutableStateFlow(ReaderStatus("Disconnected", 0, false))
    override val readerStatus: StateFlow<ReaderStatus> = _readerStatus.asStateFlow()

    private val _tagReadEvents = MutableSharedFlow<TagReadEvent>(extraBufferCapacity = 64)
    override val tagReadEvents: Flow<TagReadEvent> = _tagReadEvents.asSharedFlow()

    private val _barcodeEvents = MutableSharedFlow<BarcodeEvent>(extraBufferCapacity = 16)
    override val barcodeEvents: Flow<BarcodeEvent> = _barcodeEvents.asSharedFlow()

    private var reader: ImpinjReader? = null
    private var isConnected = false
    private var readerStarted = false
    private var isReadingEnabled = true
    
    private val inventoryList = mutableSetOf<String>()
    private var epcFilter: String = ""

    private var targetEpc: String = ""
    private var _isTracing = false
    private val _traceData = MutableSharedFlow<TracedTagInfo>(extraBufferCapacity = 16)

    override fun connectToReader(scannerSpec: String) {
        val ipAddress = scannerSpec.ifBlank { currentConfig.readerAddress.ifBlank { getSavedScannerSpec() } }
        if (ipAddress.isBlank()) {
            _readerStatus.value = ReaderStatus("Reader IP empty!", 0, false)
            return
        }

        if (isConnected && currentConfig.readerAddress == ipAddress) {
            _readerStatus.value = ReaderStatus("Connected: $ipAddress", 1, true)
            return
        }

        currentConfig.readerAddress = ipAddress
        saveConfig(currentConfig)
        
        prefs.edit { putString("reader_ip", ipAddress) }
        _readerStatus.value = ReaderStatus("Connecting...", 0, false)

        scope.launch {
            try {
                if (isConnected) {
                    reader?.disconnect()
                    isConnected = false
                    delay(500)
                }

                reader = ImpinjReader().apply {
                    connect(ipAddress)
                    
                    val settings = queryDefaultSettings()
                    settings.report.mode = ReportMode.Individual
                    settings.report.includeAntennaPortNumber = true
                    
                    // Simple configuration for all antennas
                    val ac = settings.antennas
                    ac.disableAll()
                    
                    val max = queryFeatureSet().antennaCount
                    currentConfig.antennaConfig.forEach { antConf ->
                        if (antConf.antennaPort <= max) {
                            val antenna = ac.getAntenna(antConf.antennaPort.toShort())
                            antenna.isEnabled = antConf.isEnabled
                            antenna.isMaxTxPower = false
                            antenna.isMaxRxSensitivity = false
                            antenna.txPowerinDbm = antConf.txPower
                            antenna.rxSensitivityinDbm = antConf.rxSensitivity
                        }
                    }
                    
                    applySettings(settings)
                    
                    tagReportListener = TagReportListener { _, report ->
                        val tags = report.tags
                        tags.forEach { tag ->
                            val epcStr = tag.epc.toString().replace(" ", "")
                            if (epcFilter.isNotEmpty() && !epcStr.startsWith(epcFilter, ignoreCase = true) && !epcStr.startsWith("C", ignoreCase = true)) {
                                return@forEach
                            }
                            
                            if (isReadingEnabled && !_isTracing) {
                                val isNew = inventoryList.add(epcStr)
                                scope.launch {
                                    _tagReadEvents.emit(TagReadEvent(epcStr, inventoryList.size))
                                }
                            } else if (_isTracing && epcStr.equals(targetEpc, ignoreCase = true)) {
                                val rssi = tag.peakRssiInDbm
                                // Map RSSi roughly -80 to -30 into 0-100 percentage
                                var percent = ((rssi + 80) * 2).toInt()
                                if (percent < 0) percent = 0
                                if (percent > 100) percent = 100
                                
                                scope.launch {
                                    _traceData.emit(TracedTagInfo(epcStr, percent))
                                }
                            }
                        }
                    }
                }
                
                isConnected = true
                _readerStatus.value = ReaderStatus("Connected: $ipAddress", 1, true)
            } catch (e: Exception) {
                e.printStackTrace()
                isConnected = false
                _readerStatus.value = ReaderStatus("Error: ${e.message}", 0, false)
            }
        }
    }

    override fun disconnectReader() {
        scope.launch {
            if (isConnected) {
                try {
                    reader?.disconnect()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                isConnected = false
                _readerStatus.value = ReaderStatus("Disconnected", 0, false)
            }
        }
    }

    override fun startReader() {
        if (!isConnected || readerStarted) return
        try {
            if (!_isTracing) {
                inventoryList.clear()
            }
            reader?.start()
            readerStarted = true
        } catch (e: Exception) {
            _readerStatus.value = ReaderStatus("Start Error: ${e.message}", 0, true)
        }
    }

    override fun stopReader() {
        if (!readerStarted) return
        try {
            reader?.stop()
            readerStarted = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun setReadingEnabled(enabled: Boolean) {
        isReadingEnabled = enabled
    }

    override fun getSavedScannerSpec(): String {
        return prefs.getString("reader_ip", "") ?: ""
    }

    override fun launchDeviceList(activity: Activity) {
        // Typically a dialog should be triggered from the UI for Impinj.
        // The implementation plan specifies modifying the UI to support IP entry.
    }

    override fun startTagTracing(epc: String) {
        targetEpc = epc
        _isTracing = true
        startReader()
    }

    override fun stopTrace() {
        _isTracing = false
        targetEpc = ""
        stopReader()
    }

    override fun getTraceData(): Flow<TracedTagInfo> = _traceData.asSharedFlow()

    override fun isTracingTag(): Boolean = _isTracing

    override fun switchHardware(type: String) {
        // No-op. Only Impinj is supported now.
    }

    override fun getCurrentType(): String = "IMPINJ"

    override fun launchPowerSettings(activity: Activity) {
        // Impinj power settings are handled via the ImpinjConfigDialog in the UI.
        android.widget.Toast.makeText(activity, "Use Impinj Setup for Power Settings", android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun setPowerLevel(level: Int) {
        val prefs = context.getSharedPreferences("impinj_prefs", Context.MODE_PRIVATE)
        prefs.edit().putFloat("tx_power", level.toFloat()).apply()
        if (isConnected && reader != null) {
            try {
                val settings = reader?.queryDefaultSettings()
                settings?.let { s ->
                    val antennas = s.antennas.getAntenna(1.toShort())
                    antennas.setIsMaxTxPower(false)
                    antennas.txPowerinDbm = level.toDouble()
                    reader?.applySettings(s)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun getPowerLevel(): Int {
        val prefs = context.getSharedPreferences("impinj_prefs", Context.MODE_PRIVATE)
        return prefs.getFloat("tx_power", 30f).toInt()
    }

    override fun dispose() {
        disconnectReader()
        scope.cancel()
    }

    override fun setEpcFilter(filter: String) {
        this.epcFilter = filter
    }
}
