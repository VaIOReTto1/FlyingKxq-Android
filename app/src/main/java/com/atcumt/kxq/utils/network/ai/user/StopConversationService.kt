package com.atcumt.kxq.utils.network.ai.user

import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

class StopConversationService @Inject constructor() {
    data class StopConversationDTO(
        @SerializedName("conversationId") val conversationId: String? = null
    )
    data class ResultObject(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: Any?
    )

    private val localResponse = """
    {
      "code": 200,
      "msg": "本地停止会话成功",
      "data": null
    }
    """

    fun stopConversation(
        dto: StopConversationDTO,
        callback: (ResultObject?, Throwable?) -> Unit
    ) {
        val params = mapOf("conversationId" to (dto.conversationId ?: ""))
        ApiServiceS.post(
            baseUrl  = ApiServiceS.BASE_URL_AI,
            endpoint = "user/v1/stopConversation",
            params   = params,
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
        callback: (ResultObject?, Throwable?) -> Unit
    ) {
        if (error != null || response == null) {
            callback(parseLocalData(), null)
        } else {
            try {
                val result = Gson().fromJson(response, ResultObject::class.java)
                callback(result, null)
            } catch (e: Exception) {
                callback(parseLocalData(), null)
            }
        }
    }

    private fun parseLocalData(): ResultObject {
        return Gson().fromJson(localResponse, ResultObject::class.java)
    }
}
