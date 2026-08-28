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
        private const val DEFAULT_STARTING_COINS = 50
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
