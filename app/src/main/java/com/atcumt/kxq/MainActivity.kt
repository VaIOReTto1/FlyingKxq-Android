// 文件路径: app/src/main/java/com/atcumt/kxq/MainActivity.kt
package com.atcumt.kxq

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity // 必须使用这个基类
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.utils.NavigationSetup
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WebView.setWebContentsDebuggingEnabled(true)
        setContent {
            KxqTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AdaptiveScreen {
                        Box(modifier = Modifier.fillMaxSize()) {
                            NavigationSetup()
                        }
                    }
                }
            }
        }
    }
}
