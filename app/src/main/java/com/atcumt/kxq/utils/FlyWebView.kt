package com.atcumt.kxq.utils

import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember

typealias CookieHandler = (url: String?, cookies: List<String>) -> Unit

@Composable
fun FlyWebView(
    url: String,
    handleCookies: CookieHandler? = null
) {
    val isLoading = remember { mutableStateOf(true) }
    val progress = remember { mutableFloatStateOf(0.0F) }

    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.apply {
                    javaScriptEnabled = true // 启用 JavaScript
                    loadsImagesAutomatically = true // 自动加载图片
                    domStorageEnabled = true // 启用 DOM 存储
                    useWideViewPort = true // 启用视口支持
                    loadWithOverviewMode = true // 根据屏幕大小调整内容
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                        super.onProgressChanged(view, newProgress)
                        progress.floatValue = (newProgress.toDouble() / 100.0).toFloat()
                        // 记录加载进度
                        Log.d("FlyWebView", "加载进度: $newProgress%")
                    }
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest
                    ): Boolean {
                        Log.d("FlyWebView", "正在加载 URL: ${request.url}")
                        return super.shouldOverrideUrlLoading(view, request)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        isLoading.value = false
                        // 页面加载完成，记录 URL
                        Log.d("FlyWebView", "页面加载完成: $url")

                        // 获取 cookies
                        val cookieManager = CookieManager.getInstance()
                        val cookies = cookieManager.getCookie(url).split(";").map { it.trim() }
                        handleCookies?.invoke(url, cookies)
                        // 记录获取的 cookies
                        Log.d("FlyWebView", "获取到的 cookies: $cookies")
                    }
                }
                // 加载 URL
                Log.d("FlyWebView", "开始加载 URL: $url")
                loadUrl(url)
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    if (isLoading.value) {
        // 页面加载时显示进度条
        LinearProgressIndicator(progress.floatValue)
    }
}
