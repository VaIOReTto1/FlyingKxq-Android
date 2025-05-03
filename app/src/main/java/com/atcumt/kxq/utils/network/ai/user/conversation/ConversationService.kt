package com.atcumt.kxq.utils.network.ai.user.conversation

import android.util.Log
import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.annotations.SerializedName
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener

class ConversationService {
    companion object {
        private const val TAG = "ConversationService"
    }

    data class ConversationDTO(
        @SerializedName("conversationId") val conversationId: String? = null,
        @SerializedName("parentId") val parentId: Int? = null,
        @SerializedName("textContent") val content: String,
        @SerializedName("reasoningEnabled") val reasoningEnabled: Boolean? = true,
        @SerializedName("searchEnabled") val searchEnabled: Boolean? = true
    )

    private val localResponse = """
    id:mock-1
    event:newConversation
    data:{"type":"newConversation","conversationId":"mock-1","messageId":1,"parentId":0}
    
    id:mock-1
    event:message
    data:{"type":"message","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"这是本地模拟 SSE 消息。"}
    """

    fun postConversation(
        dto: ConversationDTO,
        callback: (String?, Throwable?) -> Unit
    ) {
        val params = mapOf(
            "conversationId" to (dto.conversationId ?: ""),
            "parentId" to (dto.parentId?.toString() ?: "0"),
            "textContent" to dto.content,
            "reasoningEnabled" to dto.reasoningEnabled.toString(),
            "searchEnabled" to dto.searchEnabled.toString()
        )
        Log.d(
            TAG,
            "准备发送 SSE 请求，endpoint=${ApiServiceS.BASE_URL_AI}user/v1/conversation, params=$params"
        )

        ApiServiceS.ssePost(
            baseUrl = ApiServiceS.BASE_URL_AI,
            endpoint = "user/v1/conversation",
            params = params,
            headers = mapOf(
                "Accept" to "text/event-stream",
                "Authorization" to "Bearer VlXIBbSMvXLF63lC82FvUPj8B6JHJaoOGEVphfq0NfYRvt1LR7mGntOBZPuqyHKY3rDq5zXtvEPnK5bTCX1BYDYHomsOVBqyUDqfUwjjSjK4zA1achMCPr9aUmgosj7wEMbDHQHx8is7B5WwmHEJ9PRRo23Lf5gethJw7enY5DJOr7KRHf7qo8gu0JJXN2g7vG56SqdIW7rRU3QXENIORkf4CP9Ma8nSaf9srXfqjunBoorxmbbRyK7bVhz7qFPo"
            ),
            listener = object : EventSourceListener() {
                override fun onOpen(eventSource: EventSource, response: Response) {
                    Log.d(TAG, "SSE 连接已打开: ${response.request.url}")
                }

                override fun onEvent(
                    eventSource: EventSource,
                    id: String?,
                    type: String?,
                    data: String
                ) {
                    Log.d(TAG, "SSE 收到消息 (id=$id, type=$type): $data")
                    handleResponse(data, null, callback)
                }

                override fun onFailure(
                    eventSource: EventSource,
                    t: Throwable?,
                    response: Response?
                ) {
                    Log.e(TAG, "SSE 连接出错", t)
                    response?.let {
                        Log.e(TAG, "SSE 错误响应 code=${it.code}, message=${it.message}")
                    }
                    handleResponse(null, t ?: Exception("未知 SSE 错误"), callback)
                }

                override fun onClosed(eventSource: EventSource) {
                    Log.d(TAG, "SSE 连接已关闭")
                }
            }
        )
    }

    private fun handleResponse(
        response: String?,
        error: Throwable?,
        callback: (String?, Throwable?) -> Unit
    ) {
        if (error != null || response == null) {
            Log.w(TAG, "使用本地模拟响应")
            callback(localResponse, null)
        } else {
            callback(response, null)
        }
    }
}
