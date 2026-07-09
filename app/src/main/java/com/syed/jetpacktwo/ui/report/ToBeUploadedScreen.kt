package com.syed.jetpacktwo.ui.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel

data class RackMaster(val rakId: String, val rfid: String, val location: String)

val rackMasterData = listOf(
    RackMaster("9999000408", "C00000000000009999000408", "Shoes Rack"),
    RackMaster("9999000413", "C00000000000009999000413", "Glass Top Stand (L)"),
    RackMaster("9999000420", "C00000000000009999000420", "Standalone Stand (Steel)"),
    RackMaster("9999000427", "C00000000000009999000427", "Glass Top Stand (R)"),
    RackMaster("9999000429", "C00000000000009999000429", "Wall Rack Left Top (Corner)"),
    RackMaster("9999000432", "C00000000000009999000432", "Wall Rack Left Bottom (Corner)"),
    RackMaster("9999000434", "C00000000000009999000434", "Wall Rack Right Top (Corner)"),
    RackMaster("9999000441", "C00000000000009999000441", "Wall Rack Right Bottom (Corner)"),
    RackMaster("9999000447", "C00000000000009999000447", "Tie Rack Top"),
    RackMaster("9999000463", "C00000000000009999000463", "Tie Rack Bottom"),
    RackMaster("9999000465", "C00000000000009999000465", "White wall rack left top"),
    RackMaster("9999000475", "C00000000000009999000475", "White wall rack right top"),
    RackMaster("9999000426", "C00000000000009999000426", "White wall rack Middle"),
    RackMaster("9999000425", "C00000000000009999000425", "White wall rack Bottom"),
    RackMaster("9999000401", "C00000000000009999000401", "A"),
    RackMaster("9999000403", "C00000000000009999000403", "C"),
    RackMaster("9999000412", "C00000000000009999000412", "L"),
    RackMaster("9999000444", "C00000000000009999000444", "P"),
    RackMaster("9999000448", "C00000000000009999000448", "X"),
    RackMaster("9999000443", "C00000000000009999000443", "S"),
    RackMaster("9999000457", "C00000000000009999000457", "V"),
    RackMaster("9999000472", "C00000000000009999000472", "W"),
    RackMaster("9999000473", "C00000000000009999000473", "Reception Table"),
    RackMaster("9999000474", "C00000000000009999000474", "Coffee Table")
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToBeUploadedScreen(
    onBack: () -> Unit,
    viewModel: RfidViewModel = hiltViewModel()
) {
    val settingsViewModel: com.syed.jetpacktwo.presentation.settings.SettingsViewModel = hiltViewModel()
    val isDarkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
    val epcSet by viewModel.existingTagEpcs.collectAsStateWithLifecycle()
    
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    val filterOptions = listOf("All", "A", "C", "E", "F")
    var expanded by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredData = remember(selectedFilter, searchQuery) {
        val letterFiltered = if (selectedFilter == "All") {
            rackMasterData
        } else {
            rackMasterData.filter { 
                it.location.startsWith(selectedFilter, ignoreCase = true)
            }
        }
        
        if (searchQuery.isBlank()) {
            letterFiltered
        } else {
            letterFiltered.filter { 
                it.location.contains(searchQuery, ignoreCase = true) || 
                it.rakId.contains(searchQuery, ignoreCase = true) ||
                it.rfid.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    val foundCount = remember(filteredData, epcSet) {
        filteredData.count { epcSet.contains(it.rfid) }
    }
    
    var pendingNavigation by remember { mutableStateOf<(() -> Unit)?>(null) }
    
    LaunchedEffect(pendingNavigation) {
        if (pendingNavigation != null) {
            kotlinx.coroutines.delay(400)
            pendingNavigation?.invoke()
            pendingNavigation = null
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Location check", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Found: $foundCount / ${filteredData.size}", 
                            fontSize = 14.sp, 
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                },
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
            
            val horizontalPadding = if (isTablet) 64.dp else 16.dp
            
            // Search and Dropdown Filter Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Rack/Location...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                
                // Dropdown Filter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Filter:", 
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedFilter,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .width(120.dp),
                            textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                            singleLine = true
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            filterOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option, fontWeight = FontWeight.Bold) },
                                    onClick = { 
                                        selectedFilter = option
                                        expanded = false 
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = horizontalPadding)
                    .weight(1f)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                tonalElevation = 2.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(vertical = 16.dp, horizontal = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("RAK-ID", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.2f), fontSize = 13.sp)
                        Text("EPC", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.35f), fontSize = 13.sp)
                        Text("LOCATION", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.25f), fontSize = 13.sp)
                        Text("STATUS", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.2f), fontSize = 13.sp)
                    }
                    Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(filteredData) { index, rack ->
                            val isFound = epcSet.contains(rack.rfid)
                            
                            val backgroundColor = if (isFound) {
                                Color.Green.copy(alpha = 0.15f) // Light green for found
                            } else if (index % 2 == 0) {
                                Color.Transparent 
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.02f)
                            }
                            
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(backgroundColor)
                                    .padding(vertical = 16.dp, horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = rack.rakId,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(0.2f),
                                    fontSize = 14.sp,
                                    color = if (isFound) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = rack.rfid,
                                    modifier = Modifier.weight(0.35f),
                                    fontSize = 12.sp,
                                    color = if (isFound) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = rack.location,
                                    modifier = Modifier.weight(0.25f),
                                    fontSize = 14.sp,
                                    color = if (isFound) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (isFound) "Found" else "Not Found",
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(0.2f),
                                    fontSize = 14.sp,
                                    color = if (isFound) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                )
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
