package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.GameMode
import com.example.model.NeonTheme
import com.example.model.Player
import com.example.model.ThemeCatalog
import com.example.model.WinningLine
import com.example.ui.theme.NeonPurpleGrid
import com.example.ui.theme.NeonWhite
import kotlin.math.min
import kotlin.math.sqrt

@Composable
fun NeonGridBoard(
    grid: List<Player?>,
    mode: GameMode,
    winningLine: WinningLine?,
    onCellClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeTheme: NeonTheme = ThemeCatalog.CLASSIC_CYBER,
    winner: Player? = null
) {
    val n = mode.gridSize
    val isMini = mode == GameMode.MINI

    // Determine winning piece color: if X won -> playerXColor, if O won -> playerOColor
    val winningPlayer = winner ?: winningLine?.let { line ->
        line.winningCells.firstOrNull()?.let { (r, c) ->
            val idx = r * n + c
            grid.getOrNull(idx)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "win_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(380, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Animated strike line progress
    val strikeProgress = remember(winningLine) { Animatable(0f) }
    LaunchedEffect(winningLine) {
        if (winningLine != null) {
            strikeProgress.snapTo(0f)
            strikeProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            )
        } else {
            strikeProgress.snapTo(0f)
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .padding(
                when {
                    n <= 3 -> 10.dp
                    n <= 6 -> 6.dp
                    n <= 12 -> 4.dp
                    else -> 2.dp
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        val availableHeight = if (maxHeight.value.isFinite() && maxHeight.value > 0f) maxHeight.value else maxWidth.value
        val boardSide = min(maxWidth.value, availableHeight).dp
        val cornerRadius = when {
            n <= 3 -> 18.dp
            n <= 6 -> 14.dp
            n <= 12 -> 10.dp
            else -> 8.dp
        }

        Box(
            modifier = Modifier
                .size(boardSide)
                .clip(RoundedCornerShape(cornerRadius))
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x334A148C),
                            Color(0x221A0E38),
                            Color(0x5509041A)
                        )
                    )
                )
                .border(
                    width = if (n <= 6) 2.dp else 1.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            activeTheme.accentBorder,
                            activeTheme.playerXColor.copy(alpha = 0.5f),
                            activeTheme.playerOColor.copy(alpha = 0.5f),
                            activeTheme.accentBorder
                        )
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
        ) {
            // Draw Grid Divider Lines on Canvas
            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalWidth = size.width
                val totalHeight = size.height
                val cellSize = totalWidth / n

                val gridLineWidth = when {
                    n <= 3 -> 3f
                    n <= 6 -> 2.2f
                    n <= 12 -> 1.4f
                    else -> 0.9f
                }

                // Draw Internal Dividing Lines
                for (i in 1 until n) {
                    val pos = i * cellSize

                    // Vertical divider line + glow
                    drawLine(
                        color = NeonPurpleGrid.copy(alpha = if (n >= 12) 0.18f else 0.28f),
                        start = Offset(pos, 0f),
                        end = Offset(pos, totalHeight),
                        strokeWidth = gridLineWidth * 2.8f
                    )
                    drawLine(
                        color = NeonPurpleGrid,
                        start = Offset(pos, 0f),
                        end = Offset(pos, totalHeight),
                        strokeWidth = gridLineWidth
                    )

                    // Horizontal divider line + glow
                    drawLine(
                        color = NeonPurpleGrid.copy(alpha = if (n >= 12) 0.18f else 0.28f),
                        start = Offset(0f, pos),
                        end = Offset(totalWidth, pos),
                        strokeWidth = gridLineWidth * 2.8f
                    )
                    drawLine(
                        color = NeonPurpleGrid,
                        start = Offset(0f, pos),
                        end = Offset(totalWidth, pos),
                        strokeWidth = gridLineWidth
                    )
                }
            }

            // Cell layout using robust Column of Rows (guaranteeing exact positioning on all screen sizes)
            Column(modifier = Modifier.fillMaxSize()) {
                for (row in 0 until n) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (col in 0 until n) {
                            val index = row * n + col
                            val player = grid.getOrNull(index)
                            val isWinningCell = winningLine?.winningCells?.contains(Pair(row, col)) == true

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(
                                            bounded = true,
                                            color = activeTheme.playerOColor.copy(alpha = 0.35f)
                                        ),
                                        enabled = player == null && winningLine == null,
                                        onClick = { onCellClick(index) }
                                    )
                                    .testTag("cell_${row}_$col"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (player != null) {
                                    NeonPiece(
                                        player = player,
                                        theme = activeTheme,
                                        isWinning = isWinningCell,
                                        pulseScale = if (isWinningCell) pulseScale else 1.0f,
                                        gridSize = n
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Draw Laser Strike Line Overlay on Top
            if (winningLine != null && strikeProgress.value > 0f) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cellSize = size.width / n

                    val startX = winningLine.startCol * cellSize + cellSize / 2f
                    val startY = winningLine.startRow * cellSize + cellSize / 2f
                    val endX = winningLine.endCol * cellSize + cellSize / 2f
                    val endY = winningLine.endRow * cellSize + cellSize / 2f

                    // Extend line slightly past centers for slick laser cutter look
                    val dx = endX - startX
                    val dy = endY - startY
                    val len = sqrt(dx * dx + dy * dy)
                    val extendFactor = if (len > 0) (cellSize * 0.38f) / len else 0f

                    val actualStartX = startX - dx * extendFactor
                    val actualStartY = startY - dy * extendFactor
                    val actualEndX = endX + dx * extendFactor
                    val actualEndY = endY + dy * extendFactor

                    val currentEndX = actualStartX + (actualEndX - actualStartX) * strikeProgress.value
                    val currentEndY = actualStartY + (actualEndY - actualStartY) * strikeProgress.value

                    drawLaserStrikeLine(
                        start = Offset(actualStartX, actualStartY),
                        end = Offset(currentEndX, currentEndY),
                        gridSize = n,
                        theme = activeTheme,
                        winningPlayer = winningPlayer
                    )
                }
            }
        }
    }
}

@Composable
private fun NeonPiece(
    player: Player,
    theme: NeonTheme,
    isWinning: Boolean,
    pulseScale: Float,
    gridSize: Int
) {
    val scaleAnim = remember { Animatable(0.2f) }
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(140, easing = LinearEasing)
        )
    }

    val pieceFill = when {
        gridSize <= 3 -> 0.82f
        gridSize <= 6 -> 0.84f
        gridSize <= 12 -> 0.86f
        else -> 0.90f
    }

    Box(
        modifier = Modifier
            .fillMaxSize(pieceFill)
            .scale(scaleAnim.value * pulseScale),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeBase = when {
                gridSize <= 3 -> size.minDimension * 0.13f
                gridSize <= 6 -> size.minDimension * 0.15f
                gridSize <= 12 -> size.minDimension * 0.18f
                else -> size.minDimension * 0.22f
            }
            val alpha = alphaAnim.value

            if (player == Player.O) {
                drawNeonO(
                    center = center,
                    radius = size.minDimension * 0.38f,
                    strokeWidth = strokeBase,
                    alpha = alpha,
                    color = theme.playerOColor,
                    glowColor = theme.playerOGlow,
                    isWinning = isWinning
                )
            } else {
                drawNeonX(
                    center = center,
                    size = size.minDimension * 0.72f,
                    strokeWidth = strokeBase,
                    alpha = alpha,
                    color = theme.playerXColor,
                    glowColor = theme.playerXGlow,
                    isWinning = isWinning
                )
            }
        }
    }
}

private fun DrawScope.drawNeonO(
    center: Offset,
    radius: Float,
    strokeWidth: Float,
    alpha: Float,
    color: Color,
    glowColor: Color,
    isWinning: Boolean
) {
    val glowMult = if (isWinning) 1.6f else 1.0f

    // Broad outer ambient glow
    drawCircle(
        color = glowColor.copy(alpha = 0.35f * alpha * glowMult),
        radius = radius + strokeWidth * 1.3f,
        center = center,
        style = Stroke(width = strokeWidth * 2.0f)
    )

    // Mid vibrant neon ring
    drawCircle(
        color = color.copy(alpha = 0.9f * alpha),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth * 1.15f, cap = StrokeCap.Round)
    )

    // Inner bright core
    drawCircle(
        color = NeonWhite.copy(alpha = 0.95f * alpha),
        radius = radius,
        center = center,
        style = Stroke(width = strokeWidth * 0.5f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawNeonX(
    center: Offset,
    size: Float,
    strokeWidth: Float,
    alpha: Float,
    color: Color,
    glowColor: Color,
    isWinning: Boolean
) {
    val glowMult = if (isWinning) 1.6f else 1.0f
    val half = size / 2f

    val start1 = Offset(center.x - half, center.y - half)
    val end1 = Offset(center.x + half, center.y + half)
    val start2 = Offset(center.x + half, center.y - half)
    val end2 = Offset(center.x - half, center.y + half)

    // Broad outer glow
    drawLine(
        color = glowColor.copy(alpha = 0.35f * alpha * glowMult),
        start = start1,
        end = end1,
        strokeWidth = strokeWidth * 2.0f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = glowColor.copy(alpha = 0.35f * alpha * glowMult),
        start = start2,
        end = end2,
        strokeWidth = strokeWidth * 2.0f,
        cap = StrokeCap.Round
    )

    // Mid vibrant line
    drawLine(
        color = color.copy(alpha = 0.9f * alpha),
        start = start1,
        end = end1,
        strokeWidth = strokeWidth * 1.15f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = color.copy(alpha = 0.9f * alpha),
        start = start2,
        end = end2,
        strokeWidth = strokeWidth * 1.15f,
        cap = StrokeCap.Round
    )

    // Inner bright white core
    drawLine(
        color = NeonWhite.copy(alpha = 0.95f * alpha),
        start = start1,
        end = end1,
        strokeWidth = strokeWidth * 0.5f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = NeonWhite.copy(alpha = 0.95f * alpha),
        start = start2,
        end = end2,
        strokeWidth = strokeWidth * 0.5f,
        cap = StrokeCap.Round
    )
}

private fun DrawScope.drawLaserStrikeLine(
    start: Offset,
    end: Offset,
    gridSize: Int,
    theme: NeonTheme,
    winningPlayer: Player? = null
) {
    val laserColor = if (winningPlayer == Player.X) theme.playerXColor else theme.playerOColor
    val laserGlowColor = if (winningPlayer == Player.X) theme.playerXGlow else theme.playerOGlow

    val bloomWidth = when {
        gridSize <= 3 -> 16f
        gridSize <= 6 -> 12f
        gridSize <= 12 -> 8f
        else -> 5f
    }
    val beamWidth = when {
        gridSize <= 3 -> 8f
        gridSize <= 6 -> 6f
        gridSize <= 12 -> 4f
        else -> 2.5f
    }
    val coreWidth = when {
        gridSize <= 3 -> 3.5f
        gridSize <= 6 -> 2.6f
        gridSize <= 12 -> 1.8f
        else -> 1.2f
    }

    // 1. Broad outer laser bloom
    drawLine(
        color = laserColor.copy(alpha = 0.45f),
        start = start,
        end = end,
        strokeWidth = bloomWidth,
        cap = StrokeCap.Round
    )

    // 2. Focused vibrant beam
    drawLine(
        color = laserColor,
        start = start,
        end = end,
        strokeWidth = beamWidth,
        cap = StrokeCap.Round
    )

    // 3. Ultra-bright white inner core
    drawLine(
        color = NeonWhite,
        start = start,
        end = end,
        strokeWidth = coreWidth,
        cap = StrokeCap.Round
    )

    // End point spark glow
    drawCircle(
        color = NeonWhite,
        radius = coreWidth * 1.8f,
        center = end
    )
}
