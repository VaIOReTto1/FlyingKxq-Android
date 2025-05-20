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
        
        id:mock-2
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"**示例文章标题**"}
        
        id:mock-3
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"\n*这是一段示例正文"}
        
        id:mock-4
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"，用于展示斜体效果。"}
        
        id:mock-5
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"*\n```cpp\ni"}
        
        id:mock-6
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"nt add(int"}
        
        id:mock-7
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":" a, int b)"}
        
        id:mock-8
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":" {\n    ret"}
        
        id:mock-9
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"urn a + b;"}
        
        id:mock-10
        event:message
        data:{"type":"message","conversationId":"mock-1","messageId":2,"parentId":1,"model":"gpt","role":"assistant","content":"\n}\n```"}
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
                "Accept" to "text/event-stream"
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
                    callback("{\"type\":\"COMPLETE\"}", null)
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
