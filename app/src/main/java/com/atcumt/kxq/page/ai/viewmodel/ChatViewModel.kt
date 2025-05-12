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
import com.atcumt.kxq.utils.network.ai.user.StopConversationService
import com.atcumt.kxq.utils.network.ai.user.conversation.conversationId.DeleteConversationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// 1. Intent：扩展了会话加载、选中、新建、输入更新、历史加载、发送消息，以及深度思考和联网搜索开关
sealed class ChatIntent {
    object LoadConversations : ChatIntent()                // 加载会话列表
    data class SelectConversation(val id: String, val title: String) : ChatIntent() // 选中指定会话
    object NewConversation : ChatIntent()                  // 新建会话
    data class UpdateInput(val text: String) : ChatIntent()// 更新输入框内容
    object LoadHistory : ChatIntent()                      // 加载当前会话历史
    data class SendMessage(val userInputText: String) :
        ChatIntent() // 发送消息 (renamed content to userInputText for clarity)

    object ToggleReasoning : ChatIntent()   // 切换深度思考开关
    object ToggleSearch : ChatIntent()      // 切换联网搜索开关
    object StopStreaming : ChatIntent()      // Added
    data class DeleteConversation(val conversationId: String) : ChatIntent() // Added
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
    val reasoningEnabled: Boolean = false,
    val searchEnabled: Boolean = false,
    val isAiStreamingResponse: Boolean = false // True if AI is actively sending REASONING_MESSAGE or TEXT_MESSAGE
)

// 单条消息的数据模型
data class ChatMessage(
    val messageId: Int, // For AI messages, this is the sseMessageId. For user messages, a unique negative ID.
    val role: String, // "user" or "assistant"
    var reasoningText: String = "",    // Stores content from REASONING_MESSAGE
    var reasoningFinished: Boolean = false, // 新增：思考完成标记
    var replyText: String = "",        // Stores content from TEXT_MESSAGE and final "message". For user role, this holds their input.
    var isLoading: Boolean = false,    // True if AI is streaming TEXT_MESSAGE chunks for this message's replyText
    var isReasoning: Boolean = false,  // True if AI is streaming REASONING_MESSAGE chunks for this message's reasoningText
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
            is ChatIntent.SendMessage -> sendMessage(intent.userInputText) // Pass userInputText
            ChatIntent.ToggleReasoning -> _state.update { it.copy(reasoningEnabled = !it.reasoningEnabled) }
            ChatIntent.ToggleSearch -> _state.update { it.copy(searchEnabled = !it.searchEnabled) }
            ChatIntent.StopStreaming -> stopStreaming()
            is ChatIntent.DeleteConversation -> deleteConversation(intent.conversationId)
        }
    }

    // 辅助函数：根据当前消息列表更新全局的AI流响应状态
    private fun updateGlobalStreamingState() {
        val isStillStreaming = _state.value.messages.any { msg ->
            msg.role == "assistant" && msg.isLoading
        }
        if (_state.value.isAiStreamingResponse != isStillStreaming) {
            _state.update { it.copy(isAiStreamingResponse = isStillStreaming) }
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
                if (list.isNotEmpty() && _state.value.currentConversationId.isBlank()) {
                list.firstOrNull()?.let { first ->
                    dispatch(ChatIntent.SelectConversation(first.conversationId, first.title))
                    }
                } else if (list.isEmpty()) {
                    newConversation() // 如果没有会话，则进入新会话状态
                }
            }
        }
    }

    // 选中指定会话并加载其历史
    private fun selectConversation(id: String, title: String) {
        if (_state.value.currentConversationId == id && _state.value.messages.isNotEmpty()) return

        _state.update {
            it.copy(
                currentConversationId = id,
                currentTitle = title,
                messages = emptyList(),
                isLoadingMessages = true,
                isAiStreamingResponse = false
            )
        }
        dispatch(ChatIntent.LoadHistory)
    }

    // 新建本地会话
    private fun newConversation() {
        _state.update {
            it.copy(
                currentConversationId = "",
                currentTitle = "新对话",
                messages = emptyList(),
                inputText = "",
                isAiStreamingResponse = false
            )
        }
        refreshConversationList()
    }

    /** 拉取所有会话列表 */
    fun refreshConversationList() {
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
            }
        }
    }

    // 加载当前会话的历史消息
    private fun loadHistory() {
        val convId = _state.value.currentConversationId
        _state.update { it.copy(isAiStreamingResponse = false) } // Ensure AI not streaming during history load

        if (convId.isBlank()) {
            _state.update { it.copy(isLoadingMessages = false, messages = emptyList()) }
            return
        }
        _state.update { it.copy(isLoadingMessages = true) }

        viewModelScope.launch {
            val localMessages = chatStorageService.loadMessages(convId).map {
                // Ensure historical messages are not in loading/reasoning state
                it.copy(
                    isLoading = false,
                    reasoningText = "",
                    replyText = ""
                )
            }
            if (localMessages.isNotEmpty()) {
                _state.update {
                    it.copy(isLoadingMessages = false, messages = localMessages)
                }
                return@launch
            }

            ConversationHistoryService().getHistory(convId) { resp, error ->
            viewModelScope.launch {
                    if (resp?.data != null && error == null) {
                        val msgs = resp.data.messages.mapNotNull { historyItem ->
                            // For assistant messages, content goes to replyText. User messages also use replyText.
                            if (historyItem.role == "user" || historyItem.role == "assistant") {
                                ChatMessage(
                                    messageId = historyItem.messageId, // Assuming history items have a messageId
                                    role = historyItem.role,
                                    replyText = historyItem.content, // Historical content is the final reply
                                    reasoningText = "", // No separate reasoning history from this endpoint
                                    isLoading = false,
                                    timeStamp = System.currentTimeMillis() // Use server time if available
                                )
                            } else null
                    }
                    _state.update {
                            it.copy(isLoadingMessages = false, messages = msgs)
                        }
                        if (msgs.isNotEmpty()) {
                            chatStorageService.saveMessages(convId, msgs)
                    }
                } else {
                    _state.update { it.copy(isLoadingMessages = false) }
                    }
                }
            }
        }
    }

    // 发送消息
    private fun sendMessage(userInputText: String) {
        if (userInputText.isBlank()) return

        val nextUserMessageId =
            (_state.value.messages.filter { it.role == "user" }.minOfOrNull { it.messageId }
                ?: 0) - 1
        val userMessage = ChatMessage(
            messageId = nextUserMessageId,
            role = "user",
            replyText = userInputText, // User's text goes into replyText
            isLoading = false,

            )
        _state.update {
            it.copy(
                inputText = "",
                messages = it.messages + userMessage,
                isAiStreamingResponse = false // AI starts responding after this
            )
        }

        val currentConvId = _state.value.currentConversationId
        if (currentConvId.isNotBlank()) {
            viewModelScope.launch {
                chatStorageService.saveMessages(
                    currentConvId,
                    _state.value.messages
                )
            }
        }

        val dto = ConversationService.ConversationDTO(
            conversationId = _state.value.currentConversationId.takeIf { it.isNotBlank() },
            content = userInputText, // DTO uses 'content' for user input
            reasoningEnabled = _state.value.reasoningEnabled,
            searchEnabled = _state.value.searchEnabled
        )

        ConversationService().postConversation(dto) { sseData, error ->
            viewModelScope.launch {
                var conversationIdToSave = _state.value.currentConversationId

                if (error != null) {
                    handleSseError("与服务器通信失败: ${error.message}")
                } else if (sseData != null) {
                    try {
                        val chunk = Gson().fromJson(sseData, JsonObject::class.java)
                        val type = chunk.get("type")?.asString ?: ""
                        if (type == "COMPLETE") {
                            _state.update { st ->
                                val msgs = st.messages.map { msg ->
                                    if (msg.role == "assistant" && (msg.isLoading || msg.isReasoning))
                                        msg.copy(isLoading = false)
                                    else msg
                                }
                                // ② 关掉全局流式标记
                                st.copy(messages = msgs, isAiStreamingResponse = false)
                            }
                            return@launch
                        }
                        val sseMessageId =
                            chunk.get("messageId")?.asInt // Nullable for newConversation
                        val sseContent = chunk.get("textContent")?.asString ?: ""

                        if (sseMessageId == null && type != "newConversation" && type != "error") { // Allow error type to proceed
                            System.err.println("SSE Error: AI message chunk without sseMessageId. Type: $type, Data: $sseData")
                            // Optionally add a generic error message to UI if this case is critical
                            // For now, we will try to update the last assistant message if one is loading/reasoning
                            _state.update { state ->
                                val messages = state.messages.toMutableList()
                                val lastAiMsgIndex =
                                    messages.indexOfLast { it.role == "assistant" && (it.isLoading || it.isReasoning) }
                                if (lastAiMsgIndex != -1) {
                                    messages[lastAiMsgIndex] = messages[lastAiMsgIndex].copy(
                                        replyText = messages[lastAiMsgIndex].replyText + sseContent + "(数据块错误: 无消息ID)",
                                        isLoading = false,
                                    )
                                }
                                state.copy(messages = messages)
                            }
                            updateGlobalStreamingState()
                            return@launch
                        }


                    _state.update { state ->
                            val messages = state.messages.toMutableList()
                            // For "newConversation" and "error" types, sseMessageId might be null, handle them first.
                            if (type == "newConversation") {
                                val newConvId = chunk.get("conversationId")?.asString
                                if (newConvId != null && newConvId.isNotBlank()) {
                                    conversationIdToSave = newConvId
                                    return@update state.copy(
                                        messages = messages,
                                        currentConversationId = newConvId
                                    )
                                }
                                return@update state // No change if newConvId is missing
                            }
                            if (type == "error") { // Server-side error reported via SSE
                                val errorMessage =
                                    chunk.get("message")?.asString ?: sseContent ?: "未知服务器错误"
                                val errorSseMessageId =
                                    sseMessageId ?: ((messages.maxOfOrNull { it.messageId }
                                        ?: 0) + 1) // Use provided or new ID

                                val existingMsgIndex =
                                    messages.indexOfFirst { msg -> msg.messageId == errorSseMessageId && msg.role == "assistant" }
                                if (existingMsgIndex != -1) {
                                    messages[existingMsgIndex] = messages[existingMsgIndex].copy(
                                        replyText = messages[existingMsgIndex].replyText + " 错误: $errorMessage",
                                        isLoading = false,
                                    )
                                } else {
                                    messages.add(
                                        ChatMessage(
                                            messageId = errorSseMessageId,
                                            role = "assistant",
                                            replyText = "错误: $errorMessage"
                                        )
                                    )
                                }
                                return@update state.copy(messages = messages)
                            }


                            // Proceed only if sseMessageId is not null for other types
                            if (sseMessageId == null) {
                                System.err.println("SSE Logic Error: sseMessageId is null for type $type. Data: $sseData")
                                return@update state // No change
                            }


                            val existingMsgIndex =
                                messages.indexOfFirst { msg -> msg.messageId == sseMessageId && msg.role == "assistant" }
                            val currentMsg: ChatMessage? =
                                if (existingMsgIndex != -1) messages[existingMsgIndex] else null

                            when (type) {
                                "REASONING_MESSAGE" -> {
                                    if (currentMsg != null) {
                                        messages[existingMsgIndex] = currentMsg.copy(
                                            reasoningText = currentMsg.reasoningText + sseContent,
                                            isReasoning = true,
                                            isLoading = true, // Not loading reply text while reasoning
                                        )
                                    } else {
                                        messages.add(
                                            ChatMessage(
                                                messageId = sseMessageId, role = "assistant",
                                                reasoningText = sseContent,
                                                isReasoning = true, isLoading = true,
                                            )
                                        )
                                    }
                                }

                                "TEXT_MESSAGE" -> {
                                    if (currentMsg != null) {
                                        messages[existingMsgIndex] = currentMsg.copy(
                                            // If it was reasoning, new text replaces replyText. Otherwise, append.
                                            replyText = currentMsg.replyText + sseContent,
                                            isLoading = true,    // Actively loading reply text
                                            reasoningFinished = true,
                                        )
                                    } else {
                                        // This case (TEXT_MESSAGE for a new sseMessageId without prior REASONING_MESSAGE)
                                        // is unusual but we'll handle it by creating a new message.
                                        System.err.println("SSE Warning: TEXT_MESSAGE for unknown sseMessageId: $sseMessageId. Creating new message.")
                                        messages.add(
                                            ChatMessage(
                                                messageId = sseMessageId, role = "assistant",
                                                replyText = sseContent,
                                                isLoading = true,
                                                reasoningFinished = true,
                                            )
                                        )
                                    }
                                }

                                else -> {
                                    System.err.println("SSE Info: Received unknown event type '$type' for sseMessageId: $sseMessageId")
                                }
                            }
                            state.copy(messages = messages)
                        }

                    } catch (e: Exception) {
                        handleSseError("SSE数据解析错误: ${e.message}")
                    }
                }

                // Save and update global state after each SSE event processing
                if (conversationIdToSave.isNotBlank() && _state.value.messages.isNotEmpty()) {
                    val finalConvIdToSave =
                        if (_state.value.currentConversationId == conversationIdToSave) conversationIdToSave else _state.value.currentConversationId
                    if (finalConvIdToSave.isNotBlank()) {
                        chatStorageService.saveMessages(finalConvIdToSave, _state.value.messages)
                    }
                }
                updateGlobalStreamingState()

                if (sseData != null && Gson().fromJson(sseData, JsonObject::class.java)
                        .get("type")?.asString == "newConversation"
                ) {
                    dispatch(ChatIntent.LoadConversations)
                }
            }
        }
    }

    private fun handleSseError(errorMessage: String) {
        _state.update { state ->
            val messages = state.messages.toMutableList()
            val lastAssistantMsgIndex =
                messages.indexOfLast { it.role == "assistant" && (it.isLoading || it.isReasoning) }
            if (lastAssistantMsgIndex != -1) {
                messages[lastAssistantMsgIndex] = messages[lastAssistantMsgIndex].copy(
                    replyText = messages[lastAssistantMsgIndex].replyText + "($errorMessage)", // Append error to replyText
                    isLoading = false,
                )
            } else {
                messages.add(ChatMessage(
                    messageId = (messages.maxOfOrNull { it.messageId }
                        ?: 0) + 1, // Temporary error message ID
                    role = "assistant", replyText = errorMessage,
                    isLoading = false,
                ))
            }
            state.copy(messages = messages)
        }
        updateGlobalStreamingState() // Ensure streaming state is off after error
    }


    // 停止AI流式响应
    private fun stopStreaming() {
        val currentConvId = _state.value.currentConversationId
        val streamingMsgIndex = _state.value.messages.indexOfLast {
            it.role == "assistant" && (it.isLoading || it.isReasoning)
        }

        if (currentConvId.isNotBlank() && streamingMsgIndex != -1) {
            StopConversationService().stopConversation(
                StopConversationService.StopConversationDTO(conversationId = currentConvId)
            ) { _, _ -> // Regardless of server success, client marks as stopped
                viewModelScope.launch {
                    _state.update { state ->
                        val updatedMessages = state.messages.toMutableList()
                        if (streamingMsgIndex < updatedMessages.size && updatedMessages[streamingMsgIndex].role == "assistant") {
                            val msgToStop = updatedMessages[streamingMsgIndex]
                            val stopSuffix = " (已手动停止)"
                            updatedMessages[streamingMsgIndex] = msgToStop.copy(
                                // Append to whichever text was active or most relevant
                                reasoningText = if (msgToStop.isReasoning) msgToStop.reasoningText + stopSuffix else msgToStop.reasoningText,
                                replyText = if (!msgToStop.isReasoning && msgToStop.replyText.isNotEmpty()) msgToStop.replyText + stopSuffix else if (!msgToStop.isReasoning) stopSuffix else msgToStop.replyText,
                                isLoading = false
                            )
                        }
                        state.copy(messages = updatedMessages)
                    }
                    if (_state.value.messages.isNotEmpty()) {
                        chatStorageService.saveMessages(currentConvId, _state.value.messages)
                    }
                    updateGlobalStreamingState()
                }
            }
        } else {
            _state.update { it.copy(isAiStreamingResponse = false) }
        }
    }

    // 删除指定会话
    private fun deleteConversation(conversationId: String) {
        viewModelScope.launch {
            DeleteConversationService().deleteConversation(conversationId) { _, error ->
                viewModelScope.launch {
                    if (error == null) {
                        chatStorageService.deleteMessages(conversationId)
                        val remainingConversations =
                            _state.value.conversations.filterNot { c -> c.conversationId == conversationId }
                        _state.update { it.copy(conversations = remainingConversations) }

                        if (_state.value.currentConversationId == conversationId) {
                            remainingConversations.firstOrNull()?.let { next ->
                                dispatch(
                                    ChatIntent.SelectConversation(
                                        next.conversationId,
                                        next.title
                                    )
                                )
                            } ?: dispatch(ChatIntent.NewConversation)
                        }
                    } else {
                        System.err.println("Failed to delete conversation $conversationId: ${error.message}")
                        // Consider adding a UI error message here
                    }
                }
            }
        }
    }
}