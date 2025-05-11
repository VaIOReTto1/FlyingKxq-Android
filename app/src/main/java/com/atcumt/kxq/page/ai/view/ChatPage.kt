package com.atcumt.kxq.page.ai.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import com.atcumt.kxq.page.ai.component.ChatDrawerContent
import com.atcumt.kxq.page.ai.viewmodel.ChatIntent
import com.atcumt.kxq.page.ai.viewmodel.ChatViewModel
import com.atcumt.kxq.page.ai.component.ChatInputField
import com.atcumt.kxq.page.ai.component.ChatTopBar
import com.atcumt.kxq.page.ai.component.MessageList
import com.atcumt.kxq.page.ai.component.MessageState
import com.atcumt.kxq.ui.theme.FlyColors
import com.atcumt.kxq.utils.hdp
import com.atcumt.kxq.utils.wdp
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    isCurrentPage: Boolean
) {
    // 订阅 ViewModel 的状态
    val uiState by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    // 抽屉状态管理
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    // Effect to close drawer if page is not current
    LaunchedEffect(isCurrentPage, drawerState.isOpen) {
        if (!isCurrentPage && drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
        }
    }

    // 持有同一个 MessageState，用于保留滚动位置等状态
    val messageState = remember { MessageState() }
    // 当消息列表更新时，同步到 MessageState
    LaunchedEffect(uiState.messages) {
        messageState.messages.clear()
        messageState.messages += uiState.messages
    }

    // 首次进入页面时加载会话列表
    LaunchedEffect(viewModel) {
        viewModel.dispatch(ChatIntent.LoadConversations)
    }

    // 记录输入框高度，用于内容区域底部留白
    var inputHeight by remember { mutableStateOf(0.hdp) }
    val density = LocalDensity.current

    // 整个页面外层抽屉，用于会话列表
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .width(260.wdp)
                    .background(FlyColors.FlyBackground),
                drawerContainerColor = FlyColors.FlyBackground,
                drawerContentColor = FlyColors.FlyMain,
            ) {
                ChatDrawerContent(
                    uiState = uiState,
                    onSelectConversation = { id, title ->
                        viewModel.dispatch(ChatIntent.SelectConversation(id, title))
                    },
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        // 主内容区域，包括顶部栏、消息列表和输入框
        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            topBar = {
                ChatTopBar(
                    title = uiState.currentTitle.ifBlank { "元宝 DeepSeek" },
                    onMenu = { scope.launch { drawerState.open() } },
                    onNew = {
                        // 新建对话
                        viewModel.dispatch(ChatIntent.NewConversation)
                        if (drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                    }
                )
            },
            containerColor = FlyColors.FlyBackground
        ) { inner ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(inner)
            ) {
                // 历史消息加载中指示器
                if (uiState.isLoadingMessages) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.wdp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = FlyColors.FlyMain)
                    }
                }
                // 消息列表固定在上方，不随键盘弹出位移
                MessageList(
                    state = messageState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = inputHeight)
                )

                // 输入框贴底，随键盘上移
                ChatInputField(
                    value = uiState.inputText,
                    onValueChange = { viewModel.dispatch(ChatIntent.UpdateInput(it)) },
                    onSend = {
                        if (uiState.inputText.isNotBlank()) {
                            viewModel.dispatch(ChatIntent.SendMessage(uiState.inputText))
                        }
                    },
                    reasoningEnabled = uiState.reasoningEnabled,
                    searchEnabled = uiState.searchEnabled,
                    onDeepThink = { viewModel.dispatch(ChatIntent.ToggleReasoning) },
                    onWebSearch = { viewModel.dispatch(ChatIntent.ToggleSearch) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .imePadding()
                        .onGloballyPositioned { coords ->
                            // 将像素高度转换为 dp，用于列表底部留白
                            inputHeight = with(density) { coords.size.height.toDp() }
                        }
                )
            }
        }
    }
}