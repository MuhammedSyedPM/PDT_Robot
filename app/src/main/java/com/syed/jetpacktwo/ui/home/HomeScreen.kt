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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onScanClick: () -> Unit,
    onUploadClick: () -> Unit,
    onLogout: () -> Unit,
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
    
    var showExitDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showUploadResultDialog by remember { mutableStateOf(false) }

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

    BackHandler {
        showExitDialog = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Clean Groww-style background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Smart Stock Take",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Refresh icon (matching screenshot)
                    IconButton(onClick = { viewModel.connectLastSaved() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Theme Toggle (Groww Style)
                    IconButton(onClick = { settingsViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
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
                        IconButton(onClick = { /* Settings */ }) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
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
                                text = if (isConnected) "Hardware Connected" else "Hardware Disconnected",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (isConnected) "Device: ${readerStatus.status.replace("Connected: ", "").replace("Connected to ", "").replace("Chainway Connected ", "")}" else "Scanning for devices...",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Compact Setup Button and Battery
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
                                onClick = { 
                                    if (context is android.app.Activity) {
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
                modifier = Modifier.fillMaxWidth().widthIn(max = 800.dp).align(Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
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
                        onClick = onScanClick
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "UPLOAD",
                        subtitle = if (totalScannedCount > 0) "Sync Required" else "Everything Synced",
                        icon = Icons.Default.CloudUpload,
                        color = if (totalScannedCount > 0) Color(0xFF3182CE) else MaterialTheme.colorScheme.outline,
                        onClick = { if (totalScannedCount > 0) viewModel.uploadTags() },
                        badgeCount = totalScannedCount
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "CLEAR",
                        subtitle = "Wipe Database",
                        icon = Icons.Default.DeleteForever,
                        color = MaterialTheme.colorScheme.error,
                        onClick = { showClearDialog = true }
                    )
                    ActionCard(
                        modifier = Modifier.weight(1f),
                        title = "EXIT",
                        subtitle = "Close Session",
                        icon = Icons.Default.ExitToApp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        onClick = { showExitDialog = true }
                    )
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
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
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
    AlertDialog(
        onDismissRequest = { 
            showUploadResultDialog = false
            viewModel.resetUploadResult()
        },
        title = { 
            Text(if (result?.isSuccess == true) "Sync Successful" else "Sync Failed")
        },
        text = {
            Text(if (result?.isSuccess == true) "Inventory data has been successfully uploaded to the server." else result?.exceptionOrNull()?.message ?: "Unknown error occurred")
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
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(24.dp),
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column(
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = color.copy(alpha = 0.1f),
                    shape = CircleShape
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
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            // Notification Badge (Pill Style)
            if (badgeCount > 0) {
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd),
                    color = Color.Red,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}
