package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// 统一身份登录请求服务，继承 ApiService
class UnifiedAuthService : ApiService() {

    // 定义登录响应的数据类
    data class UnifiedAuthResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: UnifiedAuthData? // 登录数据
    )

    data class UnifiedAuthData(
        val accessToken: String?,  // 访问令牌
        val expiresIn: Long?,      // 过期时间
        val refreshToken: String?, // 刷新令牌
        val userId: String?        // 用户ID
    )

    // 统一身份登录方法
    fun loginWithUnifiedAuth(
        cookie: String,
        deviceType: String,
        callback: (UnifiedAuthResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Cookie", cookie)
            .add("Device-Type", deviceType)
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "login/unifiedAuth")

        // 调用父类的 POST 方法
        post(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseUnifiedAuthResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析统一身份登录响应
    private fun parseUnifiedAuthResponse(jsonString: String): UnifiedAuthResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", "")
            val dataJson = jsonObject.optJSONObject("data")
            val data = dataJson?.let {
                UnifiedAuthData(
                    accessToken = it.optString("accessToken"),
                    expiresIn = it.optLong("expiresIn"),
                    refreshToken = it.optString("refreshToken"),
                    userId = it.optString("userId")
                )
            }

            UnifiedAuthResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
