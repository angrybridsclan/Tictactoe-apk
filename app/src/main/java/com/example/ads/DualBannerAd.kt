package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

/**
 * Dual / Hybrid Banner Ad:
 * 1. Loads Google AdMob Banner (Ad Unit: ca-app-pub-6949224585585477/3336070070).
 * 2. If AdMob fails or has no fill, automatically falls back to Unity Banner (Banner_Android).
 * Guarantees maximum fill rate and revenue across both ad networks.
 */
@Composable
fun DualBannerAd(
    modifier: Modifier = Modifier
) {
    var isAdMobFailed by remember { mutableStateOf(false) }
    var isUnityFailed by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x2205020E))
            .border(1.dp, Color(0x3300E5FF), RoundedCornerShape(8.dp))
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (!isAdMobFailed) {
            // Priority 1: Google AdMob Banner
            AdMobBannerView(
                onAdFailedToLoad = {
                    Log.d("DualBannerAd", "AdMob banner fallback to Unity Banner")
                    isAdMobFailed = true
                }
            )
        } else if (!isUnityFailed) {
            // Priority 2: Unity Ads Banner Fallback
            UnityAdsBannerView(
                onUnityBannerFailed = {
                    isUnityFailed = true
                    // Quietly retry AdMob after 60s backoff
                    android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                        isAdMobFailed = false
                        isUnityFailed = false
                    }, 60000)
                }
            )
        }
    }
}

@Composable
private fun AdMobBannerView(
    onAdFailedToLoad: () -> Unit
) {
    var adViewRef by remember { mutableStateOf<AdView?>(null) }

    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = AdsConfig.ADMOB_BANNER_ID
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("DualBannerAd", "AdMob Banner loaded successfully")
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.w("DualBannerAd", "AdMob Banner failed: ${loadAdError.message}")
                        onAdFailedToLoad()
                    }
                }
                loadAd(AdRequest.Builder().build())
                adViewRef = this
            }
        },
        update = { /* No-op update */ },
        modifier = Modifier.fillMaxWidth()
    )

    DisposableEffect(Unit) {
        onDispose {
            try {
                adViewRef?.destroy()
                adViewRef = null
            } catch (e: Exception) {
                Log.e("DualBannerAd", "Error destroying AdMob banner", e)
            }
        }
    }
}

@Composable
private fun UnityAdsBannerView(
    onUnityBannerFailed: () -> Unit = {}
) {
    var unityBannerView by remember { mutableStateOf<BannerView?>(null) }

    AndroidView(
        factory = { context ->
            val container = FrameLayout(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }

            try {
                val activity = context.findActivity()
                if (activity != null) {
                    val banner = BannerView(
                        activity,
                        AdsConfig.UNITY_BANNER_PLACEMENT,
                        UnityBannerSize(320, 50)
                    ).apply {
                        listener = object : BannerView.IListener {
                            override fun onBannerLoaded(bannerAdView: BannerView?) {
                                Log.d("DualBannerAd", "Unity Fallback Banner loaded")
                            }

                            override fun onBannerShown(bannerAdView: BannerView?) {
                                Log.d("DualBannerAd", "Unity Fallback Banner shown")
                            }

                            override fun onBannerFailedToLoad(bannerAdView: BannerView?, errorInfo: BannerErrorInfo?) {
                                Log.d("DualBannerAd", "Unity Fallback Banner: ${errorInfo?.errorMessage}")
                                onUnityBannerFailed()
                            }

                            override fun onBannerClick(bannerAdView: BannerView?) {}
                            override fun onBannerLeftApplication(bannerAdView: BannerView?) {}
                        }
                        load()
                    }

                    container.addView(banner)
                    unityBannerView = banner
                } else {
                    onUnityBannerFailed()
                }
            } catch (e: Exception) {
                Log.e("DualBannerAd", "Error creating Unity banner", e)
                onUnityBannerFailed()
            }

            container
        },
        modifier = Modifier.fillMaxWidth()
    )

    DisposableEffect(Unit) {
        onDispose {
            try {
                unityBannerView?.destroy()
                unityBannerView = null
            } catch (e: Exception) {
                Log.e("DualBannerAd", "Error destroying Unity banner", e)
            }
        }
    }
}
