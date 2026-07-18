package com.syed.jetpacktwo.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.jetpacktwo.data.remote.model.StockStatusDto
import com.syed.jetpacktwo.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class StockStatusUiState {
    object Loading : StockStatusUiState()
    data class Success(val data: List<StockStatusDto>) : StockStatusUiState()
    data class Error(val message: String) : StockStatusUiState()
}

@HiltViewModel
class StockStatusViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<StockStatusUiState>(StockStatusUiState.Loading)
    val uiState: StateFlow<StockStatusUiState> = _uiState.asStateFlow()

    init {
        fetchStockStatus()
    }

    fun fetchStockStatus() {
        viewModelScope.launch {
            _uiState.value = StockStatusUiState.Loading
            
            val result = repository.getStockStatus("00")
            if (result.isSuccess) {
                _uiState.value = StockStatusUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = StockStatusUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        _uiState.value = StockStatusUiState.Success(emptyList())
    }
}
