package com.atcumt.kxq.page.ai.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.atcumt.kxq.page.ai.viewmodel.ChatMessage
import com.atcumt.kxq.page.component.TypewriterFadeText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.wdp

/**
 * [功能说明] 消息组件全局样式配置
 */
object MessageStyle {
    /** 消息气泡最大宽度 */
    val maxBubbleWidth = 280.wdp

    /** 打字机动画单字符延迟 */
    const val charDelay = 40L

    /** 打字机动画淡入时长 */
    const val fadeInDuration = 200
}


/**
 * [功能说明] 消息列表状态管理
 */
class MessageState(
    initialMessages: List<ChatMessage> = emptyList()
) {
    /** 消息列表数据 */
    val messages = initialMessages.toMutableStateList()

    /** 列表滚动状态，用于自动滚动到底部 */
    val listState = LazyListState()
}


/**
 * [功能说明] 消息列表容器组件
 * @param state       消息状态管理对象（必要）
 * @param modifier    布局修饰符（可选）
 */
@Composable
fun MessageList(
    state: MessageState,
    modifier: Modifier = Modifier
) {
    // 自动滚动到底部
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            state.listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    LazyColumn(
        state = state.listState,
        modifier = modifier
            .fillMaxSize()
            .background(FlyColors.FlyBackground),
        contentPadding = PaddingValues(vertical = 8.wdp, horizontal = 12.wdp),
        verticalArrangement = Arrangement.spacedBy(4.wdp)
    ) {
        items(
            items = state.messages,
//            key = { it.messageId }
        ) { msg ->
            MessageItem(msg)
        }
    }
}


/**
 * [功能说明] 单条消息布局组件
 * @param msg 消息数据（必要）
 */
@Composable
private fun MessageItem(
    msg: ChatMessage
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.role == "user") Arrangement.End else Arrangement.Start
    ) {
        if (msg.isLoading) {
            Box(
                Modifier
                    .size(24.wdp)
                    .padding(8.wdp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 2.wdp,
                    modifier = Modifier.size(46.wdp),
                    color = FlyColors.FlyMain
                )
            }
        }
        ChatBubble(msg)
    }
}


/**
 * [功能说明] 消息气泡组件，支持 Markdown 打字机动画
 * @param msg 消息数据（必要）
 * @param modifier 布局修饰符（可选）=
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(
    msg: ChatMessage,
    modifier: Modifier = Modifier
) {
    // 选择文字颜色
    val segColor = if (msg.role == "user")
        FlyColors.FlyBackground
    else
        FlyColors.FlyText

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (msg.role == "user")
                FlyColors.FlyMainLight
            else
                FlyColors.FlySecondaryBackground
        ),
        shape = RoundedCornerShape(12.wdp),
        modifier = modifier.widthIn(max = MessageStyle.maxBubbleWidth)
    ) {
        // 用 TypewriterFadeText 来替代原有的逐字符动画展示
        TypewriterFadeText(
            fullText = msg.content,
            charDelay = MessageStyle.charDelay,
            fadeInDuration = MessageStyle.fadeInDuration,
            modifier = Modifier
                .padding(vertical = 6.wdp, horizontal = 8.wdp)
                .widthIn(max = MessageStyle.maxBubbleWidth),
            style = MaterialTheme.typography.bodyMedium.copy(color = segColor)
        )
    }
}


/**
 * [功能说明] 简单 Markdown 到（字符, 样式）序列解析
 *            仅支持 **bold** 和 *italic*
 * @param markdown 原始 Markdown 文本（必要）
 * @param baseStyle 基础文本样式（必要）
 * @return 字符与样式配对列表
 */
private fun parseMarkdownSegments(
    markdown: String,
    baseStyle: TextStyle
): List<Pair<Char, TextStyle>> {
    val result = mutableListOf<Pair<Char, TextStyle>>()
    var index = 0
    val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
    val italicRegex = Regex("\\*(.*?)\\*")
    // 先处理 Bold，再处理 Italic，简化示例
    boldRegex.findAll(markdown).fold(0) { lastEnd, match ->
        // 文本块
        markdown.substring(lastEnd, match.range.first).forEach {
            result += it to baseStyle
        }
        // 加粗块
        match.groupValues[1].forEach {
            result += it to baseStyle.copy(fontWeight = FontWeight.Bold)
        }
        match.range.last + 1
    }.also { last ->
        // 剩余文本
        markdown.substring(last).forEach {
            result += it to baseStyle
        }
    }
    // 支持 *italic*（简单示意，不处理嵌套）
    return result.flatMap { (c, style) ->
        val text = c.toString()
        italicRegex.find(text)?.let { m ->
            listOf(c to style.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic))
        } ?: listOf(c to style)
    }
}
