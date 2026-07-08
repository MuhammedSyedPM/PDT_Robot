package com.syed.jetpacktwo.ui.scan

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel
import com.syed.jetpacktwo.ui.theme.ErrorRed
import kotlinx.coroutines.launch
import com.syed.jetpacktwo.util.rememberDebouncedClick
import com.syed.jetpacktwo.data.remote.model.StockStatusDto

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
    val departmentProgress by viewModel.departmentProgress.collectAsState()
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

    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }
    var showDepartmentBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(pendingNavigation) {
        pendingNavigation?.let { navAction ->
            kotlinx.coroutines.delay(300)
            navAction()
            pendingNavigation = null
        }
    }

    val handleBack = {
        // Prevent rapid double clicks
        if (pendingNavigation == null) {
            pendingNavigation = {
                if (localIsScanning) {
                    viewModel.stopReader()
                    viewModel.clearTagReads()
                }
                onBack()
            }
        }
    }

    androidx.activity.compose.BackHandler {
        handleBack()
    }

    val StartStopButton = @Composable {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                if (!isTablet && localIsScanning && !isStopping) {
                    SensorArcAnimation()
                }
                
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
            }
            
            Text(
                text = when {
                    isStopping -> "STOPPING"
                    localIsScanning -> "STOP"
                    else -> "START"
                },
                color = if (isStopping || localIsScanning) Color.Red else MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 8.dp),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
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
                    if (!isTablet && localIsScanning && !isStopping) {
                        BlinkingScanningText()
                    }
                    if (!isTablet) {
                        IconButton(onClick = { showDepartmentBottomSheet = true }) {
                            Icon(Icons.Default.ListAlt, contentDescription = "Dept Status", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
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
    ) { padding ->
        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                ) {
                    ScanAreaContent(
                        localIsScanning = localIsScanning,
                        isStopping = isStopping,
                        pendingNavigation = pendingNavigation,
                        newTagsCount = newTagsCount,
                        readerStatus = readerStatus,
                        StartStopButton = StartStopButton
                    )
                }
                
                VerticalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                ) {
                    DepartmentProgressContent(departmentProgress = departmentProgress)
                }
            }
        } else {
            // Mobile layout centers the scan count
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                ScanAreaContent(
                    localIsScanning = localIsScanning,
                    isStopping = isStopping,
                    pendingNavigation = pendingNavigation,
                    newTagsCount = newTagsCount,
                    readerStatus = readerStatus,
                    StartStopButton = StartStopButton
                )
            }
        }

        if (showDepartmentBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showDepartmentBottomSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxHeight(0.85f)) {
                    DepartmentProgressContent(departmentProgress = departmentProgress)
                }
            }
        }
    }
}

@Composable
fun ScanAreaContent(
    localIsScanning: Boolean,
    isStopping: Boolean,
    pendingNavigation: (() -> Unit)?,
    newTagsCount: Int,
    readerStatus: com.syed.jetpacktwo.domain.model.ReaderStatus,
    StartStopButton: @Composable () -> Unit
) {
    if (pendingNavigation != null) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "dots")
        val dotPhase by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 4f,
            animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing), repeatMode = RepeatMode.Restart),
            label = "dot_phase"
        )
        val blinkAlpha by infiniteTransition.animateFloat(
            initialValue = 0.2f, targetValue = 1f,
            animationSpec = infiniteRepeatable(animation = tween(600, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
            label = "blink_alpha"
        )
        
        val isVisible = localIsScanning && !isStopping
        if (isVisible) {
            val dotsCount = dotPhase.toInt().coerceIn(0, 3)
            val dots = ".".repeat(dotsCount)
            Text(
                text = "Scanning$dots",
                color = MaterialTheme.colorScheme.primary.copy(alpha = blinkAlpha),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        } else {
            Text(
                text = "Total Scanned", 
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f), 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        val configuration = androidx.compose.ui.platform.LocalConfiguration.current
        val isTablet = configuration.screenWidthDp > 600
        
        val countFontSize = if (isTablet) {
            if (newTagsCount > 999) 160.sp else 240.sp
        } else {
            if (newTagsCount > 999) 80.sp else 120.sp
        }

        Text(
            text = "$newTagsCount", 
            color = MaterialTheme.colorScheme.primary, 
            fontSize = countFontSize,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = countFontSize
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Surface(
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
                        .background(
                            if (readerStatus.isConnected) com.syed.jetpacktwo.ui.theme.GrowwGreen 
                            else if (readerStatus.isConnecting) Color.Gray 
                            else MaterialTheme.colorScheme.error, 
                            CircleShape
                        )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (readerStatus.isConnected) {
                        "Reader Connected"
                    } else if (readerStatus.status.contains("Disconnected", ignoreCase = true)) {
                        "Reader Disconnected"
                    } else {
                        readerStatus.status
                    },
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        StartStopButton()
    }
}

@Composable
fun DepartmentProgressContent(departmentProgress: List<com.syed.jetpacktwo.domain.model.DepartmentProgress>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        val overallScanned = departmentProgress.sumOf { it.totalScanned }
        val overallExpected = departmentProgress.sumOf { it.totalExpected }
        
        val overallStatusColor = when {
            overallScanned > overallExpected -> Color(0xFFD32F2F) // Professional Red
            overallScanned == overallExpected && overallExpected > 0 -> Color(0xFF388E3C) // Professional Green
            overallScanned > 0 -> Color(0xFFF57C00) // Professional Amber
            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f) // Muted Gray
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "DEPARTMENT PROGRESS",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            
            Row(verticalAlignment = Alignment.Bottom) {
                AnimatedContent(
                    targetState = overallScanned,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith fadeOut(animationSpec = tween(90))
                    },
                    label = "overallCountAnimation"
                ) { targetCount ->
                    Text(
                        text = targetCount.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = overallStatusColor
                    )
                }
                Text(
                    text = " / $overallExpected",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.padding(start = 2.dp, bottom = 2.dp)
                )
            }
        }
        
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)),
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(departmentProgress.size) { index ->
                    val deptInfo = departmentProgress[index]
                    val progress = if (deptInfo.totalExpected > 0) {
                        (deptInfo.totalScanned.toFloat() / deptInfo.totalExpected.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    val statusColor = when {
                        deptInfo.totalScanned > deptInfo.totalExpected -> Color(0xFFD32F2F)
                        deptInfo.totalScanned == deptInfo.totalExpected && deptInfo.totalExpected > 0 -> Color(0xFF388E3C)
                        deptInfo.totalScanned > 0 -> Color(0xFFF57C00)
                        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    }
                    
                    val statusText = when {
                        deptInfo.totalScanned > deptInfo.totalExpected -> "EXCESS"
                        deptInfo.totalScanned == deptInfo.totalExpected && deptInfo.totalExpected > 0 -> "COMPLETE"
                        deptInfo.totalScanned > 0 -> "PARTIAL"
                        else -> "PENDING"
                    }

                    val animatedProgress by animateFloatAsState(
                        targetValue = progress, 
                        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
                        label = "progress"
                    )
                    
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = deptInfo.department, 
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(2.dp),
                                    color = statusColor.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedContent(
                                    targetState = deptInfo.totalScanned,
                                    transitionSpec = {
                                        fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith fadeOut(animationSpec = tween(90))
                                    },
                                    label = "countAnimation"
                                ) { targetCount ->
                                    Text(
                                        text = targetCount.toString(), 
                                        fontWeight = FontWeight.SemiBold, 
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text(
                                    text = " / ${deptInfo.totalExpected}", 
                                    fontWeight = FontWeight.Normal, 
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), RoundedCornerShape(1.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction = animatedProgress)
                                    .background(statusColor, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                    
                    if (index < departmentProgress.size - 1) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlinkingScanningText() {
    val infiniteTransition = rememberInfiniteTransition(label = "blink")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink_alpha"
    )

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(Color.Green.copy(alpha = alpha), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "Scanning...",
            color = Color.Green.copy(alpha = alpha),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun SensorArcAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "sensor_arcs")
    val arcCount = 3
    val delays = listOf(0, 400, 800)
    
    // Soft, simple expansion
    val scales = List(arcCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.8f,
            targetValue = 2.5f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, delayMillis = delays[index], easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "arc_scale_$index"
        )
    }
    
    val alphas = List(arcCount) { index ->
        infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = 0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500, delayMillis = delays[index], easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "arc_alpha_$index"
        )
    }

    // Removed the wobble/shake completely for a clean look
    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        val color = MaterialTheme.colorScheme.primary
        for (i in 0 until arcCount) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().scale(scales[i].value)) {
                // "Chubby" cute stroke
                val stroke = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 8.dp.toPx(), 
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                
                val currentAlpha = alphas[i].value
                if (currentAlpha > 0f) {
                    // Left arc (135 to 225 degrees)
                    drawArc(
                        color = color.copy(alpha = currentAlpha),
                        startAngle = 135f,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = stroke
                    )
                    // Right arc (-45 to 45 degrees)
                    drawArc(
                        color = color.copy(alpha = currentAlpha),
                        startAngle = -45f,
                        sweepAngle = 90f,
                        useCenter = false,
                        style = stroke
                    )
                    
                    // Cute little dots at the tips of the arcs
                    val radius = size.width / 2
                    val dotRadius = 4.dp.toPx()
                    val angles = listOf(135.0, 225.0, -45.0, 45.0)
                    
                    for (angle in angles) {
                        drawCircle(
                            color = color.copy(alpha = currentAlpha),
                            radius = dotRadius,
                            center = androidx.compose.ui.geometry.Offset(
                                x = size.width / 2 + (radius * Math.cos(Math.toRadians(angle))).toFloat(),
                                y = size.height / 2 + (radius * Math.sin(Math.toRadians(angle))).toFloat()
                            )
                        )
                    }
                }
            }
        }
    }
}
