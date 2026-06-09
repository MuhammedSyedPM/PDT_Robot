package com.syed.jetpacktwo.ui.splash

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.syed.jetpacktwo.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    // Fade in animation for the image
    val alphaAnim by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    // Original High-Tech Animations for the right side
    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(2000, easing = LinearOutSlowInEasing)
    )

    val textOffsetY by animateDpAsState(
        targetValue = if (startAnimation) 0.dp else 40.dp,
        animationSpec = tween(1000, delayMillis = 500, easing = FastOutSlowInEasing)
    )

    val glowAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 0.3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(key1 = true) {
        delay(300) // Small initial pause
        startAnimation = true
        delay(3200) // 3.5 total seconds
        onTimeout()
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0C0F17)) // Deep Midnight
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
                    Image(
                        painter = painterResource(id = R.drawable.splsh_image),
                        contentDescription = "Splash Image",
                        contentScale = ContentScale.Crop, 
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alphaAnim),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                            androidx.compose.ui.graphics.BlendMode.SrcAtop
                        )
                    )
                }

                // Right side: 40% Original High-Tech Animation
                Box(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    // Subtle Background Glow
                    Box(
                        modifier = Modifier
                            .size(400.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // High-Tech Animated Icon
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale)
                                .rotate(rotation)
                        ) {
                            // Outer ring
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {}
                            
                            // Inner Bot Icon
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PrecisionManufacturing,
                                    contentDescription = null,
                                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Cinematic Text Reveal
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.offset(y = textOffsetY)
                        ) {
                            Text(
                                text = "STOCK EYE",
                                color = Color.White,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = if (startAnimation) 10.sp else 2.sp,
                                modifier = Modifier.animateContentSize()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "TECHNOWAVE GROUP",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.alpha(if (startAnimation) 1f else 0f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(60.dp))
                        
                        // Bottom Support Info (Professional look)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(if (startAnimation) 1f else 0f)
                        ) {
                            Text(
                                text = "SUPPORTED HARDWARE",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "IMPINJ • ZEBRA • NORDIC • CHAINWAY",
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(2.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        } else {
            // Mobile Layout
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.splsh_image),
                    contentDescription = "Splash Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alphaAnim),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        androidx.compose.ui.graphics.BlendMode.SrcAtop
                    )
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.7f))
                )

                // Render the same high-tech layout over the image for mobile
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(400.dp)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = glowAlpha),
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(140.dp)
                                .scale(scale)
                                .rotate(rotation)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color.Transparent,
                                border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                            ) {}
                            
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PrecisionManufacturing,
                                    contentDescription = null,
                                    modifier = Modifier.padding(20.dp).fillMaxSize(),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.offset(y = textOffsetY)
                        ) {
                            Text(
                                text = "STOCK EYE",
                                color = Color.White,
                                fontSize = 40.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = if (startAnimation) 8.sp else 2.sp,
                                modifier = Modifier.animateContentSize()
                            )
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text(
                                text = "TECHNOWAVE GROUP",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                                modifier = Modifier.alpha(if (startAnimation) 1f else 0f)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(60.dp))
                        
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.alpha(if (startAnimation) 1f else 0f)
                        ) {
                            Text(
                                text = "SUPPORTED HARDWARE",
                                color = Color.White.copy(alpha = 0.3f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "IMPINJ • ZEBRA\nNORDIC • CHAINWAY",
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .width(120.dp)
                                    .height(2.dp),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = Color.White.copy(alpha = 0.1f)
                            )
                        }
                    }
                }
            }
        }
    }
}
