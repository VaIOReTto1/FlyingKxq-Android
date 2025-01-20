package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义登录请求服务，继承 ApiService
class LoginService : ApiService() {

    // 定义登录请求的数据类
    data class LoginRequest(
        val deviceType: String,  // 设备类型
        val username: String,    // 用户名
        val password: String     // 密码
    )

    // 定义登录响应的数据类
    data class LoginAPIResponse(
        val code: Int?,         // 响应码
        val msg: String?,       // 消息
        val data: LoginResponseData? // 数据部分
    )

    data class LoginResponseData(
        val accessToken: String?,    // 访问令牌
        val expiresIn: Long?,        // 有效期
        val refreshToken: String?,   // 刷新令牌
        val userId: String?          // 用户ID
    )

    // 登录方法
    fun login(
        loginRequest: LoginRequest,
        callback: (LoginAPIResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Device-Type", loginRequest.deviceType)
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("username", loginRequest.username)
            .add("password", loginRequest.password)
            .build()

        val url = buildUrlWithParams(BASE_URL_AUTH, "login/username")
        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseLoginResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析登录响应
    private fun parseLoginResponse(jsonString: String): LoginAPIResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                LoginResponseData(
                    accessToken = it.optString("accessToken", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1),
                    refreshToken = it.optString("refreshToken", null.toString()),
                    userId = it.optString("userId", null.toString())
                )
            }
            LoginAPIResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
