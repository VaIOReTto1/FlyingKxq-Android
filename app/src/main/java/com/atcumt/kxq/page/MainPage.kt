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
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.atcumt.kxq.page.home.HomeTab
import com.atcumt.kxq.page.login.view.LoginPage.LoginPage
import com.atcumt.kxq.page.component.FlyText.NavBottomBarText
import com.atcumt.kxq.page.profile.ProfilePage
import kotlinx.coroutines.launch
import com.atcumt.kxq.utils.wdp

@Composable
fun MainPage() {
    // 定义底部导航栏的项目
    val items = listOf("首页", "咨询", "圈圈", "我的")
    // 创建 PagerState，用于控制和记录当前页面状态
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { items.size })

    Column(
        modifier = Modifier.fillMaxSize() // 占满整个屏幕
    ) {
        // 主内容区域，支持页面切换
        HorizontalPagerContent(
            modifier = Modifier.weight(1f), // 剩余空间分配给内容区域
            pagerState = pagerState
        )

        // 底部导航栏
        BottomNavigationBar(
            items = items,
            pagerState = pagerState
        )
    }
}

@Composable
private fun HorizontalPagerContent(
    modifier: Modifier = Modifier,
    pagerState: PagerState
) {
    // 禁用滑动手势的 HorizontalPager，仅支持通过点击切换页面
    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        userScrollEnabled = false // 禁用滑动切换
    ) { page ->
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 根据页面索引显示对应的内容
            when (page) {
                0 -> HomeTab() // 首页
                1 -> LoginPage(rememberNavController()) // 咨询
                2 -> LoginPage(rememberNavController()) // 圈圈
                3 -> ProfilePage() // 我的
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    items: List<String>,
    pagerState: PagerState
) {
    val scope = rememberCoroutineScope() // 用于切换页面的协程作用域

    BottomAppBar(
        modifier = Modifier
            .fillMaxWidth() // 占满宽度
            .height(41.wdp), // 高度固定
        contentPadding = PaddingValues(0.wdp), // 无内边距
        containerColor = MaterialTheme.colorScheme.background, // 背景颜色
        content = {
            // 使用 Row 将导航项水平排列
            Row(
                modifier = Modifier.fillMaxWidth(), // 占满宽度
                horizontalArrangement = Arrangement.SpaceEvenly, // 子项均匀分布
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 遍历导航项，逐个显示
                items.forEachIndexed { index, label ->
                    if (index == items.size / 2) { // 中间插入一个特殊按钮
                        Box(
                            modifier = Modifier
                                .width(41.wdp) // 按钮宽度
                                .height(30.wdp) // 按钮高度
                                .background(
                                    color = MaterialTheme.colorScheme.primary, // 按钮背景颜色为 primary
                                    shape = RoundedCornerShape(10.wdp) // 圆角效果
                                ).clickable(
                                    indication = null, // 无点击效果指示
                                    interactionSource = MutableInteractionSource()
                                ) {},
                            contentAlignment = Alignment.Center // 内容居中
                        ) {
                            Icon(
                                Icons.Rounded.Add, // 图标内容为加号
                                contentDescription = "+", // 描述信息
                                tint = Color.White // 图标颜色
                            )
                        }
                    }

                    // 普通导航项
                    NavBottomBarText(
                        text = label,
                        isSelected = pagerState.currentPage == index, // 高亮当前选中项
                        modifier = Modifier
                            .clickable(
                                indication = null, // 无点击效果指示
                                interactionSource = MutableInteractionSource()
                            ) {
                                // 点击时切换到对应页面
                                scope.launch {
                                    pagerState.scrollToPage(index) // 切换页面
                                }
                            }
                    )
                }
            }
        }
    )
}

@Preview
@Composable
fun MainPagePreview() {
    MainPage() // 预览 MainPage 组件
}