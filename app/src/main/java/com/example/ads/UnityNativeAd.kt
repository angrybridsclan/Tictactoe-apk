package com.example.ads

import android.app.Activity
import android.content.Context
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
 * Unity Native/Inline Ad component.
 * Game ID: 5857887
 */
@Composable
fun UnityNativeAd(
    modifier: Modifier = Modifier,
    placementId: String = UnityAdsManager.PLACEMENT_BANNER
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x330E0722))
                .border(
                    width = 1.dp,
                    color = NeonPurpleBorder.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(4.dp),
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
                        val widthPx = (320 * density).toInt()
                        val heightPx = (50 * density).toInt()
                        var activeBannerView: BannerView? = null
                        var isDestroyed = false

                        val refreshRunnable = object : Runnable {
                            override fun run() {
                                if (isDestroyed || activity.isFinishing || activity.isDestroyed) return
                                try {
                                    activeBannerView?.load()
                                } catch (e: Exception) {
                                    Log.w("UnityNativeAd", "Native banner refresh failed", e)
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
                                    UnityBannerSize(320, 50)
                                )

                                bannerView.listener = object : BannerView.IListener {
                                    override fun onBannerLoaded(bannerAdView: BannerView?) {
                                        if (isDestroyed) {
                                            bannerAdView?.destroy()
                                            return
                                        }
                                        Log.d("UnityNativeAd", "Native Banner loaded on placement: $placementId")
                                        container.removeAllViews()
                                        if (bannerAdView != null) {
                                            val lp = FrameLayout.LayoutParams(widthPx, heightPx).apply {
                                                gravity = Gravity.CENTER
                                            }
                                            container.addView(bannerAdView, lp)
                                            activeBannerView = bannerAdView
                                        }

                                        handler.removeCallbacks(refreshRunnable)
                                        handler.postDelayed(refreshRunnable, 60000)
                                    }

                                    override fun onBannerShown(bannerAdView: BannerView?) {
                                        Log.d("UnityNativeAd", "Native Banner shown: $placementId")
                                    }

                                    override fun onBannerFailedToLoad(
                                        bannerAdView: BannerView?,
                                        errorInfo: BannerErrorInfo?
                                    ) {
                                        Log.w(
                                            "UnityNativeAd",
                                            "Native Banner failed on $placementId: ${errorInfo?.errorMessage}"
                                        )
                                        handler.removeCallbacks(refreshRunnable)
                                        handler.postDelayed({
                                            if (!isDestroyed && !activity.isFinishing && !activity.isDestroyed) {
                                                loadBanner()
                                            }
                                        }, 45000)
                                    }

                                    override fun onBannerClick(bannerAdView: BannerView?) {
                                        Log.d("UnityNativeAd", "Native Banner clicked")
                                    }

                                    override fun onBannerLeftApplication(bannerAdView: BannerView?) {
                                        Log.d("UnityNativeAd", "Native Banner left application")
                                    }
                                }

                                bannerView.load()
                            } catch (e: Exception) {
                                Log.e("UnityNativeAd", "Error loading native ad on $placementId", e)
                                handler.postDelayed({
                                    if (!isDestroyed) loadBanner()
                                }, 45000)
                            }
                        }

                        loadBanner()

                        container.addOnAttachStateChangeListener(object : android.view.View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: android.view.View) {}
                            override fun onViewDetachedFromWindow(v: android.view.View) {
                                isDestroyed = true
                                handler.removeCallbacksAndMessages(null)
                                try {
                                    activeBannerView?.destroy()
                                } catch (e: Exception) {
                                    Log.w("UnityNativeAd", "Error destroying native banner on detach", e)
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
                    .height(56.dp)
            )
        }
    }
}
