package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGoldBorder
import com.example.ui.theme.NeonWhite
import kotlin.math.min

/**
 * Custom Tic-Tac-Toe "OX Mix" Cyber Coin Icon.
 * Features an intertwined glowing 'O' and 'X' emblem inside a 3D metallic cyber medallion.
 */
@Composable
fun OxCoinIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = "OX Coin" }
    ) {
        val diameter = min(this.size.width, this.size.height)
        val radius = diameter / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // 1. Outer Metallic Gold Ring / Rim
        val rimBrush = Brush.sweepGradient(
            colors = listOf(
                Color(0xFFFFF1A8),
                NeonGold,
                NeonGoldBorder,
                Color(0xFFB87800),
                NeonGold,
                Color(0xFFFFF1A8)
            ),
            center = center
        )
        drawCircle(
            brush = rimBrush,
            radius = radius,
            center = center
        )

        // 2. Beveled Inner Rim
        drawCircle(
            color = Color(0x55000000),
            radius = radius * 0.90f,
            center = center,
            style = Stroke(width = radius * 0.08f)
        )

        // 3. Deep Cyber Core Medallion Face
        val coreBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF33165E),
                Color(0xFF1F0B3D),
                Color(0xFF0F041F)
            ),
            center = center,
            radius = radius * 0.85f
        )
        drawCircle(
            brush = coreBrush,
            radius = radius * 0.82f,
            center = center
        )

        // 4. Inner Gold Hairline Ring
        drawCircle(
            color = NeonGold.copy(alpha = 0.6f),
            radius = radius * 0.80f,
            center = center,
            style = Stroke(width = maxOf(1f, radius * 0.04f))
        )

        // 5. The "OX" Emblem:
        // The 'O': Glowing Neon Cyan Ring
        val oRadius = radius * 0.44f
        val oStrokeWidth = radius * 0.18f

        // 'O' Glow shadow
        drawCircle(
            color = NeonCyan.copy(alpha = 0.35f),
            radius = oRadius,
            center = center,
            style = Stroke(width = oStrokeWidth * 1.5f)
        )
        // 'O' Core stroke
        drawCircle(
            color = NeonCyan,
            radius = oRadius,
            center = center,
            style = Stroke(width = oStrokeWidth)
        )

        // The 'X': Intersecting Neon Coral Cross passing through 'O'
        val xStrokeWidth = radius * 0.17f
        val xSpan = radius * 0.46f

        // 'X' Glow
        drawLine(
            color = NeonCoral.copy(alpha = 0.35f),
            start = Offset(center.x - xSpan, center.y - xSpan),
            end = Offset(center.x + xSpan, center.y + xSpan),
            strokeWidth = xStrokeWidth * 1.5f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = NeonCoral.copy(alpha = 0.35f),
            start = Offset(center.x + xSpan, center.y - xSpan),
            end = Offset(center.x - xSpan, center.y + xSpan),
            strokeWidth = xStrokeWidth * 1.5f,
            cap = StrokeCap.Round
        )

        // 'X' Core strokes
        drawLine(
            color = NeonCoral,
            start = Offset(center.x - xSpan, center.y - xSpan),
            end = Offset(center.x + xSpan, center.y + xSpan),
            strokeWidth = xStrokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = NeonCoral,
            start = Offset(center.x + xSpan, center.y - xSpan),
            end = Offset(center.x - xSpan, center.y + xSpan),
            strokeWidth = xStrokeWidth,
            cap = StrokeCap.Round
        )

        // Intersect Center Highlight Dot
        drawCircle(
            color = NeonWhite,
            radius = radius * 0.08f,
            center = center
        )

        // 6. Top-Left Coin Glint / Specular Sparkle
        val glintOffset = Offset(center.x - radius * 0.52f, center.y - radius * 0.52f)
        val glintPath = Path().apply {
            val glintRadius = radius * 0.14f
            moveTo(glintOffset.x, glintOffset.y - glintRadius)
            lineTo(glintOffset.x + glintRadius * 0.35f, glintOffset.y)
            lineTo(glintOffset.x + glintRadius, glintOffset.y)
            lineTo(glintOffset.x + glintRadius * 0.35f, glintOffset.y + glintRadius * 0.35f)
            lineTo(glintOffset.x, glintOffset.y + glintRadius)
            lineTo(glintOffset.x - glintRadius * 0.35f, glintOffset.y + glintRadius * 0.35f)
            lineTo(glintOffset.x - glintRadius, glintOffset.y)
            lineTo(glintOffset.x - glintRadius * 0.35f, glintOffset.y)
            close()
        }
        drawPath(
            path = glintPath,
            color = NeonWhite.copy(alpha = 0.85f)
        )
    }
}
