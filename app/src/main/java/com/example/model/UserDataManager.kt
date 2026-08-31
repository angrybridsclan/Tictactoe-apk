package com.example.model

import android.content.Context
import android.content.SharedPreferences

class UserDataManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("neon_tictactoe_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COINS = "user_coins"
        private const val KEY_UNLOCKED_THEMES = "unlocked_themes"
        private const val KEY_SELECTED_THEME = "selected_theme"
        private const val KEY_MATCHES_PLAYED = "matches_played"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_LAST_DAILY_CLAIM_DATE = "last_daily_claim_date"
        private const val KEY_DAILY_STREAK = "daily_streak_day"
        private const val DEFAULT_STARTING_COINS = 50

        val SEVEN_DAYS_REWARDS = listOf(50, 100, 150, 200, 250, 300, 1000)
    }

    var lastDailyClaimDate: String
        get() = prefs.getString(KEY_LAST_DAILY_CLAIM_DATE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_DAILY_CLAIM_DATE, value).apply()

    var dailyStreakDay: Int
        get() = prefs.getInt(KEY_DAILY_STREAK, 1).coerceIn(1, 7)
        set(value) = prefs.edit().putInt(KEY_DAILY_STREAK, value.coerceIn(1, 7)).apply()

    fun getTodayDateString(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        return sdf.format(java.util.Date())
    }

    fun isDailyRewardClaimedToday(): Boolean {
        val today = getTodayDateString()
        return lastDailyClaimDate == today
    }

    /**
     * Calculates the active claimable day (1 to 7) based on streak continuity.
     */
    fun getEffectiveDailyStreakDay(): Int {
        val lastDate = lastDailyClaimDate
        if (lastDate.isEmpty()) return 1

        val today = getTodayDateString()
        if (lastDate == today) {
            return dailyStreakDay
        }

        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
        try {
            val lastD = sdf.parse(lastDate)
            val currD = sdf.parse(today)
            if (lastD != null && currD != null) {
                val diffDays = (currD.time - lastD.time) / (1000 * 60 * 60 * 24)
                if (diffDays == 1L) {
                    // Next consecutive day
                    val nextDay = dailyStreakDay + 1
                    return if (nextDay > 7) 1 else nextDay
                } else if (diffDays > 1L) {
                    // Streak broken - reset to day 1
                    return 1
                }
            }
        } catch (_: Exception) {}

        return dailyStreakDay
    }

    fun getRewardForDay(day: Int): Int {
        val idx = (day - 1).coerceIn(0, SEVEN_DAYS_REWARDS.size - 1)
        return SEVEN_DAYS_REWARDS[idx]
    }

    /**
     * Executes the daily reward claim, adds coins, updates streak and save date.
     * Returns Pair(coinsAdded, claimedDayNumber)
     */
    fun claimDailyReward(): Pair<Int, Int> {
        val targetDay = getEffectiveDailyStreakDay()
        val rewardCoins = getRewardForDay(targetDay)

        addCoins(rewardCoins)
        dailyStreakDay = targetDay
        lastDailyClaimDate = getTodayDateString()

        return Pair(rewardCoins, targetDay)
    }

    var coins: Int
        get() = prefs.getInt(KEY_COINS, DEFAULT_STARTING_COINS)
        set(value) = prefs.edit().putInt(KEY_COINS, value.coerceAtLeast(0)).apply()

    var unlockedThemes: Set<String>
        get() = prefs.getStringSet(KEY_UNLOCKED_THEMES, setOf(ThemeCatalog.CLASSIC_CYBER.id))
            ?: setOf(ThemeCatalog.CLASSIC_CYBER.id)
        set(value) = prefs.edit().putStringSet(KEY_UNLOCKED_THEMES, value).apply()

    var selectedThemeId: String
        get() = prefs.getString(KEY_SELECTED_THEME, ThemeCatalog.CLASSIC_CYBER.id)
            ?: ThemeCatalog.CLASSIC_CYBER.id
        set(value) = prefs.edit().putString(KEY_SELECTED_THEME, value).apply()

    var matchesPlayed: Int
        get() = prefs.getInt(KEY_MATCHES_PLAYED, 0)
        set(value) = prefs.edit().putInt(KEY_MATCHES_PLAYED, value).apply()

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

    var isVibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    fun addCoins(amount: Int): Int {
        val newTotal = coins + amount
        coins = newTotal
        return newTotal
    }

    fun spendCoins(amount: Int): Boolean {
        if (coins >= amount) {
            coins -= amount
            return true
        }
        return false
    }

    fun unlockTheme(themeId: String, price: Int): Boolean {
        if (unlockedThemes.contains(themeId)) {
            selectedThemeId = themeId
            return true
        }
        if (spendCoins(price)) {
            val updated = unlockedThemes.toMutableSet()
            updated.add(themeId)
            unlockedThemes = updated
            selectedThemeId = themeId
            return true
        }
        return false
    }

    fun resetAllProgress() {
        coins = DEFAULT_STARTING_COINS
        unlockedThemes = setOf(ThemeCatalog.CLASSIC_CYBER.id)
        selectedThemeId = ThemeCatalog.CLASSIC_CYBER.id
        matchesPlayed = 0
    }
}
