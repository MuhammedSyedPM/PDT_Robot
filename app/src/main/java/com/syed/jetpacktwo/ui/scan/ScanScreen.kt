package com.syed.jetpacktwo.ui.scan

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel
import com.syed.jetpacktwo.ui.theme.ErrorRed
import kotlinx.coroutines.launch
import com.syed.jetpacktwo.util.rememberDebouncedClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    onBack: () -> Unit,
    viewModel: RfidViewModel = hiltViewModel(),
    settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel = hiltViewModel()
) {
    val readerStatus by viewModel.readerStatus.collectAsState()
    val tagReads by viewModel.tagReads.collectAsState()
    val existingTagEpcs by viewModel.existingTagEpcs.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    
    // For this implementation, I'll use a local state to manage the button toggle and count persistence
    var localIsScanning by remember { mutableStateOf(false) }
    var isStopping by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Filter tags that are NOT in the database already. Lock to 0 if not scanning.
    val newTagsCount = if (localIsScanning) {
        tagReads
            .distinctBy { it.epc }
            .count { it.epc !in existingTagEpcs }
    } else {
        0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart scan", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = rememberDebouncedClick { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                actions = {
                    IconButton(onClick = rememberDebouncedClick { settingsViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Digital Counter in Center (Groww Style)
                Text(
                    text = "Total Scanned",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "$newTagsCount",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = if (newTagsCount > 999) 80.sp else 120.sp, // Scale down for large numbers
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Core Toggle Button
                Button(
                    onClick = rememberDebouncedClick {
                        if (!isStopping) {
                            if (localIsScanning) {
                                scope.launch {
                                    isStopping = true
                                    viewModel.stopReader()
                                    viewModel.saveCurrentTags()
                                    viewModel.clearTagReads()
                                    kotlinx.coroutines.delay(1200) // 1.2 second loader
                                    localIsScanning = false
                                    isStopping = false
                                }
                            } else {
                                viewModel.clearTagReads()
                                viewModel.startReader()
                                localIsScanning = true
                            }
                        }
                    },
                    modifier = Modifier.size(120.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isStopping || localIsScanning) Color.Red else MaterialTheme.colorScheme.primary,
                        disabledContainerColor = if (isStopping) Color.Red.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    ),
                    shape = CircleShape,
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
                    enabled = !isStopping
                ) {
                    if (isStopping) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = Color.White,
                            strokeWidth = 4.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (localIsScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (localIsScanning) "Stop" else "Start",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = when {
                        isStopping -> "STOPPING..."
                        localIsScanning -> "STOP SCANNING"
                        else -> "START SCANNING"
                    },
                    color = if (isStopping || localIsScanning) Color.Red else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 16.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Space for the status indicator
            }
            
            // Connection Status Indicator at bottom
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .widthIn(max = 400.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(if (readerStatus.isConnected) com.syed.jetpacktwo.ui.theme.GrowwGreen else MaterialTheme.colorScheme.error, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (readerStatus.isConnected) "Reader Connected" else "Reader Disconnected",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
