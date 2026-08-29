package com.example.ads

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * AdMob Native Ad Composable (Cyber Galaxy Theme)
 * Unit: ca-app-pub-6949224585585477/6133623809
 */
@Composable
fun AdMobNativeAdView(
    modifier: Modifier = Modifier
) {
    var nativeAdState by remember { mutableStateOf<NativeAd?>(null) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        AndroidView(
            factory = { context ->
                val container = FrameLayout(context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                AdMobManager.loadNativeAd(
                    context = context,
                    onSuccess = { ad ->
                        nativeAdState = ad
                        val nativeView = createCyberNativeAdView(context, ad)
                        container.removeAllViews()
                        container.addView(nativeView)
                    },
                    onFailure = { error ->
                        Log.d("AdMobNativeAdView", "Native ad not loaded: ${error.message}")
                    }
                )

                container
            },
            modifier = Modifier.fillMaxWidth()
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                nativeAdState?.destroy()
                nativeAdState = null
            } catch (e: Exception) {
                Log.e("AdMobNativeAdView", "Error destroying native ad", e)
            }
        }
    }
}

private fun createCyberNativeAdView(context: Context, ad: NativeAd): NativeAdView {
    val nativeAdView = NativeAdView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    val cardLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dpToPx(context, 10), dpToPx(context, 8), dpToPx(context, 10), dpToPx(context, 8))
        background = GradientDrawable().apply {
            setColor(android.graphics.Color.parseColor("#1C0A38"))
            setStroke(dpToPx(context, 1), android.graphics.Color.parseColor("#4400E5FF"))
            cornerRadius = dpToPx(context, 12).toFloat()
        }
    }

    // Top Header: Badge + Headline + Advertiser
    val headerRow = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    // Ad Tag Badge
    val adBadge = TextView(context).apply {
        text = "AD"
        textSize = 10f
        setTypeface(typeface, Typeface.BOLD)
        setTextColor(android.graphics.Color.parseColor("#000000"))
        gravity = Gravity.CENTER
        setPadding(dpToPx(context, 6), dpToPx(context, 2), dpToPx(context, 6), dpToPx(context, 2))
        background = GradientDrawable().apply {
            setColor(android.graphics.Color.parseColor("#FFEA00"))
            cornerRadius = dpToPx(context, 4).toFloat()
        }
    }
    headerRow.addView(adBadge)

    // App Icon (if present)
    val iconView = ImageView(context).apply {
        layoutParams = LinearLayout.LayoutParams(dpToPx(context, 32), dpToPx(context, 32)).apply {
            marginStart = dpToPx(context, 8)
        }
        scaleType = ImageView.ScaleType.FIT_CENTER
    }
    ad.icon?.drawable?.let {
        iconView.setImageDrawable(it)
        headerRow.addView(iconView)
        nativeAdView.iconView = iconView
    }

    // Text details container
    val textCol = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
            marginStart = dpToPx(context, 8)
        }
    }

    // Headline
    val headlineView = TextView(context).apply {
        text = ad.headline ?: "Sponsored"
        textSize = 13f
        setTypeface(typeface, Typeface.BOLD)
        setTextColor(android.graphics.Color.parseColor("#00E5FF"))
        maxLines = 1
    }
    textCol.addView(headlineView)
    nativeAdView.headlineView = headlineView

    // Body / Advertiser
    val bodyView = TextView(context).apply {
        text = ad.body ?: ad.advertiser ?: "Click to explore now"
        textSize = 11f
        setTextColor(android.graphics.Color.parseColor("#B0A0D0"))
        maxLines = 1
    }
    textCol.addView(bodyView)
    nativeAdView.bodyView = bodyView

    headerRow.addView(textCol)

    // Call to Action Button
    val ctaButton = Button(context).apply {
        text = ad.callToAction ?: "INSTALL"
        textSize = 11f
        setTypeface(typeface, Typeface.BOLD)
        setTextColor(android.graphics.Color.parseColor("#000000"))
        background = GradientDrawable().apply {
            setColor(android.graphics.Color.parseColor("#00E5FF"))
            cornerRadius = dpToPx(context, 8).toFloat()
        }
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            dpToPx(context, 34)
        ).apply {
            marginStart = dpToPx(context, 8)
        }
        setPadding(dpToPx(context, 12), 0, dpToPx(context, 12), 0)
    }
    headerRow.addView(ctaButton)
    nativeAdView.callToActionView = ctaButton

    cardLayout.addView(headerRow)
    nativeAdView.addView(cardLayout)

    // Bind NativeAd object
    nativeAdView.setNativeAd(ad)
    return nativeAdView
}

private fun dpToPx(context: Context, dp: Int): Int {
    val density = context.resources.displayMetrics.density
    return (dp * density).toInt()
}
