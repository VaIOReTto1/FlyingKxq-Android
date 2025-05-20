package com.atcumt.kxq

import android.app.Application
import android.webkit.WebView
import com.atcumt.kxq.utils.network.RetrofitClient
import com.atcumt.kxq.utils.network.TokenProvider
import com.atcumt.kxq.utils.network.interceptor.AuthInterceptor
import com.atcumt.kxq.utils.network.interceptor.RefreshAuthenticator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {
    @Inject lateinit var authInterceptor: AuthInterceptor
    @Inject lateinit var refreshAuthenticator: RefreshAuthenticator
    @Inject
    lateinit var tokenProvider: TokenProvider
    override fun onCreate() {
        super.onCreate()
        // 移除手动创建 MainActivity 的线程
        WebView.setWebContentsDebuggingEnabled(true)
        // 主题设置已移动到 AndroidManifest.xml
        RetrofitClient.init(
            authInterceptor       = authInterceptor,
            refreshAuthenticator = refreshAuthenticator,
            tokenProvider         = tokenProvider
        )
    }
}