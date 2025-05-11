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
import com.atcumt.kxq.page.ai.utils.ChatStorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 1. Intent：扩展了会话加载、选中、新建、输入更新、历史加载、发送消息，以及深度思考和联网搜索开关
sealed class ChatIntent {
    object LoadConversations : ChatIntent()                // 加载会话列表
    data class SelectConversation(val id: String, val title: String) : ChatIntent() // 选中指定会话
    object NewConversation : ChatIntent()                  // 新建会话
    data class UpdateInput(val text: String) : ChatIntent()// 更新输入框内容
    object LoadHistory : ChatIntent()                      // 加载当前会话历史
    data class SendMessage(val content: String) : ChatIntent() // 发送消息

    object ToggleReasoning : ChatIntent()   // 切换深度思考开关
    object ToggleSearch : ChatIntent()      // 切换联网搜索开关
}

// 2. State：包含会话列表、当前会话 ID 与标题、消息列表、输入内容，以及各加载状态和开关
data class ChatState(
    val isLoadingConversations: Boolean = false,
    val conversations: List<ConversationsService.ConversationPageVO> = emptyList(),
    val currentConversationId: String = "",
    val currentTitle: String = "",
    val isLoadingMessages: Boolean = false,
    val messages: List<ChatMessage> = emptyList(),
    val inputText: String = "",
    val reasoningEnabled: Boolean = false,  // 深度思考开关状态
    val searchEnabled: Boolean = false      // 联网搜索开关状态
)

// 单条消息的数据模型
data class ChatMessage(
    val messageId: Int,
    val role: String,
    val content: String,
    val isLoading: Boolean = false,
    val timeStamp: Long = System.currentTimeMillis()
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatStorageService: ChatStorageService
) : ViewModel() {
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    // 接收并处理各种 Intent
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

    // 加载所有会话列表
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
                // 如果存在会话且当前未选中任何会话，则自动选中第一个
                if (list.isNotEmpty() && _state.value.currentConversationId.isBlank()) {
                    list.firstOrNull()?.let { first ->
                        dispatch(ChatIntent.SelectConversation(first.conversationId, first.title))
                    }
                } else if (list.isEmpty()) {
                    // 如果没有任何会话，清空当前 ID、标题和消息列表
                    _state.update { it.copy(currentConversationId = "", currentTitle = "", messages = emptyList()) }
                }
            }
        }
    }

    // 选中指定会话并加载其历史
    private fun selectConversation(id: String, title: String) {
        // 如果已经选中同一个会话且已有消息，则不重复加载
        if (_state.value.currentConversationId == id && _state.value.messages.isNotEmpty()) return

        _state.update {
            it.copy(
                currentConversationId = id,
                currentTitle = title,
                messages = emptyList(),      // 切换前先清空消息列表
                isLoadingMessages = true     // 设置加载中状态
            )
        }
        dispatch(ChatIntent.LoadHistory)
    }

    // 新建本地会话（客户端清空，以后首条消息发送时服务端返回 ID）
    private fun newConversation() {
        _state.update {
            it.copy(
                currentConversationId = "",  // 清空会话 ID
                currentTitle = "新对话",         // 默认标题
                messages = emptyList(),       // 清空消息
                inputText = ""              // 清空输入
            )
        }
        // 可选：如果服务端立即创建，会调用 LoadConversations 刷新
    }

    // 加载当前会话的历史消息：先本地，再网络
    private fun loadHistory() {
        val convId = _state.value.currentConversationId
        if (convId.isBlank()) {
            // 新会话或无会话，直接清空并结束
            _state.update { it.copy(isLoadingMessages = false, messages = emptyList()) }
            return
        }
        _state.update { it.copy(isLoadingMessages = true) }

        viewModelScope.launch {
            // 1. 尝试从本地存储加载
            val localMessages = chatStorageService.loadMessages(convId)
            if (localMessages.isNotEmpty()) {
                _state.update {
                    it.copy(
                        isLoadingMessages = false,
                        messages = localMessages
                    )
                }
                return@launch
            }

            // 2. 本地无数据则请求网络
            ConversationHistoryService().getHistory(convId) { resp, error ->
                viewModelScope.launch {
                    if (resp?.data != null && error == null) {
                        val msgs = resp.data.messages.map {
                            ChatMessage(it.messageId, it.role, it.content)
                        }
                        _state.update {
                            it.copy(
                                isLoadingMessages = false,
                                messages = msgs
                            )
                        }
                        // 将网络数据保存到本地
                        if (msgs.isNotEmpty()) {
                            chatStorageService.saveMessages(convId, msgs)
                        }
                    } else {
                        // 加载失败，结束加载状态，可显示错误提示
                        _state.update { it.copy(isLoadingMessages = false) }
                    }
                }
            }
        }
    }

    // 发送消息：先添加用户消息，再建立 SSE 请求并追加助手回复
    private fun sendMessage(content: String) {
        if (content.isBlank()) return

        // 1. 创建用户消息并添加到状态
        val userMessage = ChatMessage(
            messageId = (_state.value.messages.maxOfOrNull { it.messageId } ?: 0) + 1, // 临时客户端 ID
            role = "user",
            content = content
        )
        _state.update {
            it.copy(
                inputText = "",                       // 发送后清空输入框
                messages = it.messages + userMessage
            )
        }

        // 保存用户消息到本地
        val currentConvId = _state.value.currentConversationId
        if (currentConvId.isNotBlank()) {
            viewModelScope.launch { chatStorageService.saveMessages(currentConvId, _state.value.messages) }
        }

        // 2. 添加助手占位消息
        val assistantPlaceholder = ChatMessage(
            messageId = (_state.value.messages.maxOfOrNull { it.messageId } ?: 0) + 1,
            role = "assistant",
            content = "",
            isLoading = true
        )
        _state.update {
            it.copy(
                messages = it.messages + assistantPlaceholder
            )
        }

        // 3. 构建 SSE 请求 DTO
        val dto = ConversationService.ConversationDTO(
            conversationId = _state.value.currentConversationId.takeIf { it.isNotBlank() },
            content = content,
            reasoningEnabled = _state.value.reasoningEnabled,
            searchEnabled = _state.value.searchEnabled
        )

        // 4. 发送请求并处理 SSE 数据
        ConversationService().postConversation(dto) { sseData, error ->
            viewModelScope.launch {
                var conversationIdToSave = _state.value.currentConversationId

                if (error != null) {
                    // 网络或 SSE 错误，更新最后一条助手消息为错误状态
                    _state.update { state ->
                        val list = state.messages.toMutableList()
                        if (list.isNotEmpty() && list.last().role == "assistant" && list.last().isLoading) {
                            list[list.lastIndex] = list.last().copy(
                                content = "出错了：${error.message}", isLoading = false
                            )
                        }
                        state.copy(messages = list)
                    }
                } else if (sseData != null) {
                    try {
                        val chunk = Gson().fromJson(sseData, JsonObject::class.java)
                        // 检测并更新新的会话 ID
                        if (chunk.has("type") && chunk.get("type").asString == "newConversation" && chunk.has("conversationId")) {
                            val newConvId = chunk.get("conversationId").asString
                            if (newConvId.isNotBlank()) {
                                conversationIdToSave = newConvId
                                _state.update { it.copy(currentConversationId = newConvId) }
                                // 新会话生成后，可刷新列表以获取后台标题
                                dispatch(ChatIntent.LoadConversations)
                            }
                        }

                        // 追加助手回复的增量文本
                        val delta = chunk.get("textContent")?.asString ?: ""
                        if (delta.isNotEmpty() || (chunk.has("type") && chunk.get("type").asString != "newConversation")) {
                            _state.update { state ->
                                val list = state.messages.toMutableList()
                                if (list.isNotEmpty() && list.last().role == "assistant" && list.last().isLoading) {
                                    val last = list.removeLast()
                                    list.add(last.copy(content = last.content + delta, isLoading = false))
                                } else if (list.isNotEmpty() && list.last().role == "assistant" && !list.last().isLoading) {
                                    val last = list.removeLast()
                                    list.add(last.copy(content = last.content + delta))
                                }
                                state.copy(messages = list)
                            }
                        }
                        // 当 SSE 标记消息结束时，将 isLoading 设为 false
                        if (chunk.has("type") && chunk.get("type").asString == "message") {
                            _state.update { state ->
                                val list = state.messages.toMutableList()
                                if (list.isNotEmpty() && list.last().role == "assistant") {
                                    list[list.lastIndex] = list.last().copy(isLoading = false)
                                }
                                state.copy(messages = list)
                            }
                        }

                    } catch (e: Exception) {
                        // 增量解析出错，更新助手消息内容并结束加载状态
                        _state.update { state ->
                            val list = state.messages.toMutableList()
                            if (list.isNotEmpty() && list.last().role == "assistant" && list.last().isLoading) {
                                list[list.lastIndex] = list.last().copy(
                                    content = list.last().content + "(数据解析错误)", isLoading = false
                                )
                            }
                            state.copy(messages = list)
                        }
                    }
                }

                // 最终保存全部消息
                if (conversationIdToSave.isNotBlank()) {
                    chatStorageService.saveMessages(conversationIdToSave, _state.value.messages)
                }
            }
        }
    }
}