package com.atcumt.kxq.page.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atcumt.kxq.page.component.FlyMarkdownTextView.FlyMarkdown
import com.atcumt.kxq.page.component.FlyMarkdownTextView.FlyMarkdownViewModel
import com.atcumt.kxq.page.component.FlyMarkdownTextView.MarkdownError
import com.atcumt.kxq.page.component.FlyMarkdownTextView.MarkdownIntent
import com.atcumt.kxq.page.component.FlyMarkdownTextView.MarkdownState
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.delay

/**
 * [功能说明] 带打字机动画效果的Markdown文本组件
 *
 * 所有文本通过Markdown解析器渲染，支持逐字淡入的打字机效果，优化SSE增量文本场景。
 *
 * @param fullText 完整要显示的Markdown文本
 * @param animate 是否启用打字机动画效果
 * @param charDelay 字符间延迟(毫秒)
 * @param fadeInDuration 字符淡入动画时长(毫秒)
 * @param isCodeBlockColorful 代码块是否启用语法高亮
 * @param modifier 布局修饰符
 * @param style 文本基础样式
 * @param textColor 文本颜色
 * @param onComplete 动画完成回调
 * @param maxConcurrentChars 每批次处理的最大字符数，用于性能优化
 */
@Composable
fun TypewriterMarkdownText(
    fullText: String,
    animate: Boolean = true,
    charDelay: Long = 40L,
    fadeInDuration: Int = 200,
    isCodeBlockColorful: Boolean = true,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    textColor: Color = FlyColors.FlyText,
    onComplete: (() -> Unit)? = null,
    maxConcurrentChars: Int = 5,
    viewModel: FlyMarkdownViewModel = hiltViewModel()
) {
    if (fullText.isBlank()) {
        return
    }

    val state by viewModel.state.collectAsState()

    // 使用 derivedStateOf 优化状态计算
    val visibleText by remember(state) {
        derivedStateOf {
            (state as? MarkdownState.Success)?.sourceMarkdown ?: ""
        }
    }

    // 触发解析
    LaunchedEffect(fullText) {
        viewModel.process(
            MarkdownIntent.Parse(fullText, isCodeBlockColorful, animate)
        )
    }

    // 动画完成回调
    LaunchedEffect(state) {
        if (state is MarkdownState.Success && (state as MarkdownState.Success).visibleChars >= fullText.length) {
            onComplete?.invoke()
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        when (val currentState = state) {
            is MarkdownState.Success -> {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(animationSpec = tween(fadeInDuration))
                ) {
                    FlyMarkdown(
                        markdown = visibleText,
                        modifier = Modifier.fillMaxWidth(),
                        textColor = textColor,
                        style = style,
                        isCodeBlockColorful = isCodeBlockColorful,
                        viewModel = viewModel
                    )
                }
            }

            is MarkdownState.Error -> {
                MarkdownError(
                    errorMessage = currentState.message,
                    errorColor = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

/**
 * [功能说明] 聊天消息Markdown渲染组件
 * 专门为聊天应用设计的扩展，结合 TypewriterMarkdownText 和 ChatBubble
 *
 * @param msg 消息对象
 * @param textToDisplay 要显示的文本
 * @param animateBubble 是否启用打字机动画
 * @param modifier 修饰符
 */
@Composable
fun MarkdownChatBubble(
    msg: com.atcumt.kxq.page.ai.viewmodel.ChatMessage, // 引用ChatMessage类型
    textToDisplay: String,
    animateBubble: Boolean,
    modifier: Modifier = Modifier
) {
    if (textToDisplay.isBlank() && !animateBubble) {
        return
    }

    val segColor = if (msg.role == "user") FlyColors.FlyBackground else FlyColors.FlyText
    val stableKey =
        remember(msg.messageId, textToDisplay) { "${msg.messageId}:${textToDisplay.hashCode()}" }

    androidx.compose.material3.Card(
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = if (msg.role == "user") FlyColors.FlyMainLight else FlyColors.FlySecondaryBackground
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.wdp),
        modifier = modifier
            .widthIn(max = 280.wdp)
            .animateContentSize(tween(durationMillis = 100))
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 6.wdp, horizontal = 8.wdp)
                .widthIn(max = 280.wdp)
        ) {
//            val bubbleVm: FlyMarkdownViewModel = viewModel(key = "${msg.messageId}")
            if (!msg.role.equals("user", ignoreCase = true)) {
                // 使用Markdown渲染 + 打字机动画
                TypewriterMarkdownText(
                    fullText = textToDisplay,
                    animate = animateBubble,
                    charDelay = 40L, // MessageStyle.charDelay
                    fadeInDuration = 200, // MessageStyle.fadeInDuration
                    style = MaterialTheme.typography.bodyMedium.copy(color = segColor),
                    textColor = segColor,
//                    viewModel = bubbleVm,
                )
            } else {
                // 使用普通打字机效果
                TypewriterFadeText(
                    fullText = textToDisplay,
                    animate = animateBubble,
                    charDelay = 40L, // MessageStyle.charDelay 
                    fadeInDuration = 200, // MessageStyle.fadeInDuration
                    style = MaterialTheme.typography.bodyMedium.copy(color = segColor),
                    preserveNewlines = true // 保留换行符，确保\n在正确位置显示
                )
            }
        }
    }
} 