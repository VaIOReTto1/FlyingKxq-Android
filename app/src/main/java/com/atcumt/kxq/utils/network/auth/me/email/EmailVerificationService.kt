package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义发送验证码请求服务，继承 ApiService
class EmailVerificationService : ApiService() {

    // 定义发送验证码请求的数据类
    data class EmailVerificationRequest(
        val email: String,           // 电子邮件
        val verificationCode: String,      // 验证码
    )

    // 定义发送验证码响应的数据类
    data class EmailVerificationResponse(
        val code: Int?,             // 响应码
        val msg: String?,           // 消息
    )


    // 发送验证码方法
    fun sendVerificationCode(
        emailVerificationRequest: EmailVerificationRequest,
        callback: (EmailVerificationResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("email", emailVerificationRequest.email)
            .add("verificationCode", emailVerificationRequest.verificationCode)
            .build()

        // 构建 URL，添加查询参数
        val url = buildUrlWithParams(BASE_URL_AUTH, "verification-code/captcha")

        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseEmailVerificationResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析发送验证码响应
    private fun parseEmailVerificationResponse(jsonString: String): EmailVerificationResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            EmailVerificationResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
