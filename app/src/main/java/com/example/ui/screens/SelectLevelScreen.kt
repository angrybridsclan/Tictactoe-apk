package com.example.ui.screens

import android.app.Activity
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ads.BigRevenueRectangleBanner
import com.example.ads.BottomRevenueLeaderboardBanner
import com.example.ads.DualBannerAd
import com.example.ads.UnityAdsManager
import com.example.ads.UnityBannerAd
import com.example.ads.UnityBigBannerAd
import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.model.GameUiState
import com.example.model.OpponentType
import com.example.model.Player
import com.example.model.WinningLine
import com.example.ui.components.CoinBalanceBadge
import com.example.ui.components.GiftEventDialog
import com.example.ui.components.NeonGlowBackground
import com.example.ui.components.NeonGridBoard
import com.example.ui.components.OxCoinIcon
import com.example.ui.theme.NeonCoral
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
    onOpenGiftEvent: () -> Unit = {},
    onCloseGiftEvent: () -> Unit = {},
    onUpdateGiftEventProgress: (Int) -> Unit = {},
    onClaimGiftReward: () -> Unit = {},
    onWatchUnityAd: (() -> Unit)? = null,
    onStartGame: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
    var showModeSelectDialog by remember { mutableStateOf(false) }

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
            // Top Bar: Theme Shop, Lucky Gift Event, Coin Balance, Settings
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Theme Shop & Lucky Gift Event Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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

                    // Lucky Gift Event Trigger Icon Button with Live Notice
                    Box(
                        modifier = Modifier
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF4A148C),
                                        Color(0xFF880E4F)
                                    )
                                )
                            )
                            .border(1.5.dp, NeonGold, RoundedCornerShape(20.dp))
                            .clickable { onOpenGiftEvent() }
                            .padding(horizontal = 8.dp)
                            .testTag("btn_gift_event"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Gift Event",
                                tint = NeonGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            val topNoticeText = when {
                                uiState.isGiftEventClaimed -> "CLAIMED ✅"
                                uiState.giftEventSecondsLeft in 1..29 -> "${uiState.giftEventSecondsLeft}s LEFT ⏳"
                                else -> "GIFT +250"
                            }
                            Text(
                                text = topNoticeText,
                                color = NeonGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
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
                        text = "TAP BOARD TO PLAY • ${currentMode.subtitle}",
                        color = NeonCyanLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }

                // Interactive Level Preview Box Carousel - Clickable to launch Game Modes
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

                    // Interactive Grid Preview Frame (Click on box opens play selection)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                showModeSelectDialog = true
                            }
                            .testTag("level_preview_box"),
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

                Spacer(modifier = Modifier.height(2.dp))

                // Interactive Gift Event Banner Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.92f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF2C0B4D),
                                    Color(0xFF4A0E4E),
                                    Color(0xFF1F0B3D)
                                )
                            )
                        )
                        .border(1.5.dp, NeonGold, RoundedCornerShape(14.dp))
                        .clickable { onOpenGiftEvent() }
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("banner_gift_event"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF512DA8))
                                    .border(1.dp, NeonGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CardGiftcard,
                                    contentDescription = "Gift",
                                    tint = NeonGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                val bannerTitle = if (uiState.isGiftEventClaimed) "🎁 GIFT EVENT CLAIMED" else "🎁 LUCKY GIFT EVENT"
                                val bannerSub = when {
                                    uiState.isGiftEventClaimed -> "✅ +250 Coins Added! (Tap to visit again)"
                                    uiState.giftEventSecondsLeft in 1..29 -> "⏳ ${uiState.giftEventSecondsLeft}s remaining! (Tap to auto-claim)"
                                    else -> "Stay 30s & Auto-Claim +250 Coins"
                                }
                                Text(
                                    text = bannerTitle,
                                    color = NeonGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = bannerSub,
                                    color = NeonCyanLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (uiState.isGiftEventClaimed) Color(0xFF00E676) else NeonGold)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (uiState.isGiftEventClaimed) "DONE" else "START",
                                color = Color(0xFF1A0A00),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Main Action: START GAME Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF),
                                    Color(0xFF00B0FF),
                                    Color(0xFF7C4DFF)
                                )
                            )
                        )
                        .border(2.dp, NeonWhite.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                        .clickable {
                            showModeSelectDialog = true
                        }
                        .testTag("btn_start_game"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = Color(0xFF06031A),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "START GAME",
                            color = Color(0xFF06031A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                // Daily Reward Card replacing the 300x250 ad box
                DailyRewardCard(
                    uiState = uiState,
                    onClaimClick = onOpenGiftEvent,
                    modifier = Modifier.fillMaxWidth(0.94f)
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Bottom Leaderboard Banner (728x90 script with 10s auto-refresh)
            BottomRevenueLeaderboardBanner(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }

    // Play Selection Modal Dialog when clicking the Box
    if (showModeSelectDialog) {
        Dialog(onDismissRequest = { showModeSelectDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF0C1033))
                    .border(2.5.dp, NeonCyan, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                // Close button top right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00C8FF))
                        .border(1.5.dp, NeonWhite, CircleShape)
                        .clickable { showModeSelectDialog = false },
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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "SELECT MODE (${currentMode.title})",
                        color = NeonWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )

                    // Option 1: VS Computer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, NeonCyan, RoundedCornerShape(14.dp))
                            .clickable {
                                showModeSelectDialog = false
                                onOpponentSelected(OpponentType.VS_AI)
                                onOpenDifficultyDialog()
                            }
                            .testTag("btn_modal_vs_computer"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👤 vs 🖥️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "VS COMPUTER (${uiState.aiDifficulty.label})",
                                color = NeonCyanLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Option 2: 2 Players (Local)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, Color(0xFFFFEA00), RoundedCornerShape(14.dp))
                            .clickable {
                                showModeSelectDialog = false
                                onOpponentSelected(OpponentType.VS_PLAYER)
                                onStartGame()
                            }
                            .testTag("btn_modal_vs_player"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("👤 vs 👤", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "2 PLAYERS (LOCAL)",
                                color = Color(0xFFFFEA00),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    // Option 3: Campaign Matrix
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0D1445))
                            .border(2.dp, Color(0xFFFF9900), RoundedCornerShape(14.dp))
                            .clickable {
                                showModeSelectDialog = false
                                onOpponentSelected(OpponentType.CAMPAIGN)
                                onStartGame()
                            }
                            .testTag("btn_modal_campaign"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text("⭐", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CAMPAIGN MATRIX",
                                color = Color(0xFFFFCC80),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Fullscreen Gift Event Dialog Overlay
    if (uiState.isGiftEventOpen) {
        GiftEventDialog(
            isOpen = uiState.isGiftEventOpen,
            onDismiss = onCloseGiftEvent,
            onProgressUpdate = onUpdateGiftEventProgress,
            onClaimReward = onClaimGiftReward
        )
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

/**
 * Cyberpunk 7-Day Daily Reward System (50, 100, 150, 200, 250, 300, 1000 Coins).
 * Once per day claim with 30-second Auto-Claim & automatic return to Home Screen.
 */
@Composable
fun DailyRewardCard(
    uiState: GameUiState,
    onClaimClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isClaimedToday = uiState.isDailyClaimedToday
    val currentDay = uiState.dailyStreakDay
    val todayRewardCoins = uiState.todayDailyRewardCoins
    val rewardsList = listOf(50, 100, 150, 200, 250, 300, 1000)

    val cardBorderColor = if (isClaimedToday) Color(0xFF00E676) else NeonGold

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1E0C38),
                        Color(0xFF0D0622),
                        Color(0xFF160930)
                    )
                )
            )
            .border(2.dp, cardBorderColor.copy(alpha = 0.85f), RoundedCornerShape(18.dp))
            .padding(12.dp)
            .testTag("daily_reward_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row: Badge & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFFFFD700), Color(0xFFFF9100))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CardGiftcard,
                            contentDescription = "Daily Reward",
                            tint = Color(0xFF1A0A00),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎁 7-DAYS DAILY REWARD",
                            color = NeonGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = if (isClaimedToday) "Claimed for today! Next reward tomorrow." else "Day $currentDay Active: Claim +$todayRewardCoins Coins!",
                            color = if (isClaimedToday) Color(0xFF00E676) else NeonCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Reward Streak Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF311B92))
                        .border(1.dp, NeonGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "DAY $currentDay/7",
                        color = NeonGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7-Days Reward Horizontal Scroll / Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rewardsList.forEachIndexed { index, reward ->
                    val dayNum = index + 1
                    val isPastClaimed = dayNum < currentDay || (dayNum == currentDay && isClaimedToday)
                    val isTodayActive = dayNum == currentDay && !isClaimedToday
                    val isDay7 = dayNum == 7

                    val itemBg = when {
                        isTodayActive -> Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF311B92)))
                        isPastClaimed -> Brush.verticalGradient(listOf(Color(0xFF1B5E20), Color(0xFF0A2E0E)))
                        else -> Brush.verticalGradient(listOf(Color(0xFF1F1A3A), Color(0xFF120E24)))
                    }

                    val itemBorderColor = when {
                        isTodayActive -> NeonGold
                        isPastClaimed -> Color(0xFF00E676)
                        isDay7 -> Color(0xFFFF4081)
                        else -> Color(0xFF3A3459)
                    }

                    Box(
                        modifier = Modifier
                            .width(62.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(itemBg)
                            .border(1.5.dp, itemBorderColor, RoundedCornerShape(10.dp))
                            .padding(vertical = 6.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Day $dayNum",
                                color = if (isTodayActive) NeonGold else NeonWhite.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            if (isPastClaimed) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Claimed",
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = if (isDay7) "🔥" else "🪙",
                                    fontSize = 13.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = "+$reward",
                                color = if (isDay7) Color(0xFFFFD700) else if (isTodayActive) NeonGold else NeonWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isClaimedToday) {
                            Brush.horizontalGradient(listOf(Color(0xFF2E7D32), Color(0xFF1B5E20)))
                        } else {
                            Brush.horizontalGradient(listOf(Color(0xFFFFB300), Color(0xFFFF6D00), Color(0xFFFFAB00)))
                        }
                    )
                    .border(1.5.dp, if (isClaimedToday) Color(0xFF00E676) else NeonWhite.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .clickable {
                        if (!isClaimedToday) {
                            onClaimClick()
                        }
                    }
                    .testTag("btn_daily_reward_claim"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isClaimedToday) Icons.Default.Check else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isClaimedToday) Color.White else Color(0xFF1A0A00),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isClaimedToday) {
                            "CLAIMED TODAY (DAY $currentDay) ✅"
                        } else {
                            "CLAIM DAY $currentDay (+${todayRewardCoins} COINS)"
                        },
                        color = if (isClaimedToday) Color.White else Color(0xFF1A0A00),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.8.sp
                    )
                }
            }
        }
    }
}

