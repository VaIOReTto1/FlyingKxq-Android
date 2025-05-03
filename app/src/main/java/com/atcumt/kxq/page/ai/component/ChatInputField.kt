package com.atcumt.kxq.page.ai.component

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.atcumt.kxq.page.component.FlyText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.ssp
import com.atcumt.kxq.utils.wdp

/**
 * 聊天输入区主组件
 *
 * @param value 当前输入文本
 * @param onValueChange 文本变化回调
 * @param onSend 点击发送回调
 * @param reasoningEnabled 深度思考开关
 * @param searchEnabled 联网搜索开关
 * @param onDeepThink 切换深度思考回调
 * @param onWebSearch 切换联网搜索回调
 * @param modifier 外部修饰符
 */
@Composable
fun  ChatInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    reasoningEnabled: Boolean,
    searchEnabled: Boolean,
    onDeepThink: () -> Unit,
    onWebSearch: () -> Unit,
    modifier: Modifier = Modifier,
    onFocusChanged: (Boolean) -> Unit = {},
) {
    // 只在 value 从空到非空或反向时才触发重组
    val canSend by remember(value) { derivedStateOf { value.isNotBlank() } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            // 背景带 shape，则绘制的背景就是这个圆角，周围保持透明
            .background(
                color = FlyColors.FlySecondaryBackground,
                shape = RoundedCornerShape(topStart = 25.wdp, topEnd = 25.wdp)
            )
            .padding(vertical = 12.hdp, horizontal = 12.wdp)
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.hdp, max = 120.hdp)     // 动态高度限制
            // 焦点状态由内部 BasicTextField onFocusChanged 管理
            ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 输入框主体
            DynamicHeightInput(
                value = value,
                onValueChange = onValueChange,
                placeHolder = "请输入问题",
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.hdp, max = 120.hdp),
                onFocusChanged = onFocusChanged
            )

            // 发送按钮
            if (canSend)
                AnimatedSendButton(
                    onClick = onSend,
                    modifier = Modifier.padding(start = 8.wdp)
                )
        }

        Spacer(modifier = Modifier.height(8.wdp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.wdp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 深度思考开关
            FeatureToggleButton(
                label = "R1·深度思考",
                icon = null,
                enabled = reasoningEnabled,
                onClick = onDeepThink
            )
            // 联网搜索开关
            FeatureToggleButton(
                label = "联网搜索",
                icon = Icons.Default.Public,
                enabled = searchEnabled,
                onClick = onWebSearch
            )
        }
    }
}

/**
 * 动态高度输入框：随内容自动扩展，高度范围 [40, 120]dp
 */
@Composable
private fun DynamicHeightInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeHolder: String,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var hasFocus by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Box(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged {
                hasFocus = it.isFocused
                onFocusChanged(it.isFocused)
            },
        contentAlignment = Alignment.TopStart
    ) {
        // 自定义占位符：只有在无焦点且无文字时才显示
        if (value.isBlank() && !hasFocus) {
            FlyText(
                text = placeHolder,
                fontWeight = FontWeight.W400,
                fontSize = 18.ssp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(start = 12.wdp)
            )
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onPrimary),
            textStyle = TextStyle(
                fontWeight = FontWeight.W400,
                fontSize = 18.ssp,
                color = MaterialTheme.colorScheme.onPrimary,
                textAlign = TextAlign.Start,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.hdp, max = 120.hdp).wrapContentHeight(Alignment.CenterVertically)
                .padding(start = 12.wdp)
        )
    }
}

/**
 * 带按下动画的发送按钮
 */
@Composable
fun AnimatedSendButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 用官方的 InteractionSource 来监听按下状态
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    // 按下时缩放至 0.9
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 100), label = ""
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .size(width = 67.wdp, height = 28.wdp)
            .background(
                color = FlyColors.FlyMain,
                shape = RoundedCornerShape(20.wdp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true)
            ) {
                onClick()
            }
    ) {
        FlyText(
            text = "发送",
            fontWeight = FontWeight.Bold,
            fontSize = 16.ssp,
            color = FlyColors.FlyBackground
        )
    }
}

/**
 * 通用功能开关按钮
 *
 * @param label 按钮文字
 * @param icon 可选图标
 * @param enabled 是否已开启
 * @param onClick 点击回调
 */
@Composable
fun FeatureToggleButton(
    label: String,
    icon: ImageVector? = null,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 根据开关状态选择描边色与文字色
    val borderColor = (if (enabled) FlyColors.FlyMain else MaterialTheme.colorScheme.onPrimary)
        .copy(alpha = 0.3f)
    val contentColor = if (enabled) FlyColors.FlyMain else MaterialTheme.colorScheme.onPrimary

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(20.wdp))
            .background(FlyColors.FlySecondaryBackground)
            .border(0.5.wdp, borderColor, RoundedCornerShape(20.wdp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            )
            .padding(vertical = 8.wdp, horizontal = 12.wdp)
    ) {
        icon?.let {
            Icon(
                imageVector = it,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(14.wdp)
            )
            Spacer(modifier = Modifier.width(4.wdp))
        }
        FlyText(
            text = label,
            fontWeight = FontWeight.W400,
            fontSize = 14.ssp,
            color = contentColor
        )
    }
}


@Preview
@Composable
fun ChatInputFieldPreview() {
    MaterialTheme {
        ChatInputField(
            value = "ni",
            onValueChange = {},
            onSend = {},
            onDeepThink = {},
            onWebSearch = {},
            reasoningEnabled = true,
            searchEnabled = true
        )
    }
}