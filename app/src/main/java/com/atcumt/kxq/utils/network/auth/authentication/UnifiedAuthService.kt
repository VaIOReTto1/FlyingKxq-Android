package com.atcumt.kxq.utils.network.auth.authentication

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// 定义统一身份认证请求服务，继承 ApiService
class UnifiedAuthService : ApiService() {

    // 定义统一身份认证请求的数据类
    data class UnifiedAuthRequest(
        val cookie: String  // Cookie
    )

    // 定义统一身份认证响应的数据类
    data class UnifiedAuthResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: UnifiedAuthResponseData? // 数据部分
    )

    data class UnifiedAuthResponseData(
        val type: String?,         // 类型
        val token: String?,        // 令牌
        val expiresIn: Long?       // 有效期
    )

    // 统一身份认证方法
    fun unifiedAuth(
        unifiedAuthRequest: UnifiedAuthRequest,
        callback: (UnifiedAuthResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Cookie", unifiedAuthRequest.cookie)  // 添加 Cookie 到请求头
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "authentication/unifiedAuth")

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

    // 解析统一身份认证响应
    private fun parseUnifiedAuthResponse(jsonString: String): UnifiedAuthResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                UnifiedAuthResponseData(
                    type = it.optString("type", null.toString()),
                    token = it.optString("token", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1)
                )
            }
            UnifiedAuthResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
