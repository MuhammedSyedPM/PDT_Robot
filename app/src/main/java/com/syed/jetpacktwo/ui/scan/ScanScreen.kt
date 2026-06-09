package com.syed.jetpacktwo.ui.scan

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.ui.draw.alpha
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
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
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
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp > 600
    
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

    // Shared logic to safely stop the reader and clear data when leaving the screen
    val handleBack = {
        if (localIsScanning && !isStopping) {
            viewModel.stopReader()
            viewModel.clearTagReads() // Clears without saving, as requested
        }
        onBack()
    }

    // Intercept system back button swipe/press
    androidx.activity.compose.BackHandler {
        handleBack()
    }

    // Failsafe: if the screen is removed from composition for any reason, stop reading
    DisposableEffect(Unit) {
        onDispose {
            if (localIsScanning && !isStopping) {
                viewModel.stopReader()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart scan", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = rememberDebouncedClick { handleBack() }) {
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
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                    modifier = Modifier.size(if (isTablet) 100.dp else 80.dp),
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
                            modifier = Modifier.size(if (isTablet) 40.dp else 32.dp),
                            color = Color.White,
                            strokeWidth = 4.dp
                        )
                    } else {
                        Icon(
                            imageVector = if (localIsScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = if (localIsScanning) "Stop" else "Start",
                            modifier = Modifier.size(if (isTablet) 48.dp else 40.dp),
                            tint = Color.White
                        )
                    }
                }
                
                Text(
                    text = when {
                        isStopping -> "STOPPING..."
                        localIsScanning -> "STOP"
                        else -> "START"
                    },
                    color = if (isStopping || localIsScanning) Color.Red else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
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
                Spacer(modifier = Modifier.height(16.dp))

                // Scanning Indication Animation
                AnimatedVisibility(
                    visible = localIsScanning && !isStopping,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    val infiniteTransition = rememberInfiniteTransition()
                    
                    // Radar rotation
                    val rotation by infiniteTransition.animateFloat(
                        initialValue = 0f,
                        targetValue = 360f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1500, easing = LinearEasing),
                            repeatMode = RepeatMode.Restart
                        )
                    )

                    // Gentle pulse for the icon
                    val iconScale by infiniteTransition.animateFloat(
                        initialValue = 0.85f,
                        targetValue = 1.15f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    // Blinking alpha for text
                    val textAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        // Cute text pill with inline radar animation
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 12.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Small Radar Scanner infront of text
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .graphicsLayer { rotationZ = rotation }
                                            .background(
                                                Brush.sweepGradient(
                                                    0f to Color.Transparent,
                                                    0.8f to MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                    1f to MaterialTheme.colorScheme.primary
                                                )
                                            )
                                    )
                                    Icon(
                                        imageVector = Icons.Default.WifiTethering, 
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .align(Alignment.Center)
                                            .scale(iconScale)
                                    )
                                }
                                
                                Text(
                                    text = "SCANNING IN PROGRESS",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.alpha(textAlpha)
                                )
                            }
                        }
                    }
                }

                if (!localIsScanning || isStopping) {
                    Spacer(modifier = Modifier.height(52.dp)) // To maintain vertical spacing
                }
                
                // Digital Counter in Center (Groww Style)
                Text(
                    text = "Total Scanned",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    fontSize = if (isTablet) 24.sp else 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "$newTagsCount",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = if (isTablet) {
                        if (newTagsCount > 999) 160.sp else 240.sp
                    } else {
                        if (newTagsCount > 999) 80.sp else 120.sp
                    },
                    fontWeight = FontWeight.ExtraBold
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
