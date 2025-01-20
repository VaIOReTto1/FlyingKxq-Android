package com.atcumt.kxq.page.home

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import com.atcumt.kxq.ui.theme.KxqTheme
import com.atcumt.kxq.utils.AdaptiveScreen
import com.atcumt.kxq.page.component.FlyText.TabText
import com.atcumt.kxq.page.login.utils.FlyLoginTextField
import kotlinx.coroutines.launch
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
            TabRow(
                modifier = Modifier
                    .width(159.wdp), // TabRow 占满宽度
                containerColor = MaterialTheme.colorScheme.background, // 背景颜色
                selectedTabIndex = pagerState.currentPage, // 当前选中的 Tab 索引
                indicator = { tabPositions ->
                    if (tabPositions.isNotEmpty()) {
                        PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
                    }
                },
                divider = {} // 去除默认分割线
            ) {
                homeFolders.forEachIndexed { index, title ->
                    val selected = (pagerState.currentPage == index) // 判断是否为当前选中 Tab
                    Tab(
                        modifier = Modifier
                            .padding(horizontal = 9.wdp)
                            .height(25.wdp), // Tab 的高度
                        selected = selected,
                        selectedContentColor = MaterialTheme.colorScheme.onPrimary, // 选中颜色
                        unselectedContentColor = MaterialTheme.colorScheme.onSecondary, // 未选中颜色
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index) // 点击切换页面
                            }
                        }
                    ) {
                        TabText(text = title, isSelected = selected) // Tab 标题
                    }
                }
            }
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
 * 自定义 Pager 指示器
 * @param tabPositions Tab 的位置和宽度信息
 * @param pagerState Pager 的状态
 * @param color 指示器的颜色
 * @param percent 指示器宽度占 Tab 宽度的百分比
 * @param height 指示器的高度
 */
@Composable
fun PagerTabIndicator(
    tabPositions: List<TabPosition>,
    pagerState: PagerState,
    color: Color = MaterialTheme.colorScheme.primary,
    @FloatRange(from = 0.0, to = 1.0) percent: Float = 0.3f,
    height: Dp = 3.wdp,
) {
    val currentPage = pagerState.currentPage
    val fraction = pagerState.currentPageOffsetFraction
    val currentTab = tabPositions.getOrNull(currentPage)
    val previousTab = tabPositions.getOrNull(currentPage - 1)
    val nextTab = tabPositions.getOrNull(currentPage + 1)

    if (currentTab == null) return // 防止边界异常

    Canvas(
        modifier = Modifier.fillMaxWidth(),
        onDraw = {
            val indicatorWidth = currentTab.width.toPx() * percent // 指示器宽度
            val indicatorOffset = when {
                fraction > 0 && nextTab != null -> {
                    lerp(currentTab.left, nextTab.left, fraction).toPx()
                }

                fraction < 0 && previousTab != null -> {
                    lerp(currentTab.left, previousTab.left, -fraction).toPx()
                }

                else -> currentTab.left.toPx()
            }

            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                    y = size.height - height.toPx()
                ),
                size = Size(indicatorWidth, height.toPx()), // 指示器尺寸
                cornerRadius = CornerRadius(50f) // 圆角
            )
        }
    )
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
