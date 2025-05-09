package com.atcumt.kxq.page.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atcumt.kxq.utils.network.ai.user.ConversationsService
import com.atcumt.kxq.utils.network.ai.user.conversation.ConversationService
import com.atcumt.kxq.utils.network.ai.user.conversation.conversationId.ConversationHistoryService
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Intent：扩展了会话加载、选择、新建、输入更新、历史加载、发送消息
sealed class ChatIntent {
    data object LoadConversations : ChatIntent()                // 加载会话列表
    data class SelectConversation(val id: String, val title: String) : ChatIntent() // 选中某个会话
    data object NewConversation : ChatIntent()                  // 新建会话
    data class UpdateInput(val text: String) : ChatIntent()// 更新输入框内容
    data object LoadHistory : ChatIntent()                      // 加载当前会话历史
    data class SendMessage(val content: String) : ChatIntent() // 发送消息

    data object ToggleReasoning : ChatIntent()   // 切换深度思考
    data object ToggleSearch : ChatIntent()   // 切换联网搜索
}

// 2. State：包含会话列表、当前会话、消息、输入等
data class ChatState(
    val isLoadingConversations: Boolean = false,
    val conversations: List<ConversationsService.ConversationPageVO> = emptyList(),
    val currentConversationId: String = "",
    val currentTitle: String = "",
    val isLoadingMessages: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val reasoningEnabled: Boolean = false,  // 深度思考开关
    val searchEnabled: Boolean = false  // 联网搜索开关
)

// 单条消息数据
data class ChatMessage(
    val messageId: Int,
    val role: String,
    val content: String,
    val isLoading: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun dispatch(intent: ChatIntent) {
        when (intent) {
            ChatIntent.LoadConversations -> loadConversations()
            is ChatIntent.SelectConversation -> selectConversation(intent.id, intent.title)
            ChatIntent.NewConversation -> newConversation()
            is ChatIntent.UpdateInput -> _state.update { it.copy(inputText = intent.text) }
            ChatIntent.LoadHistory -> loadHistory()
            is ChatIntent.SendMessage -> sendMessage(intent.content)
            ChatIntent.ToggleReasoning -> _state.update { it.copy(reasoningEnabled = !it.reasoningEnabled) }
            ChatIntent.ToggleSearch -> _state.update { it.copy(searchEnabled = !it.searchEnabled) }
        }
    }

    // 拉取所有会话
    private fun loadConversations() {
        _state.update { it.copy(isLoadingConversations = true) }
        ConversationsService().getConversations(1, 100) { resp, _ ->
            viewModelScope.launch {
                val list = resp?.data?.data ?: emptyList()
                _state.update {
                    it.copy(
                        isLoadingConversations = false,
                        conversations = list
                    )
                }
                // 自动选中第一个会话
                list.firstOrNull()?.let { first ->
                    dispatch(ChatIntent.SelectConversation(first.conversationId, first.title))
                }
            }
        }
    }

    // 切换会话
    private fun selectConversation(id: String, title: String) {
        _state.update {
            it.copy(
                currentConversationId = id,
                currentTitle = title,
                messages = emptyList()
            )
        }
        dispatch(ChatIntent.LoadHistory)
    }

    // 新建会话（客户端直接清空即可；可根据服务端 API 适配）
    private fun newConversation() {
        _state.update {
            it.copy(
                currentConversationId = "",
                currentTitle = "新对话",
                messages = emptyList()
            )
        }
        // 重新刷新列表
        dispatch(ChatIntent.LoadConversations)
    }

    // 拉取当前会话历史
    private fun loadHistory() {
        val convId = _state.value.currentConversationId
        if (convId.isBlank()) return
        _state.update { it.copy(isLoadingMessages = true) }
        ConversationHistoryService().getHistory(convId) { resp, _ ->
            viewModelScope.launch {
                if (resp?.data != null) {
                    val msgs = resp.data.messages.map {
                        ChatMessage(it.messageId, it.role, it.content)
                    }
                    _state.update {
                        it.copy(
                            isLoadingMessages = false,
                            messages = msgs
                        )
                    }
                } else {
                    _state.update { it.copy(isLoadingMessages = false) }
                }
            }
        }
    }

    // 发送消息
    private fun sendMessage(content: String) {
        // 1) 先把用户自己的消息推到列表里
        _state.update {
            it.copy(
                inputText = "",
                messages = it.messages + ChatMessage(-1, "user", content)
            )
        }

        // 2) 在列表里再插入一个空的 assistant 消息
        _state.update {
            it.copy(
                messages = it.messages + ChatMessage(-1, "assistant", "", true)
            )
        }

        // 准备 SSE 请求
        val dto = ConversationService.ConversationDTO(
//            conversationId = _state.value.currentConversationId.takeIf { it.isNotBlank() },
            conversationId = "",
            content = content,
            reasoningEnabled = _state.value.reasoningEnabled,
            searchEnabled = _state.value.searchEnabled
        )

        ConversationService().postConversation(dto) { sseData, error ->
            viewModelScope.launch {
                if (error != null) {
                    // 出错时，把 error 推进同一个气泡
                    _state.update { state ->
                        val list = state.messages.toMutableList()
                        list[list.lastIndex] = list.last().copy(
                            content = "出错了：${error.message}", isLoading = false
                        )
                        state.copy(messages = list)

                    }
                } else if (sseData != null) {
                    // 解析 SSE 包裹的 JSON，取出 textContent
                    val chunk = Gson().fromJson(sseData, JsonObject::class.java)
                    val delta = chunk.get("textContent")?.asString ?: ""
                    // 把 delta 追加到最后一个 assistant 消息上
                    _state.update { state ->
                        val list = state.messages.toMutableList()
                        val last = list.removeLast()
                        list.add(last.copy(content = last.content + delta, isLoading = false))
                        state.copy(messages = list)
                    }
                }
            }
        }
    }

}
