package com.syed.jetpacktwo.presentation.settings

import androidx.lifecycle.ViewModel
import com.syed.jetpacktwo.util.ThemeManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val themeManager: ThemeManager
) : ViewModel() {
    val isDarkMode: StateFlow<Boolean> = themeManager.isDarkMode

    fun toggleTheme() {
        themeManager.toggleTheme()
    }
}
