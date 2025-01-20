package com.atcumt.kxq.utils.network.auth.me.email

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义修改邮件请求服务，继承 ApiService
class UpdateEmailService : ApiService() {

    // 定义修改邮件请求的数据类
    data class UpdateEmailRequest(
        val verificationCode: String,  // 验证码
        val email: String               // 新邮箱地址
    )

    // 定义修改邮件响应的数据类
    data class UpdateEmailResponse(
        val code: Int?,                // 响应码
        val msg: String?,              // 消息
    )

    // 修改邮箱方法
    fun updateEmail(
        updateEmailRequest: UpdateEmailRequest,
        callback: (UpdateEmailResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL，添加查询参数
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/email", mapOf(
            "verificationCode" to updateEmailRequest.verificationCode,
            "email" to updateEmailRequest.email
        ))

        // 调用父类的 PATCH 方法
        patch(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseUpdateEmailResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析修改邮箱响应
    private fun parseUpdateEmailResponse(jsonString: String): UpdateEmailResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            UpdateEmailResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
