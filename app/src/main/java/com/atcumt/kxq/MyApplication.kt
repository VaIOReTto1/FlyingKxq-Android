package com.atcumt.kxq

import android.app.Application
import android.webkit.WebView
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // 移除手动创建 MainActivity 的线程
        WebView.setWebContentsDebuggingEnabled(true)
        // 主题设置已移动到 AndroidManifest.xml
    }
}