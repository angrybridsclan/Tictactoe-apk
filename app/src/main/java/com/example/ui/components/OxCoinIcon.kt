package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGoldBorder
import com.example.ui.theme.NeonWhite
import kotlin.math.cos
import kotlin.math.sin

/**
 * Ultra-realistic 3D Gold Coin with metallic bezel, embedded glowing OX emblem,
 * and animated specular star shine.
 */
@Composable
fun OxCoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 22.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "coin_specular_shine")
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shine_progress"
    )

    Box(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "OX Gold Coin" },
        contentAlignment = Alignment.Center
    ) {
        // 1. Realistic 3D Golden Medallion Render
        Image(
            painter = painterResource(id = R.drawable.img_gold_coin_1788072234836),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .shadow(elevation = 2.dp, shape = CircleShape, spotColor = NeonGold)
        )

        // 2. Realistic Specular Golden Sheen & Glint Overlay
        Canvas(modifier = Modifier.size(size)) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            // Outer Hairline Ring with Sweep Gradient for extra metallic pop
            val rimBrush = Brush.sweepGradient(
                colors = listOf(
                    Color(0xFFFFF7C2),
                    NeonGold,
                    Color(0xFFFFB300),
                    Color(0xFFFFE082),
                    Color(0xFFB87800),
                    Color(0xFFFFF7C2)
                ),
                center = center
            )
            drawCircle(
                brush = rimBrush,
                radius = radius - 0.5f,
                center = center,
                style = Stroke(width = maxOf(1f, radius * 0.08f))
            )

            // Animated Diamond Glint Sparkle traversing top rim
            val glintAngle = (shineProgress * 2.0 * Math.PI) - Math.PI / 4.0
            val glintRadiusFromCenter = radius * 0.65f
            val glintX = center.x + glintRadiusFromCenter * cos(glintAngle).toFloat()
            val glintY = center.y + glintRadiusFromCenter * sin(glintAngle).toFloat()

            // Subtle Glint Star
            val glintAlpha = (sin(shineProgress * Math.PI).toFloat()).coerceIn(0f, 1f)
            if (glintAlpha > 0.1f) {
                val starSize = radius * 0.28f * glintAlpha
                val starPath = Path().apply {
                    moveTo(glintX, glintY - starSize)
                    lineTo(glintX + starSize * 0.3f, glintY)
                    lineTo(glintX + starSize, glintY)
                    lineTo(glintX + starSize * 0.3f, glintY + starSize * 0.3f)
                    lineTo(glintX, glintY + starSize)
                    lineTo(glintX - starSize * 0.3f, glintY + starSize * 0.3f)
                    lineTo(glintX - starSize, glintY)
                    lineTo(glintX - starSize * 0.3f, glintY - starSize * 0.3f)
                    close()
                }
                drawPath(
                    path = starPath,
                    color = NeonWhite.copy(alpha = glintAlpha * 0.9f)
                )
                drawCircle(
                    color = NeonGold.copy(alpha = glintAlpha * 0.5f),
                    radius = starSize * 0.8f,
                    center = Offset(glintX, glintY)
                )
            }
        }
    }
}
