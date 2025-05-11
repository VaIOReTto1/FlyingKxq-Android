// 文件：com/atcumt/kxq/page/MainPage.kt
package com.atcumt.kxq.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.page.home.HomePage
import com.atcumt.kxq.page.news.NewsPage
import com.atcumt.kxq.page.profile.ProfilePage
import com.atcumt.kxq.page.component.FlyText.NavBottomBarText
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.launch
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.page.ai.view.ChatScreen
import com.atcumt.kxq.page.login.view.LoginPage.LoginPage

@Composable
fun MainPage() {
    // 页面内容列表——注意中间第三项是 ChatScreen
    val labels = listOf("首页", "咨询", null, "圈圈", "我的")
    val pageCount = labels.size // Define page count based on labels

    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pageCount })
    val scope = rememberCoroutineScope()

    Column(Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = false // As per user's previous setup, scrolling is disabled
        ) { pageIndex ->
            // Directly call composables and pass parameters as needed
            when (pageIndex) {
                0 -> HomePage()
                1 -> NewsPage()
                2 -> ChatScreen(isCurrentPage = pagerState.currentPage == pageIndex)
                3 -> LoginPage(navController = rememberNavController()) // LoginPage might need a NavController
                4 -> ProfilePage()
                // else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Page Not Found") } // Fallback for safety
            }
        }

        BottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(41.wdp),
            containerColor = MaterialTheme.colorScheme.background,
            contentPadding = PaddingValues(0.wdp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                labels.forEachIndexed { index, label ->
                    if (label == null) {
                        // 中间 "+" 聊天按钮
                        Box(
                            modifier = Modifier
                                .width(41.wdp)
                                .height(30.wdp)
                                .background(
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(10.wdp)
                                )
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    // 切到 ChatScreen（索引 2）
                                    scope.launch { pagerState.animateScrollToPage(2) }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.Add,
                                contentDescription = "Chat",
                                tint = Color.White
                            )
                        }
                    } else {
                        // 普通文字导航
                        NavBottomBarText(
                            text = label,
                            isSelected = pagerState.currentPage == index,
                            modifier = Modifier
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    scope.launch { pagerState.animateScrollToPage(index) }
                                }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun MainPagePreview() {
    MainPage()
}
