package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.unity3d.ads.IUnityAdsInitializationListener
import com.unity3d.ads.IUnityAdsLoadListener
import com.unity3d.ads.IUnityAdsShowListener
import com.unity3d.ads.UnityAds
import com.unity3d.ads.UnityAdsShowOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Unity Ads Central Manager
 * Game ID: 5857887
 * Supports: Banner, Interstitial, Rewarded Video, and Native formats
 * Live Production Mode: testMode = false
 */
object UnityAdsManager {
    private const val TAG = "UnityAdsManager"

    // Unity Game ID provided by User
    const val GAME_ID = "5857887"

    // Standard Unity Placement IDs for Android
    const val PLACEMENT_BANNER = "Banner_Android"
    const val PLACEMENT_INTERSTITIAL = "Interstitial_Android"
    const val PLACEMENT_REWARDED = "Rewarded_Android"

    // Fallback Placement IDs configured in Unity Dashboard
    val BACKUP_BANNER_PLACEMENTS = listOf(PLACEMENT_BANNER)
    val BACKUP_INTERSTITIAL_PLACEMENTS = listOf(PLACEMENT_INTERSTITIAL)
    val BACKUP_REWARDED_PLACEMENTS = listOf(PLACEMENT_REWARDED)

    // Test mode: true during debug/emulator builds to guarantee 100% ad fills and prevent "No fill" errors.
    // Automatically false in release builds to serve live production revenue-generating ads.
    val TEST_MODE: Boolean = com.example.BuildConfig.DEBUG

    private val isInitialized = AtomicBoolean(false)
    private var isInitializing = false

    private val _isInitializedFlow = MutableStateFlow(false)
    val isInitializedFlow = _isInitializedFlow.asStateFlow()

    private val initListeners = Collections.synchronizedList(mutableListOf<() -> Unit>())

    // State tracking for preloaded ads
    private var isInterstitialLoaded = false
    private var isRewardedLoaded = false

    private var matchCountSinceLastAd = 0
    private const val MATCHES_PER_INTERSTITIAL = 1

    private val mainHandler = Handler(Looper.getMainLooper())

    fun initialize(context: Context, onInitComplete: (() -> Unit)? = null) {
        if (UnityAds.isInitialized || isInitialized.get()) {
            isInitialized.set(true)
            _isInitializedFlow.value = true
            onInitComplete?.invoke()
            notifyInitListeners()
            return
        }

        if (onInitComplete != null) {
            initListeners.add(onInitComplete)
        }

        if (isInitializing) return
        isInitializing = true

        try {
            Log.d(TAG, "Initializing Unity Ads with Game ID: $GAME_ID (testMode = $TEST_MODE)...")
            UnityAds.initialize(
                context.applicationContext,
                GAME_ID,
                TEST_MODE,
                object : IUnityAdsInitializationListener {
                    override fun onInitializationComplete() {
                        Log.d(TAG, "Unity Ads Initialized Successfully with Game ID: $GAME_ID")
                        isInitialized.set(true)
                        _isInitializedFlow.value = true
                        isInitializing = false
                        preloadAds(context.applicationContext)
                        notifyInitListeners()
                    }

                    override fun onInitializationFailed(
                        error: UnityAds.UnityAdsInitializationError?,
                        message: String?
                    ) {
                        Log.e(TAG, "Unity Ads Initialization Failed: $error - $message")
                        isInitializing = false
                        // Automatically retry initialization after 4 seconds
                        mainHandler.postDelayed({
                            if (!UnityAds.isInitialized) {
                                initialize(context.applicationContext)
                            }
                        }, 4000)
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Exception initializing Unity Ads", e)
            isInitializing = false
        }
    }

    private fun notifyInitListeners() {
        mainHandler.post {
            synchronized(initListeners) {
                initListeners.forEach { it.invoke() }
                initListeners.clear()
            }
        }
    }

    fun addInitListener(listener: () -> Unit) {
        if (UnityAds.isInitialized || isInitialized.get()) {
            listener()
        } else {
            initListeners.add(listener)
        }
    }

    fun isReady(): Boolean = UnityAds.isInitialized || isInitialized.get()

    fun preloadAds(context: Context) {
        if (!isReady()) return
        loadInterstitial(context, PLACEMENT_INTERSTITIAL)
        loadRewarded(context, PLACEMENT_REWARDED)
    }

    // ==========================================
    // INTERSTITIAL ADS
    // ==========================================
    fun loadInterstitial(context: Context, placementId: String = PLACEMENT_INTERSTITIAL) {
        if (!isReady()) {
            addInitListener { loadInterstitial(context, placementId) }
            return
        }
        try {
            UnityAds.load(placementId, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String?) {
                    Log.d(TAG, "Interstitial loaded for placement: $placementId")
                    isInterstitialLoaded = true
                }

                override fun onUnityAdsFailedToLoad(
                    placementId: String?,
                    error: UnityAds.UnityAdsLoadError?,
                    message: String?
                ) {
                    Log.w(TAG, "Interstitial failed to load ($placementId): $error - $message")
                    isInterstitialLoaded = false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading interstitial", e)
        }
    }

    fun showInterstitial(
        activity: Activity,
        placementId: String = PLACEMENT_INTERSTITIAL,
        candidateIndex: Int = 0,
        onAdClosed: () -> Unit
    ) {
        if (!isReady()) {
            onAdClosed()
            return
        }

        val currentPlacement = BACKUP_INTERSTITIAL_PLACEMENTS.getOrElse(candidateIndex) { placementId }

        try {
            UnityAds.show(
                activity,
                currentPlacement,
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowStart(placementId: String?) {
                        Log.d(TAG, "Interstitial show started: $placementId")
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {
                        Log.d(TAG, "Interstitial clicked: $placementId")
                    }

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        Log.d(TAG, "Interstitial completed with state: $state")
                        loadInterstitial(activity.applicationContext, currentPlacement)
                        onAdClosed()
                    }

                    override fun onUnityAdsShowFailure(
                        failedPlacementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Interstitial show failed ($currentPlacement): $error - $message")
                        // If backup placement available, try it
                        if (candidateIndex + 1 < BACKUP_INTERSTITIAL_PLACEMENTS.size) {
                            showInterstitial(activity, placementId, candidateIndex + 1, onAdClosed)
                        } else {
                            loadInterstitial(activity.applicationContext, placementId)
                            onAdClosed()
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error showing interstitial", e)
            onAdClosed()
        }
    }

    fun onMatchFinished(activity: Activity?, onComplete: () -> Unit) {
        matchCountSinceLastAd++
        if (activity != null && matchCountSinceLastAd >= MATCHES_PER_INTERSTITIAL) {
            matchCountSinceLastAd = 0
            showInterstitial(activity) {
                onComplete()
            }
        } else {
            onComplete()
        }
    }

    // ==========================================
    // REWARDED ADS (Coins added on full watch)
    // ==========================================
    fun loadRewarded(context: Context, placementId: String = PLACEMENT_REWARDED) {
        if (!isReady()) {
            addInitListener { loadRewarded(context, placementId) }
            return
        }
        try {
            UnityAds.load(placementId, object : IUnityAdsLoadListener {
                override fun onUnityAdsAdLoaded(placementId: String?) {
                    Log.d(TAG, "Rewarded ad loaded for placement: $placementId")
                    isRewardedLoaded = true
                }

                override fun onUnityAdsFailedToLoad(
                    placementId: String?,
                    error: UnityAds.UnityAdsLoadError?,
                    message: String?
                ) {
                    Log.w(TAG, "Rewarded ad failed to load ($placementId): $error - $message")
                    isRewardedLoaded = false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error loading rewarded ad", e)
        }
    }

    fun showRewardedAd(
        activity: Activity,
        placementId: String = PLACEMENT_REWARDED,
        candidateIndex: Int = 0,
        onUserEarnedReward: () -> Unit,
        onAdNotReady: () -> Unit
    ) {
        if (!isReady()) {
            onAdNotReady()
            return
        }

        val currentPlacement = BACKUP_REWARDED_PLACEMENTS.getOrElse(candidateIndex) { placementId }

        try {
            UnityAds.show(
                activity,
                currentPlacement,
                UnityAdsShowOptions(),
                object : IUnityAdsShowListener {
                    override fun onUnityAdsShowStart(placementId: String?) {
                        Log.d(TAG, "Rewarded ad started: $placementId")
                    }

                    override fun onUnityAdsShowClick(placementId: String?) {
                        Log.d(TAG, "Rewarded ad clicked: $placementId")
                    }

                    override fun onUnityAdsShowComplete(
                        placementId: String?,
                        state: UnityAds.UnityAdsShowCompletionState?
                    ) {
                        Log.d(TAG, "Rewarded ad completed: $state")
                        // Preload next rewarded ad immediately
                        loadRewarded(activity.applicationContext, currentPlacement)

                        // Strict check: Coins added ONLY when video is watched completely
                        if (state == UnityAds.UnityAdsShowCompletionState.COMPLETED) {
                            Log.d(TAG, "Full rewarded ad watched! Awarding coins to user.")
                            onUserEarnedReward()
                        } else {
                            Log.d(TAG, "Rewarded ad was skipped/closed before completion. No reward granted.")
                        }
                    }

                    override fun onUnityAdsShowFailure(
                        failedPlacementId: String?,
                        error: UnityAds.UnityAdsShowError?,
                        message: String?
                    ) {
                        Log.w(TAG, "Rewarded ad failed to show ($currentPlacement): $error - $message")
                        // Try fallback placement if possible
                        if (candidateIndex + 1 < BACKUP_REWARDED_PLACEMENTS.size) {
                            showRewardedAd(
                                activity,
                                placementId,
                                candidateIndex + 1,
                                onUserEarnedReward,
                                onAdNotReady
                            )
                        } else {
                            onAdNotReady()
                            loadRewarded(activity.applicationContext, placementId)
                        }
                    }
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error showing rewarded ad", e)
            onAdNotReady()
        }
    }
}
