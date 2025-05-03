package com.atcumt.kxq.page.ai.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.atcumt.kxq.page.ai.viewmodel.ChatIntent
import com.atcumt.kxq.page.ai.viewmodel.ChatViewModel
import com.atcumt.kxq.page.ai.component.ChatInputField
import com.atcumt.kxq.page.ai.component.ChatTopBar
import com.atcumt.kxq.page.ai.component.MessageList
import com.atcumt.kxq.page.ai.component.MessageState
import com.atcumt.kxq.page.ai.viewmodel.ChatMessage
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // 持有同一个 MessageState，保留 listState
    val messageState = remember { MessageState() }
    // 同步 ViewModel 的消息到 MessageState
    LaunchedEffect(uiState.messages) {
        messageState.messages.clear()
        messageState.messages += uiState.messages
    }

    var inputHeight by remember { mutableStateOf(0.hdp) }
    val density = LocalDensity.current

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "会话列表",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.wdp)
                )
                Divider()
                uiState.conversations.forEach { conv ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = conv.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        selected = conv.conversationId == uiState.currentConversationId,
                        onClick = {
                            viewModel.dispatch(
                                ChatIntent.SelectConversation(
                                    conv.conversationId,
                                    conv.title
                                )
                            )
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(horizontal = 8.wdp)
                    )
                }
            }
        }
    ) {
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                ChatTopBar(
                    title = uiState.currentTitle.ifBlank { "元宝 DeepSeek" },
                    onMenu = { scope.launch { drawerState.open() } },
                    onNew = { viewModel.dispatch(ChatIntent.NewConversation) }
                )
            },
            containerColor = FlyColors.FlyBackground
        ) { inner ->
            Box(Modifier.fillMaxSize().padding(inner)) {
                // 消息列表
                if (uiState.isLoadingMessages) {
                    // 可放一个加载 Spinner
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.wdp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = FlyColors.FlyMain)
                    }
                }
                // 1. 列表固定在顶部，不受 IME 影响
                MessageList(
                    state = messageState,
                    modifier = Modifier
                        .fillMaxSize().padding(bottom = inputHeight)
                )

                // 2. 输入框贴底，并让它随键盘上移
                ChatInputField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.dispatch(ChatIntent.UpdateInput(it)) },
                    onSend = { viewModel.dispatch(ChatIntent.SendMessage(uiState.inputText)) },
                    reasoningEnabled = uiState.reasoningEnabled,
                    searchEnabled = uiState.searchEnabled,
                    onDeepThink = { viewModel.dispatch(ChatIntent.ToggleReasoning) },
                    onWebSearch = { viewModel.dispatch(ChatIntent.ToggleSearch) },
                    // 聚焦时自动滚到底
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .imePadding().onGloballyPositioned { coords ->
                            // 将 px 转为 dp
                            inputHeight = with(density) { coords.size.height.toDp() }
                        }
                )
            }
        }
    }
}

