package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
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
        initialValue = 0.20f,
        targetValue = 0.40f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula1_alpha"
    )

    val nebulaAlpha2 by infiniteTransition.animateFloat(
        initialValue = 0.18f,
        targetValue = 0.38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula2_alpha"
    )

    // Shooting star animation (0f..1.6f)
    val meteorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meteor_progress"
    )

    // Pre-calculate deterministic cosmic stars (140 stars)
    val cosmicStars = remember {
        val random = Random(1337) // Stable seed
        List(140) { index ->
            val colorChoice = random.nextFloat()
            val color = when {
                colorChoice < 0.45f -> Color(0xFFFFFFFF) // Crisp white starlight
                colorChoice < 0.70f -> Color(0xFF80DEEA) // Cyan starlight
                colorChoice < 0.88f -> Color(0xFFE1BEE7) // Cosmic purple starlight
                else -> Color(0xFFFFE082)                // Warm golden star
            }
            val isMajor = index % 16 == 0
            val radius = if (isMajor) random.nextFloat() * 1.6f + 2.0f else random.nextFloat() * 1.2f + 0.7f

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF04010A))
    ) {
        // 1. Realistic Photorealistic Galaxy Background Image Layer
        Image(
            painter = painterResource(id = R.drawable.img_realistic_galaxy_bg_1788072220618),
            contentDescription = "Realistic Cosmic Galaxy Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.72f)
        )

        // 2. Cosmic Canvas with Dynamic Deep Space Nebulas & Twinkling Stellar Dust
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Smooth Deep Space Neon Galaxy Dark Vignette Gradient
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE604010D),
                        Color(0x8809021E),
                        Color(0x55110433),
                        Color(0x880B0224),
                        Color(0xF003010A)
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Dynamic Nebula Core Swirls
            // Nebula 1: Electric Cyan galaxy arm (top left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = nebulaAlpha1),
                        Color(0xFF1A387A).copy(alpha = nebulaAlpha1 * 0.5f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.25f, h * 0.22f),
                    radius = w * 0.85f
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.25f, h * 0.22f)
            )

            // Nebula 2: Deep Galactic Magenta & Purple Swirl (center right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonPurple.copy(alpha = nebulaAlpha2),
                        Color(0xFF7B1FA2).copy(alpha = nebulaAlpha2 * 0.45f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.78f, h * 0.52f),
                    radius = w * 0.90f
                ),
                radius = w * 0.90f,
                center = Offset(w * 0.78f, h * 0.52f)
            )

            // Nebula 3: Cosmic Coral & Amber interstellar dust (bottom center)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        NeonCoral.copy(alpha = nebulaAlpha1 * 0.28f),
                        Color(0xFFE040FB).copy(alpha = nebulaAlpha1 * 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.40f, h * 0.84f),
                    radius = w * 0.75f
                ),
                radius = w * 0.75f,
                center = Offset(w * 0.40f, h * 0.84f)
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
                        color = starColor.copy(alpha = alpha * 0.35f),
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
                if (star.hasSpikes && twinkle > 0.60f) {
                    val spikeLen = currentRadius * 3.4f * twinkle
                    val spikeAlpha = alpha * 0.7f
                    val spikeColor = starColor.copy(alpha = spikeAlpha)

                    // Horizontal spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx - spikeLen, cy),
                        end = Offset(cx + spikeLen, cy),
                        strokeWidth = 1.2f
                    )
                    // Vertical spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx, cy - spikeLen),
                        end = Offset(cx, cy + spikeLen),
                        strokeWidth = 1.2f
                    )
                }
            }

            // 4. Animated Shooting Star / Meteor
            if (meteorProgress in 0.0f..1.0f) {
                val startX = w * (0.88f - meteorProgress * 0.95f)
                val startY = h * (0.06f + meteorProgress * 0.55f)
                val tailLength = 90f
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
                            Color.White.copy(alpha = meteorAlpha * 0.95f),
                            NeonCyan.copy(alpha = meteorAlpha * 0.7f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(tailX, tailY)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(tailX, tailY),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                // Meteor glowing head
                drawCircle(
                    color = Color.White.copy(alpha = meteorAlpha),
                    radius = 4f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = NeonCyan.copy(alpha = meteorAlpha * 0.6f),
                    radius = 8.5f,
                    center = Offset(startX, startY)
                )
            }
        }

        // Foreground application content
        content()
    }
}

