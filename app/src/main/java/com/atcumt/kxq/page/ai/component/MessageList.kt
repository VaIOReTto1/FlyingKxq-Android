package com.atcumt.kxq.page.ai.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.atcumt.kxq.page.ai.viewmodel.ChatMessage
import com.atcumt.kxq.page.component.MarkdownChatBubble
import com.atcumt.kxq.page.component.TypewriterFadeText
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.delay

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
 * [功能说明] 消息列表状态管理 (消息列表状态管理类)
 */
class MessageState(
    initialMessages: List<ChatMessage> = emptyList()
) {
    /** 消息列表数据 (消息列表的可变状态列表) */
    val messages = initialMessages.toMutableStateList()

    /** 列表滚动状态，用于自动滚动到底部 (LazyList 的状态，用于控制滚动) */
    val listState = LazyListState()
}


/**
 * [功能说明] 消息列表容器组件 (承载消息列表的Composable函数)
 * @param state       消息状态管理对象（必要） (包含消息数据和列表滚动状态)
 * @param modifier    布局修饰符（可选）
 */
@Composable
fun MessageList(
    state: MessageState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = state.listState,
        modifier = modifier
            .fillMaxSize()
            .background(FlyColors.FlyBackground),
        contentPadding = PaddingValues(horizontal = 12.wdp, vertical = 8.hdp),
        verticalArrangement = Arrangement.spacedBy(4.wdp),
        reverseLayout = true,
    ) {
        items(
            items = state.messages.asReversed(),
            key = { it.messageId }
        ) { msg ->
            MessageItem(msg)
        }
    }
}


/**
 * [功能说明] 单条消息布局组件 (单个消息项的Composable函数)
 * @param msg 消息数据对象（必要） from ChatViewModel
 */
@Composable
private fun MessageItem(
    msg: ChatMessage // ChatMessage from com.atcumt.kxq.page.ai.viewmodel
) {
    when (msg.role) {
        "user" -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                // 用户消息直接显示 replyText，不使用打字机动画
                ChatBubble(msg = msg, textToDisplay = msg.replyText, animateBubble = false)
            }
        }

        "assistant" -> {
            Column(modifier = Modifier.fillMaxWidth()) { // Assistant messages are in a Column for potential stacking of reasoning + reply
                // 1. 显示"思考中"的文本 (如果处于思考状态并且有思考内容)
                if (msg.isReasoning && msg.reasoningText.isNotBlank()) {
                    ReasoningMessageDisplay(msg = msg, textToDisplay = msg.reasoningText)
                }

                // 2. 显示AI的回复或加载指示器
                //    - 如果没有在思考 (isReasoning is false)
                //    - 并且 (有回复文本了 OR 正在加载回复文本)
                if (msg.replyText.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.Top // Align loading indicator with top of bubble
                    ) {
                        // 显示加载圈：仅当isLoading为true且当前还没有任何replyText时
                        // 这样一旦开始有文字，加载圈就消失，由打字机动画接管加载状态的视觉反馈
                        if (msg.isLoading && msg.replyText.isBlank()) {
                            Box(
                                Modifier
                                    .size(40.wdp)
                                    .padding(end = 8.wdp, top = 8.wdp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.wdp,
                                    modifier = Modifier.size(20.wdp),
                                    color = FlyColors.FlyMain
                                )
                            }
                        }
                        // 显示消息气泡：有回复文本，或者正在加载（即使文本暂时为空，也可能需要显示空的气泡结构和加载动画）
                        // 打字机动画根据 msg.isLoading 决定
                        if (msg.replyText.isNotBlank() || msg.isLoading) { // Ensures bubble structure is present if loading reply
                            ChatBubble(
                                msg = msg,
                                textToDisplay = msg.replyText,
                                animateBubble = msg.isLoading
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * [功能说明] "思考中"消息的专属展示组件 ("思考中"消息的Composable函数)
 * @param msg 消息数据对象（必要）
 * @param textToDisplay 要显示的思考文本 (来自 msg.reasoningText)
 */
@Composable
private fun ReasoningMessageDisplay(
    msg: ChatMessage,
    textToDisplay: String
) {
    var thinkingSeconds by remember(
        msg.messageId,
        msg.timeStamp
    ) { mutableLongStateOf(0L) } // Re-key on reasoningStartTime too

    LaunchedEffect(msg.messageId, msg.reasoningFinished, msg.timeStamp) {
        if (msg.isReasoning) {
            thinkingSeconds = (System.currentTimeMillis() - msg.timeStamp) / 1000
            while (msg.isReasoning) {
                delay(1000)
                if (msg.reasoningFinished) break
                thinkingSeconds = (System.currentTimeMillis() - msg.timeStamp) / 1000
            }
        } else {
            thinkingSeconds = 0L
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.wdp, horizontal = 12.wdp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = if (msg.reasoningFinished) "已思考 $thinkingSeconds 秒" else "正在深度思考中... $thinkingSeconds 秒",
            style = MaterialTheme.typography.bodySmall,
            color = FlyColors.FlyText.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.wdp)
        )
        TypewriterFadeText(
            fullText = textToDisplay,       // (使用传入的思考文本)
            animate = !msg.reasoningFinished,                 // (思考文本总是播放动画)
            charDelay = MessageStyle.charDelay,
            fadeInDuration = MessageStyle.fadeInDuration,
            modifier = Modifier
                .padding(vertical = 6.wdp, horizontal = 8.wdp)
                .widthIn(max = MessageStyle.maxBubbleWidth),
            style = MaterialTheme.typography.bodyMedium.copy(color = FlyColors.FlyText.copy(alpha = 0.4f))
        )
    }
}


/**
 * [功能说明] 消息气泡组件
 * @param msg 消息数据对象（必要）
 * @param textToDisplay 要显示的文本 (来自 msg.replyText for AI/User)
 * @param animateBubble 是否应用打字机动画 (对AI回复，此为 msg.isLoading；对用户，此为 false)
 * @param modifier 布局修饰符（可选）
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatBubble(
    msg: ChatMessage, // Needed for role to determine bubble color/alignment
    textToDisplay: String,
    animateBubble: Boolean, // Controls TypewriterFadeText animation
    modifier: Modifier = Modifier
) {
    // 使用新的 MarkdownChatBubble 组件来同时支持Markdown渲染和打字机效果
    MarkdownChatBubble(
        msg = msg,
        textToDisplay = textToDisplay,
        animateBubble = animateBubble,
        modifier = modifier
    )
}