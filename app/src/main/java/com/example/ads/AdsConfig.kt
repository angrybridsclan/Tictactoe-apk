package com.example.ads

/**
 * Production Ad Configuration for Dual Ad Networks:
 * Google AdMob + Unity Ads (Parallel / Hybrid Waterfall Mediation)
 *
 * Test Ads are OFF (testMode = false) to ensure live revenue generation.
 */
object AdsConfig {
    // Google AdMob Configuration
    const val ADMOB_APP_ID = "ca-app-pub-6949224585585477~9832601170"
    const val ADMOB_APP_OPEN_ID = "ca-app-pub-6949224585585477/2968304761"
    const val ADMOB_BANNER_ID = "ca-app-pub-6949224585585477/4538625390"
    const val ADMOB_INTERSTITIAL_ID = "ca-app-pub-6949224585585477/5693390008"
    const val ADMOB_REWARDED_ID = "ca-app-pub-6949224585585477/4940536991"
    const val ADMOB_NATIVE_ID = "ca-app-pub-6949224585585477/1037726059"
    const val ADMOB_REWARDED_INTERSTITIAL_ID = "ca-app-pub-6949224585585477/9832601170"

    // Unity Ads Configuration
    const val UNITY_GAME_ID = "5857887"
    const val UNITY_BANNER_PLACEMENT = "Banner_Android"
    const val UNITY_INTERSTITIAL_PLACEMENT = "Interstitial_Android"
    const val UNITY_REWARDED_PLACEMENT = "Rewarded_Android"

    // Live Production Mode: Test ads OFF for real impressions & revenue
    const val TEST_ADS_ENABLED = false

    // Waterfall priority settings
    const val PREFER_ADMOB_FIRST = true
}
