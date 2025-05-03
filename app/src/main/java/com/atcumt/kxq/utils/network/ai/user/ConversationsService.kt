package com.atcumt.kxq.utils.network.ai.user

import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class ConversationsService {
    data class ConversationPageVO(
        @SerializedName("conversationId") val conversationId: String,
        @SerializedName("title") val title: String,
        @SerializedName("createTime") val createTime: String,
        @SerializedName("updateTime") val updateTime: String
    )
    data class SimplePageQueryVO<T>(
        @SerializedName("page") val page: Int,
        @SerializedName("size") val size: Int,
        @SerializedName("data") val data: List<T>?
    )
    data class ConversationsResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: SimplePageQueryVO<ConversationPageVO>?
    )

    private val localResponse = """
    {
      "code": 200,
      "msg": "本地会话列表",
      "data": {
        "page": 1,
        "size": 1,
        "data": [
          {
            "conversationId": "mock-id",
            "title": "本地对话",
            "createTime": "2025-04-30T16:00:00Z",
            "updateTime": "2025-04-30T16:05:00Z"
          }
        ]
      }
    }
    """

    fun getConversations(
        page: Int,
        size: Int,
        callback: (ConversationsResponse?, Throwable?) -> Unit
    ) {
        val params = mapOf("page" to page.toString(), "size" to size.toString())
        ApiServiceS.get(
            baseUrl  = ApiServiceS.BASE_URL_AI,
            endpoint = "user/v1/conversations",
            params   = params,
            headers  = mapOf("Accept" to "application/json")
        ) { resp, err ->
            handleResponse(resp, err, callback)
        }
    }

    private fun handleResponse(
        response: String?,
        error: Throwable?,
        callback: (ConversationsResponse?, Throwable?) -> Unit
    ) {
        if (error != null || response == null) {
            callback(parseLocalData(), null)
        } else {
            try {
                val result = Gson().fromJson(response, ConversationsResponse::class.java)
                callback(result, null)
            } catch (e: Exception) {
                callback(parseLocalData(), null)
            }
        }
    }

    private fun parseLocalData(): ConversationsResponse {
        return Gson().fromJson(localResponse, ConversationsResponse::class.java)
    }
}
