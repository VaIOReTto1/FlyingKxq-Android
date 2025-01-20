package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义刷新 Token 请求服务，继承 ApiService
class RefreshTokenService : ApiService() {

    // 定义刷新 Token 请求的数据类
    data class RefreshTokenRequest(
        val refreshToken: String  // 刷新令牌
    )

    // 定义刷新 Token 响应的数据类
    data class RefreshTokenResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: RefreshTokenResponseData? // 数据部分
    )

    data class RefreshTokenResponseData(
        val accessToken: String?,  // 新的访问令牌
        val expiresIn: Long?,      // 新的有效期
        val refreshToken: String?, // 新的刷新令牌
        val userId: String?        // 用户 ID
    )

    // 刷新 Token 方法
    fun refreshToken(
        refreshTokenRequest: RefreshTokenRequest,
        callback: (RefreshTokenResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("refreshToken", refreshTokenRequest.refreshToken)
            .build()

        val url = buildUrlWithParams(BASE_URL_AUTH, "refresh_token")
        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseRefreshTokenResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析刷新 Token 响应
    private fun parseRefreshTokenResponse(jsonString: String): RefreshTokenResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                RefreshTokenResponseData(
                    accessToken = it.optString("accessToken", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1),
                    refreshToken = it.optString("refreshToken", null.toString()),
                    userId = it.optString("userId", null.toString())
                )
            }
            RefreshTokenResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
