package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// 使用电子邮件登录请求服务，继承 ApiService
class EmailLoginService : ApiService() {

    data class EmailLoginRequest(
        val email: String,       // 电子邮件地址
        val verificationCode: String  // 验证码
    )

    // 定义登录响应的数据类
    data class EmailLoginResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: EmailLoginData?  // 登录数据
    )

    data class EmailLoginData(
        val accessToken: String?,  // 访问令牌
        val expiresIn: Long?,      // 过期时间
        val refreshToken: String?, // 刷新令牌
        val userId: String?        // 用户ID
    )

    // 使用电子邮件登录方法
    fun loginWithEmail(
        deviceType: String,
        emailLoginRequest: EmailLoginRequest,
        callback: (EmailLoginResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Device-Type", deviceType)
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "login/email")

        // 构建请求体
        val formbody = FormBody.Builder()
            .add("email", emailLoginRequest.email)
            .add("verificationCode", emailLoginRequest.verificationCode)
            .build()

        // 调用父类的 POST 方法
        post(url, headers, formbody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseEmailLoginResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析电子邮件登录响应
    private fun parseEmailLoginResponse(jsonString: String): EmailLoginResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", "")
            val dataJson = jsonObject.optJSONObject("data")
            val data = dataJson?.let {
                EmailLoginData(
                    accessToken = it.optString("accessToken"),
                    expiresIn = it.optLong("expiresIn"),
                    refreshToken = it.optString("refreshToken"),
                    userId = it.optString("userId")
                )
            }

            EmailLoginResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
