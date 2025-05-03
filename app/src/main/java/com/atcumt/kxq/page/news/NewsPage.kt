package com.atcumt.kxq.page.news

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.page.component.FlyingTab
import com.atcumt.kxq.page.profile.ProfilePage
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen

@Composable
fun NewsPage() {
    Column {
        // Tab 布局
        FlyingTab(
            list = listOf("推荐", "校园", "学院"),
            content = {
                ProfilePage()
            }
        )
    }
}

/**
 * 预览主页面布局
 */
@Preview
@Composable
fun PreviewNewsPage() {
    KxqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdaptiveScreen {
                Box(modifier = Modifier.fillMaxSize()) {
                    NewsPage()
                }
            }
        }
    }
}