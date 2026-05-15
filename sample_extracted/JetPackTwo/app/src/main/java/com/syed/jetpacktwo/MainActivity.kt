package com.syed.jetpacktwo

import android.content.Intent
import android.view.KeyEvent
import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
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
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

import com.syed.jetpacktwo.ui.theme.ErrorRed
import dagger.hilt.android.AndroidEntryPoint

private const val DEFAULT_TRACE_EPC = "000000000000000000002421"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: RfidViewModel by viewModels()

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
        
        // Ensure reader is disconnected when app is closed to avoid "busy" connection on restart
        lifecycle.addObserver(object : androidx.lifecycle.LifecycleEventObserver {
            override fun onStateChanged(source: androidx.lifecycle.LifecycleOwner, event: androidx.lifecycle.Lifecycle.Event) {
                if (event == androidx.lifecycle.Lifecycle.Event.ON_DESTROY) {
                    viewModel.disconnect()
                }
            }
        })

        enableEdgeToEdge()
        
        checkAndRequestPermissions()

        setContent {
            // Theme selection state
            val themePrefs = remember { getSharedPreferences("theme_prefs", android.content.Context.MODE_PRIVATE) }
            var isDarkTheme by remember { 
                mutableStateOf(themePrefs.getBoolean("is_dark", true)) 
            }

            JetPackTwoTheme(darkTheme = isDarkTheme) {
                    var showTraceScreen by remember { mutableStateOf(false) }
                var showStylePicker by remember { mutableStateOf(false) }
                var traceEpc by remember { mutableStateOf(DEFAULT_TRACE_EPC) }
                var traceStyle by remember { mutableStateOf(TraceScreenStyle.GAUGE) }
                val readerStatus by viewModel.readerStatus.collectAsState()
                val tagReads by viewModel.tagReads.collectAsState()

                // Hardware selection state
                var showHardwareDialog by remember { mutableStateOf(false) }
                val manufacturer = android.os.Build.MANUFACTURER
                val isIndependentPhone = !manufacturer.contains("Zebra", true) && 
                                       !manufacturer.contains("Chainway", true) && 
                                       !manufacturer.contains("C5", true)
                
                val currentHardware = remember {
                    mutableStateOf(viewModel.getCurrentHardwareType())
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    Scaffold(
                        containerColor = Color.Transparent,
                        topBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "RFID Portal",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        fontWeight = FontWeight.Bold
                                    )
                                    
                                    IconButton(
                                        onClick = { 
                                            isDarkTheme = !isDarkTheme
                                            themePrefs.edit().putBoolean("is_dark", isDarkTheme).apply()
                                        }
                                    ) {
                                            Icon(
                                                imageVector = if (isDarkTheme) Icons.Default.Refresh else Icons.Default.Add,
                                                contentDescription = "Toggle Theme",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                    }
                                }
                            }
                        }
                    ) { innerPadding ->
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                                    .verticalScroll(rememberScrollState())
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 32.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (!showTraceScreen) {
                                // Status card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .size(12.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (readerStatus.isConnected) Color(0xFF66BB6A)
                                                        else Color(0xFFE53935)
                                                    )
                                            )
                                            Column(modifier = Modifier.padding(start = 14.dp)) {
                                                Text(
                                                    text = if (readerStatus.isConnected) "Hardware Connected" else "Hardware Disconnected",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = MaterialTheme.colorScheme.onSurface,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = readerStatus.status,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                                )
                                                
                                                if (readerStatus.isConnecting) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    LinearProgressIndicator(
                                                        modifier = Modifier.fillMaxWidth().height(2.dp),
                                                        color = Color(0xFF64B5F6),
                                                        trackColor = Color.White.copy(alpha = 0.1f)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Trace section
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            "Trace Asset",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            "Locate tag using proximity scanner",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                        OutlinedTextField(
                                            value = traceEpc,
                                            onValueChange = { traceEpc = it },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            placeholder = { Text("EPC (Hex)", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                                            singleLine = true,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                                                cursorColor = MaterialTheme.colorScheme.primary,
                                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                                focusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                                unfocusedContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                                            ),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 12.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            androidx.compose.material3.Button(
                                                onClick = {
                                                    if (traceEpc.isBlank()) traceEpc = DEFAULT_TRACE_EPC
                                                    showStylePicker = true
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Text("Launch Trace", fontWeight = FontWeight.Bold)
                                            }
                                            if (tagReads.isNotEmpty()) {
                                                androidx.compose.material3.OutlinedButton(
                                                    onClick = {
                                                        traceEpc = tagReads.last().epc
                                                        showStylePicker = true
                                                    },
                                                    modifier = Modifier.weight(1f),
                                                    shape = RoundedCornerShape(16.dp),
                                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                                        contentColor = MaterialTheme.colorScheme.onSurface
                                                    ),
                                                    border = androidx.compose.foundation.BorderStroke(
                                                        1.dp,
                                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                    )
                                                ) {
                                                    Text("Latest Tag", fontWeight = FontWeight.Medium)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Connection
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            "Hardware Configuration",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        
                                        if (isIndependentPhone) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 8.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
                                                    .clickable { showHardwareDialog = true }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Hardware Mode: ${currentHardware.value}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Icon(
                                                    imageVector = Icons.Default.Settings,
                                                    contentDescription = "Switch Hardware",
                                                    tint = MaterialTheme.colorScheme.primary,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            androidx.compose.material3.Button(
                                                onClick = { viewModel.launchDeviceList(this@MainActivity) },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                                    contentColor = MaterialTheme.colorScheme.onSurface
                                                )
                                            ) {
                                                Text("Select", fontWeight = FontWeight.Bold)
                                            }
                                            androidx.compose.material3.Button(
                                                onClick = { viewModel.connectLastSaved() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                    contentColor = MaterialTheme.colorScheme.primary
                                                )
                                            ) {
                                                Text("Reload", fontWeight = FontWeight.Bold)
                                            }
                                            androidx.compose.material3.OutlinedButton(
                                                onClick = { viewModel.disconnect() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                                    contentColor = ErrorRed
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    ErrorRed.copy(alpha = 0.4f)
                                                )
                                            ) {
                                                Text("Off", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                // Reader controls
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp)) {
                                        Text(
                                            "RFID Inventory",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(top = 16.dp),
                                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                            androidx.compose.material3.Button(
                                                onClick = { viewModel.startReader() },
                                                modifier = Modifier.weight(1.2f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                                    containerColor = MaterialTheme.colorScheme.primary,
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Text("Start Scan", fontWeight = FontWeight.Bold)
                                            }
                                            androidx.compose.material3.OutlinedButton(
                                                onClick = { viewModel.stopReader() },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(16.dp),
                                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.onSurface
                                                ),
                                                border = androidx.compose.foundation.BorderStroke(
                                                    1.dp,
                                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                                )
                                            ) {
                                                Text("Stop", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                                }
                            }
                        }
                    }
                if (showStylePicker) {
                    TraceStylePickerDialog(
                        onStyleSelected = { style ->
                            traceStyle = style
                            showStylePicker = false
                            showTraceScreen = true
                        },
                        onDismiss = { showStylePicker = false }
                    )
                }
                if (showTraceScreen) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        TraceContentInline(
                            viewModel = viewModel,
                            epc = traceEpc,
                            style = traceStyle,
                            onClose = { showTraceScreen = false }
                        )
                    }
                }

                if (showHardwareDialog) {
                    AlertDialog(
                        onDismissRequest = { showHardwareDialog = false },
                        containerColor = Color(0xFF1A1A2E),
                        titleContentColor = Color.White,
                        textContentColor = Color.White.copy(alpha = 0.8f),
                        title = { Text("Select Hardware", fontWeight = FontWeight.Bold) },
                        text = {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("Choose the reader hardware to use with this phone.")
                                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                                val options = listOf(
                                    "NORDIC" to "Nordic ID / Bluetooth",
                                    "ZEBRA" to "Zebra Bluetooth Readers",
                                    "CHAINWAY" to "Chainway Bluetooth"
                                )
                                options.forEach { (key, label) ->
                                    androidx.compose.material3.Button(
                                        onClick = {
                                            viewModel.switchHardware(key)
                                            currentHardware.value = key
                                            showHardwareDialog = false
                                            Toast.makeText(this@MainActivity, "Hardware switched to $label", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                            containerColor = if (currentHardware.value == key) Color(0xFF5C6BC0) else Color.White.copy(alpha = 0.05f)
                                        )
                                    ) {
                                        Text(label)
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = { showHardwareDialog = false }) {
                                Text("Cancel", color = Color.White.copy(alpha = 0.5f))
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!viewModel.readerStatus.value.isConnected) {
            viewModel.stopReader()
            viewModel.disconnect()
            viewModel.connectLastSaved()
        }
    }

    override fun onStop() {
        if (viewModel.readerStatus.value.isConnected) {
            viewModel.stopReader()
            viewModel.disconnect()
        }
        super.onStop()
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            
            val missingPermissions = permissions.filter {
                checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
            }
            
            if (missingPermissions.isNotEmpty()) {
                requestPermissionLauncher.launch(missingPermissions.toTypedArray())
            }
        } else {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }
}
