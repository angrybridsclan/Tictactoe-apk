package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ads.UnityBannerAd
import com.example.model.GameMode
import com.example.model.GameUiState
import com.example.model.OpponentType
import com.example.model.Player
import com.example.ui.components.CoinBalanceBadge
import com.example.ui.components.NeonGlowBackground
import com.example.ui.components.NeonGridBoard
import com.example.ui.components.NeonTargetIndicator
import com.example.ui.components.OxCoinIcon
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonDarkSurface
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonTextMuted
import com.example.ui.theme.NeonWhite

@Composable
fun GameScreen(
    uiState: GameUiState,
    onCellClick: (Int) -> Unit,
    onRestartGame: (Activity?) -> Unit,
    onBackToMenu: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleSound: () -> Unit = {},
    onOpenShop: () -> Unit,
    onDoubleReward: (Activity?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isGameOver = uiState.winner != null || uiState.isDraw
    val context = LocalContext.current
    val activity = context as? Activity

    NeonGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0x331F1045), CircleShape)
                        .border(1.dp, Color(0x446A3E9F), CircleShape)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Level Selection",
                        tint = NeonWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Coins Badge
                CoinBalanceBadge(
                    coins = uiState.coins,
                    onClick = onOpenShop
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x331F1045), CircleShape)
                            .border(1.dp, Color(0x446A3E9F), CircleShape)
                            .testTag("settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Open Settings",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { onRestartGame(activity) },
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color(0x331F1045), CircleShape)
                            .border(1.dp, Color(0x446A3E9F), CircleShape)
                            .testTag("restart_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restart Round",
                            tint = NeonGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Players Header
            PlayerVsHeader(
                uiState = uiState,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            // Central Board with dynamic theme
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                NeonGridBoard(
                    grid = uiState.grid,
                    mode = uiState.mode,
                    winningLine = uiState.winningLine,
                    onCellClick = onCellClick,
                    activeTheme = uiState.activeTheme,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = if (uiState.mode == GameMode.MINI) 12.dp else 4.dp)
                )
            }

            // Bottom Win Target Indicator & Scores
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                NeonTargetIndicator(
                    mode = uiState.mode,
                    isWon = uiState.winner != null
                )

                Spacer(modifier = Modifier.height(2.dp))

                ScoreIndicator(
                    uiState = uiState,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Game Over Celebration / Status Banner
            AnimatedVisibility(
                visible = isGameOver,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                GameOverBanner(
                    uiState = uiState,
                    onPlayAgain = { onRestartGame(activity) },
                    onBackToMenu = onBackToMenu,
                    onDoubleReward = { onDoubleReward(activity) }
                )
            }

            // Unity Banner Ad BELOW the Tic Tac Toe board
            UnityBannerAd(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun PlayerVsHeader(
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    val theme = uiState.activeTheme
    val isOTurn = uiState.currentPlayer == Player.O && uiState.winner == null && !uiState.isDraw
    val isXTurn = uiState.currentPlayer == Player.X && uiState.winner == null && !uiState.isDraw

    val infiniteTransition = rememberInfiniteTransition(label = "turn_pulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Player O
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isOTurn) theme.playerOColor.copy(alpha = 0.2f * pulseGlow) else Color(0x22130A2A)
                    )
                    .border(
                        width = if (isOTurn) 2.5.dp else 1.dp,
                        color = if (isOTurn) theme.playerOColor.copy(alpha = pulseGlow) else theme.playerOColor.copy(alpha = 0.35f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "O",
                    color = theme.playerOColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (uiState.opponentType == OpponentType.VS_AI) "PLAYER" else "PLAYER 1",
                color = if (isOTurn) theme.playerOColor else NeonTextMuted,
                fontSize = 12.sp,
                fontWeight = if (isOTurn) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.8.sp
            )
        }

        // Center VS
        Text(
            text = "vs",
            color = NeonPurple,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 6.dp)
        )

        // Player X
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isXTurn) theme.playerXColor.copy(alpha = 0.2f * pulseGlow) else Color(0x22130A2A)
                    )
                    .border(
                        width = if (isXTurn) 2.5.dp else 1.dp,
                        color = if (isXTurn) theme.playerXColor.copy(alpha = pulseGlow) else theme.playerXColor.copy(alpha = 0.35f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "X",
                    color = theme.playerXColor,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = if (uiState.opponentType == OpponentType.VS_AI) {
                    if (uiState.isAiThinking) "THINKING..." else "COMPUTER"
                } else {
                    "PLAYER 2"
                },
                color = if (isXTurn) theme.playerXColor else NeonTextMuted,
                fontSize = 12.sp,
                fontWeight = if (isXTurn) FontWeight.Bold else FontWeight.Medium,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
private fun ScoreIndicator(
    uiState: GameUiState,
    modifier: Modifier = Modifier
) {
    val scores = uiState.scores
    val theme = uiState.activeTheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${scores.playerOWins}",
                color = theme.playerOColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (uiState.opponentType == OpponentType.VS_AI) "P1 (O)" else "Player 1",
                color = NeonTextMuted,
                fontSize = 10.sp
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(Color(0x33FFFFFF))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${scores.draws}",
                color = NeonGold,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Draws",
                color = NeonTextMuted,
                fontSize = 10.sp
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(20.dp)
                .background(Color(0x33FFFFFF))
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${scores.playerXWins}",
                color = theme.playerXColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (uiState.opponentType == OpponentType.VS_AI) "AI (X)" else "Player 2",
                color = NeonTextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun GameOverBanner(
    uiState: GameUiState,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit,
    onDoubleReward: () -> Unit
) {
    val theme = uiState.activeTheme
    val titleText = when {
        uiState.winner == Player.O -> if (uiState.opponentType == OpponentType.VS_AI) "YOU WON!" else "PLAYER 1 WINS!"
        uiState.winner == Player.X -> if (uiState.opponentType == OpponentType.VS_AI) "COMPUTER WINS!" else "PLAYER 2 WINS!"
        else -> "IT'S A DRAW!"
    }

    val bannerColor = when {
        uiState.winner == Player.O -> theme.playerOColor
        uiState.winner == Player.X -> theme.playerXColor
        else -> NeonGold
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonDarkSurface,
                        Color(0xFF1B0C3C)
                    )
                )
            )
            .border(
                width = 2.dp,
                color = bannerColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = titleText,
                color = bannerColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )

            // Earned coins banner
            if (uiState.lastMatchEarnedCoins > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OxCoinIcon(size = 18.dp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+${uiState.lastMatchEarnedCoins} COINS EARNED!",
                        color = NeonGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }

                // Double with Rewarded Ad button
                if (!uiState.isRewardDoubled) {
                    Button(
                        onClick = onDoubleReward,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .testTag("btn_double_reward")
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
                                text = "🎬 WATCH AD TO DOUBLE (+${uiState.lastMatchEarnedCoins * 2} COINS)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = bannerColor,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_play_again")
                ) {
                    Text(
                        text = "PLAY AGAIN",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                OutlinedButton(
                    onClick = onBackToMenu,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NeonWhite
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("btn_menu")
                ) {
                    Text(
                        text = "LEVELS",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
