package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义注册请求服务，继承 ApiService
class RegisterService : ApiService() {

    // 定义注册请求的数据类
    data class RegisterRequest(
        val deviceType: String,               // 设备类型
        val unifiedAuthToken: String,         // 统一认证令牌
        val username: String,                 // 用户名
        val password: String,                 // 密码
        val qqAuthorizationCode: String?,     // QQ 授权码，可选
        val appleAuthorizationCode: String?   // Apple 授权码，可选
    )

    // 定义注册响应的数据类
    data class RegisterResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: RegisterResponseData? // 数据部分
    )

    data class RegisterResponseData(
        val accessToken: String?,  // 访问令牌
        val expiresIn: Long?,      // 有效时间
        val refreshToken: String?, // 刷新令牌
        val userId: String?        // 用户 ID
    )

    // 注册方法
    fun register(
        registerRequest: RegisterRequest,
        callback: (RegisterResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Device-Type", registerRequest.deviceType)
            .add("Content-Type", "application/x-www-form-urlencoded")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("unifiedAuthToken", registerRequest.unifiedAuthToken)
            .add("username", registerRequest.username)
            .add("password", registerRequest.password)
            .apply {
                registerRequest.qqAuthorizationCode?.let { add("qqAuthorizationCode", it) }
                registerRequest.appleAuthorizationCode?.let { add("appleAuthorizationCode", it) }
            }
            .build()

        val url = buildUrlWithParams(BASE_URL_AUTH, "register")

        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseRegisterResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析注册响应
    private fun parseRegisterResponse(jsonString: String): RegisterResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                RegisterResponseData(
                    accessToken = it.optString("accessToken", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1),
                    refreshToken = it.optString("refreshToken", null.toString()),
                    userId = it.optString("userId", null.toString())
                )
            }
            RegisterResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
