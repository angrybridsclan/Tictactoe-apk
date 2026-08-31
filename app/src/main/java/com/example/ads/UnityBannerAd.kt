package com.example.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.NeonPurpleBorder
import com.unity3d.services.banners.BannerErrorInfo
import com.unity3d.services.banners.BannerView
import com.unity3d.services.banners.UnityBannerSize

/**
 * Continuous Unity Banner Ad Composable.
 * Always stays active, safely auto-refreshes, and prevents rapid-retry loops.
 * Game ID: 5857887
 */
@Composable
fun UnityBannerAd(
    modifier: Modifier = Modifier,
    placementId: String = UnityAdsManager.PLACEMENT_BANNER,
    widthDp: Int = 320,
    heightDp: Int = 50
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x220A051B))
                .border(
                    width = 1.dp,
                    color = NeonPurpleBorder.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    val activity = ctx.findActivity()
                    val container = FrameLayout(ctx).apply {
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.WRAP_CONTENT
                        )
                    }

                    if (activity != null) {
                        val handler = Handler(Looper.getMainLooper())
                        val density = ctx.resources.displayMetrics.density
                        val widthPx = (widthDp * density).toInt()
                        val heightPx = (heightDp * density).toInt()
                        var activeBannerView: BannerView? = null
                        var isDestroyed = false

                        val refreshRunnable = object : Runnable {
                            override fun run() {
                                if (isDestroyed || activity.isFinishing || activity.isDestroyed) return
                                try {
                                    activeBannerView?.load()
                                } catch (e: Exception) {
                                    Log.w("UnityBannerAd", "Banner refresh failed", e)
                                }
                            }
                        }

                        fun loadBanner() {
                            if (isDestroyed || activity.isFinishing || activity.isDestroyed) return

                            if (!UnityAdsManager.isReady()) {
                                UnityAdsManager.addInitListener {
                                    handler.post { loadBanner() }
                                }
                                return
                            }

                            try {
                                activeBannerView?.destroy()
                                activeBannerView = null

                                val bannerView = BannerView(
                                    activity,
                                    placementId,
                                    UnityBannerSize(widthDp, heightDp)
                                )

                                bannerView.listener = object : BannerView.IListener {
                                    override fun onBannerLoaded(bannerAdView: BannerView?) {
                                        if (isDestroyed) {
                                            bannerAdView?.destroy()
                                            return
                                        }
                                        Log.d("UnityBannerAd", "Banner loaded successfully on placement: $placementId")
                                        container.removeAllViews()
                                        if (bannerAdView != null) {
                                            val lp = FrameLayout.LayoutParams(widthPx, heightPx).apply {
                                                gravity = Gravity.CENTER
                                            }
                                            container.addView(bannerAdView, lp)
                                            activeBannerView = bannerAdView
                                        }

                                        // Auto-refresh banner every 60 seconds
                                        handler.removeCallbacks(refreshRunnable)
                                        handler.postDelayed(refreshRunnable, 60000)
                                    }

                                    override fun onBannerShown(bannerAdView: BannerView?) {
                                        Log.d("UnityBannerAd", "Banner shown: $placementId")
                                    }

                                    override fun onBannerFailedToLoad(
                                        bannerAdView: BannerView?,
                                        errorInfo: BannerErrorInfo?
                                    ) {
                                        Log.w(
                                            "UnityBannerAd",
                                            "Banner failed on $placementId: ${errorInfo?.errorMessage} (code: ${errorInfo?.errorCode})"
                                        )
                                        // Wait 45 seconds before retrying to prevent hammering the ad network
                                        handler.removeCallbacks(refreshRunnable)
                                        handler.postDelayed({
                                            if (!isDestroyed && !activity.isFinishing && !activity.isDestroyed) {
                                                loadBanner()
                                            }
                                        }, 45000)
                                    }

                                    override fun onBannerClick(bannerAdView: BannerView?) {
                                        Log.d("UnityBannerAd", "Banner clicked")
                                    }

                                    override fun onBannerLeftApplication(bannerAdView: BannerView?) {
                                        Log.d("UnityBannerAd", "Banner left app")
                                    }
                                }

                                bannerView.load()
                            } catch (e: Exception) {
                                Log.e("UnityBannerAd", "Error loading banner on $placementId", e)
                                handler.postDelayed({
                                    if (!isDestroyed) loadBanner()
                                }, 45000)
                            }
                        }

                        // Trigger load
                        loadBanner()

                        container.addOnAttachStateChangeListener(object : android.view.View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: android.view.View) {}
                            override fun onViewDetachedFromWindow(v: android.view.View) {
                                isDestroyed = true
                                handler.removeCallbacksAndMessages(null)
                                try {
                                    activeBannerView?.destroy()
                                } catch (e: Exception) {
                                    Log.w("UnityBannerAd", "Error destroying banner on detach", e)
                                }
                                activeBannerView = null
                                container.removeAllViews()
                            }
                        })
                    }

                    container
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(heightDp.dp)
            )
        }
    }
}

/**
 * Big Prominent Unity Banner Ad (320x100 or Large Banner format).
 */
@Composable
fun UnityBigBannerAd(
    modifier: Modifier = Modifier,
    placementId: String = UnityAdsManager.PLACEMENT_BANNER
) {
    UnityBannerAd(
        modifier = modifier,
        placementId = placementId,
        widthDp = 320,
        heightDp = 100
    )
}

/**
 * Helper to safely extract Activity from any ContextWrapper chain.
 */
internal fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
