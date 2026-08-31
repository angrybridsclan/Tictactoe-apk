package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.MotionEvent
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val TARGET_SECONDS = 30
// Hidden sponsor URL - completely enclosed inside the native Fullscreen WebView
private const val EVENT_SPONSOR_URL = "https://omg10.com/4/11693020"

/**
 * 100% Fullscreen Interactive Gift Event Overlay.
 *
 * 1. Fullscreen WebView with complete isolation from external browser/Google redirects.
 * 2. Background countdown (30s) starts ONLY after the sponsor page is fully loaded.
 * 3. Requires active interaction / taps on the screen to progress the timer.
 * 4. No timer numbers are displayed on screen during browsing (silent background counting).
 * 5. When 30 seconds of active interaction completes, it AUTOMATICALLY claims +250 Coins
 *    and dismisses the overlay with a reward celebration.
 */
@SuppressLint("SetJavaScriptEnabled", "ClickableViewAccessibility")
@Composable
fun GiftEventDialog(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onProgressUpdate: (Int) -> Unit = {},
    onClaimReward: () -> Unit
) {
    if (!isOpen) return

    var secondsLeft by remember { mutableIntStateOf(TARGET_SECONDS) }
    var lastInteractionTime by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isPageFullyLoaded by remember { mutableStateOf(false) }
    var isAutoClaimTriggered by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }

    // Silent background countdown loop
    LaunchedEffect(isOpen, isPageFullyLoaded, isAutoClaimTriggered) {
        if (isPageFullyLoaded && !isAutoClaimTriggered) {
            lastInteractionTime = System.currentTimeMillis()
            while (isActive && secondsLeft > 0 && !isAutoClaimTriggered) {
                delay(1000L)
                val now = System.currentTimeMillis()
                // Advances when user interacted within the last 5 seconds
                if ((now - lastInteractionTime) <= 5000L) {
                    secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
                    onProgressUpdate(secondsLeft)

                    // Auto-claim immediately when timer hits 0
                    if (secondsLeft == 0 && !isAutoClaimTriggered) {
                        isAutoClaimTriggered = true
                        onClaimReward()
                        break
                    }
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF070415))
        ) {
            // Fullscreen Native WebView Container (with touch detection & safe navigation)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                lastInteractionTime = System.currentTimeMillis()
                            },
                            onTap = {
                                lastInteractionTime = System.currentTimeMillis()
                            }
                        )
                    }
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                            val webViewInstance = this
                            android.webkit.CookieManager.getInstance().apply {
                                setAcceptCookie(true)
                                setAcceptThirdPartyCookies(webViewInstance, true)
                            }

                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                loadWithOverviewMode = true
                                useWideViewPort = true
                                databaseEnabled = true
                                allowContentAccess = true
                                allowFileAccess = true
                                setSupportMultipleWindows(true)
                                javaScriptCanOpenWindowsAutomatically = true
                                mediaPlaybackRequiresUserGesture = false
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                setSupportZoom(true)
                                builtInZoomControls = true
                                displayZoomControls = false
                                userAgentString =
                                    "Mozilla/5.0 (Linux; Android 14; Mobile; Pixel 8 Pro) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.6613.127 Mobile Safari/537.36"
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isPageFullyLoaded = true
                                    lastInteractionTime = System.currentTimeMillis()
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val url = request?.url?.toString() ?: return false
                                    return handleUrlNavigation(view, url)
                                }

                                @Deprecated("Deprecated in Java")
                                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                                    if (url == null) return false
                                    return handleUrlNavigation(view, url)
                                }

                                private fun handleUrlNavigation(view: WebView?, url: String): Boolean {
                                    val lower = url.lowercase()
                                    // Strictly block launching external browser, intents, or Google search / Play Store
                                    if (url.startsWith("intent:") ||
                                        url.startsWith("market:") ||
                                        url.startsWith("android-app:") ||
                                        url.startsWith("tel:") ||
                                        url.startsWith("mailto:") ||
                                        lower.contains("google.com") ||
                                        lower.contains("google.co") ||
                                        lower.contains("play.google") ||
                                        lower.contains("www.google")
                                    ) {
                                        // Ignore and prevent going to google
                                        return true
                                    }
                                    if (url.startsWith("http://") || url.startsWith("https://")) {
                                        view?.loadUrl(url)
                                        lastInteractionTime = System.currentTimeMillis()
                                        return true
                                    }
                                    return true
                                }

                                override fun onReceivedError(
                                    view: WebView?,
                                    request: WebResourceRequest?,
                                    error: WebResourceError?
                                ) {
                                    super.onReceivedError(view, request, error)
                                    if (request?.isForMainFrame == true) {
                                        isPageFullyLoaded = true
                                    }
                                }
                            }

                            webChromeClient = object : WebChromeClient() {
                                override fun onCreateWindow(
                                    view: WebView?,
                                    isDialog: Boolean,
                                    isUserGesture: Boolean,
                                    resultMsg: android.os.Message?
                                ): Boolean {
                                    val transport = resultMsg?.obj as? WebView.WebViewTransport
                                    transport?.webView = view
                                    resultMsg?.sendToTarget()
                                    return true
                                }

                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    if (newProgress >= 85) {
                                        isPageFullyLoaded = true
                                    }
                                }
                            }

                            setOnTouchListener { _, event ->
                                if (event.action == MotionEvent.ACTION_DOWN ||
                                    event.action == MotionEvent.ACTION_MOVE ||
                                    event.action == MotionEvent.ACTION_UP
                                ) {
                                    lastInteractionTime = System.currentTimeMillis()
                                }
                                false
                            }

                            loadUrl(EVENT_SPONSOR_URL)
                            webViewRef = this
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )

                // Loading overlay while initial page is fetching
                if (!isPageFullyLoaded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF09041A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(
                                color = NeonCyan,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Loading Lucky Gift Event...",
                                color = NeonWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Minimalist Floating Floating Header: Sleek Pill and Close Button
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(12.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Floating Gift Pill (No countdown numbers, purely brand indication)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xDD0C0624))
                            .border(1.dp, NeonGold, RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CardGiftcard,
                                contentDescription = "Gift Event",
                                tint = NeonGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (!isPageFullyLoaded) "Loading..." else "🎁 Lucky Gift Event Active",
                                color = NeonGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    // Floating Sleek Close Button
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xDD210738))
                            .border(1.2.dp, Color(0xFFFF5252), CircleShape)
                            .clickable { onDismiss() }
                            .testTag("btn_close_gift_event"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

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
