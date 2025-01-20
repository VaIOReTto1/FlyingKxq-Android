package com.atcumt.kxq.page.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.page.component.FlyTabRow
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.page.login.utils.FlyLoginTextField
import com.atcumt.kxq.utils.wdp

/**
 * 主页面的 Tab
 */
@Composable
fun HomeTab() {
    val homeFolders = listOf("关注", "广场", "热搜") // Tab 的标题列表
    val pagerState =
        rememberPagerState(initialPage = 0, pageCount = { homeFolders.size }) // Pager 状态
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) {
        // Tab 布局
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 9.wdp, vertical = 2.wdp)
        ){
            FlyTabRow(homeFolders,pagerState,159.wdp,25.wdp)
            FlyLoginTextField(
                text = "搜索",
                modifier = Modifier.height(30.wdp).width(116.wdp),
                round = 15.wdp
            )
        }

        // 页面内容
        HorizontalPagerContent(pagerState = pagerState)
    }
}



/**
 * 页面内容区域
 * @param pagerState Pager 的状态
 * @param noteFolders Tab 对应的页面内容
 */
@Composable
private fun HorizontalPagerContent(
    pagerState: PagerState,
) {
    HorizontalPager(
        state = pagerState,
    ) { page ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        }
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
                    HomeTab()
                }
            }
        }
    }
}
