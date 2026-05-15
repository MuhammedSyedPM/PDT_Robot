package com.syed.jetpacktwo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.syed.jetpacktwo.data.model.LoginRequest
import com.syed.jetpacktwo.data.model.LoginResponse
import com.syed.jetpacktwo.data.repository.AuthRepository
import com.syed.jetpacktwo.domain.repository.RfidRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val rfidRepository: RfidRepository
) : ViewModel() {
    
    private val _hardwareType = MutableStateFlow(rfidRepository.getCurrentType())
    val hardwareType: StateFlow<String> = _hardwareType.asStateFlow()

    private val _username = MutableStateFlow("admin")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("open")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _baseUrl = MutableStateFlow("")
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    private val _devices = MutableStateFlow<List<com.syed.jetpacktwo.data.model.Device>>(emptyList())
    val devices: StateFlow<List<com.syed.jetpacktwo.data.model.Device>> = _devices.asStateFlow()

    private val _selectedDeviceName = MutableStateFlow("Select Device")
    val selectedDeviceName: StateFlow<String> = _selectedDeviceName.asStateFlow()

    private val _selectedDeviceId = MutableStateFlow("")
    val selectedDeviceId: StateFlow<String> = _selectedDeviceId.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginResult = MutableStateFlow<Result<LoginResponse>?>(null)
    val loginResult: StateFlow<Result<LoginResponse>?> = _loginResult.asStateFlow()

    private val _epcFilter = MutableStateFlow("")
    val epcFilter: StateFlow<String> = _epcFilter.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getBaseUrl().collectLatest {
                _baseUrl.value = it
            }
        }
        viewModelScope.launch {
            repository.getDeviceName().collectLatest {
                if (it.isNotEmpty()) _selectedDeviceName.value = it
            }
        }
        viewModelScope.launch {
            repository.getDeviceId().collectLatest {
                _selectedDeviceId.value = it
            }
        }
        viewModelScope.launch {
            repository.getEpcFilter().collectLatest {
                _epcFilter.value = it
            }
        }
    }

    fun saveEpcFilter(filter: String) {
        viewModelScope.launch {
            repository.saveEpcFilter(filter)
        }
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onBaseUrlChange(value: String) {
        _baseUrl.value = value
    }

    fun saveBaseUrl() {
        viewModelScope.launch {
            repository.saveBaseUrl(_baseUrl.value)
        }
    }

    fun fetchDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.getDevices()
            result.onSuccess {
                _devices.value = it.dataSet.data
            }.onFailure {
                // Handle error if needed
            }
            _isLoading.value = false
        }
    }

    fun onDeviceSelect(device: com.syed.jetpacktwo.data.model.Device) {
        _selectedDeviceName.value = device.deviceName
        _selectedDeviceId.value = device.id.toString()
        viewModelScope.launch {
            repository.saveDevice(device.id.toString(), device.deviceName)
        }
    }

    fun onHardwareTypeSelect(type: String) {
        _hardwareType.value = type
        rfidRepository.switchHardware(type)
    }

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            val request = LoginRequest(
                custID = "00", // Default as per example
                userID = _username.value,
                password = _password.value,
                regToken = "string",
                deviceMACID = "string",
                locationID = 0,
                deviceID = _selectedDeviceId.value.ifEmpty { "1" }
            )
            val result = repository.login(request)
            _loginResult.value = result
            _isLoading.value = false
        }
    }
    
    fun resetLoginResult() {
        _loginResult.value = null
    }
}
