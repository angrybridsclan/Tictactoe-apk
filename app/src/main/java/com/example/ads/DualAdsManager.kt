package com.example.ads

import android.app.Activity
import android.content.Context
import android.util.Log

/**
 * Dual Ad Network Mediation & Waterfall Controller
 *
 * Combines Google AdMob & Unity Ads for Maximum Revenue and 100% Fill Rate.
 * - Waterfall Strategy: AdMob First (Highest eCPM) -> Unity Ads Fallback -> Automatic Preloading.
 * - Formats: App Open, Banner, Interstitial, Rewarded, and Native Ads.
 * - Test Ads: OFF (Real Live Production Ads).
 */
object DualAdsManager {
    private const val TAG = "DualAdsManager"

    private var matchCountSinceLastAd = 0
    private const val MATCHES_PER_INTERSTITIAL = 1

    fun initialize(context: Context, onComplete: (() -> Unit)? = null) {
        Log.d(TAG, "Initializing Dual Ad Networks (AdMob + Unity Ads)...")

        // 1. Initialize AdMob
        AdMobManager.initialize(context) {
            // Preload App Open Ad
            AppOpenAdManager.loadAd(context)
        }

        // 2. Initialize Unity Ads
        UnityAdsManager.initialize(context) {
            Log.d(TAG, "Unity Ads initialized in Dual Mediation")
        }

        onComplete?.invoke()
    }

    /**
     * Shows Interstitial Ad with waterfall priority (AdMob -> Unity Ads).
     */
    fun showInterstitial(
        activity: Activity,
        onAdClosed: () -> Unit
    ) {
        // Priority 1: AdMob Interstitial (Highest eCPM)
        if (AdMobManager.isInterstitialReady()) {
            Log.d(TAG, "Showing AdMob Interstitial Ad")
            val shown = AdMobManager.showInterstitial(activity) {
                // Preload next ads
                AdMobManager.preloadInterstitial(activity)
                UnityAdsManager.preloadAds(activity)
                onAdClosed()
            }
            if (shown) return
        }

        // Priority 2: Unity Ads Interstitial (Fallback fill)
        if (UnityAdsManager.isInterstitialReady()) {
            Log.d(TAG, "Showing Unity Ads Interstitial Ad")
            UnityAdsManager.showInterstitial(activity) {
                // Preload next ads
                AdMobManager.preloadInterstitial(activity)
                onAdClosed()
            }
            return
        }

        // If neither was ready right now, continue game immediately and trigger preloads
        Log.d(TAG, "No Interstitial ready, skipping and preloading...")
        AdMobManager.preloadInterstitial(activity)
        UnityAdsManager.preloadAds(activity)
        onAdClosed()
    }

    /**
     * Shows Rewarded Ad with waterfall priority (AdMob -> Unity Ads).
     */
    fun showRewardedAd(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdNotReady: () -> Unit = {}
    ) {
        // Priority 1: AdMob Rewarded (Highest eCPM)
        if (AdMobManager.isRewardedReady()) {
            Log.d(TAG, "Showing AdMob Rewarded Ad")
            val shown = AdMobManager.showRewarded(
                activity = activity,
                onUserEarnedReward = onUserEarnedReward,
                onAdClosed = {
                    AdMobManager.preloadRewarded(activity)
                    AdMobManager.preloadRewardedInterstitial(activity)
                    UnityAdsManager.preloadAds(activity)
                }
            )
            if (shown) return
        }

        // Priority 1.5: AdMob Rewarded Interstitial (High eCPM)
        if (AdMobManager.isRewardedInterstitialReady()) {
            Log.d(TAG, "Showing AdMob Rewarded Interstitial Ad")
            val shown = AdMobManager.showRewardedInterstitial(
                activity = activity,
                onUserEarnedReward = onUserEarnedReward,
                onAdClosed = {
                    AdMobManager.preloadRewarded(activity)
                    AdMobManager.preloadRewardedInterstitial(activity)
                    UnityAdsManager.preloadAds(activity)
                }
            )
            if (shown) return
        }

        // Priority 2: Unity Rewarded (High Fill Fallback)
        if (UnityAdsManager.isRewardedReady()) {
            Log.d(TAG, "Showing Unity Rewarded Ad")
            UnityAdsManager.showRewardedAd(
                activity = activity,
                onUserEarnedReward = onUserEarnedReward,
                onAdNotReady = onAdNotReady
            )
            return
        }

        // If neither is immediately ready, try Unity direct show / fallback
        Log.d(TAG, "Rewarded ads loading, attempting Unity fallback show...")
        UnityAdsManager.showRewardedAd(
            activity = activity,
            onUserEarnedReward = onUserEarnedReward,
            onAdNotReady = {
                AdMobManager.preloadRewarded(activity)
                onAdNotReady()
            }
        )
    }

    /**
     * Interstitial trigger after match conclusion.
     */
    fun onMatchFinished(
        activity: Activity?,
        onContinue: () -> Unit
    ) {
        if (activity == null) {
            onContinue()
            return
        }

        matchCountSinceLastAd++
        if (matchCountSinceLastAd >= MATCHES_PER_INTERSTITIAL) {
            matchCountSinceLastAd = 0
            showInterstitial(activity, onContinue)
        } else {
            onContinue()
        }
    }
}
