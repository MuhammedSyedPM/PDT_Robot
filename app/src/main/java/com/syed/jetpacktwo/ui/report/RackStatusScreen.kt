package com.syed.jetpacktwo.ui.report

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.syed.jetpacktwo.data.remote.model.RackStatusDto

enum class RackFilter {
    ALL, TICK, CROSS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RackStatusScreen(
    onBack: () -> Unit,
    viewModel: RackStatusViewModel = hiltViewModel()
) {
    val settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel = hiltViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val maxNoLength = remember(uiState) {
        if (uiState is RackStatusUiState.Success) {
            val maxNo = (uiState as RackStatusUiState.Success).data.maxOfOrNull { it.no } ?: 0
            maxNo.toString().length
        } else {
            1
        }
    }

    var selectedFilter by remember { mutableStateOf(RackFilter.ALL) }
    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(pendingNavigation) {
        if (pendingNavigation != null) {
            kotlinx.coroutines.delay(400)
            pendingNavigation?.invoke()
            pendingNavigation = null
        }
    }

    val filteredData = remember(selectedFilter, uiState) {
        val data = if (uiState is RackStatusUiState.Success) {
            (uiState as RackStatusUiState.Success).data
        } else emptyList()
        
        when (selectedFilter) {
            RackFilter.ALL -> data
            RackFilter.TICK -> data.filter { it.isStatusOk }
            RackFilter.CROSS -> data.filter { !it.isStatusOk }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rack Status", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { pendingNavigation = onBack }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { settingsViewModel.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle Theme",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    if (isTablet) {
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .selectableGroup(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // All
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .selectable(
                                        selected = selectedFilter == RackFilter.ALL,
                                        onClick = { selectedFilter = RackFilter.ALL },
                                        role = Role.RadioButton
                                    )
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedFilter == RackFilter.ALL,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("All", fontWeight = FontWeight.Bold)
                            }

                            // Tick
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .selectable(
                                        selected = selectedFilter == RackFilter.TICK,
                                        onClick = { selectedFilter = RackFilter.TICK },
                                        role = Role.RadioButton
                                    )
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedFilter == RackFilter.TICK,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.CheckCircle, contentDescription = "Tick", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                            }

                            // Cross
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .selectable(
                                        selected = selectedFilter == RackFilter.CROSS,
                                        onClick = { selectedFilter = RackFilter.CROSS },
                                        role = Role.RadioButton
                                    )
                                    .padding(8.dp)
                            ) {
                                RadioButton(
                                    selected = selectedFilter == RackFilter.CROSS,
                                    onClick = null
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Cancel, contentDescription = "X", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (pendingNavigation != null) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                )
            }
            if (!isTablet) {
                // Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .selectableGroup(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // All
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = selectedFilter == RackFilter.ALL,
                                onClick = { selectedFilter = RackFilter.ALL },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedFilter == RackFilter.ALL,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("All", fontWeight = FontWeight.Bold)
                    }

                    // Tick
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = selectedFilter == RackFilter.TICK,
                                onClick = { selectedFilter = RackFilter.TICK },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedFilter == RackFilter.TICK,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.CheckCircle, contentDescription = "Tick", tint = Color(0xFF4CAF50), modifier = Modifier.size(20.dp))
                    }

                    // Cross
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .selectable(
                                selected = selectedFilter == RackFilter.CROSS,
                                onClick = { selectedFilter = RackFilter.CROSS },
                                role = Role.RadioButton
                            )
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedFilter == RackFilter.CROSS,
                            onClick = null
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Cancel, contentDescription = "X", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                    }
                }
            }

            val horizontalPadding = if (isTablet) 64.dp else 16.dp

            if (uiState is RackStatusUiState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (uiState is RackStatusUiState.Error) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text((uiState as RackStatusUiState.Error).message, color = MaterialTheme.colorScheme.error)
                }
            } else {
                // Unified Table Container
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = horizontalPadding)
                        .weight(1f) // Let it fill remaining space nicely
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp,
                    tonalElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                ) {
                    Column {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                                .padding(vertical = 16.dp, horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isTablet) {
                                Text("Sl. No", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.3f), fontSize = 13.sp)
                            }
                            Text("Department", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1.5f), fontSize = 13.sp)
                            Text("Rack", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1.7f), fontSize = 13.sp)
                            Text("Status", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.End, modifier = Modifier.weight(0.5f), fontSize = 13.sp)
                        }
                        Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))

                        // Body
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(filteredData) { index, item ->
                                val backgroundColor = if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                                
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(backgroundColor)
                                        .padding(vertical = 16.dp, horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isTablet) {
                                        Text(
                                            text = item.no.toString().padStart(maxNoLength, '0'),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(0.3f),
                                            fontSize = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = item.department,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1.5f),
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = item.description,
                                        modifier = Modifier.weight(1.7f),
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Box(
                                        modifier = Modifier.weight(0.5f),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Icon(
                                            imageVector = if (item.isStatusOk) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                            contentDescription = if (item.isStatusOk) "Status OK" else "Status Error",
                                            tint = if (item.isStatusOk) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                if (index < filteredData.lastIndex) {
                                    Divider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
