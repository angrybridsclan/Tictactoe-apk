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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkBg
import com.example.ui.theme.NeonPurple
import kotlin.math.sin
import kotlin.random.Random

private data class CosmicStar(
    val xPct: Float,
    val yPct: Float,
    val baseRadius: Float,
    val color: Color,
    val twinklePhase: Float,
    val hasSpikes: Boolean = false
)

@Composable
fun NeonGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "galaxy_bg_anims")

    // Global twinkling animation phase
    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28318f, // 2 * PI
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_phase"
    )

    // Nebula pulsing alpha
    val nebulaAlpha1 by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.48f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula1_alpha"
    )

    val nebulaAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.42f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula2_alpha"
    )

    // Shooting star animation (0f..1.4f)
    val meteorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meteor_progress"
    )

    // Pre-calculate deterministic cosmic stars (120 stars)
    val cosmicStars = remember {
        val random = Random(42) // Fixed seed for stable galaxy map
        List(120) { index ->
            val colorChoice = random.nextFloat()
            val color = when {
                colorChoice < 0.50f -> Color(0xFFFFFFFF) // Crisp white starlight
                colorChoice < 0.75f -> Color(0xFF80DEEA) // Cyan starlight
                colorChoice < 0.90f -> Color(0xFFE1BEE7) // Cosmic purple starlight
                else -> Color(0xFFFFE082)                // Warm golden star
            }
            val isMajor = index % 18 == 0
            val radius = if (isMajor) random.nextFloat() * 1.5f + 2.2f else random.nextFloat() * 1.2f + 0.8f

            CosmicStar(
                xPct = random.nextFloat(),
                yPct = random.nextFloat(),
                baseRadius = radius,
                color = color,
                twinklePhase = random.nextFloat() * 6.28f,
                hasSpikes = isMajor
            )
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep Celestial Space Void Base Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF03010B), // Deep void black
                        Color(0xFF0B041C), // Deep cosmic purple
                        Color(0xFF140733), // Galactic center indigo
                        Color(0xFF1B0A3D), // Nebula purple core
                        Color(0xFF0A031E), // Deep cosmos
                        Color(0xFF04010E)  // Starlight floor
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // 2. Cosmic Nebula Clouds & Spiral Glows
            // Nebula Core 1: Electric Cyan & Indigo galaxy arm (top left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = nebulaAlpha1),
                        Color(0xFF1A387A).copy(alpha = nebulaAlpha1 * 0.6f),
                        Color(0xFF0C1942).copy(alpha = nebulaAlpha1 * 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.28f, h * 0.25f),
                    radius = w * 0.85f
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.28f, h * 0.25f)
            )

            // Nebula Core 2: Deep Galactic Magenta & Purple Swirl (center right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = nebulaAlpha2),
                        Color(0xFF7B1FA2).copy(alpha = nebulaAlpha2 * 0.5f),
                        Color(0xFF38006B).copy(alpha = nebulaAlpha2 * 0.2f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.52f),
                    radius = w * 0.90f
                ),
                radius = w * 0.90f,
                center = Offset(w * 0.78f, h * 0.52f)
            )

            // Nebula Core 3: Cosmic Coral & Amber interstellar dust (bottom center)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCoral.copy(alpha = nebulaAlpha1 * 0.35f),
                        Color(0xFFE040FB).copy(alpha = nebulaAlpha1 * 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.40f, h * 0.82f),
                    radius = w * 0.75f
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.40f, h * 0.82f)
            )

            // 3. Twinkling Cosmic Stars Rendering
            for (star in cosmicStars) {
                val cx = star.xPct * w
                val cy = star.yPct * h

                // Twinkle factor between 0.35 and 1.0
                val twinkle = (0.65f + 0.35f * sin(twinklePhase + star.twinklePhase)).coerceIn(0.2f, 1.0f)
                val currentRadius = star.baseRadius * twinkle
                val alpha = (star.color.alpha * twinkle).coerceIn(0.2f, 1.0f)
                val starColor = star.color.copy(alpha = alpha)

                // Outer halo glow for bright stars
                if (star.baseRadius > 1.8f) {
                    drawCircle(
                        color = starColor.copy(alpha = alpha * 0.25f),
                        radius = currentRadius * 2.8f,
                        center = Offset(cx, cy)
                    )
                }

                // Core stellar body
                drawCircle(
                    color = starColor,
                    radius = currentRadius,
                    center = Offset(cx, cy)
                )

                // 4-point stellar diffraction spikes on major stars
                if (star.hasSpikes && twinkle > 0.65f) {
                    val spikeLen = currentRadius * 3.2f * twinkle
                    val spikeAlpha = alpha * 0.65f
                    val spikeColor = starColor.copy(alpha = spikeAlpha)

                    // Horizontal spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx - spikeLen, cy),
                        end = Offset(cx + spikeLen, cy),
                        strokeWidth = 1f
                    )
                    // Vertical spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx, cy - spikeLen),
                        end = Offset(cx, cy + spikeLen),
                        strokeWidth = 1f
                    )
                }
            }

            // 4. Animated Shooting Star / Meteor
            if (meteorProgress in 0.0f..1.0f) {
                val startX = w * (0.85f - meteorProgress * 0.9f)
                val startY = h * (0.08f + meteorProgress * 0.5f)
                val tailLength = 80f
                val tailX = startX + tailLength * 0.85f
                val tailY = startY - tailLength * 0.52f

                val meteorAlpha = when {
                    meteorProgress < 0.2f -> meteorProgress / 0.2f
                    meteorProgress > 0.8f -> (1.0f - meteorProgress) / 0.2f
                    else -> 1.0f
                }

                // Meteor Tail
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = meteorAlpha * 0.9f),
                            NeonCyan.copy(alpha = meteorAlpha * 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(tailX, tailY)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(tailX, tailY),
                    strokeWidth = 2.5f,
                    cap = StrokeCap.Round
                )

                // Meteor glowing head
                drawCircle(
                    color = Color.White.copy(alpha = meteorAlpha),
                    radius = 3.5f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = NeonCyan.copy(alpha = meteorAlpha * 0.5f),
                    radius = 7.0f,
                    center = Offset(startX, startY)
                )
            }
        }

        content()
    }
}

