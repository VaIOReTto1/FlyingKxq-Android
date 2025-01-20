package com.atcumt.kxq.page.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

object FlyButton {

    /**
     * 实现主按钮（登录按钮）
     * @param content 按钮内容，可以是文本或任意组件
     * @param modifier 修饰符
     * @param onClick 点击事件
     */
    @Composable
    fun FlyMainButton(
        content: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        onClick: () -> Unit
    ) {
        Box(
            modifier = modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(FlyColors.FlyMain, FlyColors.FlyMainLight)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ).clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }

    /**
     * 实现弱化按钮（注册按钮）
     * @param content 按钮内容，可以是文本或任意组件
     * @param modifier 修饰符
     * @param onClick 点击事件
     */
    @Composable
    fun FlyWeakenButton(
        content: @Composable () -> Unit,
        modifier: Modifier = Modifier,
        onClick: () -> Unit,
        width: Dp = 2.wdp
    ) {
        Box(
            modifier = modifier
                .border(
                    width = width,
                    brush = Brush.linearGradient(
                        colors = listOf(FlyColors.FlyMain, FlyColors.FlyMainLight)
                    ),
                    shape = RoundedCornerShape(26.dp)
                ).clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = MutableInteractionSource()
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}