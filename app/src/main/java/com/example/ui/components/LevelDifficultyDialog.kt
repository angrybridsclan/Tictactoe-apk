package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AIDifficulty
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonWhite

@Composable
fun LevelDifficultyDialog(
    currentDifficulty: AIDifficulty,
    onDifficultySelected: (AIDifficulty) -> Unit,
    onDismiss: () -> Unit
) {
    val initialSliderVal = when (currentDifficulty) {
        AIDifficulty.EASY -> 0f
        AIDifficulty.MEDIUM -> 1f
        AIDifficulty.MASTER -> 2f
    }
    var sliderPosition by remember { mutableFloatStateOf(initialSliderVal) }

    val activeDifficulty = when {
        sliderPosition < 0.66f -> AIDifficulty.EASY
        sliderPosition < 1.33f -> AIDifficulty.MEDIUM
        else -> AIDifficulty.MASTER
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(22.dp))
                .background(Color(0xFF0C1033))
                .border(2.5.dp, NeonCyan, RoundedCornerShape(22.dp))
                .padding(20.dp)
        ) {
            // Close Button in Top-Right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF00C8FF))
                    .border(1.5.dp, NeonWhite, CircleShape)
                    .clickable { onDismiss() }
                    .testTag("btn_close_difficulty_dialog"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color(0xFF0C1033),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // Title: Level difficult
                Text(
                    text = "Level difficult",
                    color = NeonWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                // Big Animated Emoji Face (Matching screenshot 3)
                AnimatedContent(
                    targetState = activeDifficulty,
                    transitionSpec = {
                        scaleIn(spring(stiffness = Spring.StiffnessMediumLow)) togetherWith scaleOut()
                    },
                    label = "emoji_anim"
                ) { diff ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFEA00).copy(alpha = 0.15f))
                                .border(3.dp, Color(0xFFFFEA00), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = diff.emoji,
                                fontSize = 48.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Difficulty Label in glowing yellow
                        Text(
                            text = diff.label,
                            color = Color(0xFFFFEA00),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
                        )
                    }
                }

                // Interactive Neon Slider
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Slider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..2f,
                        steps = 1,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFFEA00),
                            activeTrackColor = Color(0xFFFFEA00),
                            inactiveTrackColor = Color(0xFF1E265C),
                            activeTickColor = Color(0xFFFFEA00),
                            inactiveTickColor = Color(0xFF425199)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("difficulty_slider")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("EASY", color = if (activeDifficulty == AIDifficulty.EASY) Color(0xFFFFEA00) else Color(0xFF8B9BB4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("MEDIUM", color = if (activeDifficulty == AIDifficulty.MEDIUM) Color(0xFFFFEA00) else Color(0xFF8B9BB4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("HARD", color = if (activeDifficulty == AIDifficulty.MASTER) Color(0xFFFFEA00) else Color(0xFF8B9BB4), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // OK Button
                Box(
                    modifier = Modifier
                        .width(160.dp)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0051A8).copy(alpha = 0.6f))
                        .border(2.dp, NeonCyan, RoundedCornerShape(12.dp))
                        .clickable {
                            onDifficultySelected(activeDifficulty)
                            onDismiss()
                        }
                        .testTag("btn_confirm_difficulty"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OK",
                        color = NeonWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                }
            }
        }
    }
}
