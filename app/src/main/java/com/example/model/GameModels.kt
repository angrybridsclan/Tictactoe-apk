package com.example.model

enum class GameType(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconEmoji: String,
    val stars: Int = 5
) {
    TIC_TAC_TOE("tictactoe", "TIC TAC TOE", "Classic 3×3, 6×6 & 12×12 Neon Grid", "⭕", 5)
}

enum class GameMode(
    val title: String,
    val subtitle: String,
    val gridSize: Int,
    val targetToWin: Int
) {
    MINI(
        title = "Mini (3×3)",
        subtitle = "3 in a row to win",
        gridSize = 3,
        targetToWin = 3
    ),
    BIG(
        title = "Big (6×6)",
        subtitle = "4 in a row to win",
        gridSize = 6,
        targetToWin = 4
    ),
    MEGA(
        title = "Mega (12×12)",
        subtitle = "5 in a row to win",
        gridSize = 12,
        targetToWin = 5
    )
}

enum class OpponentType(val displayName: String) {
    VS_AI("Player vs Computer"),
    VS_PLAYER("Player vs Player"),
    CAMPAIGN("Campaign Levels")
}

enum class Player(val symbol: String) {
    O("O"),
    X("X");

    fun other(): Player = if (this == O) X else O
}

enum class AIDifficulty(val label: String, val description: String, val emoji: String) {
    EASY("EASY", "Casual & Relaxed", "😀"),
    MEDIUM("MEDIUM", "Tactical & Balanced", "😊"),
    MASTER("HARD", "Very Hard & Unbeatable", "😈")
}

data class WinningLine(
    val startRow: Int,
    val startCol: Int,
    val endRow: Int,
    val endCol: Int,
    val winningCells: Set<Pair<Int, Int>>
)

data class GameScore(
    val playerOWins: Int = 0,
    val playerXWins: Int = 0,
    val draws: Int = 0
)

data class GameUiState(
    val selectedGame: GameType = GameType.TIC_TAC_TOE,
    val mode: GameMode = GameMode.MINI,
    val opponentType: OpponentType = OpponentType.VS_AI,
    val aiDifficulty: AIDifficulty = AIDifficulty.MASTER,
    val grid: List<Player?> = List(9) { null },
    val currentPlayer: Player = Player.O,
    val winner: Player? = null,
    val isDraw: Boolean = false,
    val winningLine: WinningLine? = null,
    val isAiThinking: Boolean = false,
    val scores: GameScore = GameScore(),
    val isSoundEnabled: Boolean = true,
    val isMusicEnabled: Boolean = true,
    val isVibrationEnabled: Boolean = true,
    val moveCount: Int = 0,
    val coins: Int = 50,
    val activeThemeId: String = ThemeCatalog.CLASSIC_CYBER.id,
    val unlockedThemeIds: Set<String> = setOf(ThemeCatalog.CLASSIC_CYBER.id),
    val lastMatchEarnedCoins: Int = 0,
    val isRewardDoubled: Boolean = false,
    val isShopOpen: Boolean = false,
    val isPrivacyPolicyOpen: Boolean = false,
    val isDifficultyDialogOpen: Boolean = false,
    val isSettingsDialogOpen: Boolean = false,
    val isGiftEventOpen: Boolean = false,
    val giftEventSecondsLeft: Int = 30,
    val isGiftEventClaimed: Boolean = false,
    val isAdLoading: Boolean = false,
    val rewardToastMessage: String? = null
) {
    val activeTheme: NeonTheme
        get() = ThemeCatalog.getThemeById(activeThemeId)
}
