package com.syed.jetpacktwo.ui.download

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.jetpacktwo.data.local.PreferenceManager
import com.syed.jetpacktwo.data.local.db.ExpectedItemDao
import com.syed.jetpacktwo.data.local.db.ExpectedItemEntity
import com.syed.jetpacktwo.data.remote.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.syed.jetpacktwo.util.getErrorMessage

sealed class DownloadRackUiState {
    object Idle : DownloadRackUiState()
    object Downloading : DownloadRackUiState()
    data class Success(
        val data: List<ExpectedItemEntity>,
        val downloadTime: String
    ) : DownloadRackUiState()
    data class Error(val message: String) : DownloadRackUiState()
}

@HiltViewModel
class DownloadRackViewModel @Inject constructor(
    private val apiService: ApiService,
    private val preferenceManager: PreferenceManager,
    private val expectedItemDao: ExpectedItemDao
) : ViewModel() {

    private val _uiState = MutableStateFlow<DownloadRackUiState>(DownloadRackUiState.Idle)
    val uiState: StateFlow<DownloadRackUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            expectedItemDao.getAllExpectedItems().collect { items ->
                if (_uiState.value !is DownloadRackUiState.Downloading) {
                    val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    _uiState.value = DownloadRackUiState.Success(items, currentTime)
                }
            }
        }
    }

    fun downloadData() {
        viewModelScope.launch {
            _uiState.value = DownloadRackUiState.Downloading
            
            try {
                val baseUrl = preferenceManager.baseUrl.first()
                val fullUrl = if (baseUrl.endsWith("/")) {
                    "${baseUrl}StockTake/GetAllRacks"
                } else {
                    "${baseUrl}/StockTake/GetAllRacks"
                }

                val response = apiService.getAllRacks(fullUrl, "00")
                if (response.isSuccessful) {
                    val dtos = response.body() ?: emptyList()
                    val entities = dtos.map {
                        ExpectedItemEntity(
                            epc = it.shelfTagID,
                            description = it.shelfName,
                            department = it.department
                        )
                    }
                    expectedItemDao.replaceAll(entities)
                    
                    val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                    _uiState.value = DownloadRackUiState.Success(entities, currentTime)
                } else {
                    _uiState.value = DownloadRackUiState.Error(response.getErrorMessage())
                }
            } catch (e: Exception) {
                _uiState.value = DownloadRackUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        viewModelScope.launch {
            // Restore success state if data exists, else Idle
            val items = expectedItemDao.getAllExpectedItems().first()
            if (items.isNotEmpty()) {
                val currentTime = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
                _uiState.value = DownloadRackUiState.Success(items, currentTime)
            } else {
                _uiState.value = DownloadRackUiState.Idle
            }
        }
    }
}
