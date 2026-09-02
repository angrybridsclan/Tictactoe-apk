package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.AdMobNativeAdView
import com.example.model.GameUiState
import com.example.model.NeonTheme
import com.example.model.ThemeCatalog
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonPurpleBorder
import com.example.ui.theme.NeonTextMuted
import com.example.ui.theme.NeonWhite

@Composable
fun ThemeShopDialog(
    uiState: GameUiState,
    onSelectTheme: (String) -> Unit,
    onBuyTheme: (String, Int) -> Unit,
    onWatchAdForCoins: () -> Unit,
    onDismiss: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shop_ad_pulse")
    val adPulse by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ad_pulse"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E0C38),
                            NeonDarkSurface,
                            Color(0xFF120524)
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
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Title & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEON THEME SHOP",
                        color = NeonWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_shop")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Shop",
                            tint = NeonTextMuted
                        )
                    }
                }

                // Coin Balance and Watch Ad Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0x333F1070))
                        .border(1.dp, Color(0x66FFD700), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OxCoinIcon(size = 22.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${uiState.coins}",
                                color = NeonGold,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = " Coins",
                                color = NeonTextMuted,
                                fontSize = 12.sp
                            )
                        }

                        // Watch Ad Button for +100 Coins
                        Button(
                            onClick = onWatchAdForCoins,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_watch_ad_shop")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Black
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "+100 COINS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AdMob Native Ad (Cyber Galaxy Styled) inside Theme Shop
                AdMobNativeAdView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Themes Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                ) {
                    items(ThemeCatalog.allThemes) { theme ->
                        val isUnlocked = uiState.unlockedThemeIds.contains(theme.id) || theme.price == 0
                        val isSelected = uiState.activeThemeId == theme.id

                        ThemeCard(
                            theme = theme,
                            isUnlocked = isUnlocked,
                            isSelected = isSelected,
                            userCoins = uiState.coins,
                            onSelect = { onSelectTheme(theme.id) },
                            onBuy = { onBuyTheme(theme.id, theme.price) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeCard(
    theme: NeonTheme,
    isUnlocked: Boolean,
    isSelected: Boolean,
    userCoins: Int,
    onSelect: () -> Unit,
    onBuy: () -> Unit
) {
    val canAfford = userCoins >= theme.price

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF220D45) else Color(0x33140828)
        ),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) NeonCyan else Color(0x338A2BE2)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Theme Name
            Text(
                text = theme.name,
                color = NeonWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            // Live Mini Preview of O and X in Theme Colors
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0D051A)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Mini O preview
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawCircle(
                            color = theme.playerOColor,
                            radius = size.minDimension * 0.4f,
                            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                        )
                    }

                    // Mini X preview
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val half = size.minDimension * 0.38f
                        drawLine(
                            color = theme.playerXColor,
                            start = Offset(center.x - half, center.y - half),
                            end = Offset(center.x + half, center.y + half),
                            strokeWidth = 3.5f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = theme.playerXColor,
                            start = Offset(center.x + half, center.y - half),
                            end = Offset(center.x - half, center.y + half),
                            strokeWidth = 3.5f,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            // Bottom Action Button
            when {
                isSelected -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, NeonCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "EQUIPPED",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                isUnlocked -> {
                    Button(
                        onClick = onSelect,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF6200EA),
                            contentColor = NeonWhite
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    ) {
                        Text(
                            text = "EQUIP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                else -> {
                    Button(
                        onClick = onBuy,
                        enabled = canAfford,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (canAfford) NeonGold else Color(0x33555555),
                            contentColor = if (canAfford) Color.Black else NeonTextMuted
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(32.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            OxCoinIcon(size = 14.dp)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${theme.price}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}
