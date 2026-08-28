package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBg
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBorder
import com.example.ui.theme.NeonTextMuted
import com.example.ui.theme.NeonWhite
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    onLoadingFinished: () -> Unit
) {
    val progress = remember { Animatable(0f) }
    var statusText by remember { mutableStateOf("INITIALIZING NEON MATRIX...") }

    val infiniteTransition = rememberInfiniteTransition(label = "loading_anims")
    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ring_rot"
    )

    val logoPulse by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logo_pulse"
    )

    LaunchedEffect(Unit) {
        // Animate progress with cyber status updates
        progress.animateTo(
            targetValue = 0.30f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
        statusText = "CALIBRATING TACTICAL AI CORES..."

        progress.animateTo(
            targetValue = 0.65f,
            animationSpec = tween(600, easing = LinearEasing)
        )
        statusText = "SYNCHRONIZING CYBER THEMES..."

        progress.animateTo(
            targetValue = 0.95f,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
        statusText = "SYSTEMS READY - ENTERING GRID!"

        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(250, easing = LinearEasing)
        )
        delay(250)
        onLoadingFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF220A45),
                        NeonDarkBg,
                        Color(0xFF06020E)
                    ),
                    radius = 900f
                )
            )
            .testTag("loading_screen"),
        contentAlignment = Alignment.Center
    ) {
        // Background Matrix Grid Lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 40.dp.toPx()
            val w = size.width
            val h = size.height

            for (x in 0..(w / step).toInt()) {
                drawLine(
                    color = NeonPurpleBorder.copy(alpha = 0.12f),
                    start = Offset(x * step, 0f),
                    end = Offset(x * step, h),
                    strokeWidth = 1f
                )
            }
            for (y in 0..(h / step).toInt()) {
                drawLine(
                    color = NeonPurpleBorder.copy(alpha = 0.12f),
                    start = Offset(0f, y * step),
                    end = Offset(w, y * step),
                    strokeWidth = 1f
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(28.dp)
        ) {
            // Cyber Animated Icon Ring
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .scale(logoPulse),
                contentAlignment = Alignment.Center
            ) {
                // Outer rotating cyber ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(ringRotation)
                ) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonCyan,
                                NeonPurple,
                                NeonCoral,
                                NeonGold,
                                NeonCyan
                            )
                        ),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner pulsing glowing circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(
                                    Color(0xFF3B156E),
                                    Color(0xFF15082E)
                                )
                            )
                        )
                        .border(1.5.dp, NeonCyan.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Futuristic Dual O & X Center
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "O",
                            color = NeonCyan,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "X",
                            color = NeonCoral,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Game Title
            Text(
                text = "TIC TAC TOE",
                color = NeonWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "CYBER GLOW EDITION",
                color = NeonCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Cyber Progress Bar
            val percent = (progress.value * 100).toInt()
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF1E0C38))
                    .border(1.dp, NeonPurpleBorder.copy(alpha = 0.8f), RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.value)
                        .height(10.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(
                                    NeonPurple,
                                    NeonCyan,
                                    NeonGold
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress percentage & Status text
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusText,
                    color = NeonTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "$percent%",
                    color = NeonGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
