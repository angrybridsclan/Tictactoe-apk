package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Google AdMob Central Manager
 * App ID: ca-app-pub-6949224585585477~4176392925
 * Units:
 * - App Open: ca-app-pub-6949224585585477/7470099660
 * - Banner: ca-app-pub-6949224585585477/3336070070
 * - Interstitial: ca-app-pub-6949224585585477/9709906737
 * - Rewarded: ca-app-pub-6949224585585477/7824126020
 * - Native: ca-app-pub-6949224585585477/6133623809
 */
object AdMobManager {
    private const val TAG = "AdMobManager"

    private val isInitialized = AtomicBoolean(false)
    private var isInitializing = false

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

    private val mainHandler = Handler(Looper.getMainLooper())

    fun initialize(context: Context, onComplete: (() -> Unit)? = null) {
        if (isInitialized.get()) {
            onComplete?.invoke()
            return
        }

        if (isInitializing) return
        isInitializing = true

        try {
            Log.d(TAG, "Initializing Google Mobile Ads (AdMob)...")
            MobileAds.initialize(context.applicationContext) { initStatus ->
                Log.d(TAG, "AdMob initialization completed: $initStatus")
                isInitialized.set(true)
                isInitializing = false
                preloadAds(context.applicationContext)
                onComplete?.invoke()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing AdMob", e)
            isInitializing = false
        }
    }

    fun preloadAds(context: Context) {
        preloadInterstitial(context)
        preloadRewarded(context)
    }

    // ==========================================
    // INTERSTITIAL ADS
    // ==========================================
    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null || isInterstitialLoading) return
        isInterstitialLoading = true

        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Loading AdMob Interstitial Ad: ${AdsConfig.ADMOB_INTERSTITIAL_ID}")

        InterstitialAd.load(
            context.applicationContext,
            AdsConfig.ADMOB_INTERSTITIAL_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "AdMob Interstitial loaded successfully")
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "AdMob Interstitial failed to load: ${loadAdError.message} (code=${loadAdError.code})")
                    interstitialAd = null
                    isInterstitialLoading = false
                    // Retry loading with backoff
                    mainHandler.postDelayed({
                        preloadInterstitial(context)
                    }, 30000)
                }
            }
        )
    }

    fun isInterstitialReady(): Boolean {
        return interstitialAd != null
    }

    fun showInterstitial(
        activity: Activity,
        onAdClosed: () -> Unit
    ): Boolean {
        val currentAd = interstitialAd
        if (currentAd == null) {
            Log.d(TAG, "AdMob Interstitial not ready to show")
            preloadInterstitial(activity)
            return false
        }

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AdMob Interstitial dismissed")
                interstitialAd = null
                preloadInterstitial(activity)
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "AdMob Interstitial failed to show: ${adError.message}")
                interstitialAd = null
                preloadInterstitial(activity)
                onAdClosed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "AdMob Interstitial showing")
                interstitialAd = null
            }
        }

        currentAd.show(activity)
        return true
    }

    // ==========================================
    // REWARDED ADS
    // ==========================================
    fun preloadRewarded(context: Context) {
        if (rewardedAd != null || isRewardedLoading) return
        isRewardedLoading = true

        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Loading AdMob Rewarded Ad: ${AdsConfig.ADMOB_REWARDED_ID}")

        RewardedAd.load(
            context.applicationContext,
            AdsConfig.ADMOB_REWARDED_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "AdMob Rewarded Ad loaded successfully")
                    rewardedAd = ad
                    isRewardedLoading = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "AdMob Rewarded Ad failed to load: ${loadAdError.message} (code=${loadAdError.code})")
                    rewardedAd = null
                    isRewardedLoading = false
                    // Retry loading with backoff
                    mainHandler.postDelayed({
                        preloadRewarded(context)
                    }, 30000)
                }
            }
        )
    }

    fun isRewardedReady(): Boolean {
        return rewardedAd != null
    }

    fun showRewarded(
        activity: Activity,
        onUserEarnedReward: () -> Unit,
        onAdClosed: () -> Unit
    ): Boolean {
        val currentAd = rewardedAd
        if (currentAd == null) {
            Log.d(TAG, "AdMob Rewarded Ad not ready to show")
            preloadRewarded(activity)
            return false
        }

        var rewardEarned = false

        currentAd.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "AdMob Rewarded Ad dismissed")
                rewardedAd = null
                preloadRewarded(activity)
                if (rewardEarned) {
                    onUserEarnedReward()
                }
                onAdClosed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "AdMob Rewarded Ad failed to show: ${adError.message}")
                rewardedAd = null
                preloadRewarded(activity)
                onAdClosed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "AdMob Rewarded Ad showing")
                rewardedAd = null
            }
        }

        currentAd.show(activity) { rewardItem ->
            Log.d(TAG, "User earned AdMob reward: ${rewardItem.amount} ${rewardItem.type}")
            rewardEarned = true
        }

        return true
    }

    // ==========================================
    // NATIVE ADS
    // ==========================================
    fun loadNativeAd(
        context: Context,
        onSuccess: (NativeAd) -> Unit,
        onFailure: (LoadAdError) -> Unit
    ) {
        val adLoader = AdLoader.Builder(context, AdsConfig.ADMOB_NATIVE_ID)
            .forNativeAd { nativeAd ->
                Log.d(TAG, "AdMob Native Ad loaded: ${nativeAd.headline}")
                onSuccess(nativeAd)
            }
            .withAdListener(object : com.google.android.gms.ads.AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "AdMob Native Ad failed: ${loadAdError.message}")
                    onFailure(loadAdError)
                }
            })
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT)
                    .build()
            )
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}
