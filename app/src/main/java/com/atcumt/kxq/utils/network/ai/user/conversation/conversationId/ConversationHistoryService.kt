package com.atcumt.kxq.utils.network.ai.user.conversation.conversationId

import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ConversationHistoryService {
    data class MessageVO(
        @SerializedName("messageId") val messageId: Int,
        @SerializedName("parentId") val parentId: Int,
        @SerializedName("model") val model: String,
        @SerializedName("role") val role: String,
        @SerializedName("content") val content: String,
        @SerializedName("thinkingEnabled") val thinkingEnabled: Boolean,
        @SerializedName("thinkingTime") val thinkingTime: Int,
        @SerializedName("thinkingStatus") val thinkingStatus: String,
        @SerializedName("searchEnabled") val searchEnabled: Boolean,
        @SerializedName("searchResults") val searchResults: List<Any>?,
        @SerializedName("files") val files: List<Any>?,
        @SerializedName("status") val status: String,
        @SerializedName("createTime") val createTime: String
    )

    data class ConversationVO(
        @SerializedName("conversationId") val conversationId: String,
        @SerializedName("userId") val userId: String,
        @SerializedName("title") val title: String,
        @SerializedName("currentMessageId") val currentMessageId: Int,
        @SerializedName("messages") val messages: List<MessageVO>,
        @SerializedName("createTime") val createTime: String,
        @SerializedName("updateTime") val updateTime: String
    )

    data class ConversationHistoryResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: ConversationVO?
    )

    private val localResponse = """
    {
      "code": 200,
      "msg": "本地历史数据",
      "data": {
        "conversationId": "mock-id",
        "userId": "user123",
        "title": "本地对话",
        "currentMessageId": 2,
        "messages": [
          {
            "messageId": -3,
            "parentId": 0,
            "model": "gpt",
            "role": "user",
            "content": "你好",
            "thinkingEnabled": false,
            "thinkingTime": 0,
            "thinkingStatus": "",
            "searchEnabled": false,
            "searchResults": [],
            "files": [],
            "status": "",
            "createTime": "2025-04-30T16:00:00Z"
          },
          {
            "messageId": -2,
            "parentId": 1,
            "model": "gpt",
            "role": "assistant",
            "content": "你好！这是本地 Mock 回复。",
            "thinkingEnabled": true,
            "thinkingTime": 100,
            "thinkingStatus": "done",
            "searchEnabled": false,
            "searchResults": [],
            "files": [],
            "status": "success",
            "createTime": "2025-04-30T16:01:00Z"
          }
        ],
        "createTime": "2025-04-30T16:00:00Z",
        "updateTime": "2025-04-30T16:01:00Z"
      }
    }
    """

    fun getHistory(
        conversationId: String,
        callback: (ConversationHistoryResponse?, Throwable?) -> Unit
    ) {
        ApiServiceS.get(
            baseUrl = ApiServiceS.BASE_URL_AI,
            endpoint = "user/v1/conversation/$conversationId",
            headers = mapOf(
                "Accept" to "application/json"
            )
        ) { resp, err ->
            handleResponse(resp, err, callback)
        }
    }

    private fun handleResponse(
        response: String?,
        error: Throwable?,
        callback: (ConversationHistoryResponse?, Throwable?) -> Unit
    ) {
        if (error != null || response == null) {
            callback(parseLocalData(), null)
        } else {
            try {
                val result = Gson().fromJson(response, ConversationHistoryResponse::class.java)
                callback(result, null)
            } catch (e: Exception) {
                callback(parseLocalData(), null)
            }
        }
    }

    private fun parseLocalData(): ConversationHistoryResponse {
        return Gson().fromJson(localResponse, ConversationHistoryResponse::class.java)
    }
}
