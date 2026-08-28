package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ads.UnityBannerAd
import com.example.ads.UnityNativeAd
import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.model.GameUiState
import com.example.model.OpponentType
import com.example.model.Player
import com.example.model.WinningLine
import com.example.ui.components.CoinBalanceBadge
import com.example.ui.components.NeonGlowBackground
import com.example.ui.components.NeonGridBoard
import com.example.ui.components.OxCoinIcon
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanLight
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurpleBorder
import com.example.ui.theme.NeonWhite

@Composable
fun SelectLevelScreen(
    uiState: GameUiState,
    onModeSelected: (GameMode) -> Unit,
    onOpponentSelected: (OpponentType) -> Unit,
    onDifficultySelected: (AIDifficulty) -> Unit,
    onOpenDifficultyDialog: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleSound: () -> Unit = {},
    onToggleMusic: () -> Unit = {},
    onToggleVibration: () -> Unit = {},
    onOpenShop: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val modes = listOf(GameMode.MINI, GameMode.BIG, GameMode.MEGA)
    var selectedIndex by remember(uiState.mode) {
        val idx = modes.indexOf(uiState.mode)
        mutableIntStateOf(if (idx >= 0) idx else 0)
    }

    val currentMode = modes[selectedIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "title_glow")
    val titleGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "title_glow"
    )

    val scrollState = rememberScrollState()

    NeonGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: Theme Shop, Coin Balance, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Theme Shop
                IconButton(
                    onClick = onOpenShop,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0x331F1045), CircleShape)
                        .border(1.5.dp, NeonPurpleBorder, CircleShape)
                        .testTag("btn_theme_shop")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Theme Shop",
                        tint = Color(0xFFFF4081),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Center: Coins Badge
                CoinBalanceBadge(
                    coins = uiState.coins,
                    onClick = onOpenShop
                )

                // Right: Settings Gear (Sound, Music, Difficulty & More)
                IconButton(
                    onClick = onOpenSettings,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0x331F1045), CircleShape)
                        .border(1.5.dp, NeonCyan, CircleShape)
                        .testTag("btn_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Scrollable Center Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header Title: TIC TAC TOE
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "TIC TAC TOE",
                        color = NeonWhite.copy(alpha = titleGlow),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 3.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "SELECT LEVEL • ${currentMode.subtitle}",
                        color = NeonCyanLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }

                // Interactive Level Preview Carousel (3x3, 6x6, 12x12)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Arrow Button
                    IconButton(
                        onClick = {
                            selectedIndex = (selectedIndex - 1 + modes.size) % modes.size
                            onModeSelected(modes[selectedIndex])
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x331F1045), CircleShape)
                            .border(1.5.dp, Color(0xFFFFEA00), CircleShape)
                            .testTag("prev_level_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Mode",
                            tint = Color(0xFFFFEA00),
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Grid Preview Frame
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(
                            targetState = currentMode,
                            transitionSpec = {
                                slideInHorizontally { width -> width / 2 } + fadeIn() togetherWith
                                        slideOutHorizontally { width -> -width / 2 } + fadeOut()
                            },
                            label = "mode_preview_anim"
                        ) { mode ->
                            LevelPreviewCard(mode = mode, uiState = uiState)
                        }
                    }

                    // Right Arrow Button
                    IconButton(
                        onClick = {
                            selectedIndex = (selectedIndex + 1) % modes.size
                            onModeSelected(modes[selectedIndex])
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0x331F1045), CircleShape)
                            .border(1.5.dp, Color(0xFFFFEA00), CircleShape)
                            .testTag("next_level_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Mode",
                            tint = Color(0xFFFFEA00),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Mode Quick Switcher Pills (3x3, 6x6, 12x12)
                Row(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    modes.forEachIndexed { index, mode ->
                        val isSelected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0x331F1045))
                                .border(
                                    1.2.dp,
                                    if (isSelected) NeonCyan else Color(0x446A3E9F),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selectedIndex = index
                                    onModeSelected(modes[index])
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = when (mode) {
                                    GameMode.MINI -> "3×3 Mini"
                                    GameMode.BIG -> "6×6 Big"
                                    GameMode.MEGA -> "12×12 Mega"
                                },
                                color = if (isSelected) NeonCyan else NeonWhite.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                            )
                        }
                    }
                }

                // Pagination Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(modes.size) { index ->
                        val isSelected = index == selectedIndex
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .size(if (isSelected) 8.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) NeonCyan else Color(0x66755BB4))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Cyber Reward Callout Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x22130B2E))
                        .border(1.dp, NeonPurpleBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    OxCoinIcon(size = 16.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Win matches to earn OX Coins & unlock custom themes",
                        color = NeonCyan.copy(alpha = 0.9f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Mode Action Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.92f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Button 1: 👤 vs 🖥️ (Player vs Computer)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, NeonCyan, RoundedCornerShape(14.dp))
                            .clickable {
                                onOpponentSelected(OpponentType.VS_AI)
                                onOpenDifficultyDialog()
                            }
                            .testTag("btn_vs_computer"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👤 vs 🖥️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "VS COMPUTER (${uiState.aiDifficulty.label})",
                                color = NeonCyanLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Button 2: 👤 vs 👤 (Player vs Player)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, Color(0xFFFFEA00), RoundedCornerShape(14.dp))
                            .clickable {
                                onOpponentSelected(OpponentType.VS_PLAYER)
                                onStartGame()
                            }
                            .testTag("btn_vs_player"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👤 vs 👤", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2 PLAYERS (LOCAL)",
                                color = Color(0xFFFFEA00),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Button 3: CAMPAIGN
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, Color(0xFFFF9900), RoundedCornerShape(14.dp))
                            .clickable {
                                onOpponentSelected(OpponentType.CAMPAIGN)
                                onStartGame()
                            }
                            .testTag("btn_campaign"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐ CAMPAIGN", fontSize = 16.sp, color = Color(0xFFFF9900), fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LEVEL MATRIX",
                                color = Color(0xFFFFCC80),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
            }

            // Bottom Banner Ad
            UnityBannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun LevelPreviewCard(
    mode: GameMode,
    uiState: GameUiState
) {
    val sampleBoard = remember(mode) {
        val total = mode.gridSize * mode.gridSize
        val list = MutableList<Player?>(total) { null }
        when (mode) {
            GameMode.MINI -> {
                list[0] = Player.O; list[1] = Player.X
                list[3] = Player.X; list[4] = Player.O; list[5] = Player.X
                list[7] = Player.X; list[8] = Player.O
            }
            GameMode.BIG -> {
                list[1 * 6 + 2] = Player.O
                list[2 * 6 + 3] = Player.O
                list[3 * 6 + 4] = Player.O
                list[4 * 6 + 5] = Player.O

                list[0 * 6 + 2] = Player.O
                list[1 * 6 + 0] = Player.X
                list[1 * 6 + 1] = Player.X
                list[1 * 6 + 4] = Player.X
                list[2 * 6 + 2] = Player.O
                list[3 * 6 + 1] = Player.X
                list[3 * 6 + 2] = Player.X
                list[4 * 6 + 3] = Player.X
            }
            GameMode.MEGA -> {
                for (k in 0..4) {
                    list[(3 + k) * 12 + (3 + k)] = Player.O
                }
                list[4 * 12 + 2] = Player.X
                list[5 * 12 + 2] = Player.X
                list[6 * 12 + 2] = Player.X
                list[7 * 12 + 2] = Player.X
                list[2 * 12 + 5] = Player.X
                list[3 * 12 + 6] = Player.O
                list[4 * 12 + 7] = Player.X
            }
        }
        list
    }

    val sampleWinningLine = remember(mode) {
        when (mode) {
            GameMode.MINI -> {
                WinningLine(
                    startRow = 0,
                    startCol = 0,
                    endRow = 2,
                    endCol = 2,
                    winningCells = setOf(Pair(0, 0), Pair(1, 1), Pair(2, 2))
                )
            }
            GameMode.BIG -> {
                WinningLine(
                    startRow = 1,
                    startCol = 2,
                    endRow = 4,
                    endCol = 5,
                    winningCells = setOf(Pair(1, 2), Pair(2, 3), Pair(3, 4), Pair(4, 5))
                )
            }
            GameMode.MEGA -> {
                WinningLine(
                    startRow = 3,
                    startCol = 3,
                    endRow = 7,
                    endCol = 7,
                    winningCells = (0..4).map { Pair(3 + it, 3 + it) }.toSet()
                )
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = mode.title,
            color = NeonWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        NeonGridBoard(
            grid = sampleBoard,
            mode = mode,
            winningLine = sampleWinningLine,
            onCellClick = { /* preview only */ },
            activeTheme = uiState.activeTheme,
            modifier = Modifier.fillMaxWidth(if (mode == GameMode.MINI) 0.88f else 0.94f)
        )
    }
}
