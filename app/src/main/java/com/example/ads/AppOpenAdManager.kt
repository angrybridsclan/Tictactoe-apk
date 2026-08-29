package com.example.ads

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

/**
 * Google AdMob App Open Ad Manager
 * App Open Ad ID: ca-app-pub-6949224585585477/7470099660
 * Shows when the app is launched or resumed from background.
 */
object AppOpenAdManager {
    private const val TAG = "AppOpenAdManager"

    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    private var isShowingAd = false
    private var loadTime: Long = 0

    private val mainHandler = Handler(Looper.getMainLooper())

    fun loadAd(context: Context, onAdLoaded: (() -> Unit)? = null) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        Log.d(TAG, "Loading AdMob App Open Ad: ${AdsConfig.ADMOB_APP_OPEN_ID}")

        AppOpenAd.load(
            context.applicationContext,
            AdsConfig.ADMOB_APP_OPEN_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "AdMob App Open Ad loaded successfully")
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    onAdLoaded?.invoke()
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "AdMob App Open Ad failed to load: ${loadAdError.message}")
                    isLoadingAd = false
                    appOpenAd = null
                    // Retry with backoff
                    mainHandler.postDelayed({
                        loadAd(context)
                    }, 30000)
                }
            }
        )
    }

    private fun wasLoadTimeLessThan4HoursAgo(): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4
    }

    fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThan4HoursAgo()
    }

    fun showAdIfAvailable(activity: Activity, onAdDismissed: () -> Unit = {}) {
        if (isShowingAd) {
            Log.d(TAG, "App open ad is already showing.")
            onAdDismissed()
            return
        }

        if (!isAdAvailable()) {
            Log.d(TAG, "App open ad is not ready.")
            loadAd(activity)
            onAdDismissed()
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "App open ad dismissed.")
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "App open ad failed to show: ${adError.message}")
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "App open ad showing.")
                isShowingAd = true
            }
        }

        isShowingAd = true
        appOpenAd?.show(activity)
    }
}
