package com.atcumt.kxq.page.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.page.component.FlyingTab
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.page.profile.ProfilePage


@Composable
fun HomePage() {
    Column {
        // Tab 布局
        FlyingTab(
            list = listOf("关注", "广场", "热搜"),
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
fun PreviewRegisterPage() {
    KxqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AdaptiveScreen {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomePage()
                }
            }
        }
    }
}
