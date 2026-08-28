package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.GameMode
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonWhite

@Composable
fun NeonTargetIndicator(
    mode: GameMode,
    isWon: Boolean,
    modifier: Modifier = Modifier
) {
    val count = mode.targetToWin
    val infiniteTransition = rememberInfiniteTransition(label = "target_glow")

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val widthDp = (count * 38 + 50).dp

    Canvas(
        modifier = modifier
            .width(widthDp)
            .height(44.dp)
    ) {
        val centerY = size.height / 2f
        val startX = 20f
        val endX = size.width - 20f
        val totalSpan = endX - startX
        val step = totalSpan / (count - 1)
        val radius = 13f

        // Horizontal connecting laser line
        // 1. Soft glow
        drawLine(
            color = NeonCyan.copy(alpha = 0.35f * pulseGlow),
            start = Offset(startX - 15f, centerY),
            end = Offset(endX + 15f, centerY),
            strokeWidth = 9f,
            cap = StrokeCap.Round
        )
        // 2. Focused cyan line
        drawLine(
            color = NeonCyanLight.copy(alpha = 0.85f * pulseGlow),
            start = Offset(startX - 15f, centerY),
            end = Offset(endX + 15f, centerY),
            strokeWidth = 3.5f,
            cap = StrokeCap.Round
        )
        // 3. Crisp white center
        drawLine(
            color = NeonWhite.copy(alpha = if (isWon) 1f else 0.85f),
            start = Offset(startX - 15f, centerY),
            end = Offset(endX + 15f, centerY),
            strokeWidth = 1.8f,
            cap = StrokeCap.Round
        )

        // Connected Rings
        for (i in 0 until count) {
            val cx = startX + i * step
            val center = Offset(cx, centerY)

            // Clear behind the ring center so line doesn't muddy inside if needed, or overlay rings
            // Outer glow
            drawCircle(
                color = NeonCyan.copy(alpha = 0.4f * pulseGlow),
                radius = radius + 4f,
                center = center,
                style = Stroke(width = 7f)
            )
            // Mid vibrant ring
            drawCircle(
                color = NeonCyanLight,
                radius = radius,
                center = center,
                style = Stroke(width = 3.5f)
            )
            // Inner crisp white ring
            drawCircle(
                color = NeonWhite,
                radius = radius,
                center = center,
                style = Stroke(width = 1.6f)
            )
        }
    }
}
