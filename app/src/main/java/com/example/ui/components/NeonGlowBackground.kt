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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class AnimeStar(
    val xPct: Float,
    val yPct: Float,
    val baseRadius: Float,
    val color: Color,
    val twinklePhase: Float,
    val isDiamondSparkle: Boolean = false,
    val isCrossStar: Boolean = false
)

private data class FloatingStardust(
    val xPct: Float,
    val initialYPct: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val driftPhase: Float
)

/**
 * Animated "Your Name" (Kimi no Na wa) Inspired Anime Twilight Galaxy Background.
 *
 * Features:
 * - Rich Kataware-doki twilight palette (Deep Indigo, Sunset Magenta, Amber & Rose).
 * - Authentic Twin Split Comets (Tiamat Comet) with luminous particle trails.
 * - Layered twinkling anime stars with 4-point diamond diffraction spikes.
 * - Floating glowing anime stardust motes drifting gracefully.
 * - Soft breathing twilight nebula clouds and cinematic rim lighting.
 */
@Composable
fun NeonGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "your_name_anime_bg_anims")

    // Global twinkling animation phase
    val twinklePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "twinkle_phase"
    )

    // Primary & Secondary Twin Comet Animation (Tiamat Comet)
    val cometProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 7500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "tiamat_comet_progress"
    )

    // Secondary Shooting Star (faster mini meteor)
    val meteorProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "meteor_progress"
    )

    // Stardust floating upward progress
    val stardustProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "stardust_float"
    )

    // Ethereal Twilight Sky Aurora Breathing
    val auroraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.22f,
        targetValue = 0.48f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aurora_breathing"
    )

    // Pre-calculate deterministic anime stars (160 stars)
    val animeStars = remember {
        val random = Random(2024)
        List(160) { index ->
            val colorChoice = random.nextFloat()
            val starColor = when {
                colorChoice < 0.40f -> Color(0xFFFFFFFF) // Pure starlight white
                colorChoice < 0.65f -> Color(0xFF8CE8FF) // Anime aqua cyan
                colorChoice < 0.85f -> Color(0xFFF3C4FB) // Twilight lavender rose
                else -> Color(0xFFFFE57F)                // Soft golden stardust
            }
            val isCross = index % 14 == 0
            val isDiamond = index % 9 == 0 && !isCross
            val radius = when {
                isCross -> random.nextFloat() * 1.5f + 2.2f
                isDiamond -> random.nextFloat() * 1.2f + 1.6f
                else -> random.nextFloat() * 1.0f + 0.6f
            }

            AnimeStar(
                xPct = random.nextFloat(),
                yPct = random.nextFloat() * 0.90f,
                baseRadius = radius,
                color = starColor,
                twinklePhase = random.nextFloat() * (2 * PI).toFloat(),
                isDiamondSparkle = isDiamond,
                isCrossStar = isCross
            )
        }
    }

    // Pre-calculate floating anime stardust motes (36 particles)
    val stardustParticles = remember {
        val random = Random(4040)
        List(36) { index ->
            val colorChoice = random.nextFloat()
            val color = when {
                colorChoice < 0.4f -> Color(0xAAFFFFFF)
                colorChoice < 0.7f -> Color(0xAA80DEEA)
                else -> Color(0xAAFF80AB)
            }
            FloatingStardust(
                xPct = random.nextFloat(),
                initialYPct = random.nextFloat(),
                size = random.nextFloat() * 2.2f + 1.2f,
                speed = random.nextFloat() * 0.6f + 0.7f,
                color = color,
                driftPhase = random.nextFloat() * (2 * PI).toFloat()
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF070415))
    ) {
        // 1. "Your Name" Anime Twilight Sky Base Graphic Layer
        Image(
            painter = painterResource(id = R.drawable.anime_your_name_bg_1788169453716),
            contentDescription = "Your Name Anime Celestial Twilight Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.85f)
        )

        // 2. Dynamic Anime Sky Overlay (Twilight Gradients, Twinkling Stars, Twin Tiamat Comets & Stardust)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Deep Kataware-Doki Twilight Atmospheric Gradient Vignette
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xE005021A), // Deep Celestial Space Navy
                        Color(0x990E0836), // Twilight Indigo
                        Color(0x66290C47), // Sunset Magenta Mist
                        Color(0x773A0C4E), // Rose-Purple Horizon Haze
                        Color(0xEA060317)  // Bottom Ground Silhouette Contrast
                    ),
                    startY = 0f,
                    endY = h
                )
            )

            // Dynamic Anime Nebula Swirl 1: Radiant Cyan Celestial Arm (Top Left)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = auroraAlpha * 0.45f),
                        Color(0xFF3D5AFE).copy(alpha = auroraAlpha * 0.25f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.20f, h * 0.18f),
                    radius = w * 0.90f
                ),
                radius = w * 0.90f,
                center = Offset(w * 0.20f, h * 0.18f)
            )

            // Dynamic Anime Nebula Swirl 2: Sunset Magenta & Rose Cloud Haze (Center Right)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF4081).copy(alpha = auroraAlpha * 0.38f),
                        Color(0xFF7C4DFF).copy(alpha = auroraAlpha * 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.82f, h * 0.45f),
                    radius = w * 0.95f
                ),
                radius = w * 0.95f,
                center = Offset(w * 0.82f, h * 0.45f)
            )

            // 3. Twinkling Anime Celestial Stars
            for (star in animeStars) {
                val cx = star.xPct * w
                val cy = star.yPct * h

                val twinkle = (0.60f + 0.40f * sin(twinklePhase + star.twinklePhase)).coerceIn(0.2f, 1.0f)
                val currentRadius = star.baseRadius * twinkle
                val starAlpha = (star.color.alpha * twinkle).coerceIn(0.25f, 1.0f)
                val activeColor = star.color.copy(alpha = starAlpha)

                // Soft outer glowing aura for major stars
                if (star.baseRadius > 1.8f) {
                    drawCircle(
                        color = activeColor.copy(alpha = starAlpha * 0.35f),
                        radius = currentRadius * 3.0f,
                        center = Offset(cx, cy)
                    )
                }

                // Core star center
                drawCircle(
                    color = activeColor,
                    radius = currentRadius,
                    center = Offset(cx, cy)
                )

                // 4-Point Anime Cross Diffraction Spikes (✦ Anime Lens Flare)
                if (star.isCrossStar && twinkle > 0.55f) {
                    val spikeLen = currentRadius * 4.2f * twinkle
                    val spikeAlpha = starAlpha * 0.85f
                    val spikeColor = activeColor.copy(alpha = spikeAlpha)

                    // Horizontal spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx - spikeLen, cy),
                        end = Offset(cx + spikeLen, cy),
                        strokeWidth = 1.4f
                    )
                    // Vertical spike
                    drawLine(
                        color = spikeColor,
                        start = Offset(cx, cy - spikeLen),
                        end = Offset(cx, cy + spikeLen),
                        strokeWidth = 1.4f
                    )
                }

                // Diamond 4-point sparkle for secondary stars
                if (star.isDiamondSparkle && twinkle > 0.65f) {
                    val diaLen = currentRadius * 2.5f * twinkle
                    val diaAlpha = starAlpha * 0.60f
                    val diaColor = activeColor.copy(alpha = diaAlpha)

                    drawLine(
                        color = diaColor,
                        start = Offset(cx - diaLen, cy),
                        end = Offset(cx + diaLen, cy),
                        strokeWidth = 1.0f
                    )
                    drawLine(
                        color = diaColor,
                        start = Offset(cx, cy - diaLen),
                        end = Offset(cx, cy + diaLen),
                        strokeWidth = 1.0f
                    )
                }
            }

            // 4. Iconic Twin Split Comets ("Tiamat Comet" from Your Name)
            if (cometProgress in 0.0f..1.15f) {
                val t = cometProgress / 1.15f
                val cometAlpha = when {
                    t < 0.15f -> t / 0.15f
                    t > 0.82f -> (1.0f - t) / 0.18f
                    else -> 1.0f
                }.coerceIn(0f, 1f)

                // Primary Comet (Streaking from Top-Right diagonally down towards Center-Left)
                val startX = w * (0.92f - t * 0.90f)
                val startY = h * (0.04f + t * 0.52f)
                val tailLength = w * 0.40f
                val tailAngle = Math.toRadians(32.0)
                val tailEndX = startX + (tailLength * cos(tailAngle)).toFloat()
                val tailEndY = startY - (tailLength * sin(tailAngle)).toFloat()

                // Primary Comet Luminous Tail (Cyan to Emerald & Soft Violet)
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = cometAlpha * 0.95f),
                            Color(0xFF00E5FF).copy(alpha = cometAlpha * 0.80f),
                            Color(0xFF00B0FF).copy(alpha = cometAlpha * 0.45f),
                            Color(0xFF7C4DFF).copy(alpha = cometAlpha * 0.15f),
                            Color.Transparent
                        ),
                        start = Offset(startX, startY),
                        end = Offset(tailEndX, tailEndY)
                    ),
                    start = Offset(startX, startY),
                    end = Offset(tailEndX, tailEndY),
                    strokeWidth = 5.0f,
                    cap = StrokeCap.Round
                )

                // Primary Comet Nucleus Glow
                drawCircle(
                    color = Color.White.copy(alpha = cometAlpha),
                    radius = 5.5f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = Color(0xFF00E5FF).copy(alpha = cometAlpha * 0.7f),
                    radius = 12f,
                    center = Offset(startX, startY)
                )
                drawCircle(
                    color = Color(0xFF18FFFF).copy(alpha = cometAlpha * 0.35f),
                    radius = 22f,
                    center = Offset(startX, startY)
                )

                // Split Comet Fragment (Splitting off with Rose-Magenta trail)
                if (t > 0.20f) {
                    val splitFrac = (t - 0.20f) / 0.80f
                    val splitOffset = splitFrac * 40f
                    val splitX = startX + splitOffset * 0.8f + 14f
                    val splitY = startY + splitOffset * 1.1f + 18f
                    val splitTailEndX = splitX + (tailLength * 0.65f * cos(tailAngle + 0.12)).toFloat()
                    val splitTailEndY = splitY - (tailLength * 0.65f * sin(tailAngle + 0.12)).toFloat()

                    // Split Comet Tail
                    drawLine(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = cometAlpha * 0.90f),
                                Color(0xFFFF4081).copy(alpha = cometAlpha * 0.75f),
                                Color(0xFFE040FB).copy(alpha = cometAlpha * 0.35f),
                                Color.Transparent
                            ),
                            start = Offset(splitX, splitY),
                            end = Offset(splitTailEndX, splitTailEndY)
                        ),
                        start = Offset(splitX, splitY),
                        end = Offset(splitTailEndX, splitTailEndY),
                        strokeWidth = 3.5f,
                        cap = StrokeCap.Round
                    )

                    // Split Fragment Nucleus
                    drawCircle(
                        color = Color.White.copy(alpha = cometAlpha * 0.9f),
                        radius = 4f,
                        center = Offset(splitX, splitY)
                    )
                    drawCircle(
                        color = Color(0xFFFF4081).copy(alpha = cometAlpha * 0.65f),
                        radius = 8.5f,
                        center = Offset(splitX, splitY)
                    )
                }
            }

            // 5. Secondary Quick Meteor
            if (meteorProgress in 0.0f..1.0f) {
                val mX = w * (0.65f - meteorProgress * 0.70f)
                val mY = h * (0.12f + meteorProgress * 0.40f)
                val mTailX = mX + 75f
                val mTailY = mY - 45f
                val mAlpha = when {
                    meteorProgress < 0.2f -> meteorProgress / 0.2f
                    meteorProgress > 0.8f -> (1.0f - meteorProgress) / 0.2f
                    else -> 1.0f
                }

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = mAlpha * 0.9f),
                            Color(0xFFFFD700).copy(alpha = mAlpha * 0.6f),
                            Color.Transparent
                        ),
                        start = Offset(mX, mY),
                        end = Offset(mTailX, mTailY)
                    ),
                    start = Offset(mX, mY),
                    end = Offset(mTailX, mTailY),
                    strokeWidth = 2.4f,
                    cap = StrokeCap.Round
                )
            }

            // 6. Floating Anime Stardust Motes
            for (dust in stardustParticles) {
                val currentYPct = (dust.initialYPct - stardustProgress * dust.speed + 1.0f) % 1.0f
                val currentXPct = dust.xPct + 0.02f * sin(twinklePhase + dust.driftPhase)

                val dx = currentXPct * w
                val dy = currentYPct * h
                val dustAlpha = (0.4f + 0.3f * sin(twinklePhase * 1.5f + dust.driftPhase)).coerceIn(0.1f, 0.8f)

                drawCircle(
                    color = dust.color.copy(alpha = dustAlpha),
                    radius = dust.size,
                    center = Offset(dx, dy)
                )
            }
        }

        // Foreground application content
        content()
    }
}
