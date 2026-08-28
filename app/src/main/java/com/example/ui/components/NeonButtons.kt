package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ripple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGoldBorder
import com.example.ui.theme.NeonGoldGlow
import com.example.ui.theme.NeonWhite

@Composable
fun NeonModeButton(
    title: String,
    isAiMode: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    val themeColor = if (isAiMode) NeonCyan else NeonGold
    val lightColor = if (isAiMode) NeonCyanLight else NeonWhite
    val borderColor = if (isAiMode) NeonCyan.copy(alpha = 0.85f) else NeonGoldBorder

    val infiniteTransition = rememberInfiniteTransition(label = "btn_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "btn_glow_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(14.dp),
                spotColor = themeColor.copy(alpha = glowAlpha),
                ambientColor = themeColor.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonDarkSurface.copy(alpha = 0.95f),
                        Color(0xFF0F0724).copy(alpha = 0.95f)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        borderColor.copy(alpha = 0.5f),
                        borderColor,
                        borderColor.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = themeColor),
                onClick = onClick
            )
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Left Icon: Person
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = lightColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "vs",
                color = themeColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Right Icon: Computer or Person
            Icon(
                imageVector = if (isAiMode) Icons.Default.Computer else Icons.Default.Person,
                contentDescription = null,
                tint = lightColor,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                color = NeonWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
