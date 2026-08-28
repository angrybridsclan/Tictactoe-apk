package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBg
import com.example.ui.theme.NeonPurple

@Composable
fun NeonGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "neon_bg_glow")

    val orb1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1_alpha"
    )

    val orb2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.20f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2_alpha"
    )

    val orbOffsetY by infiniteTransition.animateFloat(
        initialValue = -30f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_offset"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Base deep dark cosmic gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF070314),
                        Color(0xFF0F0728),
                        Color(0xFF160938),
                        Color(0xFF090318)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Top ambient blue/cyan glowing orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = orb1Alpha),
                        NeonCyan.copy(alpha = orb1Alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.35f, h * 0.22f + orbOffsetY),
                    radius = w * 0.7f
                ),
                radius = w * 0.7f,
                center = Offset(w * 0.35f, h * 0.22f + orbOffsetY)
            )

            // Center-right purple neon orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = orb2Alpha),
                        NeonPurple.copy(alpha = orb2Alpha * 0.3f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.8f, h * 0.5f - orbOffsetY),
                    radius = w * 0.8f
                ),
                radius = w * 0.8f,
                center = Offset(w * 0.8f, h * 0.5f - orbOffsetY)
            )

            // Bottom subtle coral/magenta ambient glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCoral.copy(alpha = orb1Alpha * 0.35f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.2f, h * 0.85f),
                    radius = w * 0.65f
                ),
                radius = w * 0.65f,
                center = Offset(w * 0.2f, h * 0.85f)
            )
        }

        content()
    }
}
