package com.atcumt.kxq.utils.network.auth.verificationcode

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义发送验证码请求服务，继承 ApiService
class CaptchaVerificationService : ApiService() {

    // 定义发送验证码请求的数据类
    data class CaptchaVerificationRequest(
        val email: String,         // 用户邮箱
        val captchaId: String,     // 验证码ID
        val captchaCode: String    // 用户输入的验证码
    )

    // 定义发送验证码响应的数据类
    data class CaptchaVerificationResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: CaptchaVerificationResponseData? // 数据部分
    )

    data class CaptchaVerificationResponseData(
        val type: String?,         // 类型
        val token: String?,        // 验证码令牌
        val expiresIn: Long?       // 验证码有效期
    )

    // 发送验证码方法
    fun sendCaptchaVerification(
        captchaVerificationRequest: CaptchaVerificationRequest,
        callback: (CaptchaVerificationResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("email", captchaVerificationRequest.email)
            .add("captchaId", captchaVerificationRequest.captchaId)
            .add("captchaCode", captchaVerificationRequest.captchaCode)
            .build()

        val url = buildUrlWithParams(BASE_URL_AUTH, "verification-code/captcha")

        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseCaptchaVerificationResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析发送验证码响应
    private fun parseCaptchaVerificationResponse(jsonString: String): CaptchaVerificationResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                CaptchaVerificationResponseData(
                    type = it.optString("type", null.toString()),
                    token = it.optString("token", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1)
                )
            }
            CaptchaVerificationResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
