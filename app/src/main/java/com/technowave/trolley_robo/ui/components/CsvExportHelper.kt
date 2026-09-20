package com.technowave.trolley_robo.ui.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.technowave.trolley_robo.data.local.db.ScannedTagDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CsvExportViewModel @Inject constructor(
    private val dao: ScannedTagDao
) : ViewModel() {
    
    fun exportToCsv(context: Context, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val tags = dao.getAllTags().first()
                if (tags.isEmpty()) {
                    onError("No scanned tags to export")
                    return@launch
                }
                
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) {
                    downloadsDir.mkdirs()
                }
                
                val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "scanned_tags_$timeStamp.csv"
                val file = File(downloadsDir, fileName)
                
                java.io.BufferedWriter(FileWriter(file)).use { writer ->
                    writer.append("EPC,SchedulerId,Timestamp\n")
                    tags.forEach { tag ->
                        writer.append("=\"${tag.epc}\",=\"${tag.schedulerId}\",${tag.timestamp}\n")
                    }
                }
                
                onSuccess(file.absolutePath)
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error occurred")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CsvExportDialog(
    onDismiss: () -> Unit,
    viewModel: CsvExportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted || Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            viewModel.exportToCsv(
                context = context,
                onSuccess = { path ->
                    Toast.makeText(context, "Exported successfully to: $path", Toast.LENGTH_LONG).show()
                    onDismiss()
                },
                onError = { error ->
                    Toast.makeText(context, "Export failed: $error", Toast.LENGTH_LONG).show()
                    onDismiss()
                }
            )
        } else {
            Toast.makeText(context, "Storage permission is required", Toast.LENGTH_LONG).show()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Export to CSV",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Enter password to export data to Downloads folder.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { 
                        password = it
                        errorMessage = ""
                    },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    isError = errorMessage.isNotEmpty()
                )
                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                    Button(
                        onClick = {
                            if (password == "2255") {
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                                    val hasPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasPermission) {
                                        viewModel.exportToCsv(
                                            context = context,
                                            onSuccess = { path ->
                                                Toast.makeText(context, "Exported successfully to: $path", Toast.LENGTH_LONG).show()
                                                onDismiss()
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, "Export failed: $error", Toast.LENGTH_LONG).show()
                                                onDismiss()
                                            }
                                        )
                                    } else {
                                        launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    }
                                } else {
                                    viewModel.exportToCsv(
                                        context = context,
                                        onSuccess = { path ->
                                            Toast.makeText(context, "Exported successfully to: $path", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        },
                                        onError = { error ->
                                            Toast.makeText(context, "Export failed: $error", Toast.LENGTH_LONG).show()
                                            onDismiss()
                                        }
                                    )
                                }
                            } else {
                                errorMessage = "Incorrect password"
                            }
                        }
                    ) {
                        Text("EXPORT")
                    }
                }
            }
        }
    }
}
