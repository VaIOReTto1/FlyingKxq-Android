package com.atcumt.kxq.utils

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atcumt.kxq.utils.globalScaleX
import com.atcumt.kxq.utils.globalScaleY

/**
 * 屏幕适配工具，按照 393 × 852 的比例动态调整 UI
 * 改进后的适配方案，分开控制宽高缩放比例，避免间距过大。
 *
 * @param modifier 修饰符
 * @param content Composable 内容
 */
@Composable
fun AdaptiveScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // 设计图的宽高
    val designWidth = 393.dp
    val designHeight = 852.dp

    BoxWithConstraints(modifier = modifier,
        contentAlignment = Alignment.Center) {
        val scaleX = maxWidth / designWidth
        val scaleY = maxHeight / designHeight
        globalScaleX = scaleX // 设置全局比例
        globalScaleY = scaleY

        content()
    }
}
