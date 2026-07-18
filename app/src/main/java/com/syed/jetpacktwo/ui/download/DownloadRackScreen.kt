package com.syed.jetpacktwo.ui.download

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.syed.jetpacktwo.data.remote.model.RackStatusDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadRackScreen(
    onBack: () -> Unit,
    viewModel: DownloadRackViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    
    val configuration = LocalConfiguration.current
    val context = androidx.compose.ui.platform.LocalContext.current
    val isTablet = configuration.screenWidthDp > 600

    if (uiState is DownloadRackUiState.Error) {
        com.syed.jetpacktwo.ui.components.ApiErrorDialog(
            errorMessage = (uiState as DownloadRackUiState.Error).message,
            onDismiss = { viewModel.clearError() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Download Racks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isTablet) {
            // Split-screen Layout for Tablet
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left 50% - Rack List
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    if (uiState is DownloadRackUiState.Success) {
                        val items = (uiState as DownloadRackUiState.Success).data
                        RackListSection(items)
                    } else {
                        // Empty state before download
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No racks downloaded yet.",
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // Divider
                Divider(
                    modifier = Modifier.fillMaxHeight().width(1.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Right 50% - Download Controls
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    DownloadControlsSection(
                        uiState = uiState,
                        onDownloadClick = { viewModel.downloadData() },
                        onViewListClick = { }, // Not used in tablet
                        showViewListButton = false
                    )
                }
            }
        } else {
            // Mobile Layout
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                DownloadControlsSection(
                    uiState = uiState,
                    onDownloadClick = { viewModel.downloadData() },
                    onViewListClick = { showBottomSheet = true },
                    showViewListButton = true
                )
            }
        }
    }

    // Bottom sheet for mobile only
    if (!isTablet && showBottomSheet && uiState is DownloadRackUiState.Success) {
        val items = (uiState as DownloadRackUiState.Success).data
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        ) {
            RackListSection(items, padding = 16.dp)
        }
    }
}

@Composable
fun RackListSection(items: List<com.syed.jetpacktwo.data.local.db.ExpectedItemEntity>, padding: androidx.compose.ui.unit.Dp = 0.dp) {
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredItems = remember(searchQuery, items) {
        if (searchQuery.isBlank()) items else items.filter { 
            it.description.contains(searchQuery, ignoreCase = true) || 
            it.department.contains(searchQuery, ignoreCase = true) 
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Downloaded Racks",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                placeholder = { Text("Search by rack or department") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
            )
        }
        items(filteredItems) { rack ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Rack: ${rack.description}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = "Dept: ${rack.department}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DownloadControlsSection(
    uiState: DownloadRackUiState,
    onDownloadClick: () -> Unit,
    onViewListClick: () -> Unit,
    showViewListButton: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top Right Time
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            if (uiState is DownloadRackUiState.Success) {
                val time = uiState.downloadTime
                Text(
                    text = "Downloaded: $time",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }
        }

        // Center Stylish Count
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (uiState is DownloadRackUiState.Downloading) {
                    AnimatedDownloadIcon()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please wait downloading..",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    val count = if (uiState is DownloadRackUiState.Success) {
                        uiState.data.size
                    } else {
                        0
                    }
                    
                    Text(
                        text = count.toString(),
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "RACKS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 4.sp
                    )
                }
            }
        }

        // Bottom Buttons
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onDownloadClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = uiState !is DownloadRackUiState.Downloading
            ) {
                Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Download", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            if (showViewListButton) {
                Button(
                    onClick = onViewListClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    enabled = uiState is DownloadRackUiState.Success
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Downloaded Items", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnimatedDownloadIcon() {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
        Icon(
            imageVector = Icons.Default.CloudDownload,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .scale(scale)
                .alpha(alpha),
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
