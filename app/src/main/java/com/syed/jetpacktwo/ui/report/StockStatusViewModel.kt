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
            kotlinx.coroutines.delay(300)
            
            // Unique departments and their expected counts based on the image
            val staticData = listOf(
                StockStatusDto(2, "Womens", 63, 57),
                StockStatusDto(3, "Mens", 375, 396),
                StockStatusDto(4, "Kids", 211, 125),
                StockStatusDto(5, "Boys", 9, 9),
                StockStatusDto(6, "Common", 23, 15),
                StockStatusDto(7, "Bedding", 13, 7)
            )
            _uiState.value = StockStatusUiState.Success(staticData)
        }
    }
}
