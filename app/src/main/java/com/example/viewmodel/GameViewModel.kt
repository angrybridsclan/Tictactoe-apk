package com.example.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.DualAdsManager
import com.example.audio.CyberMusicPlayer
import com.example.audio.NeonSoundManager
import com.example.game.TicTacToeEngine
import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.model.GameScore
import com.example.model.GameType
import com.example.model.GameUiState
import com.example.model.OpponentType
import com.example.model.Player
import com.example.model.ThemeCatalog
import com.example.model.UserDataManager
import com.example.notification.NotificationHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    val soundManager = NeonSoundManager(application.applicationContext)
    val userDataManager = UserDataManager(application.applicationContext)

    private val _uiState = MutableStateFlow(
        GameUiState(
            coins = userDataManager.coins,
            activeThemeId = userDataManager.selectedThemeId,
            unlockedThemeIds = userDataManager.unlockedThemes,
            isSoundEnabled = userDataManager.isSoundEnabled,
            isMusicEnabled = userDataManager.isMusicEnabled,
            isVibrationEnabled = userDataManager.isVibrationEnabled,
            dailyStreakDay = userDataManager.getEffectiveDailyStreakDay(),
            isDailyClaimedToday = userDataManager.isDailyRewardClaimedToday(),
            isGiftEventClaimed = userDataManager.isDailyRewardClaimedToday(),
            todayDailyRewardCoins = userDataManager.getRewardForDay(userDataManager.getEffectiveDailyStreakDay())
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        // Initialize Dual Ad Mediation (AdMob + Unity Ads) for maximum fill and revenue
        DualAdsManager.initialize(application.applicationContext)
        // Refresh daily streak status
        refreshDailyRewardState()
        // Start background cyber soundtrack if enabled
        if (userDataManager.isMusicEnabled) {
            CyberMusicPlayer.startMusic(true)
        }
    }

    fun refreshDailyRewardState() {
        val streak = userDataManager.getEffectiveDailyStreakDay()
        val isClaimed = userDataManager.isDailyRewardClaimedToday()
        val reward = userDataManager.getRewardForDay(streak)
        _uiState.update {
            it.copy(
                dailyStreakDay = streak,
                isDailyClaimedToday = isClaimed,
                isGiftEventClaimed = isClaimed,
                todayDailyRewardCoins = reward
            )
        }
    }

    fun selectMode(mode: GameMode) {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { current ->
            val totalCells = mode.gridSize * mode.gridSize
            current.copy(
                mode = mode,
                grid = List(totalCells) { null },
                currentPlayer = Player.O,
                winner = null,
                isDraw = false,
                winningLine = null,
                isAiThinking = false,
                moveCount = 0,
                lastMatchEarnedCoins = 0,
                isRewardDoubled = false
            )
        }
    }

    fun selectOpponentType(opponentType: OpponentType) {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { current ->
            current.copy(
                opponentType = opponentType,
                scores = GameScore()
            )
        }
        resetGame(keepScores = true)
    }

    fun selectDifficulty(difficulty: AIDifficulty) {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(aiDifficulty = difficulty) }
    }

    fun toggleSound() {
        val newState = !_uiState.value.isSoundEnabled
        userDataManager.isSoundEnabled = newState
        if (newState) soundManager.playButtonClick(true)
        _uiState.update { it.copy(isSoundEnabled = newState) }
    }

    fun toggleMusic() {
        val newState = !_uiState.value.isMusicEnabled
        userDataManager.isMusicEnabled = newState
        CyberMusicPlayer.setMuted(!newState)
        if (newState) soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isMusicEnabled = newState) }
    }

    fun toggleVibration() {
        val newState = !_uiState.value.isVibrationEnabled
        userDataManager.isVibrationEnabled = newState
        if (newState) soundManager.triggerVibrate(durationMs = 30, enabled = true)
        _uiState.update { it.copy(isVibrationEnabled = newState) }
    }

    fun openSettingsDialog() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isSettingsDialogOpen = true) }
    }

    fun closeSettingsDialog() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isSettingsDialogOpen = false) }
    }

    fun openShop() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isShopOpen = true) }
    }

    fun closeShop() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isShopOpen = false) }
    }

    fun openPrivacyPolicy() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isPrivacyPolicyOpen = true) }
    }

    fun closePrivacyPolicy() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isPrivacyPolicyOpen = false) }
    }

    fun openGiftEvent() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isGiftEventOpen = true) }
    }

    fun closeGiftEvent() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isGiftEventOpen = false) }
    }

    fun updateGiftEventProgress(secondsLeft: Int) {
        _uiState.update { it.copy(giftEventSecondsLeft = secondsLeft) }
    }

    fun claimGiftEventReward() {
        val (rewardCoins, claimedDay) = userDataManager.claimDailyReward()
        soundManager.playWinSound(_uiState.value.isSoundEnabled)
        _uiState.update {
            it.copy(
                coins = userDataManager.coins,
                isGiftEventOpen = false,
                giftEventSecondsLeft = 0,
                isGiftEventClaimed = true,
                isDailyClaimedToday = true,
                dailyStreakDay = claimedDay,
                todayDailyRewardCoins = rewardCoins,
                rewardToastMessage = "🎉 Day $claimedDay Reward Claimed: +$rewardCoins OX Coins Added!"
            )
        }
    }

    fun selectTheme(themeId: String) {
        if (_uiState.value.unlockedThemeIds.contains(themeId)) {
            userDataManager.selectedThemeId = themeId
            soundManager.playButtonClick(_uiState.value.isSoundEnabled)
            _uiState.update { it.copy(activeThemeId = themeId) }
        }
    }

    fun buyTheme(themeId: String, price: Int) {
        val success = userDataManager.unlockTheme(themeId, price)
        if (success) {
            soundManager.playWinSound(_uiState.value.isSoundEnabled)
            _uiState.update {
                it.copy(
                    coins = userDataManager.coins,
                    unlockedThemeIds = userDataManager.unlockedThemes,
                    activeThemeId = themeId
                )
            }
        }
    }

    fun watchUnityInterstitialAd(activity: Activity?) {
        if (activity == null) {
            val newTotal = userDataManager.addCoins(50)
            soundManager.playWinSound(_uiState.value.isSoundEnabled)
            _uiState.update {
                it.copy(
                    coins = newTotal,
                    rewardToastMessage = "⚡ +50 Unity Bonus Coins Received!"
                )
            }
            return
        }

        com.example.ads.UnityAdsManager.showInterstitial(activity) {
            val newTotal = userDataManager.addCoins(50)
            soundManager.playWinSound(_uiState.value.isSoundEnabled)
            _uiState.update {
                it.copy(
                    coins = newTotal,
                    rewardToastMessage = "⚡ +50 Unity Bonus Coins Claimed!"
                )
            }
        }
    }

    fun watchAdForFreeCoins(activity: Activity?) {
        if (activity == null) {
            val newTotal = userDataManager.addCoins(100)
            soundManager.playWinSound(_uiState.value.isSoundEnabled)
            _uiState.update {
                it.copy(
                    coins = newTotal,
                    rewardToastMessage = "+100 Coins Received!"
                )
            }
            return
        }

        DualAdsManager.showRewardedAd(
            activity = activity,
            onUserEarnedReward = {
                val newTotal = userDataManager.addCoins(100)
                soundManager.playWinSound(_uiState.value.isSoundEnabled)
                _uiState.update {
                    it.copy(
                        coins = newTotal,
                        rewardToastMessage = "🎉 +100 Free Cyber Coins Claimed!"
                    )
                }
            },
            onAdNotReady = {
                _uiState.update {
                    it.copy(
                        rewardToastMessage = "Reward Ad is loading, please try again."
                    )
                }
            }
        )
    }

    fun doubleMatchRewardWithAd(activity: Activity?) {
        val earned = _uiState.value.lastMatchEarnedCoins
        if (earned <= 0 || _uiState.value.isRewardDoubled) return

        if (activity == null) {
            val newTotal = userDataManager.addCoins(earned)
            soundManager.playWinSound(_uiState.value.isSoundEnabled)
            _uiState.update {
                it.copy(
                    coins = newTotal,
                    isRewardDoubled = true,
                    rewardToastMessage = "2X Reward Claimed: +$earned Extra Coins!"
                )
            }
            return
        }

        DualAdsManager.showRewardedAd(
            activity = activity,
            onUserEarnedReward = {
                val newTotal = userDataManager.addCoins(earned)
                soundManager.playWinSound(_uiState.value.isSoundEnabled)
                _uiState.update {
                    it.copy(
                        coins = newTotal,
                        isRewardDoubled = true,
                        rewardToastMessage = "🔥 2X Reward Claimed: +$earned Extra Coins!"
                    )
                }
            },
            onAdNotReady = {
                _uiState.update {
                    it.copy(
                        rewardToastMessage = "Reward Ad is loading, please try again."
                    )
                }
            }
        )
    }

    fun clearRewardToast() {
        _uiState.update { it.copy(rewardToastMessage = null) }
    }

    fun onCellClicked(index: Int, activity: Activity? = null) {
        val currentState = _uiState.value
        if (currentState.grid[index] != null || currentState.winner != null || currentState.isDraw) return
        if (currentState.isAiThinking) return
        if (currentState.opponentType == OpponentType.VS_AI && currentState.currentPlayer == Player.X) return

        makeMove(index, currentState.currentPlayer, activity)
    }

    private fun makeMove(index: Int, player: Player, activity: Activity? = null) {
        val currentState = _uiState.value
        val newGrid = currentState.grid.toMutableList().also { it[index] = player }

        soundManager.playMoveSound(isO = player == Player.O, enabled = currentState.isSoundEnabled)
        soundManager.triggerVibrate(durationMs = 25, enabled = currentState.isVibrationEnabled)

        // Check for Win
        val win = TicTacToeEngine.checkWin(newGrid, currentState.mode)
        if (win != null) {
            val isHumanWin = (currentState.opponentType == OpponentType.VS_AI && player == Player.O) ||
                currentState.opponentType == OpponentType.VS_PLAYER

            val earnedCoins = calculateMatchReward(
                winner = player,
                mode = currentState.mode,
                difficulty = currentState.aiDifficulty,
                opponentType = currentState.opponentType,
                isDraw = false
            )
            val updatedCoins = userDataManager.addCoins(earnedCoins)
            userDataManager.matchesPlayed = userDataManager.matchesPlayed + 1

            if (isHumanWin) {
                soundManager.playWinSound(currentState.isSoundEnabled)
                soundManager.triggerWinVibrate(currentState.isVibrationEnabled)

                // Trigger victory system notification
                NotificationHelper.sendVictoryNotification(
                    context = getApplication<Application>().applicationContext,
                    gameTitle = "Tic Tac Toe (${currentState.mode.title})",
                    coinsEarned = earnedCoins
                )
            } else {
                soundManager.playDrawSound(currentState.isSoundEnabled)
            }

            val newScores = if (player == Player.O) {
                currentState.scores.copy(playerOWins = currentState.scores.playerOWins + 1)
            } else {
                currentState.scores.copy(playerXWins = currentState.scores.playerXWins + 1)
            }

            _uiState.update { state ->
                state.copy(
                    grid = newGrid,
                    winner = player,
                    winningLine = win,
                    scores = newScores,
                    coins = updatedCoins,
                    lastMatchEarnedCoins = earnedCoins,
                    isRewardDoubled = false,
                    moveCount = state.moveCount + 1
                )
            }
            return
        }

        // Check for Draw
        val isFull = newGrid.none { it == null }
        if (isFull) {
            val drawCoins = calculateMatchReward(
                winner = null,
                mode = currentState.mode,
                difficulty = currentState.aiDifficulty,
                opponentType = currentState.opponentType,
                isDraw = true
            )
            val updatedCoins = userDataManager.addCoins(drawCoins)
            userDataManager.matchesPlayed = userDataManager.matchesPlayed + 1
            soundManager.playDrawSound(currentState.isSoundEnabled)

            _uiState.update { state ->
                state.copy(
                    grid = newGrid,
                    isDraw = true,
                    scores = state.scores.copy(draws = state.scores.draws + 1),
                    coins = updatedCoins,
                    lastMatchEarnedCoins = drawCoins,
                    isRewardDoubled = false,
                    moveCount = state.moveCount + 1
                )
            }
            return
        }

        // Advance turn
        val nextPlayer = player.other()
        _uiState.update { state ->
            state.copy(
                grid = newGrid,
                currentPlayer = nextPlayer,
                moveCount = state.moveCount + 1
            )
        }

        // Check if AI turn is next
        if (currentState.opponentType == OpponentType.VS_AI && nextPlayer == Player.X) {
            triggerAiTurn(newGrid)
        }
    }

    private fun calculateMatchReward(
        winner: Player?,
        mode: GameMode,
        difficulty: AIDifficulty,
        opponentType: OpponentType,
        isDraw: Boolean
    ): Int {
        if (isDraw) return 10

        val baseCoins = if (opponentType == OpponentType.VS_AI) {
            if (winner == Player.O) {
                when (difficulty) {
                    AIDifficulty.EASY -> 20
                    AIDifficulty.MEDIUM -> 35
                    AIDifficulty.MASTER -> 50
                }
            } else {
                5 // Consolation for loss
            }
        } else {
            25 // PvP match win
        }

        val modeBonus = when (mode) {
            GameMode.MINI -> 0
            GameMode.BIG -> 15
            GameMode.MEGA -> 30
        }
        return baseCoins + modeBonus
    }

    private fun triggerAiTurn(currentBoard: List<Player?>) {
        _uiState.update { it.copy(isAiThinking = true) }

        viewModelScope.launch {
            delay(380)

            val state = _uiState.value
            if (state.winner != null || state.isDraw) {
                _uiState.update { it.copy(isAiThinking = false) }
                return@launch
            }

            val aiMove = TicTacToeEngine.getAiMove(
                board = currentBoard,
                mode = state.mode,
                difficulty = state.aiDifficulty,
                aiPlayer = Player.X
            )

            _uiState.update { it.copy(isAiThinking = false) }

            if (aiMove in currentBoard.indices && currentBoard[aiMove] == null) {
                makeMove(aiMove, Player.X)
            }
        }
    }

    fun selectGame(game: GameType) {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(selectedGame = game) }
    }

    fun openDifficultyDialog() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isDifficultyDialogOpen = true) }
    }

    fun closeDifficultyDialog() {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)
        _uiState.update { it.copy(isDifficultyDialogOpen = false) }
    }

    fun addGameRewardCoins(earned: Int, gameTitle: String) {
        val newTotal = userDataManager.addCoins(earned)
        soundManager.playWinSound(_uiState.value.isSoundEnabled)
        _uiState.update {
            it.copy(
                coins = newTotal,
                rewardToastMessage = "+$earned Coins Earned in $gameTitle!"
            )
        }
    }

    fun resetGame(keepScores: Boolean = true, activity: Activity? = null) {
        soundManager.playButtonClick(_uiState.value.isSoundEnabled)

        // Show ad loading flag if interstitial ready
        _uiState.update { it.copy(isAdLoading = true) }

        // Trigger interstitial ad check between matches (Dual Waterfall: AdMob -> Unity Ads)
        DualAdsManager.onMatchFinished(activity) {
            _uiState.update { state ->
                val totalCells = state.mode.gridSize * state.mode.gridSize
                state.copy(
                    grid = List(totalCells) { null },
                    currentPlayer = Player.O,
                    winner = null,
                    isDraw = false,
                    winningLine = null,
                    isAiThinking = false,
                    moveCount = 0,
                    lastMatchEarnedCoins = 0,
                    isRewardDoubled = false,
                    isAdLoading = false,
                    scores = if (keepScores) state.scores else GameScore()
                )
            }
        }
    }
}
