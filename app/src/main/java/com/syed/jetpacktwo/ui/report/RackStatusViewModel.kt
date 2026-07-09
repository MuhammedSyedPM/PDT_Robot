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
            kotlinx.coroutines.delay(300) // slight delay for loading effect
            
            val staticData = listOf(

                RackStatusDto(2, "Womens", "Glass Top Stand (L)", true),
                RackStatusDto(3, "Womens", "Standalone Stand (Steel)", true),
                RackStatusDto(4, "Womens", "Glass Top Stand (R)", true),
                RackStatusDto(5, "Mens", "Wall Rack Left Top (Corner)", true),
                RackStatusDto(6, "Mens", "Wall Rack Left Bottom (Corner)", false), // 2 = red cross
                RackStatusDto(7, "Mens", "Wall Rack Right Top (Corner)", false), // 2 = red cross
                RackStatusDto(8, "Mens", "Wall Rack Right Bottom (Corner)", true),
                RackStatusDto(9, "Mens", "Tie Rack Top", true),
                RackStatusDto(10, "Mens", "Tie Rack Bottom", true),
                RackStatusDto(11, "Womens", "White wall rack left top", true),
                RackStatusDto(12, "Womens", "White wall rack right top", true),
                RackStatusDto(13, "Womens", "White wall rack Middle", true),
                RackStatusDto(14, "Womens", "White wall rack Bottom", true),
                RackStatusDto(15, "Kids", "A", true),
                RackStatusDto(16, "Kids", "C", true),
                RackStatusDto(17, "Kids", "L", true),
                RackStatusDto(18, "Kids", "P", true),
                RackStatusDto(19, "Boys", "X", true),
                RackStatusDto(20, "Boys", "S", true),
                RackStatusDto(21, "Boys", "V", true),
                RackStatusDto(22, "Boys", "W", true),
                RackStatusDto(1, "Common", "Shoes Rack", true),
                RackStatusDto(23, "Common", "Reception Table", true),
                RackStatusDto(24, "Bedding", "Coffee Table", true)
            )
            _uiState.value = RackStatusUiState.Success(staticData)
        }
    }
}
