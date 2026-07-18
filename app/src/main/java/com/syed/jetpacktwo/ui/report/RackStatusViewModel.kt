package com.syed.jetpacktwo.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.jetpacktwo.data.remote.model.RackStatusDto
import com.syed.jetpacktwo.domain.repository.ReportRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RackStatusUiState {
    object Loading : RackStatusUiState()
    data class Success(val data: List<RackStatusDto>) : RackStatusUiState()
    data class Error(val message: String) : RackStatusUiState()
}

@HiltViewModel
class RackStatusViewModel @Inject constructor(
    private val repository: ReportRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RackStatusUiState>(RackStatusUiState.Loading)
    val uiState: StateFlow<RackStatusUiState> = _uiState.asStateFlow()

    init {
        fetchRackStatus()
    }

    fun fetchRackStatus() {
        viewModelScope.launch {
            _uiState.value = RackStatusUiState.Loading
            
            val result = repository.getRackStatus("00")
            if (result.isSuccess) {
                _uiState.value = RackStatusUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = RackStatusUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    fun clearError() {
        _uiState.value = RackStatusUiState.Success(emptyList())
    }
}
