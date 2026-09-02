package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class CyberParticle(
    val xPct: Float,
    val initialYPct: Float,
    val size: Float,
    val speed: Float,
    val color: Color,
    val driftPhase: Float,
    val isHexagon: Boolean = false
)

private data class CircuitNode(
    val startXPct: Float,
    val startYPct: Float,
    val midXPct: Float,
    val midYPct: Float,
    val endXPct: Float,
    val endYPct: Float,
    val color: Color,
    val pulsePhase: Float
)

/**
 * State-of-the-art Futuristic Cyberpunk Neon Arcade Background.
 *
 * Visual Features:
 * - Ultra-deep Obsidian & Indigo Cyberpunk Matrix Canvas.
 * - Dynamic 3D Synthwave Horizon Perspective Laser Grid with continuous flow.
 * - Glowing Neon Laser Circuit Lines with animated energy packet pulses.
 * - Multi-spectral Cyber Auroras (Neon Cyan, Electric Magenta, Deep Violet & Gold Plasma).
 * - Floating Luminescent Cyber Embers & Micro-Hexagons.
 */
@Composable
fun NeonGlowBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "cyber_neon_bg_anims")

    // Dynamic grid movement phase
    val gridFlowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "grid_flow_phase"
    )

    // Circuit Energy Pulses
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "circuit_pulse_phase"
    )

    // Nebula breathing aura
    val nebulaBreath by infiniteTransition.animateFloat(
        initialValue = 0.28f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "nebula_breath"
    )

    // Floating Cyber Particles
    val particleFlow by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_flow"
    )

    // Laser Scanline sweep
    val scanlineY by infiniteTransition.animateFloat(
        initialValue = -0.1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanline_sweep"
    )

    // Precompute cyber particles (48 particles)
    val cyberParticles = remember {
        val random = Random(3301)
        List(48) { index ->
            val colorPicker = random.nextFloat()
            val color = when {
                colorPicker < 0.35f -> Color(0xFF00E5FF) // Cyber Cyan
                colorPicker < 0.70f -> Color(0xFFFF007F) // Electric Magenta
                colorPicker < 0.88f -> Color(0xFFB388FF) // Neon Lavender
                else -> Color(0xFFFFD700)                // Cyber Gold
            }
            CyberParticle(
                xPct = random.nextFloat(),
                initialYPct = random.nextFloat(),
                size = random.nextFloat() * 2.6f + 1.2f,
                speed = random.nextFloat() * 0.5f + 0.6f,
                color = color,
                driftPhase = random.nextFloat() * (2 * PI).toFloat(),
                isHexagon = index % 5 == 0
            )
        }
    }

    // Precompute cyber circuit trace lines
    val circuitNodes = remember {
        listOf(
            CircuitNode(0.05f, 0.15f, 0.22f, 0.15f, 0.28f, 0.28f, Color(0xFF00E5FF), 0f),
            CircuitNode(0.95f, 0.20f, 0.78f, 0.20f, 0.72f, 0.35f, Color(0xFFFF007F), 1.2f),
            CircuitNode(0.08f, 0.82f, 0.25f, 0.82f, 0.32f, 0.70f, Color(0xFF7C4DFF), 2.4f),
            CircuitNode(0.92f, 0.78f, 0.75f, 0.78f, 0.68f, 0.62f, Color(0xFF00E5FF), 3.6f),
            CircuitNode(0.50f, 0.05f, 0.50f, 0.18f, 0.62f, 0.24f, Color(0xFFFFD700), 4.8f)
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF06030F))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 1. Deep Obsidian / Space Dark Ambient Base
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF06030E),
                        Color(0xFF090418),
                        Color(0xFF0F0726),
                        Color(0xFF070314)
                    )
                )
            )

            // 2. High-Octane Radial Neon Nebula Auras
            // Nebula 1: Top Left Electric Cyan
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00E5FF).copy(alpha = nebulaBreath * 0.32f),
                        Color(0xFF3D5AFE).copy(alpha = nebulaBreath * 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.15f, h * 0.18f),
                    radius = w * 0.85f
                ),
                radius = w * 0.85f,
                center = Offset(w * 0.15f, h * 0.18f)
            )

            // Nebula 2: Center Right Electric Magenta
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFF007F).copy(alpha = nebulaBreath * 0.30f),
                        Color(0xFF7C4DFF).copy(alpha = nebulaBreath * 0.16f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.88f, h * 0.45f),
                    radius = w * 0.90f
                ),
                radius = w * 0.90f,
                center = Offset(w * 0.88f, h * 0.45f)
            )

            // Nebula 3: Bottom Cyber Gold & Indigo Plasma
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF651FFF).copy(alpha = nebulaBreath * 0.25f),
                        Color(0xFFFFB300).copy(alpha = nebulaBreath * 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(w * 0.5f, h * 0.90f),
                    radius = w * 0.80f
                ),
                radius = w * 0.80f,
                center = Offset(w * 0.5f, h * 0.90f)
            )

            // 3. 3D Cyber Synthwave Horizon Perspective Grid (Bottom Section)
            val horizonY = h * 0.62f
            val gridHeight = h - horizonY

            // Ambient Horizon Glow Bar
            drawLine(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color(0xFFFF007F).copy(alpha = 0.8f),
                        Color(0xFF00E5FF).copy(alpha = 0.6f),
                        Color.Transparent
                    )
                ),
                start = Offset(0f, horizonY),
                end = Offset(w, horizonY),
                strokeWidth = 2.5f
            )

            // Perspective Vanishing Lines (Radiating outward from horizon center)
            val vanishingPoint = Offset(w * 0.5f, horizonY)
            val numVanishingLines = 14
            for (i in 0..numVanishingLines) {
                val bottomX = w * (-0.2f + 1.4f * (i.toFloat() / numVanishingLines))
                val lineColor = if (i % 2 == 0) Color(0xFF00E5FF) else Color(0xFFFF007F)
                val lineAlpha = 0.18f + 0.12f * sin(pulsePhase + i * 0.4f)

                drawLine(
                    color = lineColor.copy(alpha = lineAlpha),
                    start = vanishingPoint,
                    end = Offset(bottomX, h),
                    strokeWidth = 1.2f
                )
            }

            // Flowing Horizontal Grid Lines with logarithmic perspective spacing
            val numHorizLines = 9
            for (i in 0 until numHorizLines) {
                val rawT = (i.toFloat() + gridFlowPhase) / numHorizLines
                val warpedT = rawT * rawT // Exponential perspective
                val lineY = horizonY + gridHeight * warpedT
                val alpha = (warpedT * 0.35f).coerceIn(0.04f, 0.35f)

                drawLine(
                    color = Color(0xFF00E5FF).copy(alpha = alpha),
                    start = Offset(0f, lineY),
                    end = Offset(w, lineY),
                    strokeWidth = 1.0f + warpedT * 1.5f
                )
            }

            // 4. Futuristic Neon Cyber Circuit Traces & Pulsing Node Packets
            for (node in circuitNodes) {
                val p1 = Offset(node.startXPct * w, node.startYPct * h)
                val p2 = Offset(node.midXPct * w, node.midYPct * h)
                val p3 = Offset(node.endXPct * w, node.endYPct * h)

                val path = Path().apply {
                    moveTo(p1.x, p1.y)
                    lineTo(p2.x, p2.y)
                    lineTo(p3.x, p3.y)
                }

                // Base faint circuit wire
                drawPath(
                    path = path,
                    color = node.color.copy(alpha = 0.22f),
                    style = Stroke(width = 1.4f, cap = StrokeCap.Round)
                )

                // Wire Start & Joint Dots
                drawCircle(
                    color = node.color.copy(alpha = 0.5f),
                    radius = 2.8f,
                    center = p1
                )
                drawCircle(
                    color = node.color.copy(alpha = 0.5f),
                    radius = 2.8f,
                    center = p2
                )
                drawCircle(
                    color = node.color.copy(alpha = 0.7f),
                    radius = 3.6f,
                    center = p3
                )

                // Animated Travelling Energy Pulse Packet along wire
                val packetProgress = ((pulsePhase + node.pulsePhase) / (2 * PI).toFloat()) % 1.0f
                val packetPos = when {
                    packetProgress < 0.5f -> {
                        val t = packetProgress / 0.5f
                        Offset(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t)
                    }
                    else -> {
                        val t = (packetProgress - 0.5f) / 0.5f
                        Offset(p2.x + (p3.x - p2.x) * t, p2.y + (p3.y - p2.y) * t)
                    }
                }

                // Glowing Packet
                drawCircle(
                    color = node.color.copy(alpha = 0.85f),
                    radius = 3.2f,
                    center = packetPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 1.6f,
                    center = packetPos
                )
            }

            // 5. Floating Luminescent Cyber Embers & Micro-Hexagons
            for (p in cyberParticles) {
                val currentY = (p.initialYPct - particleFlow * p.speed + 1.0f) % 1.0f
                val currentX = p.xPct + 0.025f * sin(pulsePhase + p.driftPhase)

                val px = currentX * w
                val py = currentY * h
                val pAlpha = (0.35f + 0.35f * sin(pulsePhase * 1.4f + p.driftPhase)).coerceIn(0.12f, 0.85f)

                if (p.isHexagon) {
                    // Draw Mini Cyber Hexagon
                    val hexRadius = p.size * 1.6f
                    val hexPath = Path().apply {
                        for (i in 0..5) {
                            val angle = (i * 60) * (PI / 180.0)
                            val hx = (px + hexRadius * cos(angle)).toFloat()
                            val hy = (py + hexRadius * sin(angle)).toFloat()
                            if (i == 0) moveTo(hx, hy) else lineTo(hx, hy)
                        }
                        close()
                    }
                    drawPath(
                        path = hexPath,
                        color = p.color.copy(alpha = pAlpha),
                        style = Stroke(width = 1.0f)
                    )
                } else {
                    // Soft glowing ember
                    drawCircle(
                        color = p.color.copy(alpha = pAlpha * 0.4f),
                        radius = p.size * 2.2f,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = p.color.copy(alpha = pAlpha),
                        radius = p.size,
                        center = Offset(px, py)
                    )
                }
            }

            // 6. Laser Scanline Holographic Sweep
            if (scanlineY in 0f..1f) {
                val sy = scanlineY * h
                drawLine(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF00E5FF).copy(alpha = 0.20f),
                            Color(0xFFFF007F).copy(alpha = 0.28f),
                            Color(0xFF00E5FF).copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    ),
                    start = Offset(0f, sy),
                    end = Offset(w, sy),
                    strokeWidth = 2.0f
                )
            }
        }

        // Content
        content()
    }
}

