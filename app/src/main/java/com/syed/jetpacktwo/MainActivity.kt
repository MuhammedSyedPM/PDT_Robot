package com.syed.jetpacktwo

import android.content.Intent
import android.view.KeyEvent
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.viewModels
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel
import com.syed.jetpacktwo.presentation.trace.TraceContentInline
import com.syed.jetpacktwo.presentation.trace.TraceScreenStyle
import com.syed.jetpacktwo.presentation.trace.TraceStylePickerDialog
import com.syed.jetpacktwo.ui.theme.JetPackTwoTheme
import com.technowave.techno_rfid.NurApi.NurDeviceListActivity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate

import androidx.compose.ui.res.stringResource
import com.syed.jetpacktwo.R
import com.syed.jetpacktwo.util.PermissionManager
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.syed.jetpacktwo.ui.navigation.NavGraph

import com.syed.jetpacktwo.ui.theme.ErrorRed
import dagger.hilt.android.AndroidEntryPoint

private const val DEFAULT_TRACE_EPC = "3039ECBC01DE0D94F47FA397"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: RfidViewModel by viewModels()
    // Using hiltViewModel() to ensure it uses the same Hilt factory
    private lateinit var settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            viewModel.connectLastSaved()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual assignment to ensure Hilt handles it
        // Note: In Activity we usually use viewModels(), but hiltViewModel() works too with context
        // However, let's stick to a more standard way for Activity
        settingsViewModel = androidx.lifecycle.ViewModelProvider(this).get(com.syed.jetpacktwo.presentation.settings.SettingsViewModel::class.java)
        
        // Ensure reader is disconnected when app is closed to avoid "busy" connection on restart
        lifecycle.addObserver(object : androidx.lifecycle.LifecycleEventObserver {
            override fun onStateChanged(source: androidx.lifecycle.LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
                if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) {
                    viewModel.dispose()
                }
            }
        })

        enableEdgeToEdge()
        
        checkAndRequestPermissions()
        handleIntent(intent)

        setContent {
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            
            JetPackTwoTheme(darkTheme = isDarkMode) {
                NavGraph(
                    onExit = { finish() }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Connect to last saved hardware on start or restart
        viewModel.connectLastSaved()
        // Set standard mode on start
        viewModel.setReadingEnabled(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release RFID resources when app is closed
        viewModel.dispose()
    }

    override fun onStop() {
        if (viewModel.readerStatus.value.isConnected) {
            viewModel.stopReader()
            viewModel.disconnect()
        }
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED) {
            // Nordic USB attached, try to connect
            viewModel.connect("type=USB")
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == NurDeviceListActivity.REQUEST_SELECT_DEVICE &&
            resultCode == NurDeviceListActivity.RESULT_OK &&
            data != null
        ) {
            val specStr = data.getStringExtra(NurDeviceListActivity.SPECSTR) ?: return
            viewModel.connect(specStr)
        }
    }

    override fun onKeyDown(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293 || keyCode == 294 || 
            keyCode == 291 || keyCode == 311 || keyCode == 312 || keyCode == 313 || keyCode == 315) {
            if (event?.repeatCount == 0) {
                viewModel.startReader()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: android.view.KeyEvent?): Boolean {
        if (keyCode == 139 || keyCode == 280 || keyCode == 293 || keyCode == 294 || 
            keyCode == 291 || keyCode == 311 || keyCode == 312 || keyCode == 313 || keyCode == 315) {
            viewModel.stopReader()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    private fun checkAndRequestPermissions() {
        val missing = PermissionManager.getMissingPermissions(this)
        if (missing.isNotEmpty()) {
            requestPermissionLauncher.launch(missing.toTypedArray())
        }
    }
}
