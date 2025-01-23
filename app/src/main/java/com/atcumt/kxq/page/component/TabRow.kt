package com.atcumt.kxq.page.component

import androidx.annotation.FloatRange
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.lerp
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun FlyTabRow(
    folders: List<String>, // 文件夹名称列表
    pagerState: PagerState, // PagerState 用于跟踪当前页面
    width: Dp, // TabRow 的宽度
    height: Dp, // TabRow 的高度
    fontSize: TextUnit = 16.ssp,
    scope: CoroutineScope = rememberCoroutineScope()
) {
    TabRow(
        modifier = Modifier.width(width), // TabRow 占满宽度
        containerColor = MaterialTheme.colorScheme.background, // 背景颜色
        selectedTabIndex = pagerState.currentPage, // 当前选中的 Tab 索引
        indicator = { tabPositions ->
            // 自定义指示器，当 Tab 有位置时绘制指示器
            if (tabPositions.isNotEmpty()) {
                PagerTabIndicator(tabPositions = tabPositions, pagerState = pagerState)
            }
        },
        divider = {} // 去除默认分割线
    ) {
        folders.forEachIndexed { index, title ->
            // 判断是否为当前选中的 Tab
            val selected = (pagerState.currentPage == index)

            Tab(
                modifier = Modifier
                    .padding(horizontal = 9.wdp) // 设置 Tab 的水平内边距
                    .height(height), // 设置 Tab 的高度,
                selected = selected, // 当前是否选中
                selectedContentColor = MaterialTheme.colorScheme.onPrimary, // 选中 Tab 时的文本颜色
                unselectedContentColor = MaterialTheme.colorScheme.onSecondary, // 未选中 Tab 时的文本颜色
                onClick = {
                          scope.launch {
                              pagerState.animateScrollToPage(index)
                          }
                }, // 通过 onClick 设置一个空的函数来禁用点击事件
//                enabled = false, // 禁用 Tab 的点击事件
            ) {
                // Tab 标题
                FlyText(
                    text = title,
                    fontWeight = if (selected) FontWeight.W600 else FontWeight.W500,
                    fontSize = fontSize,
                )
            }
        }
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

    Canvas(modifier = Modifier.fillMaxWidth(), onDraw = {
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
            color = color, topLeft = Offset(
                x = indicatorOffset + (currentTab.width.toPx() * (1 - percent) / 2),
                y = size.height - height.toPx()
            ), size = Size(indicatorWidth, height.toPx()), // 指示器尺寸
            cornerRadius = CornerRadius(50f) // 圆角
        )
    })
}