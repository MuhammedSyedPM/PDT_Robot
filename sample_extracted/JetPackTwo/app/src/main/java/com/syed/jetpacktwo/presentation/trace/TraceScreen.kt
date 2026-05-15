package com.syed.jetpacktwo.presentation.trace

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syed.jetpacktwo.domain.model.TracedTagInfo
import com.syed.jetpacktwo.presentation.rfid.RfidViewModel

private const val RSSI_MIN = -90
private const val RSSI_MAX = -45

fun rssiToPercentage(scaledRssi: Int): Int {
    return when {
        scaledRssi in 0..100 -> scaledRssi
        scaledRssi in 0..255 -> (scaledRssi * 100 / 255).coerceIn(0, 100)
        scaledRssi in RSSI_MIN..RSSI_MAX -> {
            val range = RSSI_MAX - RSSI_MIN
            ((scaledRssi - RSSI_MIN).toFloat() / range * 100f).toInt().coerceIn(0, 100)
        }
        else -> scaledRssi.coerceIn(0, 100)
    }
}

enum class ProximityZone(val label: String, val emoji: String, val color: Color, val glowColor: Color) {
    FAR("Far", "◉", Color(0xFFE53935), Color(0x44E53935)),
    CLOSE("Close", "◎", Color(0xFFFFB74D), Color(0x44FFB74D)),
    NEAR("Near", "●", Color(0xFF66BB6A), Color(0x6666BB6A))
}

fun proximityFromPercentage(percentage: Int): ProximityZone = when {
    percentage <= 33 -> ProximityZone.FAR
    percentage <= 66 -> ProximityZone.CLOSE
    else -> ProximityZone.NEAR
}

enum class TraceScreenStyle(val title: String, val subtitle: String) {
    GAUGE("Screen 1", "Circular gauge view"),
    SIGNAL("Screen 2", "Signal bars view"),
    SCREEN3("Screen 3", "Classic gauge view"),
    SCREEN4("Screen 4", "Stunning animated view"),
    SCREEN5("Screen 5", "Luminous ring · charging style"),
    SCREEN6("Screen 6", "Animated bar chart"),
    SCREEN7("Screen 7", "Search progress bar"),
    SCREEN8("Screen 8", "Line graph · Fair/Good/Excellent"),
    SCREEN9("Screen 9", "Placeholder"),
    SCREEN10("Screen 10", "Bar chart with trend line"),
    SCREEN11("Screen 11", "Light bulb · Liquid fill"),
    SCREEN12("Screen 12", "Glass tube · Liquid fill")
}

@Composable
fun TraceStylePickerDialog(
    onStyleSelected: (TraceScreenStyle) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.45f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Choose View Style",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Pick a premium interface for tag tracing",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 6.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))
                TraceScreenStyle.entries.forEach { style ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onStyleSelected(style) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        when (style) {
                                            TraceScreenStyle.GAUGE -> Color(0xFF5C6BC0).copy(alpha = 0.3f)
                                            TraceScreenStyle.SIGNAL -> Color(0xFF26A69A).copy(alpha = 0.3f)
                                            TraceScreenStyle.SCREEN3 -> Color(0xFF7E57C2).copy(alpha = 0.3f)
                                            TraceScreenStyle.SCREEN4 -> Color(0xFFE040FB).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN5 -> Color(0xFF00B8D4).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN6 -> Color(0xFFFF6B35).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN7 -> Color(0xFF4CAF50).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN8 -> Color(0xFF66BB6A).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN9 -> Color(0xFF9E9E9E).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN10 -> Color(0xFFFF9800).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN11 -> Color(0xFFFFEB3B).copy(alpha = 0.4f)
                                            TraceScreenStyle.SCREEN12 -> Color(0xFF26C6DA).copy(alpha = 0.4f)
                                        }
                                    )
                            ) {
                                Text(
                                    text = when (style) {
                                        TraceScreenStyle.GAUGE -> "◐"
                                        TraceScreenStyle.SIGNAL -> "▮"
                                        TraceScreenStyle.SCREEN3 -> "◎"
                                        TraceScreenStyle.SCREEN4 -> "✦"
                                        TraceScreenStyle.SCREEN5 -> "◯"
                                        TraceScreenStyle.SCREEN6 -> "▮"
                                        TraceScreenStyle.SCREEN7 -> "▬"
                                        TraceScreenStyle.SCREEN8 -> "📈"
                                        TraceScreenStyle.SCREEN9 -> "◻"
                                        TraceScreenStyle.SCREEN10 -> "📊"
                                        TraceScreenStyle.SCREEN11 -> "💡"
                                        TraceScreenStyle.SCREEN12 -> "🧪"
                                    },
                                    color = Color.White,
                                    modifier = Modifier.align(Alignment.Center),
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Column(modifier = Modifier.padding(start = 16.dp)) {
                                Text(
                                    style.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    style.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Cancel", color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceScreen(
    viewModel: RfidViewModel,
    epc: String,
    style: TraceScreenStyle,
    onBack: () -> Unit
) {
    var currentTrace by remember { mutableStateOf<TracedTagInfo?>(null) }

    LaunchedEffect(epc) {
        viewModel.startTagTracing(epc)
        viewModel.getTraceData().collect { currentTrace = it }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.stopTrace() }
    }

    val percentage = currentTrace?.scaledRssi?.let { rssiToPercentage(it) } ?: 0
    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(280),
        label = "gauge"
    )
    val zone = proximityFromPercentage(percentage)
    val glowAlpha by animateFloatAsState(
        targetValue = if (zone == ProximityZone.NEAR && percentage > 80) 0.4f else 0.15f,
        animationSpec = tween(400),
        label = "glow"
    )

    when (style) {
        TraceScreenStyle.GAUGE -> TraceContentGauge(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            glowAlpha = glowAlpha,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SIGNAL -> TraceContentSignal(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN3 -> TraceContentScreen3(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            glowAlpha = glowAlpha,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN4 -> TraceContentScreen4(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN5 -> TraceContentScreen5(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN6 -> TraceContentScreen6(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN7 -> TraceContentScreen7(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN8 -> TraceContentScreen8(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN9 -> TraceContentScreen9(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN10 -> TraceContentScreen10(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN11 -> TraceContentScreen11(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
        TraceScreenStyle.SCREEN12 -> TraceContentScreen12(
            epc = epc,
            percentage = percentage,
            animatedPercent = animatedPercent,
            zone = zone,
            currentTrace = currentTrace,
            onBack = onBack
        )
    }
}

/** Full-screen trace panel for Screen 1, 2, 3, 4. */
@Composable
fun TraceContentInline(
    viewModel: RfidViewModel,
    epc: String,
    style: TraceScreenStyle,
    onClose: () -> Unit
) {
    var currentTrace by remember { mutableStateOf<TracedTagInfo?>(null) }
    LaunchedEffect(epc) {
        viewModel.startTagTracing(epc)
        viewModel.getTraceData().collect { currentTrace = it }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopTrace() }
    }
    val percentage = currentTrace?.scaledRssi?.let { rssiToPercentage(it) } ?: 0
    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(280),
        label = "gauge"
    )
    val zone = proximityFromPercentage(percentage)
    val glowAlpha by animateFloatAsState(
        targetValue = if (zone == ProximityZone.NEAR && percentage > 80) 0.4f else 0.15f,
        animationSpec = tween(400),
        label = "glow"
    )
    Box(modifier = Modifier.fillMaxSize()) {
        when (style) {
            TraceScreenStyle.GAUGE -> TraceContentGauge(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                glowAlpha = glowAlpha,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SIGNAL -> TraceContentSignal(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN3 -> TraceContentScreen3(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                glowAlpha = glowAlpha,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN4 -> TraceContentScreen4(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN5 -> TraceContentScreen5(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN6 -> TraceContentScreen6(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN7 -> TraceContentScreen7(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN8 -> TraceContentScreen8(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN9 -> TraceContentScreen9(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN10 -> TraceContentScreen10(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN11 -> TraceContentScreen11(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
            TraceScreenStyle.SCREEN12 -> TraceContentScreen12(
                epc = epc,
                percentage = percentage,
                animatedPercent = animatedPercent,
                zone = zone,
                currentTrace = currentTrace,
                onBack = onClose
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen3(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    glowAlpha: Float,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            zone.glowColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace tag",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "TAGGED EPC",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = epc,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.95f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(300.dp)) {
                    Canvas(modifier = Modifier.size(300.dp)) {
                        val strokeWidth = 2.dp.toPx()
                        val radius = (size.minDimension / 2f) - 24.dp.toPx()
                        val center = Offset(size.width / 2f, size.height / 2f)
                        drawArc(
                            color = zone.color.copy(alpha = 0.2f),
                            startAngle = 135f,
                            sweepAngle = 270f * animatedPercent,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 24.dp.toPx() + strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Card(
                        modifier = Modifier.size(280.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16162A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 18.dp.toPx()
                                val radius = (size.minDimension / 2f) - strokeWidth - 4.dp.toPx()
                                val center = Offset(size.width / 2f, size.height / 2f)
                                drawArc(
                                    color = Color.White.copy(alpha = 0.08f),
                                    startAngle = 135f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                for (i in 0..9) {
                                    val angle = 135f + (270f * i / 10f)
                                    rotate(angle, center) {
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.06f),
                                            start = Offset(center.x, center.y - radius - strokeWidth / 2),
                                            end = Offset(center.x, center.y - radius + strokeWidth / 2),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                }
                                drawArc(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFFE53935), zone.color, zone.color)
                                    ),
                                    startAngle = 135f,
                                    sweepAngle = 270f * animatedPercent,
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$percentage",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = (-1).sp
                                )
                                Text(
                                    text = "%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = zone.label.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = zone.color,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(ProximityZone.FAR, ProximityZone.CLOSE, ProximityZone.NEAR).forEach { z ->
                        val active = z == zone
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (active) z.color.copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (active) 1.5.dp else 0.5.dp,
                                    color = if (active) z.color else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "${z.emoji} ${z.label}",
                                color = if (active) z.color else Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Card(
                    modifier = Modifier.fillMaxWidth().height(12.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(2.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedPercent)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(zone.color.copy(alpha = 0.9f), zone.color)
                                    )
                                )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.45f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Move the tag toward the reader to increase signal strength.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.35f)
                )
            }
        }
    }
}

// Green neon palette for Screen 4 (line progress / tech loading style)
private val NeonGreen = Color(0xFF00FF88)
private val NeonGreenBright = Color(0xFF39FFA4)
private val NeonGreenGlow = Color(0xFF00FF88)
private val DarkBg = Color(0xFF051510)
private val TrackDark = Color(0xFF0A1F18)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen4(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val lineGlowOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        lineGlowOffset.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val pulseGlow = remember { Animatable(0.4f) }
    LaunchedEffect(Unit) {
        pulseGlow.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1400
                    0.4f at 0
                    1f at 700
                    0.4f at 1400
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(400),
        label = "pct"
    )
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "num"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF031210),
                        DarkBg,
                        Color(0xFF061A14)
                    )
                )
            )
    ) {
        // Subtle abstract horizontal lines (tech / loading feel)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val lineAlpha = 0.06f
            val step = 24.dp.toPx()
            var y = step
            while (y < size.height) {
                drawLine(
                    color = NeonGreen.copy(alpha = lineAlpha),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f
                )
                y += step
            }
        }
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Live",
                        color = NeonGreenBright.copy(alpha = 0.95f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonGreenBright.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = NeonGreenBright
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(20) + if (epc.length > 20) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = NeonGreen.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "$displayedPercent",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = NeonGreenBright
                )
                Text(
                    text = "% signal",
                    style = MaterialTheme.typography.titleMedium,
                    color = NeonGreen.copy(alpha = 0.8f),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = zone.label.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = NeonGreen.copy(alpha = pulseGlow.value),
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                // ——— Main neon line progress (inspired by green neon line progress / loading) ———
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                ) {
                    // Track (dark with neon edge)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .background(TrackDark)
                            .then(
                                Modifier.drawWithContent {
                                    drawContent()
                                    drawRect(
                                        brush = Brush.linearGradient(
                                            colors = listOf(
                                                NeonGreen.copy(alpha = 0.15f),
                                                Color.Transparent,
                                                Color.Transparent,
                                                NeonGreen.copy(alpha = 0.15f)
                                            ),
                                            start = Offset(0f, 0f),
                                            end = Offset(size.width, size.height)
                                        )
                                    )
                                }
                            )
                    )
                    // Outer glow stroke
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                            .drawWithContent {
                                drawContent()
                                val corner = 10.dp.toPx()
                                drawRoundRect(
                                    color = NeonGreen.copy(alpha = 0.35f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(corner),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
                    )
                    // Filled progress (neon green)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercent)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        NeonGreen.copy(alpha = 0.9f),
                                        NeonGreenBright,
                                        NeonGreen
                                    )
                                )
                            )
                    )
                    // Traveling glow highlight along the line (animated)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                drawContent()
                                val w = size.width * animatedPercent
                                if (w > 0f) {
                                    val x = size.width * lineGlowOffset.value * (animatedPercent + 0.2f).coerceAtMost(1f)
                                    if (x < w) {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    Color.White.copy(alpha = 0.5f),
                                                    NeonGreenBright.copy(alpha = 0.2f),
                                                    Color.Transparent
                                                )
                                            ),
                                            radius = 24.dp.toPx(),
                                            center = Offset(x, size.height / 2f)
                                        )
                                    }
                                }
                            }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Secondary thin line (abstract loading accent)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(TrackDark)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercent)
                            .clip(RoundedCornerShape(2.dp))
                            .background(NeonGreen.copy(alpha = 0.6f + 0.2f * pulseGlow.value))
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonGreen.copy(alpha = 0.6f)
                )
            }
        }
    }
}

// Screen 5: Luminous ring with percentages (charging/loading style)
private val LuminousRingCyan = Color(0xFF00E5FF)
private val LuminousRingWhite = Color(0xFFE0F7FA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen5(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val ringGlowPhase = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        ringGlowPhase.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1800, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val pulseBrightness = remember { Animatable(0.7f) }
    LaunchedEffect(Unit) {
        pulseBrightness.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 1200
                    0.7f at 0
                    1f at 600
                    0.7f at 1200
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0A1520),
                        Color(0xFF051018),
                        Color(0xFF020810)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Signal",
                        color = LuminousRingCyan.copy(alpha = 0.95f),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LuminousRingWhite.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = LuminousRingCyan
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.4f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                // Luminous ring (filled with color + numbers in center)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(280.dp)
                ) {
                    Canvas(modifier = Modifier.size(280.dp)) {
                        val strokeWidth = 18.dp.toPx()
                        val radius = (size.minDimension / 2f) - strokeWidth / 2f - 4.dp.toPx()
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val startAngle = 270f
                        val fullSweep = 360f
                        // Track (dim ring)
                        drawArc(
                            color = Color.White.copy(alpha = 0.08f),
                            startAngle = startAngle,
                            sweepAngle = fullSweep,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Filled luminous arc (bright ring filled by percentage)
                        drawArc(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    LuminousRingCyan.copy(alpha = 0.5f),
                                    LuminousRingWhite.copy(alpha = pulseBrightness.value),
                                    zone.color.copy(alpha = 0.95f),
                                    LuminousRingCyan.copy(alpha = 0.8f)
                                )
                            ),
                            startAngle = startAngle,
                            sweepAngle = fullSweep * animatedPercent,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        // Traveling glow dot on the ring (motion)
                        val glowAngle = startAngle + fullSweep * ringGlowPhase.value
                        val rad = Math.toRadians(glowAngle.toDouble())
                        val dotRadius = 6.dp.toPx()
                        val dotX = center.x + radius * kotlin.math.cos(rad).toFloat()
                        val dotY = center.y + radius * kotlin.math.sin(rad).toFloat()
                        if (ringGlowPhase.value <= animatedPercent + 0.02f) {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color.White,
                                        LuminousRingCyan.copy(alpha = 0.6f),
                                        Color.Transparent
                                    )
                                ),
                                radius = dotRadius * 2f,
                                center = Offset(dotX, dotY)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$displayedPercent",
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            color = LuminousRingWhite
                        )
                        Text(
                            text = "%",
                            style = MaterialTheme.typography.headlineMedium,
                            color = LuminousRingCyan.copy(alpha = 0.9f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = zone.label.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = zone.color.copy(alpha = 0.9f),
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 2.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}

// Screen 6: Animated bar chart with percentages (3D style)
private val BarColors = listOf(
    Color(0xFFE53935), // Red
    Color(0xFFFFB74D), // Yellow
    Color(0xFF42A5F5), // Blue
    Color(0xFF26C6DA), // Cyan
    Color(0xFFFFCA28), // Yellow
    Color(0xFFEF5350), // Red
    Color(0xFFFF9800), // Orange
    Color(0xFF66BB6A)  // Green
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen6(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )
    // Generate 8 bar heights based on percentage (ascending trend)
    val barHeights = remember(percentage) {
        (0 until 8).map { index ->
            // Create ascending trend: each bar is a bit higher, scaled by percentage
            val baseHeight = (index + 1) / 8f
            val scaledHeight = baseHeight * animatedPercent
            scaledHeight.coerceIn(0f, 1f)
        }
    }
    val barHeightsAnimated = barHeights.mapIndexed { index, target ->
        animateFloatAsState(
            targetValue = target,
            animationSpec = tween(400 + index * 50),
            label = "bar$index"
        ).value
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Bar Chart",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$displayedPercent%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = zone.label.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = zone.color,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Bar chart with 8 bars
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .align(Alignment.BottomCenter),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        BarColors.forEachIndexed { index, color ->
                            val barHeight = barHeightsAnimated[index]
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.BottomCenter
                            ) {
                                // Animated bar (behind)
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height((barHeight.coerceIn(0.15f, 1f) * 200f).dp)
                                        .clip(RoundedCornerShape(4.dp, 4.dp, 0.dp, 0.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    color.copy(alpha = 0.95f),
                                                    color.copy(alpha = 0.75f)
                                                )
                                            )
                                        )
                                )
                                // Percentage cube/dice in front (overlapping)
                                Box(
                                    modifier = Modifier
                                        .offset(y = (-12).dp)
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White)
                                        .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "%",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE53935)
                                    )
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 7: Search progress bar (glossy horizontal progress)
private val SearchProgressGreen = Color(0xFF4CAF50)
private val SearchProgressGreenBright = Color(0xFF66BB6A)
private val SearchProgressTrack = Color(0xFFE0E0E0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen7(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val shimmerOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        shimmerOffset.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Search",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(20) + if (epc.length > 20) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "$displayedPercent",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = SearchProgressGreen,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = zone.label.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = zone.color,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(40.dp))
                // Glossy horizontal progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                ) {
                    // Track (unfilled portion - light gray)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        SearchProgressTrack.copy(alpha = 0.9f),
                                        SearchProgressTrack.copy(alpha = 0.7f)
                                    )
                                )
                            )
                            .drawWithContent {
                                drawContent()
                                // Top highlight on track
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            Color.Transparent
                                        ),
                                        start = Offset(0f, 0f),
                                        end = Offset(0f, size.height / 2f)
                                    )
                                )
                            }
                    )
                    // Filled portion (luminous green)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercent)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        SearchProgressGreen,
                                        SearchProgressGreenBright,
                                        SearchProgressGreen
                                    )
                                )
                            )
                            .drawWithContent {
                                drawContent()
                                // Glossy highlight streak on top
                                val highlightY = size.height * 0.25f
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.White.copy(alpha = 0.6f),
                                            Color.White.copy(alpha = 0.2f),
                                            Color.Transparent
                                        ),
                                        start = Offset(0f, highlightY),
                                        end = Offset(size.width, highlightY + size.height * 0.15f)
                                    )
                                )
                                // Shimmer effect
                                val shimmerX = size.width * shimmerOffset.value
                                drawRect(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.White.copy(alpha = 0.4f),
                                            Color.Transparent
                                        ),
                                        start = Offset(shimmerX - 40.dp.toPx(), 0f),
                                        end = Offset(shimmerX + 40.dp.toPx(), size.height)
                                    )
                                )
                            }
                    )
                    // Soft border/shadow
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(12.dp))
                            .drawWithContent {
                                drawContent()
                                drawRoundRect(
                                    color = Color(0xFFBDBDBD).copy(alpha = 0.3f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx()),
                                    style = Stroke(width = 0.5.dp.toPx())
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 8: Line graph with Fair/Good/Excellent zones
private val FairColor = Color(0xFFE53935) // Red
private val GoodColor = Color(0xFFFFB74D) // Yellow
private val ExcellentColor = Color(0xFF66BB6A) // Green

enum class SignalQuality(val label: String, val color: Color, val minPercent: Int, val maxPercent: Int) {
    FAIR("Fair", FairColor, 0, 33),
    GOOD("Good", GoodColor, 34, 66),
    EXCELLENT("Excellent", ExcellentColor, 67, 100)
}

fun getSignalQuality(percentage: Int): SignalQuality = when {
    percentage <= 33 -> SignalQuality.FAIR
    percentage <= 66 -> SignalQuality.GOOD
    else -> SignalQuality.EXCELLENT
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen8(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )
    val signalQuality = getSignalQuality(percentage)
    
    // Generate sample data points for the line graph (simulating signal over time)
    val graphPoints = remember(percentage) {
        (0..20).map { index ->
            val x = index / 20f
            // Create a fluctuating line that trends toward current percentage
            val baseY = 0.3f + (percentage / 100f) * 0.4f
            val variation = kotlin.math.sin(index * 0.5f) * 0.15f
            val y = (baseY + variation).coerceIn(0.1f, 0.9f)
            Offset(x, y)
        }
    }
    val animatedGraphPoints = graphPoints.mapIndexed { index, point ->
        val targetY = point.y
        val animatedY = animateFloatAsState(
            targetValue = targetY,
            animationSpec = tween(300 + index * 20),
            label = "point$index"
        ).value
        Offset(point.x, animatedY)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Signal Graph",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$displayedPercent%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = signalQuality.color
                )
                Text(
                    text = signalQuality.label.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = signalQuality.color,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Line graph with zones
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val graphWidth = size.width
                        val graphHeight = size.height
                        val padding = 8.dp.toPx()
                        val chartWidth = graphWidth - padding * 2
                        val chartHeight = graphHeight - padding * 2
                        val chartTop = padding
                        val chartLeft = padding
                        
                        // Draw zone backgrounds (Fair/Good/Excellent)
                        val fairHeight = chartHeight * 0.33f
                        val goodHeight = chartHeight * 0.33f
                        val excellentHeight = chartHeight * 0.34f
                        
                        // Fair zone (red) - bottom
                        drawRect(
                            color = FairColor.copy(alpha = 0.15f),
                            topLeft = Offset(chartLeft, chartTop + excellentHeight + goodHeight),
                            size = Size(chartWidth, fairHeight)
                        )
                        // Good zone (yellow) - middle
                        drawRect(
                            color = GoodColor.copy(alpha = 0.15f),
                            topLeft = Offset(chartLeft, chartTop + excellentHeight),
                            size = Size(chartWidth, goodHeight)
                        )
                        // Excellent zone (green) - top
                        drawRect(
                            color = ExcellentColor.copy(alpha = 0.15f),
                            topLeft = Offset(chartLeft, chartTop),
                            size = Size(chartWidth, excellentHeight)
                        )
                        
                        // Zone labels will be drawn outside the Canvas using Text composables
                        
                        // Draw line graph
                        if (animatedGraphPoints.size > 1) {
                            val path = androidx.compose.ui.graphics.Path().apply {
                                val firstPoint = animatedGraphPoints[0]
                                val x = chartLeft + firstPoint.x * chartWidth
                                val y = chartTop + (1f - firstPoint.y) * chartHeight
                                moveTo(x, y)
                                animatedGraphPoints.drop(1).forEach { point ->
                                    val px = chartLeft + point.x * chartWidth
                                    val py = chartTop + (1f - point.y) * chartHeight
                                    lineTo(px, py)
                                }
                            }
                            // Draw filled area under the line
                            path.lineTo(chartLeft + chartWidth, chartTop + chartHeight)
                            path.lineTo(chartLeft, chartTop + chartHeight)
                            path.close()
                            drawPath(
                                path = path,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        signalQuality.color.copy(alpha = 0.3f),
                                        signalQuality.color.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            // Draw the line
                            drawPath(
                                path = androidx.compose.ui.graphics.Path().apply {
                                    val firstPoint = animatedGraphPoints[0]
                                    val x = chartLeft + firstPoint.x * chartWidth
                                    val y = chartTop + (1f - firstPoint.y) * chartHeight
                                    moveTo(x, y)
                                    animatedGraphPoints.drop(1).forEach { point ->
                                        val px = chartLeft + point.x * chartWidth
                                        val py = chartTop + (1f - point.y) * chartHeight
                                        lineTo(px, py)
                                    }
                                },
                                color = signalQuality.color,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Draw points
                            animatedGraphPoints.forEachIndexed { index, point ->
                                val px = chartLeft + point.x * chartWidth
                                val py = chartTop + (1f - point.y) * chartHeight
                                drawCircle(
                                    color = signalQuality.color,
                                    radius = 4.dp.toPx(),
                                    center = Offset(px, py)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Zone indicators
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SignalQuality.entries.forEach { quality ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(quality.color)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = quality.label,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (quality == signalQuality) quality.color else Color(0xFF999999),
                                fontWeight = if (quality == signalQuality) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 9: Placeholder (can be customized later)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen9(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Screen 9",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = zone.color
                )
                Text(
                    text = epc,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 10: Bar chart with trend line
private val TrendLineColor = Color(0xFFFF9800) // Orange-yellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen10(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )
    val signalQuality = getSignalQuality(percentage)
    
    // Generate 6 bars with increasing heights (trending upward)
    val barHeights = remember(percentage) {
        (0..5).map { index ->
            // Create ascending trend: each bar is progressively higher
            val baseHeight = 0.2f + (index / 5f) * 0.6f
            val scaledHeight = baseHeight * (0.5f + animatedPercent * 0.5f)
            scaledHeight.coerceIn(0.15f, 0.95f)
        }
    }
    val barHeightsAnimated = barHeights.mapIndexed { index, target ->
        animateFloatAsState(
            targetValue = target,
            animationSpec = tween(400 + index * 50),
            label = "bar$index"
        ).value
    }
    
    // Trend line points (upward slope)
    val trendLinePoints = remember(percentage) {
        (0..5).map { index ->
            val x = index / 5f
            val baseY = 0.25f + (index / 5f) * 0.5f
            val scaledY = baseY * (0.6f + animatedPercent * 0.4f)
            Offset(x, scaledY.coerceIn(0.2f, 0.9f))
        }
    }
    val animatedTrendPoints = trendLinePoints.mapIndexed { index, point ->
        val targetY = point.y
        val animatedY = animateFloatAsState(
            targetValue = targetY,
            animationSpec = tween(500 + index * 30),
            label = "trend$index"
        ).value
        Offset(point.x, animatedY)
    }
    
    // Bar colors (varied)
    val barColors = listOf(
        Color(0xFF42A5F5), // Blue
        Color(0xFFE53935), // Red
        Color(0xFF9E9E9E), // Gray
        Color(0xFFFFB74D), // Orange-yellow
        Color(0xFF5C6BC0), // Medium blue
        signalQuality.color // Green (or current quality color)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Growth Chart",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$displayedPercent%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = signalQuality.color
                )
                Text(
                    text = signalQuality.label.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = signalQuality.color,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Bar chart with trend line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(20.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val chartWidth = size.width
                        val chartHeight = size.height
                        val padding = 12.dp.toPx()
                        val barAreaWidth = chartWidth - padding * 2
                        val barAreaHeight = chartHeight - padding * 2
                        val chartTop = padding
                        val chartLeft = padding
                        val barWidth = (barAreaWidth / 6f) * 0.6f
                        val barSpacing = (barAreaWidth / 6f) * 0.4f
                        
                        // Draw bars
                        barHeightsAnimated.forEachIndexed { index, height ->
                            val x = chartLeft + index * (barWidth + barSpacing) + barSpacing / 2
                            val barHeight = height * barAreaHeight
                            val barY = chartTop + barAreaHeight - barHeight
                            
                            // Bar shadow
                            drawRect(
                                color = Color(0xFFE0E0E0),
                                topLeft = Offset(x + 2.dp.toPx(), barY + 2.dp.toPx()),
                                size = Size(barWidth, barHeight)
                            )
                            // Bar
                            drawRect(
                                color = barColors[index],
                                topLeft = Offset(x, barY),
                                size = Size(barWidth, barHeight)
                            )
                            // Bar highlight
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                ),
                                topLeft = Offset(x, barY),
                                size = Size(barWidth, barHeight * 0.3f)
                            )
                        }
                        
                        // Draw trend line (bright orange-yellow)
                        if (animatedTrendPoints.size > 1) {
                            val trendPath = Path().apply {
                                val firstPoint = animatedTrendPoints[0]
                                val px = chartLeft + firstPoint.x * barAreaWidth
                                val py = chartTop + (1f - firstPoint.y) * barAreaHeight
                                moveTo(px, py)
                                animatedTrendPoints.drop(1).forEach { point ->
                                    val px2 = chartLeft + point.x * barAreaWidth
                                    val py2 = chartTop + (1f - point.y) * barAreaHeight
                                    lineTo(px2, py2)
                                }
                            }
                            // Draw trend line
                            drawPath(
                                path = trendPath,
                                color = TrendLineColor,
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 11: Light bulb with liquid fill (water shake animation)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen11(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )
    
    // Same as Screen 12: 10–20 red, 70–100 green, else yellow (fill by percentage like Screen 12)
    val liquidColor = when {
        percentage in 10..20 -> FairColor   // Red
        percentage in 70..100 -> ExcellentColor // Green
        else -> GoodColor                   // Yellow
    }
    val qualityLabel = when {
        percentage in 10..20 -> "FAIR"
        percentage in 70..100 -> "EXCELLENT"
        else -> "GOOD"
    }

    // Water shake (subtle bulb sway + liquid surface wave)
    val wavePhase = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        wavePhase.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        shakeOffset.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 700
                    0f at 0
                    0.04f at 175
                    -0.04f at 350
                    0.02f at 525
                    -0.02f at 700
                },
                repeatMode = RepeatMode.Restart
            )
        )
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F5F5),
                        Color(0xFFE8E8E8)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Light Bulb",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$displayedPercent",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = liquidColor
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = liquidColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = qualityLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = liquidColor,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Light bulb: classic shape + water shaking
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .offset(
                            x = (shakeOffset.value * 5f).dp,
                            y = (kotlin.math.sin(shakeOffset.value * kotlin.math.PI * 2).toFloat() * 2.5f).dp
                        )
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val centerX = size.width / 2f
                        val bulbRadius = size.width * 0.35f
                        val bulbCenterY = size.height * 0.4f
                        val baseY = size.height * 0.75f
                        val baseWidth = size.width * 0.24f
                        val baseHeight = size.height * 0.16f
                        
                        // Clear glass dome (subtle reflection)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.15f),
                                    Color.White.copy(alpha = 0.05f),
                                    Color.Transparent
                                )
                            ),
                            radius = bulbRadius * 1.12f,
                            center = Offset(centerX, bulbCenterY)
                        )
                        
                        val bulbBottom = bulbCenterY + bulbRadius
                        val bulbHeight = bulbRadius * 2f
                        val fillFraction = (percentage / 100f).coerceIn(0f, 1f)
                        val fillHeight = bulbHeight * fillFraction
                        val liquidTopY = bulbBottom - fillHeight
                            
                        if (fillFraction > 0.002f) {
                            val dy = liquidTopY - bulbCenterY
                            val normalizedDy = (dy / bulbRadius).coerceIn(-1f, 1f)
                            val rightAngleRad = kotlin.math.asin(normalizedDy.toDouble()).toFloat()
                            val leftAngleRad = kotlin.math.PI.toFloat() - rightAngleRad
                            val bulbRect = androidx.compose.ui.geometry.Rect(
                                offset = Offset(centerX - bulbRadius, bulbCenterY - bulbRadius),
                                size = Size(bulbRadius * 2f, bulbRadius * 2f)
                            )
                            val rightX = centerX + kotlin.math.cos(rightAngleRad.toDouble()).toFloat() * bulbRadius
                            val leftX = centerX + kotlin.math.cos(leftAngleRad.toDouble()).toFloat() * bulbRadius

                            // Wavy liquid surface (like water shaking)
                            val steps = 56
                            val waveFrequency = 2.3f
                            val baseAmp = 2.2.dp.toPx()
                            val shakeAmp = (kotlin.math.abs(shakeOffset.value) * 1.3f).dp.toPx()
                            val waveAmp = baseAmp + shakeAmp
                            val phase = wavePhase.value * (kotlin.math.PI.toFloat() * 2f)

                            // Liquid path: bottom -> right arc -> wavy surface (right->left) -> left arc -> close
                            val liquidPath = Path().apply {
                                fillType = PathFillType.NonZero
                                moveTo(centerX, bulbBottom)
                                val toRightSweepDeg = (rightAngleRad - kotlin.math.PI.toFloat() / 2f) * 180f / kotlin.math.PI.toFloat()
                                arcTo(rect = bulbRect, startAngleDegrees = 90f, sweepAngleDegrees = toRightSweepDeg, forceMoveTo = false)

                                // Draw from right intersection to left intersection with a wave
                                for (i in 0..steps) {
                                    val t = i / steps.toFloat()
                                    val x = rightX + (leftX - rightX) * t
                                    val wave = kotlin.math.sin(t * waveFrequency * kotlin.math.PI.toFloat() * 2f + phase) * waveAmp
                                    lineTo(x, liquidTopY + wave)
                                }

                                val toBottomSweepDeg = (kotlin.math.PI.toFloat() / 2f - leftAngleRad) * 180f / kotlin.math.PI.toFloat()
                                arcTo(rect = bulbRect, startAngleDegrees = leftAngleRad * 180f / kotlin.math.PI.toFloat(), sweepAngleDegrees = toBottomSweepDeg, forceMoveTo = false)
                                close()
                            }
                            // Opaque fill (vibrant color like reference)
                            drawPath(
                                path = liquidPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        liquidColor.copy(alpha = 0.98f),
                                        liquidColor
                                    ),
                                    startY = liquidTopY,
                                    endY = bulbBottom
                                )
                            )
                        }
                        
                        // Glass outline (thin, neutral)
                        drawCircle(
                            color = Color(0xFFB0BEC5).copy(alpha = 0.5f),
                            radius = bulbRadius,
                            center = Offset(centerX, bulbCenterY),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        
                        // Metallic screw base (silver/chrome)
                        drawRoundRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFE0E0E0),
                                    Color(0xFFBDBDBD),
                                    Color(0xFF9E9E9E)
                                ),
                                startY = baseY,
                                endY = baseY + baseHeight
                            ),
                            topLeft = Offset(centerX - baseWidth / 2f, baseY),
                            size = Size(baseWidth, baseHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(5.dp.toPx())
                        )
                        for (i in 0..3) {
                            val threadY = baseY + baseHeight * (i + 1) / 5f
                            drawLine(
                                color = Color(0xFF757575),
                                start = Offset(centerX - baseWidth / 2f + 2.dp.toPx(), threadY),
                                end = Offset(centerX + baseWidth / 2f - 2.dp.toPx(), threadY),
                                strokeWidth = 1.5.dp.toPx()
                            )
                        }
                        // Black contact at bottom
                        drawCircle(
                            color = Color(0xFF212121),
                            radius = 4.dp.toPx(),
                            center = Offset(centerX, baseY + baseHeight + 4.dp.toPx())
                        )
                        
                        // Glass highlight (glossy reflection on dome)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.1f),
                                    Color.Transparent
                                )
                            ),
                            radius = bulbRadius * 0.35f,
                            center = Offset(centerX - bulbRadius * 0.35f, bulbCenterY - bulbRadius * 0.35f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

// Screen 12: Glass tube containers with liquid fill
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentScreen12(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    val displayedPercent by animateIntAsState(
        targetValue = percentage,
        animationSpec = tween(500),
        label = "pct"
    )
    
    // Liquid color: 10-20% red, 70-100% green, else yellow
    val liquidColor = when {
        percentage in 10..20 -> FairColor // Red
        percentage in 70..100 -> ExcellentColor // Green
        else -> GoodColor // Yellow
    }
    val qualityLabel = when {
        percentage in 10..20 -> "FAIR"
        percentage in 70..100 -> "EXCELLENT"
        else -> "GOOD"
    }
    
    // Wave animation for liquid surface
    val wavePhase = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        wavePhase.animateTo(
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1500, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
    }
    
    val animatedFillPercent by animateFloatAsState(
        targetValue = animatedPercent,
        animationSpec = tween(600),
        label = "fill"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF5F5F5),
                        Color(0xFFE8E8E8)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Glass Tube",
                        color = Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF1A1A1A)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF1A1A1A)
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "$displayedPercent",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = liquidColor
                )
                Text(
                    text = "%",
                    style = MaterialTheme.typography.headlineMedium,
                    color = liquidColor,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = qualityLabel,
                    style = MaterialTheme.typography.titleMedium,
                    color = liquidColor,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(32.dp))
                // Glass tube container
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(320.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val tubeWidth = size.width
                        val tubeHeight = size.height
                        val tubeTop = 0f
                        val tubeBottom = tubeHeight
                        val cornerRadius = 8.dp.toPx()
                        
                        // Tube shadow
                        drawRoundRect(
                            color = Color(0xFFE0E0E0),
                            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
                            size = Size(tubeWidth, tubeHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                        )
                        
                        // Glass tube (transparent top section)
                        val fillHeight = tubeHeight * animatedFillPercent
                        val liquidTop = tubeBottom - fillHeight
                        
                        // Empty glass section (top)
                        if (animatedFillPercent < 1f) {
                            val emptyHeight = liquidTop - tubeTop
                            val emptyPath = Path().apply {
                                // Top-left rounded corner
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        offset = Offset(0f, tubeTop),
                                        size = Size(cornerRadius * 2, cornerRadius * 2)
                                    ),
                                    startAngleDegrees = 180f,
                                    sweepAngleDegrees = 90f,
                                    forceMoveTo = false
                                )
                                // Top edge
                                lineTo(tubeWidth - cornerRadius, tubeTop)
                                // Top-right rounded corner
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        offset = Offset(tubeWidth - cornerRadius * 2, tubeTop),
                                        size = Size(cornerRadius * 2, cornerRadius * 2)
                                    ),
                                    startAngleDegrees = 270f,
                                    sweepAngleDegrees = 90f,
                                    forceMoveTo = false
                                )
                                // Right edge
                                lineTo(tubeWidth, liquidTop)
                                // Bottom edge (straight)
                                lineTo(0f, liquidTop)
                                close()
                            }
                            drawPath(
                                path = emptyPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFFE8E8E8).copy(alpha = 0.6f),
                                        Color(0xFFF5F5F5).copy(alpha = 0.4f)
                                    )
                                )
                            )
                            // Glass border (top section)
                            drawPath(
                                path = emptyPath,
                                color = Color(0xFFBDBDBD).copy(alpha = 0.5f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        
                        // Liquid fill section (bottom)
                        if (animatedFillPercent > 0f) {
                            // Wave effect on liquid surface
                            val waveAmplitude = 3.dp.toPx()
                            val waveFrequency = 2f
                            val liquidPath = Path().apply {
                                val steps = 40
                                moveTo(0f, liquidTop)
                                for (i in 0..steps) {
                                    val x = tubeWidth * (i / steps.toFloat())
                                    val wave = (kotlin.math.sin((x / tubeWidth) * waveFrequency * kotlin.math.PI + wavePhase.value * 2 * kotlin.math.PI) * waveAmplitude).toFloat()
                                    lineTo(x, liquidTop + wave)
                                }
                                lineTo(tubeWidth, tubeBottom)
                                lineTo(0f, tubeBottom)
                                close()
                            }
                            
                            // Draw liquid fill with gradient
                            drawPath(
                                path = liquidPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        liquidColor.copy(alpha = 0.95f),
                                        liquidColor.copy(alpha = 0.85f),
                                        liquidColor.copy(alpha = 0.9f)
                                    ),
                                    startY = liquidTop,
                                    endY = tubeBottom
                                )
                            )
                            
                            // Liquid highlight/shine
                            val highlightPath = Path().apply {
                                val steps = 20
                                val startX = tubeWidth * 0.2f
                                val endX = tubeWidth * 0.5f
                                moveTo(startX, liquidTop)
                                for (i in 0..steps) {
                                    val x = startX + (endX - startX) * (i / steps.toFloat())
                                    val wave = (kotlin.math.sin((x / tubeWidth) * waveFrequency * kotlin.math.PI + wavePhase.value * 2 * kotlin.math.PI) * waveAmplitude).toFloat()
                                    lineTo(x, liquidTop + wave)
                                }
                                lineTo(endX, tubeBottom)
                                lineTo(startX, tubeBottom)
                                close()
                            }
                            drawPath(
                                path = highlightPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.3f),
                                        Color.Transparent
                                    )
                                )
                            )
                            
                            // Liquid border (bottom section)
                            val liquidBorderPath = Path().apply {
                                // Top edge (straight)
                                moveTo(0f, liquidTop)
                                lineTo(tubeWidth, liquidTop)
                                // Right edge
                                lineTo(tubeWidth, tubeBottom - cornerRadius)
                                // Bottom-right rounded corner
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        offset = Offset(tubeWidth - cornerRadius * 2, tubeBottom - cornerRadius * 2),
                                        size = Size(cornerRadius * 2, cornerRadius * 2)
                                    ),
                                    startAngleDegrees = 0f,
                                    sweepAngleDegrees = 90f,
                                    forceMoveTo = false
                                )
                                // Bottom edge
                                lineTo(cornerRadius, tubeBottom)
                                // Bottom-left rounded corner
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(
                                        offset = Offset(0f, tubeBottom - cornerRadius * 2),
                                        size = Size(cornerRadius * 2, cornerRadius * 2)
                                    ),
                                    startAngleDegrees = 90f,
                                    sweepAngleDegrees = 90f,
                                    forceMoveTo = false
                                )
                                // Left edge
                                lineTo(0f, liquidTop)
                                close()
                            }
                            drawPath(
                                path = liquidBorderPath,
                                color = liquidColor.copy(alpha = 0.8f),
                                style = Stroke(width = 2.dp.toPx())
                            )
                        }
                        
                        // Outer glass border
                        drawRoundRect(
                            color = Color(0xFF9E9E9E).copy(alpha = 0.6f),
                            topLeft = Offset(0f, tubeTop),
                            size = Size(tubeWidth, tubeHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
                            style = Stroke(width = 2.dp.toPx())
                        )
                        
                        // Glass reflection/highlight
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    Color.Transparent
                                )
                            ),
                            topLeft = Offset(0f, tubeTop),
                            size = Size(tubeWidth * 0.3f, tubeHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                // Colored shadow below tube
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    liquidColor.copy(alpha = 0.3f),
                                    liquidColor.copy(alpha = 0.1f),
                                    liquidColor.copy(alpha = 0.3f)
                                )
                            )
                        )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF666666)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentGauge(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    glowAlpha: Float,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Subtle radial glow behind gauge
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 140.dp)
                .size(280.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            zone.glowColor.copy(alpha = glowAlpha),
                            Color.Transparent
                        )
                    ),
                    CircleShape
                )
        )
    Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace tag",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // EPC chip
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.06f)),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color.White.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "TAGGED EPC",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.5f),
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = epc,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.95f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(28.dp))

                // Main gauge
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(300.dp)
                ) {
                    // Outer glow ring
                    Canvas(modifier = Modifier.size(300.dp)) {
                        val strokeWidth = 2.dp.toPx()
                        val radius = (size.minDimension / 2f) - 24.dp.toPx()
                        val center = Offset(size.width / 2f, size.height / 2f)
                        drawArc(
                            color = zone.color.copy(alpha = 0.2f),
                            startAngle = 135f,
                            sweepAngle = 270f * animatedPercent,
                            useCenter = false,
                            topLeft = Offset(center.x - radius, center.y - radius),
                            size = Size(radius * 2, radius * 2),
                            style = Stroke(width = 24.dp.toPx() + strokeWidth, cap = StrokeCap.Round)
                        )
                    }
                    Card(
                        modifier = Modifier.size(280.dp),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF16162A)),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color.White.copy(alpha = 0.08f)
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeWidth = 18.dp.toPx()
                                val radius = (size.minDimension / 2f) - strokeWidth - 4.dp.toPx()
                                val center = Offset(size.width / 2f, size.height / 2f)

                                // Track
                                drawArc(
                                    color = Color.White.copy(alpha = 0.08f),
                                    startAngle = 135f,
                                    sweepAngle = 270f,
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                                // Segment ticks (optional subtle marks)
                                for (i in 0..9) {
                                    val angle = 135f + (270f * i / 10f)
                                    rotate(angle, center) {
                                        drawLine(
                                            color = Color.White.copy(alpha = 0.06f),
                                            start = Offset(center.x, center.y - radius - strokeWidth / 2),
                                            end = Offset(center.x, center.y - radius + strokeWidth / 2),
                                            strokeWidth = 1.dp.toPx()
                                        )
                                    }
                                }
                                // Value arc with gradient
                                drawArc(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFFE53935),
                                            zone.color,
                                            zone.color
                                        )
                                    ),
                                    startAngle = 135f,
                                    sweepAngle = 270f * animatedPercent,
                                    useCenter = false,
                                    topLeft = Offset(center.x - radius, center.y - radius),
                                    size = Size(radius * 2, radius * 2),
                                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$percentage",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    letterSpacing = (-1).sp
                                )
                                Text(
                                    text = "%",
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = zone.label.uppercase(),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = zone.color,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.5.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Zone pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(ProximityZone.FAR, ProximityZone.CLOSE, ProximityZone.NEAR).forEach { z ->
                        val active = z == zone
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (active) z.color.copy(alpha = 0.25f)
                                    else Color.White.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (active) 1.5.dp else 0.5.dp,
                                    color = if (active) z.color else Color.White.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "${z.emoji} ${z.label}",
                                color = if (active) z.color else Color.White.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progress bar
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(animatedPercent)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(4.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(zone.color.copy(alpha = 0.9f), zone.color)
                                    )
                                )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.45f)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Move the tag toward the reader to increase signal strength.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.35f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TraceContentSignal(
    epc: String,
    percentage: Int,
    animatedPercent: Float,
    zone: ProximityZone,
    currentTrace: TracedTagInfo?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A1628),
                        Color(0xFF0D2137),
                        Color(0xFF061018)
                    )
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Trace · Signal",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White.copy(alpha = 0.9f)
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                )
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = epc.take(18) + if (epc.length > 18) "…" else "",
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = zone.color
                )
                Text(
                    text = zone.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = zone.color.copy(alpha = 0.9f),
                    modifier = Modifier.padding(top = 4.dp)
                )
                Spacer(modifier = Modifier.height(40.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    listOf(0, 1, 2, 3, 4, 5, 6).forEach { index ->
                        val barFill = (animatedPercent * 7 - index).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                                .height(120.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.08f))
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(barFill)
                                    .align(Alignment.BottomCenter)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                zone.color,
                                                zone.color.copy(alpha = 0.6f)
                                            )
                                        )
                                    )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf(ProximityZone.FAR, ProximityZone.CLOSE, ProximityZone.NEAR).forEach { z ->
                        val active = z == zone
                        Text(
                            text = z.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (active) z.color else Color.White.copy(alpha = 0.4f),
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedPercent)
                            .align(Alignment.CenterStart)
                            .clip(RoundedCornerShape(4.dp))
                            .background(zone.color)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "RSSI ${currentTrace?.scaledRssi ?: "—"}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.45f)
                )
            }
        }
    }
}

