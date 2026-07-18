package com.syed.jetpacktwo.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import com.syed.jetpacktwo.util.debouncedClickable
import com.syed.jetpacktwo.util.rememberDebouncedClick

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onExit: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
    settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel = hiltViewModel()
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loginResult by viewModel.loginResult.collectAsState()
    val baseUrl by viewModel.baseUrl.collectAsState()

    var showConfigDialog by remember { mutableStateFlow(false) }
    var passwordVisible by remember { mutableStateFlow(false) }

    LaunchedEffect(loginResult) {
        loginResult?.let {
            if (it.isSuccess) {
                onLoginSuccess()
                viewModel.resetLoginResult()
            }
        }
    }

    var startAnimation by remember { mutableStateOf(false) }
    
    // Entry animations
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )
    
    val cardOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 100.dp,
        animationSpec = tween(1000, easing = FastOutSlowInEasing)
    )
    
    val contentAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(1200)
    )

    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(2000, easing = LinearOutSlowInEasing)
    )

    LaunchedEffect(Unit) {
        delay(100)
        startAnimation = true
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val isTablet = maxWidth > 600.dp
        
        if (isTablet) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Left side: 60% Image
                Box(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.syed.jetpacktwo.R.drawable.splsh_image),
                        contentDescription = "Login Image",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop, 
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(contentAlpha),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            androidx.compose.ui.graphics.BlendMode.SrcAtop
                        )
                    )
                }

                // Right side: 40% Form
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    LoginFormContent(
                        viewModel = viewModel,
                        username = username,
                        password = password,
                        isLoading = isLoading,
                        loginResult = loginResult,
                        passwordVisible = passwordVisible,
                        onPasswordVisibleChange = { passwordVisible = it },
                        showConfigDialog = { showConfigDialog = true },
                        onExit = onExit,
                        logoScale = logoScale,
                        logoRotation = rotation,
                        contentAlpha = contentAlpha,
                        cardOffsetY = cardOffsetY,
                        startAnimation = startAnimation
                    )
                }
            }
        } else {
            // Mobile view
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Mobile image background
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.syed.jetpacktwo.R.drawable.splsh_image),
                    contentDescription = "Login Image",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(contentAlpha),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        androidx.compose.ui.graphics.BlendMode.SrcAtop
                    )
                )

                // Translucent dim over the image
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                )
                
                LoginFormContent(
                    viewModel = viewModel,
                    username = username,
                    password = password,
                    isLoading = isLoading,
                    loginResult = loginResult,
                    passwordVisible = passwordVisible,
                    onPasswordVisibleChange = { passwordVisible = it },
                    showConfigDialog = { showConfigDialog = true },
                    onExit = onExit,
                    logoScale = logoScale,
                    logoRotation = rotation,
                    contentAlpha = contentAlpha,
                    cardOffsetY = cardOffsetY,
                    startAnimation = startAnimation
                )
            }
        }
    }

    if (showConfigDialog) {
        ConfigDialog(
            baseUrl = baseUrl,
            onUrlChange = { viewModel.onBaseUrlChange(it) },
            onSaveUrl = {
                viewModel.saveBaseUrl()
            },
            devices = viewModel.devices.collectAsState().value,
            selectedDeviceName = viewModel.selectedDeviceName.collectAsState().value,
            onDeviceSelect = { viewModel.onDeviceSelect(it) },
            onFetchDevices = { viewModel.fetchDevices() },
            isLoading = viewModel.isLoading.collectAsState().value,
            onDismiss = { showConfigDialog = false },
            epcFilter = viewModel.epcFilter.collectAsState().value,
            onSaveFilter = { viewModel.saveEpcFilter(it) },
            hardwareType = viewModel.hardwareType.collectAsState().value,
            onHardwareTypeSelect = { viewModel.onHardwareTypeSelect(it) },
            settingsViewModel = settingsViewModel
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigDialog(
    baseUrl: String,
    onUrlChange: (String) -> Unit,
    onSaveUrl: () -> Unit,
    devices: List<com.syed.jetpacktwo.data.model.Device>,
    selectedDeviceName: String,
    onDeviceSelect: (com.syed.jetpacktwo.data.model.Device) -> Unit,
    onFetchDevices: () -> Unit,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    epcFilter: String,
    onSaveFilter: (String) -> Unit,
    hardwareType: String,
    onHardwareTypeSelect: (String) -> Unit,
    settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel
) {
    var configMode by remember { mutableStateOf("MENU") } // MENU, URL, DEVICE, FILTER, HARDWARE, THEME
    var currentFilterText by remember { mutableStateOf(epcFilter) }
    
    // Update local state when prop changes
    LaunchedEffect(epcFilter) {
        currentFilterText = epcFilter
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = when(configMode) {
                        "URL" -> "Base URL Settings"
                        "DEVICE" -> "Device Settings"
                        "FILTER" -> "EPC Filter Settings"
                        "HARDWARE" -> "Hardware Type"
                        "THEME" -> "Theme Settings"
                        else -> "System Configuration"
                    },
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                when (configMode) {
                    "MENU" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            ConfigMenuButton(
                                title = "Base URL Settings",
                                icon = Icons.Default.Language,
                                onClick = { configMode = "URL" }
                            )
                            ConfigMenuButton(
                                title = "Device Settings",
                                icon = Icons.Default.PrecisionManufacturing,
                                onClick = { 
                                    configMode = "DEVICE"
                                    onFetchDevices()
                                }
                            )
                            ConfigMenuButton(
                                title = "EPC Filter Settings",
                                icon = Icons.Default.FilterList,
                                onClick = { configMode = "FILTER" }
                            )
                            ConfigMenuButton(
                                title = "Hardware Provider",
                                icon = Icons.Default.DeveloperBoard,
                                onClick = { configMode = "HARDWARE" }
                            )
                            ConfigMenuButton(
                                title = "Theme Settings",
                                icon = Icons.Default.Palette,
                                onClick = { configMode = "THEME" }
                            )
                        }
                    }
                    "URL" -> {
                        OutlinedTextField(
                            value = baseUrl,
                            onValueChange = onUrlChange,
                            label = { Text("API Base URL") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.Link, contentDescription = null) }
                        )
                    }
                    "DEVICE" -> {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                        } else {
                            var expanded by remember { mutableStateOf(false) }
                            Box(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = selectedDeviceName,
                                    onValueChange = {},
                                    readOnly = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    label = { Text("Select Hardware Device") },
                                    shape = RoundedCornerShape(16.dp),
                                    trailingIcon = {
                                        IconButton(onClick = { expanded = true }) {
                                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                        }
                                    }
                                )
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier.fillMaxWidth(0.8f)
                                ) {
                                    devices.forEach { device ->
                                        DropdownMenuItem(
                                            text = { Text("${device.deviceName} (${device.location})") },
                                            onClick = {
                                                onDeviceSelect(device)
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "FILTER" -> {
                        OutlinedTextField(
                            value = currentFilterText,
                            onValueChange = { currentFilterText = it },
                            label = { Text("EPC Prefix Filter (e.g. AS)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) },
                            placeholder = { Text("Empty to scan all tags") }
                        )
                    }
                    "HARDWARE" -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            listOf("NORDIC", "ZEBRA", "ZEBRA FIXED", "CHAINWAY", "IMPINJ").forEach { type ->
                                val isSelected = hardwareType == type
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onHardwareTypeSelect(type) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp, 
                                        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        RadioButton(selected = isSelected, onClick = { onHardwareTypeSelect(type) })
                                        Text(
                                            text = type,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                    "THEME" -> {
                        val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Dark Mode", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Switch(
                                    checked = isDarkMode,
                                    onCheckedChange = { settingsViewModel.toggleTheme() }
                                )
                            }
                            
                            Text("Primary Color", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            
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
                                modifier = Modifier.fillMaxWidth().height(180.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(colors.size) { index ->
                                    val color = colors[index]
                                    Surface(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .clickable {
                                                settingsViewModel.setPrimaryColor(color.toArgb().toLong())
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
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    if (configMode != "MENU") {
                        TextButton(onClick = { configMode = "MENU" }) {
                            Text("BACK")
                        }
                    } else {
                        TextButton(onClick = onDismiss) {
                            Text("")
                        }
                    }

                    Button(
                        onClick = { 
                            when(configMode) {
                                "URL" -> {
                                    onSaveUrl()
                                    onDismiss()
                                }
                                "DEVICE" -> onDismiss()
                                "FILTER" -> {
                                    onSaveFilter(currentFilterText)
                                    onDismiss()
                                }
                                else -> onDismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(0.6f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (configMode == "MENU") "CLOSE" else "SAVE")
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigMenuButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(text = title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.weight(1f))
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun <T> mutableStateFlow(value: T): MutableState<T> = mutableStateOf(value)

@Composable
fun LoginFormContent(
    viewModel: LoginViewModel,
    username: String,
    password: String,
    isLoading: Boolean,
    loginResult: Result<com.syed.jetpacktwo.data.model.LoginResponse>?,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    showConfigDialog: () -> Unit,
    onExit: () -> Unit,
    logoScale: Float,
    logoRotation: Float,
    contentAlpha: Float,
    cardOffsetY: androidx.compose.ui.unit.Dp,
    startAnimation: Boolean
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
        
        // Animated Logo Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .scale(logoScale)
                .alpha(contentAlpha)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(90.dp)
                    .rotate(logoRotation)
            ) {
                // Outer ring
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape,
                    color = Color.Transparent,
                    border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {}
                
                Surface(
                    modifier = Modifier.size(76.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.syed.jetpacktwo.R.drawable.stock_eye_logo),
                        contentDescription = "Stock Eye Logo",
                        modifier = Modifier.padding(10.dp).fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                "STOCK EYE".forEachIndexed { index, char ->
                    val charAlpha by animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = 400 + (index * 80),
                            easing = LinearOutSlowInEasing
                        ),
                        label = "char_alpha_$index"
                    )
                    val charScale by animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0.4f,
                        animationSpec = spring(
                            dampingRatio = 0.6f,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "char_scale_$index"
                    )
                    Text(
                        text = char.toString(),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier
                            .graphicsLayer(
                                alpha = charAlpha,
                                scaleX = charScale,
                                scaleY = charScale
                            ),
                        letterSpacing = 1.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.Center) {
                "SMART STOCK TAKE SYSTEM".forEachIndexed { index, char ->
                    val charAlpha by animateFloatAsState(
                        targetValue = if (startAnimation) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = 1000 + (index * 30),
                            easing = LinearOutSlowInEasing
                        ),
                        label = "sub_alpha_$index"
                    )
                    Text(
                        text = char.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.graphicsLayer(alpha = charAlpha),
                        letterSpacing = if (char == ' ') 3.sp else 0.5.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Animated Login Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 480.dp)
                .offset(y = cardOffsetY)
                .alpha(contentAlpha),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(32.dp),
            shadowElevation = 4.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                OutlinedTextField(
                    value = username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                        IconButton(onClick = rememberDebouncedClick { onPasswordVisibleChange(!passwordVisible) }) {
                            Icon(imageVector = image, contentDescription = null)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )

                Button(
                    onClick = rememberDebouncedClick { viewModel.login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("LOGIN", fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = rememberDebouncedClick { showConfigDialog() }) {
                        Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("CONFIG", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    TextButton(onClick = rememberDebouncedClick { onExit() }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXIT", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }

        // Animated Error Message is now replaced by ApiErrorDialog
        if (loginResult?.isFailure == true) {
            val errorMsg = loginResult.exceptionOrNull()?.message ?: "Login failed"
            com.syed.jetpacktwo.ui.components.ApiErrorDialog(
                errorMessage = errorMsg,
                onDismiss = { viewModel.resetLoginResult() }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
