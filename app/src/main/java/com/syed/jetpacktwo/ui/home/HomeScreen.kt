package com.syed.jetpacktwo.ui.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.blur
import com.syed.jetpacktwo.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.input.KeyboardType
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel
import com.syed.jetpacktwo.util.debouncedClickable
import com.syed.jetpacktwo.util.rememberDebouncedClick
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onUploadClick: () -> Unit,
    onLogout: () -> Unit,
    onReportClick: (String) -> Unit = {},
    onDownloadRackClick: () -> Unit = {},
    viewModel: RfidViewModel = hiltViewModel(),
    settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel = hiltViewModel()
) {
    val readerStatus by viewModel.readerStatus.collectAsState()
    val isConnected = readerStatus.isConnected
    val totalScannedCount by viewModel.totalScannedCount.collectAsState()
    val scannerSpec by viewModel.scannerSpec.collectAsState()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val uploadResult by viewModel.uploadResult.collectAsState()
    val configuredDeviceName by viewModel.configuredDeviceName.collectAsState()
    
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    
    var showExitDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showUploadResultDialog by remember { mutableStateOf(false) }
    var showImpinjConfigDialog by remember { mutableStateOf(false) }
    var showZebraFixedConfigDialog by remember { mutableStateOf(false) }
    var showPowerDialog by remember { mutableStateOf(false) }
    var showColorPickerDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }

    // Handle Upload Result
    LaunchedEffect(uploadResult) {
        if (uploadResult != null) {
            showUploadResultDialog = true
        }
    }

    val context = androidx.compose.ui.platform.LocalContext.current

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }
    LaunchedEffect(pendingNavigation) {
        pendingNavigation?.let { navAction ->
            delay(300) // Show progress for a short time to improve UX
            navAction()
            pendingNavigation = null
        }
    }

    BackHandler {
        showExitDialog = true
    }

    Scaffold(
        containerColor = Color.Transparent // Clean Groww-style background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Image with Blur and Overlay
            Image(
                painter = painterResource(id = R.drawable.splsh_image),
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(radius = 32.dp)
            )
            // Overlay to ensure text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = if (isDarkMode) 0.8f else 0.95f))
            )
            
            if (pendingNavigation != null) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
            // Personalized Header (Groww Style)
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (configuredDeviceName.isNotEmpty() && configuredDeviceName != "Select Device") {
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        ) {
                            Text(
                                text = configuredDeviceName,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    Text(
                        text = "Welcome!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Refresh icon (matching screenshot)
                    IconButton(onClick = rememberDebouncedClick { viewModel.connectLastSaved() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Theme Toggle (Groww Style)
                    IconButton(onClick = rememberDebouncedClick { settingsViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Color Palette Icon
                    IconButton(onClick = rememberDebouncedClick { showColorPickerDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Choose Theme Color",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Small Profile/Settings icon
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        IconButton(onClick = rememberDebouncedClick { 
                            val hwType = viewModel.getCurrentHardwareType()
                            if (hwType == "NORDIC") {
                                if (context is android.app.Activity) {
                                    viewModel.launchPowerSettings(context)
                                }
                            } else if (hwType == "IMPINJ") {
                                showImpinjConfigDialog = true
                            } else if (hwType == "ZEBRA FIXED") {
                                showZebraFixedConfigDialog = true
                            } else {
                                showPowerDialog = true
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Power Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Screenshot-style Connection Status Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp) // Limit width for large tablets
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Animated Status Dot with Glow Effect
                        Box(contentAlignment = Alignment.Center) {
                            if (isConnected) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .graphicsLayer(scaleX = pulseScale, scaleY = pulseScale, alpha = pulseAlpha)
                                        .background(com.syed.jetpacktwo.ui.theme.GrowwGreen.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .background(if (isConnected) com.syed.jetpacktwo.ui.theme.GrowwGreen else Color.Red, CircleShape)
                                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isConnected) "Hardware Connected" else if (readerStatus.isConnecting) "Connecting..." else "Hardware Disconnected",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isConnected) {
                                    "Device: ${readerStatus.status.replace("Connected: ", "").replace("Connected to ", "").replace("Chainway Connected ", "")}"
                                } else if (readerStatus.status.contains("Disconnected", ignoreCase = true)) {
                                    "Scanning for devices..."
                                } else {
                                    readerStatus.status
                                },
                                fontSize = 12.sp,
                                color = if (!isConnected && !readerStatus.isConnecting && !readerStatus.status.contains("Disconnected", ignoreCase = true)) 
                                    MaterialTheme.colorScheme.error 
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }


                        Column(horizontalAlignment = Alignment.End) {
                            if (readerStatus.batteryLevel != null) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (readerStatus.batteryLevel!! > 20) Icons.Default.BatteryFull else Icons.Default.BatteryAlert,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (readerStatus.batteryLevel!! > 20) com.syed.jetpacktwo.ui.theme.GrowwGreen else Color.Red
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${readerStatus.batteryLevel}%",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            
                            TextButton(
                                onClick = rememberDebouncedClick { 
                                    if (viewModel.getCurrentHardwareType() == "IMPINJ") {
                                        showImpinjConfigDialog = true
                                    } else if (viewModel.getCurrentHardwareType() == "ZEBRA FIXED") {
                                        showZebraFixedConfigDialog = true
                                    } else if (context is android.app.Activity) {
                                        viewModel.launchDeviceList(context)
                                    }
                                }
                            ) {
                                Text("Setup", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (readerStatus.isConnecting && !isConnected) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Quick Actions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Responsive Action Grid
            Column(
                modifier = Modifier.fillMaxWidth().widthIn(max = if (isTablet) 1200.dp else 800.dp).align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (isTablet) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "SCAN",
                            subtitle = "Start Inventory",
                            icon = Icons.Default.Sensors,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = onScanClick }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "UPLOAD",
                            subtitle = if (totalScannedCount > 0) "Sync Required" else "Everything Synced",
                            icon = Icons.Default.CloudUpload,
                            color = if (totalScannedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            onClick = { pendingNavigation = { if (totalScannedCount > 0) viewModel.uploadTags() } },
                            badgeCount = totalScannedCount
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "RACKS",
                            subtitle = "Download Racks",
                            icon = Icons.Default.CloudDownload,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = onDownloadRackClick }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "CLEAR",
                            subtitle = "Wipe Database",
                            icon = Icons.Default.DeleteForever,
                            color = MaterialTheme.colorScheme.error,
                            onClick = { pendingNavigation = { showClearDialog = true } }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "REPORT",
                            subtitle = "Summary",
                            icon = Icons.Default.Assessment,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = { showReportDialog = true } }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "POWER",
                            subtitle = "Settings",
                            icon = Icons.Default.Settings,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = {
                                pendingNavigation = {
                                    val hwType = viewModel.getCurrentHardwareType()
                                    if (hwType == "NORDIC") {
                                        if (context is android.app.Activity) {
                                            viewModel.launchPowerSettings(context)
                                        }
                                    } else if (hwType == "IMPINJ") {
                                        showImpinjConfigDialog = true
                                    } else if (hwType == "ZEBRA FIXED") {
                                        showZebraFixedConfigDialog = true
                                    } else {
                                        showPowerDialog = true
                                    }
                                }
                            }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "EXIT",
                            subtitle = "Close Session",
                            icon = Icons.Default.ExitToApp,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = { showExitDialog = true } }
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "SCAN",
                            subtitle = "Start Inventory",
                            icon = Icons.Default.Sensors,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = onScanClick }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "UPLOAD",
                            subtitle = if (totalScannedCount > 0) "Sync Required" else "Everything Synced",
                            icon = Icons.Default.CloudUpload,
                            color = if (totalScannedCount > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                            onClick = { pendingNavigation = { if (totalScannedCount > 0) viewModel.uploadTags() } },
                            badgeCount = totalScannedCount
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "RACKS",
                            subtitle = "Download Racks",
                            icon = Icons.Default.CloudDownload,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = onDownloadRackClick }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "CLEAR",
                            subtitle = "Wipe Database",
                            icon = Icons.Default.DeleteForever,
                            color = MaterialTheme.colorScheme.error,
                            onClick = { pendingNavigation = { showClearDialog = true } }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "REPORT",
                            subtitle = "Summary",
                            icon = Icons.Default.Assessment,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = { showReportDialog = true } }
                        )
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "POWER",
                            subtitle = "Settings",
                            icon = Icons.Default.Settings,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = {
                                pendingNavigation = {
                                    val hwType = viewModel.getCurrentHardwareType()
                                    if (hwType == "NORDIC") {
                                        if (context is android.app.Activity) {
                                            viewModel.launchPowerSettings(context)
                                        }
                                    } else if (hwType == "IMPINJ") {
                                        showImpinjConfigDialog = true
                                    } else if (hwType == "ZEBRA FIXED") {
                                        showZebraFixedConfigDialog = true
                                    } else {
                                        showPowerDialog = true
                                    }
                                }
                            }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        ActionCard(
                            modifier = Modifier.weight(1f),
                            title = "EXIT",
                            subtitle = "Close Session",
                            icon = Icons.Default.ExitToApp,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { pendingNavigation = { showExitDialog = true } }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }

        // Global Upload Loading Overlay
        if (isUploading) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color.Black.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedUploadingIcon()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Uploading Inventory...", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Upload Result Dialog
if (showUploadResultDialog) {
    val result = uploadResult
    val isSuccess = result?.isSuccess == true
    AlertDialog(
        onDismissRequest = { 
            showUploadResultDialog = false
            viewModel.resetUploadResult()
        },
        title = { 
            Text(if (isSuccess) "Sync Successful" else "Sync Failed")
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                AnimatedSyncResultIcon(isSuccess = isSuccess)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isSuccess) "Inventory data has been uploaded successfully!" else (result?.exceptionOrNull()?.message ?: "Unknown error occurred"),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(onClick = { 
                showUploadResultDialog = false
                viewModel.resetUploadResult()
            }) {
                Text("OK")
            }
        }
    )
}

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear Data") },
            text = { Text("Do you want to clear all scanned tags?") },
            confirmButton = {
                TextButton(onClick = { 
                    viewModel.clearAllTags()
                    showClearDialog = false 
                }) {
                    Text("YES", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("NO")
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Session Options") },
            text = { Text("Would you like to logout or exit the application?") },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = { 
                        showExitDialog = false
                        onLogout()
                    }) {
                        Text("LOGOUT", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { 
                        showExitDialog = false
                        viewModel.dispose()
                        if (context is android.app.Activity) {
                            context.finish()
                        }
                    }) {
                        Text("EXIT APP", color = MaterialTheme.colorScheme.error)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text("CANCEL")
                }
            }
        )
    }

    // Color Picker Dialog
    if (showColorPickerDialog) {
        AlertDialog(
            onDismissRequest = { showColorPickerDialog = false },
            title = { 
                Text(text = "Select Theme Color", fontWeight = FontWeight.Bold) 
            },
            text = {
                val colors = listOf(
                    Color(0xFF00D09C), // Groww Green
                    Color(0xFF2196F3), // Blue
                    Color(0xFF3F51B5), // Indigo
                    Color(0xFF673AB7), // Purple
                    Color(0xFFE91E63), // Pink
                    Color(0xFFF44336), // Red
                    Color(0xFFFF5722), // Deep Orange
                    Color(0xFFFF9800), // Orange
                    Color(0xFFFFC107), // Amber
                    Color(0xFF4CAF50), // Green
                    Color(0xFF009688), // Teal
                    Color(0xFF00BCD4), // Cyan
                    Color(0xFF607D8B), // Blue Grey
                    Color(0xFF795548), // Brown
                    Color(0xFF111111)  // Almost Black
                )
                androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                    columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(5),
                    modifier = Modifier.fillMaxWidth().height(180.dp).padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(colors.size) { index ->
                        val color = colors[index]
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .clickable {
                                    settingsViewModel.setPrimaryColor(color.toArgb().toLong())
                                    showColorPickerDialog = false
                                },
                            color = color,
                            border = androidx.compose.foundation.BorderStroke(
                                width = 2.dp,
                                color = if (MaterialTheme.colorScheme.primary == color) 
                                    MaterialTheme.colorScheme.onSurface 
                                else Color.Transparent
                            )
                        ) {}
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPickerDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showPowerDialog) {
        val hwType = viewModel.getCurrentHardwareType()
        val currentPower = viewModel.getPowerLevel()
        var selectedPower by remember { mutableStateOf(currentPower.toString()) }
        val maxPower = if (hwType == "ZEBRA") 300 else 30
        
        AlertDialog(
            onDismissRequest = { showPowerDialog = false },
            title = { Text(if (hwType == "ZEBRA") "Zebra Power Settings" else "Chainway Power Settings", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Select power level (1-$maxPower):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = selectedPower,
                        onValueChange = { selectedPower = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = { 
                    val p = selectedPower.toIntOrNull() ?: currentPower
                    val clamped = p.coerceIn(1, maxPower)
                    viewModel.setPowerLevel(clamped)
                    showPowerDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPowerDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showImpinjConfigDialog) {
        ImpinjConfigDialog(
            initialIp = viewModel.scannerSpec.collectAsState().value,
            viewModel = viewModel,
            onDismiss = { showImpinjConfigDialog = false },
            onConnect = { ip -> 
                showImpinjConfigDialog = false
                viewModel.connect(ip)
            }
        )
    }

    if (showZebraFixedConfigDialog) {
        ZebraFixedConfigDialog(
            initialIp = viewModel.scannerSpec.collectAsState().value,
            onDismiss = { showZebraFixedConfigDialog = false },
            onConnect = { ip -> 
                showZebraFixedConfigDialog = false
                viewModel.connect(ip)
            }
        )
    }

    if (showReportDialog) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showReportDialog = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val dialogWidth = if (isTablet) 600.dp else (configuration.screenWidthDp * 0.9).dp
            Surface(
                modifier = Modifier
                    .width(dialogWidth)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(28.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Icon(
                        imageVector = Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp).padding(bottom = 12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Report Options",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Select a report type to view analytics",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
                    )
                    
                    // Options in Row for Tablet, Column for Mobile
                    if (isTablet) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ReportOptionCard(
                                title = "Rack Status",
                                icon = Icons.Default.ViewList,
                                modifier = Modifier.weight(1f),
                                onClick = { 
                                    showReportDialog = false 
                                    onReportClick("rack_status")
                                }
                            )
                            ReportOptionCard(
                                title = "Stock Status",
                                icon = Icons.Default.Inventory,
                                modifier = Modifier.weight(1f),
                                onClick = { 
                                    showReportDialog = false
                                    onReportClick("stock_status")
                                }
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ReportOptionCard(
                                title = "Rack Status",
                                icon = Icons.Default.ViewList,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { 
                                    showReportDialog = false
                                    onReportClick("rack_status")
                                }
                            )
                            ReportOptionCard(
                                title = "Stock Status",
                                icon = Icons.Default.Inventory,
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { 
                                    showReportDialog = false
                                    onReportClick("stock_status")
                                }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    TextButton(
                        onClick = { showReportDialog = false },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CLOSE", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0
) {
    // CRED-inspired sharp, cute, and premium design
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.94f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "button_bounce"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = rememberDebouncedClick { onClick() }
            ),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp), // Sharper corners
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)), // Glowing neon-like border
        shadowElevation = if (isPressed) 2.dp else 6.dp
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            color.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                // Sharp icon container
                Surface(
                    modifier = Modifier.size(46.dp),
                    color = color.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = color
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = title.uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Notification Badge (Sharp Pill Style)
            if (badgeCount > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp),
                    color = com.syed.jetpacktwo.ui.theme.GrowwGreen,
                    shape = RoundedCornerShape(6.dp),
                    shadowElevation = 4.dp
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color(0xFF111111), // Sharp, highly readable dark text on green background
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImpinjConfigDialog(
    initialIp: String,
    viewModel: RfidViewModel,
    onDismiss: () -> Unit,
    onConnect: (String) -> Unit
) {
    var ipAddress by remember { mutableStateOf(initialIp) }
    var config by remember { 
        mutableStateOf(
            viewModel.getImpinjConfig()?.copy() ?: com.syed.jetpacktwo.data.repository.ImpinjConfig(
                readerAddress = initialIp,
                antennaConfig = (1..4).map { com.syed.jetpacktwo.data.repository.AntennaConfig(it, 30.0, -70.0, true) }
            )
        ) 
    }
    var selectedPort by remember { mutableStateOf(1) }
    
    val currentAntenna = config.antennaConfig.find { it.antennaPort == selectedPort } 
        ?: com.syed.jetpacktwo.data.repository.AntennaConfig(selectedPort, 30.0, -70.0, true)
    
    var expandedPort by remember { mutableStateOf(false) }
    var expandedTx by remember { mutableStateOf(false) }
    var expandedRx by remember { mutableStateOf(false) }
    
    val txPowers = (10..30).map { it.toDouble() }
    val rxSensitivities = (-80..-30).map { it.toDouble() }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reader Configuration") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("Reader IP Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Antenna Port Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedPort,
                    onExpandedChange = { expandedPort = it }
                ) {
                    OutlinedTextField(
                        value = "Antenna Port $selectedPort",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Choose Antenna") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedPort) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedPort,
                        onDismissRequest = { expandedPort = false }
                    ) {
                        (1..4).forEach { port ->
                            DropdownMenuItem(
                                text = { Text("Antenna Port $port") },
                                onClick = { 
                                    selectedPort = port
                                    expandedPort = false 
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Tx Power Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedTx,
                    onExpandedChange = { expandedTx = it }
                ) {
                    OutlinedTextField(
                        value = "${currentAntenna.txPower} dBm",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("TX Power") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTx) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTx,
                        onDismissRequest = { expandedTx = false }
                    ) {
                        txPowers.forEach { pwr ->
                            DropdownMenuItem(
                                text = { Text("$pwr dBm") },
                                onClick = { 
                                    val newConfig = config.antennaConfig.map { if(it.antennaPort == selectedPort) it.copy(txPower = pwr) else it }
                                    config = config.copy(antennaConfig = newConfig)
                                    expandedTx = false 
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                // Rx Sensitivity Dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedRx,
                    onExpandedChange = { expandedRx = it }
                ) {
                    OutlinedTextField(
                        value = "${currentAntenna.rxSensitivity} dBm",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("RX Sensitivity") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedRx) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedRx,
                        onDismissRequest = { expandedRx = false }
                    ) {
                        rxSensitivities.forEach { rx ->
                            DropdownMenuItem(
                                text = { Text("$rx dBm") },
                                onClick = { 
                                    val newConfig = config.antennaConfig.map { if(it.antennaPort == selectedPort) it.copy(rxSensitivity = rx) else it }
                                    config = config.copy(antennaConfig = newConfig)
                                    expandedRx = false 
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Enable Antenna", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = currentAntenna.isEnabled,
                        onCheckedChange = { checked -> 
                            val newConfig = config.antennaConfig.map { if(it.antennaPort == selectedPort) it.copy(isEnabled = checked) else it }
                            config = config.copy(antennaConfig = newConfig)
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { 
                config.readerAddress = ipAddress
                viewModel.saveImpinjConfig(config)
                onConnect(ipAddress) 
            }) {
                Text("SAVE & CONNECT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun ZebraFixedConfigDialog(
    initialIp: String,
    onDismiss: () -> Unit,
    onConnect: (String) -> Unit
) {
    var ipAddress by remember { mutableStateOf(initialIp) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zebra Fixed Configuration") },
        text = {
            Column {
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("Reader IP Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConnect(ipAddress) }) {
                Text("CONNECT")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL")
            }
        }
    )
}

@Composable
fun AnimatedSyncResultIcon(isSuccess: Boolean) {
    var animationPlayed by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        animationPlayed = true
    }
    
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = animationPlayed,
            enter = scaleIn(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)) + fadeIn(animationSpec = tween(500))
        ) {
            Icon(
                imageVector = if (isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                contentDescription = if (isSuccess) "Success" else "Error",
                tint = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun AnimatedUploadingIcon() {
    val infiniteTransition = rememberInfiniteTransition()

    // Gentle pulsating cloud
    val cloudScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cloudScale"
    )

    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Data packets flowing up
        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            for (i in 0..2) {
                // Staggered delays for a continuous flow
                val delay = i * 400
                val offsetY by infiniteTransition.animateFloat(
                    initialValue = 50f,
                    targetValue = -30f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, delayMillis = delay, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "dotOffsetY_$i"
                )
                
                val alpha by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = keyframes {
                            durationMillis = 1200
                            delayMillis = delay
                            0f at 0
                            1f at 200 // Fade in quickly at bottom
                            1f at 700 // Stay visible
                            0f at 1200 // Fade out near cloud center
                        },
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "dotAlpha_$i"
                )
                
                Box(
                    modifier = Modifier
                        .offset(y = offsetY.dp)
                        .size(10.dp)
                        .alpha(alpha)
                        .background(Color(0xFF00E5FF), CircleShape) // Vibrant Cyan data packets
                )
            }
        }

        // The Cloud
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = "Server",
            tint = Color.White,
            modifier = Modifier
                .padding(top = 10.dp)
                .size(80.dp)
                .scale(cloudScale)
        )
    }
}

@Composable
fun ReportOptionCard(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.96f else 1f, label = "scale")

    Surface(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
