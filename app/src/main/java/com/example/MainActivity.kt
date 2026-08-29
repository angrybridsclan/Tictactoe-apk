package com.example

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AppOpenAdManager
import com.example.ads.DualAdsManager
import com.example.audio.CyberMusicPlayer
import com.example.notification.NotificationHelper
import com.example.ui.components.LevelDifficultyDialog
import com.example.ui.components.PrivacyPolicyDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.ThemeShopDialog
import com.example.ui.screens.GameScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.screens.SelectLevelScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonDarkBg
import com.example.viewmodel.GameViewModel

enum class AppScreen {
    LOADING,
    LEVEL_SELECT,
    GAME
}

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Dual Ad Mediation (AdMob + Unity Ads) for maximum fill & revenue
        DualAdsManager.initialize(applicationContext)

        // Initialize Notification Channels
        NotificationHelper.createNotificationChannels(applicationContext)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = NeonDarkBg
                ) {
                    TicTacToeApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Try showing AdMob App Open Ad if available
        AppOpenAdManager.showAdIfAvailable(this)
        // Cancel pending inactivity notification since user is active
        NotificationHelper.cancelOfflineReminder(this)
        // Resume background soundtrack if enabled
        CyberMusicPlayer.resumeMusic(viewModel.userDataManager.isMusicEnabled)
    }

    override fun onPause() {
        super.onPause()
        // Pause background soundtrack
        CyberMusicPlayer.pauseMusic()
        // Schedule offline reminder to notify user after 1 hour of inactivity
        NotificationHelper.scheduleOfflineReminder(this)
    }
}

@Composable
fun TicTacToeApp(viewModel: GameViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentScreen by remember { mutableStateOf(AppScreen.LOADING) }
    val context = LocalContext.current
    val activity = context as? Activity

    // Request Notification Permission on Android 13+ (POST_NOTIFICATIONS)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = Manifest.permission.POST_NOTIFICATIONS
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(permission)
            }
        }
    }

    // Toast notifications for rewards
    LaunchedEffect(uiState.rewardToastMessage) {
        uiState.rewardToastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearRewardToast()
        }
    }

    // Back handler for returning from game to level select
    BackHandler(enabled = currentScreen == AppScreen.GAME) {
        currentScreen = AppScreen.LEVEL_SELECT
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (initialState == AppScreen.LOADING) {
                fadeIn() togetherWith fadeOut()
            } else if (targetState == AppScreen.GAME) {
                slideInHorizontally { it } + fadeIn() togetherWith
                        slideOutHorizontally { -it } + fadeOut()
            } else {
                slideInHorizontally { -it } + fadeIn() togetherWith
                        slideOutHorizontally { it } + fadeOut()
            }
        },
        label = "screen_nav_transition"
    ) { screen ->
        when (screen) {
            AppScreen.LOADING -> {
                LoadingScreen(
                    onLoadingFinished = {
                        currentScreen = AppScreen.LEVEL_SELECT
                    }
                )
            }
            AppScreen.LEVEL_SELECT -> {
                SelectLevelScreen(
                    uiState = uiState,
                    onModeSelected = { mode -> viewModel.selectMode(mode) },
                    onOpponentSelected = { opponent -> viewModel.selectOpponentType(opponent) },
                    onDifficultySelected = { diff -> viewModel.selectDifficulty(diff) },
                    onOpenDifficultyDialog = { viewModel.openDifficultyDialog() },
                    onOpenSettings = { viewModel.openSettingsDialog() },
                    onToggleSound = { viewModel.toggleSound() },
                    onToggleMusic = { viewModel.toggleMusic() },
                    onToggleVibration = { viewModel.toggleVibration() },
                    onOpenShop = { viewModel.openShop() },
                    onOpenPrivacyPolicy = { viewModel.openPrivacyPolicy() },
                    onStartGame = { currentScreen = AppScreen.GAME }
                )
            }
            AppScreen.GAME -> {
                GameScreen(
                    uiState = uiState,
                    onCellClick = { index -> viewModel.onCellClicked(index, activity) },
                    onRestartGame = { act -> viewModel.resetGame(keepScores = true, activity = act ?: activity) },
                    onBackToMenu = { currentScreen = AppScreen.LEVEL_SELECT },
                    onOpenSettings = { viewModel.openSettingsDialog() },
                    onOpenShop = { viewModel.openShop() },
                    onDoubleReward = { act -> viewModel.doubleMatchRewardWithAd(act ?: activity) }
                )
            }
        }
    }

    // AI Difficulty Selection Dialog
    if (uiState.isDifficultyDialogOpen) {
        LevelDifficultyDialog(
            currentDifficulty = uiState.aiDifficulty,
            onDifficultySelected = { diff ->
                viewModel.selectDifficulty(diff)
                viewModel.closeDifficultyDialog()
                currentScreen = AppScreen.GAME
            },
            onDismiss = { viewModel.closeDifficultyDialog() }
        )
    }

    // Settings Dialog overlay
    if (uiState.isSettingsDialogOpen) {
        SettingsDialog(
            uiState = uiState,
            onToggleSound = { viewModel.toggleSound() },
            onToggleMusic = { viewModel.toggleMusic() },
            onToggleVibration = { viewModel.toggleVibration() },
            onDifficultySelected = { diff -> viewModel.selectDifficulty(diff) },
            onOpenPrivacyPolicy = {
                viewModel.closeSettingsDialog()
                viewModel.openPrivacyPolicy()
            },
            onDismiss = { viewModel.closeSettingsDialog() }
        )
    }

    // Theme Shop Dialog overlay
    if (uiState.isShopOpen) {
        ThemeShopDialog(
            uiState = uiState,
            onSelectTheme = { themeId -> viewModel.selectTheme(themeId) },
            onBuyTheme = { themeId, price -> viewModel.buyTheme(themeId, price) },
            onWatchAdForCoins = { viewModel.watchAdForFreeCoins(activity) },
            onDismiss = { viewModel.closeShop() }
        )
    }

    // Privacy Policy Dialog overlay
    if (uiState.isPrivacyPolicyOpen) {
        PrivacyPolicyDialog(
            onDismiss = { viewModel.closePrivacyPolicy() }
        )
    }
}
