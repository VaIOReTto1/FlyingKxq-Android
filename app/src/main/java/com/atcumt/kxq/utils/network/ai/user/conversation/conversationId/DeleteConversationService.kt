package com.atcumt.kxq.utils.network.ai.user.conversation.conversationId

import com.atcumt.kxq.utils.network.ApiServiceS
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import javax.inject.Inject

class DeleteConversationService @Inject constructor() {
    data class ResultObject(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: Any?
    )

    private val localResponse = """
    {
      "code": 200,
      "msg": "本地删除成功",
      "data": null
    }
    """

    fun deleteConversation(
        conversationId: String,
        callback: (ResultObject?, Throwable?) -> Unit
    ) {
        ApiServiceS.delete(
            baseUrl  = ApiServiceS.BASE_URL_AI,
            endpoint = "user/v1/conversation/$conversationId",
            headers = mapOf(
                "Accept" to "application/json",
                "Authorization" to "Bearer NOmdUNImd5sEmpEzLF1Z3Y6T3rNUH1KHsTA95oHsRRAXYazXvRand2F1RU14QLMzySUu104A8mcp6N1blRMXlhKro92UR2f0RGzQB5QMpcG2NcDFvptt5TU7Pjo7xKUW1TuTquIGwZ9htX9zNRDkDX1GoNPkUrEPCXd1NPxODobIhkgHkJQfFKbpLqRqVkE78RsgmQTc4WN2ZfR2oAN2aoylHzr55busFGYtIAda7NCQFaqtBLlKjygj0zsYoAcZ"
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
