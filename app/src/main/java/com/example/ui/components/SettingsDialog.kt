package com.example.ui.components

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
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AIDifficulty
import com.example.model.GameUiState
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBorder
import com.example.ui.theme.NeonTextMuted
import com.example.ui.theme.NeonWhite

@Composable
fun SettingsDialog(
    uiState: GameUiState,
    onToggleSound: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleVibration: () -> Unit,
    onDifficultySelected: (AIDifficulty) -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1F0B3D),
                            NeonDarkSurface,
                            Color(0xFF100424)
                        )
                    )
                )
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            NeonPurpleBorder,
                            NeonCyan,
                            NeonGold,
                            NeonPurpleBorder
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(20.dp)
                .testTag("settings_dialog")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.15f))
                                .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings Icon",
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Text(
                            text = "SETTINGS",
                            color = NeonWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = NeonTextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Toggle 1: Sound Effects (SFX)
                SettingToggleItem(
                    title = "Sound Effects",
                    subtitle = "Neon move, hit & victory tones",
                    icon = if (uiState.isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                    iconTint = if (uiState.isSoundEnabled) NeonCyan else NeonTextMuted,
                    isChecked = uiState.isSoundEnabled,
                    onCheckedChange = { onToggleSound() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle 2: Background Music
                SettingToggleItem(
                    title = "Background Music",
                    subtitle = "Ambient cyber synth soundtrack",
                    icon = if (uiState.isMusicEnabled) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    iconTint = if (uiState.isMusicEnabled) NeonGold else NeonTextMuted,
                    isChecked = uiState.isMusicEnabled,
                    onCheckedChange = { onToggleMusic() }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Toggle 3: Phone Vibration
                SettingToggleItem(
                    title = "Phone Vibration",
                    subtitle = "Haptic feedback for grid taps & wins",
                    icon = Icons.Default.Vibration,
                    iconTint = if (uiState.isVibrationEnabled) Color(0xFFFF007F) else NeonTextMuted,
                    isChecked = uiState.isVibrationEnabled,
                    onCheckedChange = { onToggleVibration() }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section: AI Difficulty
                Text(
                    text = "AI DIFFICULTY",
                    color = NeonTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AIDifficulty.values().forEach { difficulty ->
                        val isSelected = uiState.aiDifficulty == difficulty
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) NeonPurple.copy(alpha = 0.45f)
                                    else Color(0xFF16082A)
                                )
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) NeonCyan else NeonPurpleBorder.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onDifficultySelected(difficulty) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = difficulty.emoji,
                                    fontSize = 18.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = difficulty.label,
                                    color = if (isSelected) NeonCyan else NeonWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Privacy Policy Button
                Button(
                    onClick = {
                        onDismiss()
                        onOpenPrivacyPolicy()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0x334A154B)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = NeonPurpleBorder.copy(alpha = 0.6f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Policy,
                            contentDescription = "Privacy Policy",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Privacy Policy & Terms",
                            color = NeonWhite,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "v2.5.0 • Cyber Edition • AI Studio",
                    color = NeonTextMuted.copy(alpha = 0.6f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF17092C)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            NeonPurpleBorder.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = NeonWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        color = NeonTextMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = NeonCyan,
                    checkedTrackColor = NeonPurple,
                    uncheckedThumbColor = NeonTextMuted,
                    uncheckedTrackColor = Color(0xFF281347)
                )
            )
        }
    }
}
