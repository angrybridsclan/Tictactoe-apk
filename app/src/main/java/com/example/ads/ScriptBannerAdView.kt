package com.example.ads

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

/**
 * High Revenue Script Banner Ad Composable.
 * Renders HTML/JS Ad script inside an optimized WebView with 10s auto-refresh interval.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ScriptBannerAdView(
    adKey: String,
    width: Int,
    height: Int,
    refreshIntervalSeconds: Long = 10L,
    modifier: Modifier = Modifier
) {
    var refreshTick by remember { mutableIntStateOf(0) }

    // 10-second auto-refresh timer loop
    LaunchedEffect(refreshIntervalSeconds, adKey) {
        while (isActive) {
            delay(refreshIntervalSeconds * 1000L)
            refreshTick++
        }
    }

    val htmlContent = remember(adKey, width, height, refreshTick) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                html, body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    background-color: transparent;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    overflow: hidden;
                }
                #ad-wrapper {
                    width: ${width}px;
                    height: ${height}px;
                    display: flex;
                    justify-content: center;
                    align-items: center;
                }
            </style>
        </head>
        <body>
            <div id="ad-wrapper">
                <script type="text/javascript">
                    atOptions = {
                        'key' : '$adKey',
                        'format' : 'iframe',
                        'height' : $height,
                        'width' : $width,
                        'params' : {}
                    };
                </script>
                <script type="text/javascript" src="//www.highrevenueformat.com/$adKey/invoke.js"></script>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(0) // Transparent background
                settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    allowContentAccess = true
                    allowFileAccess = true
                    databaseEnabled = true
                    cacheMode = WebSettings.LOAD_DEFAULT
                    mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    setSupportZoom(false)
                    userAgentString = "Mozilla/5.0 (Linux; Android 13; Mobile) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                        return false
                    }
                    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                        return false
                    }
                }
                webChromeClient = WebChromeClient()
                loadDataWithBaseURL("https://www.highrevenueformat.com/", htmlContent, "text/html", "UTF-8", null)
                webViewRef = this
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL("https://www.highrevenueformat.com/", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )

    DisposableEffect(Unit) {
        onDispose {
            try {
                webViewRef?.stopLoading()
                webViewRef?.destroy()
                webViewRef = null
            } catch (_: Exception) {}
        }
    }
}

/**
 * Big Medium Rectangle Ad (300x250) with 10 sec refresh
 */
@Composable
fun BigRevenueRectangleBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF09041A).copy(alpha = 0.90f))
            .border(1.5.dp, Color(0xFF00E5FF).copy(alpha = 0.8f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        ScriptBannerAdView(
            adKey = "fa2102db9bd753d2bae73df625a6f30e",
            width = 300,
            height = 250,
            refreshIntervalSeconds = 10L,
            modifier = Modifier
                .width(300.dp)
                .height(250.dp)
        )
    }
}

/**
 * Bottom Standard Banner Ad (320x50) with 10 sec refresh
 */
@Composable
fun BottomRevenueLeaderboardBanner(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF070314).copy(alpha = 0.92f))
            .border(1.dp, Color(0xFFE040FB).copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        ScriptBannerAdView(
            adKey = "f3d568ca5bc5361c6d2011c5eb43072a",
            width = 320,
            height = 50,
            refreshIntervalSeconds = 10L,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
    }
}
