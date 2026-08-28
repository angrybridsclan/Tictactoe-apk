package com.example.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.NeonCoral
import com.example.ui.theme.NeonCoralGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonCyanGlow
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonPurpleBorder

data class NeonTheme(
    val id: String,
    val name: String,
    val price: Int,
    val playerOColor: Color,
    val playerOGlow: Color,
    val playerXColor: Color,
    val playerXGlow: Color,
    val accentBorder: Color,
    val isDefault: Boolean = false
)

object ThemeCatalog {
    val CLASSIC_CYBER = NeonTheme(
        id = "classic_cyber",
        name = "Cyber Neon",
        price = 0,
        playerOColor = NeonCyan,
        playerOGlow = NeonCyanGlow,
        playerXColor = NeonCoral,
        playerXGlow = NeonCoralGlow,
        accentBorder = NeonPurpleBorder,
        isDefault = true
    )

    val GOLD_EMERALD = NeonTheme(
        id = "gold_emerald",
        name = "Gold & Matrix",
        price = 150,
        playerOColor = Color(0xFFFFD700),
        playerOGlow = Color(0x66FFD700),
        playerXColor = Color(0xFF00FF66),
        playerXGlow = Color(0x6600FF66),
        accentBorder = Color(0x99FFD700)
    )

    val SYNTHWAVE_SUNSET = NeonTheme(
        id = "synthwave",
        name = "Synthwave",
        price = 250,
        playerOColor = Color(0xFFFF007F),
        playerOGlow = Color(0x66FF007F),
        playerXColor = Color(0xFF39FF14),
        playerXGlow = Color(0x6639FF14),
        accentBorder = Color(0x99D500F9)
    )

    val FIRE_ICE = NeonTheme(
        id = "fire_ice",
        name = "Fire & Ice",
        price = 350,
        playerOColor = Color(0xFF00F0FF),
        playerOGlow = Color(0x6600F0FF),
        playerXColor = Color(0xFFFF4500),
        playerXGlow = Color(0x66FF4500),
        accentBorder = Color(0x9900F0FF)
    )

    val ELECTRIC_PULSE = NeonTheme(
        id = "electric_pulse",
        name = "Electric Volt",
        price = 450,
        playerOColor = Color(0xFFE040FB),
        playerOGlow = Color(0x66E040FB),
        playerXColor = Color(0xFFFFEA00),
        playerXGlow = Color(0x66FFEA00),
        accentBorder = Color(0x99E040FB)
    )

    val GALAXY_PLASMA = NeonTheme(
        id = "galaxy_plasma",
        name = "Galaxy Plasma",
        price = 550,
        playerOColor = Color(0xFF7C4DFF),
        playerOGlow = Color(0x667C4DFF),
        playerXColor = Color(0xFF00E676),
        playerXGlow = Color(0x6600E676),
        accentBorder = Color(0x997C4DFF)
    )

    val TOKYO_MIDNIGHT = NeonTheme(
        id = "tokyo_midnight",
        name = "Tokyo Midnight",
        price = 650,
        playerOColor = Color(0xFFFF00A0),
        playerOGlow = Color(0x66FF00A0),
        playerXColor = Color(0xFF00F5FF),
        playerXGlow = Color(0x6600F5FF),
        accentBorder = Color(0x99FF00A0)
    )

    val CRIMSON_BLOOD = NeonTheme(
        id = "crimson_blood",
        name = "Blood Moon",
        price = 750,
        playerOColor = Color(0xFFFF1744),
        playerOGlow = Color(0x66FF1744),
        playerXColor = Color(0xFFFFB300),
        playerXGlow = Color(0x66FFB300),
        accentBorder = Color(0x99FF1744)
    )

    val ARCTIC_FROST = NeonTheme(
        id = "arctic_frost",
        name = "Arctic Frost",
        price = 850,
        playerOColor = Color(0xFFE0F7FA),
        playerOGlow = Color(0x66E0F7FA),
        playerXColor = Color(0xFF18FFFF),
        playerXGlow = Color(0x6618FFFF),
        accentBorder = Color(0x9918FFFF)
    )

    val ROYAL_AMETHYST = NeonTheme(
        id = "royal_amethyst",
        name = "Royal Amethyst",
        price = 950,
        playerOColor = Color(0xFFBA68C8),
        playerOGlow = Color(0x66BA68C8),
        playerXColor = Color(0xFFFFD54F),
        playerXGlow = Color(0x66FFD54F),
        accentBorder = Color(0x99BA68C8)
    )

    val SOLAR_FLARE = NeonTheme(
        id = "solar_flare",
        name = "Solar Flare",
        price = 1100,
        playerOColor = Color(0xFFFF6D00),
        playerOGlow = Color(0x66FF6D00),
        playerXColor = Color(0xFFFFEE58),
        playerXGlow = Color(0x66FFEE58),
        accentBorder = Color(0x99FF6D00)
    )

    val ACID_LIME = NeonTheme(
        id = "acid_lime",
        name = "Acid Cyber",
        price = 1250,
        playerOColor = Color(0xFF76FF03),
        playerOGlow = Color(0x6676FF03),
        playerXColor = Color(0xFFF50057),
        playerXGlow = Color(0x66F50057),
        accentBorder = Color(0x9976FF03)
    )

    val OBSIDIAN_PHANTOM = NeonTheme(
        id = "obsidian_phantom",
        name = "Obsidian Ghost",
        price = 1400,
        playerOColor = Color(0xFFFFFFFF),
        playerOGlow = Color(0x66FFFFFF),
        playerXColor = Color(0xFFB0BEC5),
        playerXGlow = Color(0x66B0BEC5),
        accentBorder = Color(0x99CFD8DC)
    )

    val RETRO_ARCADE_84 = NeonTheme(
        id = "retro_arcade_84",
        name = "Arcade 1984",
        price = 1600,
        playerOColor = Color(0xFF00E5FF),
        playerOGlow = Color(0x6600E5FF),
        playerXColor = Color(0xFFFF9100),
        playerXGlow = Color(0x66FF9100),
        accentBorder = Color(0x99FF9100)
    )

    val allThemes: List<NeonTheme> = listOf(
        CLASSIC_CYBER,
        GOLD_EMERALD,
        SYNTHWAVE_SUNSET,
        FIRE_ICE,
        ELECTRIC_PULSE,
        GALAXY_PLASMA,
        TOKYO_MIDNIGHT,
        CRIMSON_BLOOD,
        ARCTIC_FROST,
        ROYAL_AMETHYST,
        SOLAR_FLARE,
        ACID_LIME,
        OBSIDIAN_PHANTOM,
        RETRO_ARCADE_84
    )

    fun getThemeById(id: String): NeonTheme {
        return allThemes.find { it.id == id } ?: CLASSIC_CYBER
    }
}
