package com.atcumt.kxq.utils.network.auth.me.password

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义重置密码请求服务，继承 ApiService
class ResetPasswordService : ApiService() {

    // 定义重置密码请求的数据类
    data class ResetPasswordRequest(
        val password: String  // 新密码
    )

    // 定义重置密码响应的数据类
    data class ResetPasswordResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 重置密码方法
    fun resetPassword(
        resetPasswordRequest: ResetPasswordRequest,
        callback: (ResetPasswordResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("password", resetPasswordRequest.password)
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/password/reset")

        // 调用父类的 PATCH 方法
        patch(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseResetPasswordResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析重置密码响应
    private fun parseResetPasswordResponse(jsonString: String): ResetPasswordResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            ResetPasswordResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
