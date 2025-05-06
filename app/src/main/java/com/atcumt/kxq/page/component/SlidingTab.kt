package com.atcumt.kxq.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// -------- MVI 核心：UI State 与 Event --------
/**
 * UI State：持有当前页码与展开状态
 */
data class SlidingTabUiState(
    val currentPage: Int = 0,
    val expanded: Boolean = false
)

/**
 * UI 事件：切换展开/收起，选择页签
 */
sealed class SlidingTabEvent {
    data object ToggleExpand : SlidingTabEvent()
    data class SelectPage(val index: Int) : SlidingTabEvent()
}

// -------- ViewModel：解耦 UI 状态与业务 --------
@HiltViewModel
class SlidingTabViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SlidingTabUiState())
    val uiState: StateFlow<SlidingTabUiState> = _uiState.asStateFlow()

    /**
     * 统一处理 UI 事件，保持单向数据流
     */
    fun onEvent(event: SlidingTabEvent) {
        when (event) {
            is SlidingTabEvent.ToggleExpand -> {
                _uiState.update { it.copy(expanded = !it.expanded) }
            }

            is SlidingTabEvent.SelectPage -> {
                _uiState.update { it.copy(currentPage = event.index) }
            }
        }
    }
}

// -------- UI 组件：可复用的 SlidingTab --------
/**
 * @param list 标签列表
 * @param pagerState 外部 hoist 的 PagerState
 * @param expanded 是否展开
 * @param onEvent UI 事件回调
 * @param content 页面内容 Slot
 */
@Composable
fun SlidingTab(
    list: List<String>,
    pagerState: PagerState,
    expanded: Boolean,
    onEvent: (SlidingTabEvent) -> Unit,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        // 标签层，保证始终在内容层之上
        Column(
            modifier = Modifier.background(FlyColors.FlyBackground)
                .fillMaxWidth().padding(vertical = 8.hdp)
                .zIndex(1f)
        ) {
            if (expanded) {
                // 展开状态：多行六列 + 上拉按钮居中
                val rows = kotlin.math.ceil(list.size / 6f).toInt()
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    repeat(rows) { rowIdx ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.wdp),
                            horizontalArrangement = Arrangement.spacedBy(12.wdp)
                        ) {
                            val start = rowIdx * 6
                            val end = minOf(start + 6, list.size)
                            for (i in start until end) {
                                val selected = pagerState.currentPage == i
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (selected)
                                                FlyColors.FlySecondaryBackground
                                            else Color.Transparent,
                                            shape = RoundedCornerShape(17.wdp)
                                        )
                                        .height(25.hdp)
                                        .padding(horizontal = 10.wdp)
                                        .clickable {
                                            onEvent(SlidingTabEvent.SelectPage(i))
                                            // 保持展开：不触发 ToggleExpand
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    FlyText(
                                        text = list[i],
                                        fontSize = 14.ssp,
                                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                        color = if (selected) FlyColors.FlyText else FlyColors.FlyTextGray,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "收起",
                        tint = FlyColors.FlyTextGray,
                        modifier = Modifier
                            .size(20.wdp)
                            .clickable { onEvent(SlidingTabEvent.ToggleExpand) }
                    )
                }
            } else {
                // 折叠状态：可左右滚动 + 下拉按钮
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(1f) // 确保整体在内容层之上
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.wdp),
                        horizontalArrangement = Arrangement.spacedBy(8.wdp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        list.forEachIndexed { idx, title ->
                            val selected = pagerState.currentPage == idx
                            Box(
                                modifier = Modifier.background(
                                    color = if (selected)
                                        FlyColors.FlySecondaryBackground
                                    else Color.Transparent,
                                    shape = RoundedCornerShape(17.wdp)
                                ).height(25.hdp)
                                    .padding(horizontal = 10.wdp)
                                    .clickable { onEvent(SlidingTabEvent.SelectPage(idx)) },
                                contentAlignment = Alignment.Center
                            ) {
                                FlyText(
                                    text = title,
                                    fontSize = 14.ssp,
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (selected) FlyColors.FlyText else FlyColors.FlyTextGray,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                    }
                    if (list.size > 6)
                        Box(
                            modifier = Modifier.background(FlyColors.FlyBackground)
                                .align(Alignment.TopEnd).padding(end = 11.wdp).height(26.hdp)
                                .width(25.wdp)
                                .zIndex(2f) // 确保在标签之上
                                .clickable { onEvent(SlidingTabEvent.ToggleExpand) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "展开",
                                tint = FlyColors.FlyTextGray,
                                modifier = Modifier
                                    .size(20.wdp)

                            )
                        }
                }

            }
        }

        // 页面内容层
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { content() }
    }
}

// -------- Screen：Hoist State to ViewModel + 同步 PagerState --------
@Composable
fun SlidingTabScreen(
    viewModel: SlidingTabViewModel = hiltViewModel(),
    list: List<String>,
    content: @Composable () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { list.size }
    )
    val scope = rememberCoroutineScope()

    // 当 ViewModel 中的 currentPage 改变时，同步到 PagerState
    LaunchedEffect(uiState.currentPage) {
        scope.launch { pagerState.animateScrollToPage(uiState.currentPage) }
    }
    // 当用户滑动或点击 Pager 时，同步到 ViewModel
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { viewModel.onEvent(SlidingTabEvent.SelectPage(it)) }
    }

    SlidingTab(
        list = list,
        pagerState = pagerState,
        expanded = uiState.expanded,
        onEvent = viewModel::onEvent,
        content = content
    )
}

// -------- Preview --------
@Preview(showBackground = true)
@Composable
fun SlidingTabScreenPreview() {
    SlidingTabScreen(
        list = listOf("Tab1", "Tab2", "Tab3", "Tab4", "Tab5", "Tab6", "Tab7")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            repeat(10) {
                FlyText(text = "Content $it", modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
