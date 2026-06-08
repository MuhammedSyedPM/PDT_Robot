package com.syed.jetpacktwo.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val preferenceManager: com.syed.jetpacktwo.data.local.PreferenceManager
) {
    private val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main + kotlinx.coroutines.SupervisorJob())
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private val _primaryColor = MutableStateFlow(0xFF00D09CL)
    val primaryColor: StateFlow<Long> = _primaryColor

    init {
        scope.launch {
            preferenceManager.isDarkMode.collect {
                _isDarkMode.value = it
            }
        }
        scope.launch {
            preferenceManager.primaryColor.collect {
                _primaryColor.value = it
            }
        }
    }

    fun toggleTheme() {
        val nextValue = !_isDarkMode.value
        _isDarkMode.value = nextValue
        scope.launch {
            preferenceManager.saveTheme(nextValue)
        }
    }

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        scope.launch {
            preferenceManager.saveTheme(enabled)
        }
    }

    fun setPrimaryColor(color: Long) {
        _primaryColor.value = color
        scope.launch {
            preferenceManager.savePrimaryColor(color)
        }
    }
}
