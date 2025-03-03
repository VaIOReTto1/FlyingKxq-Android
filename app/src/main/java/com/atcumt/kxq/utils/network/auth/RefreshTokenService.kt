package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.io.IOException

// 定义刷新 Token 请求服务
class RefreshTokenService {

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

    // 本地数据
    private val localResponse = """
        {
            "code": 200,
            "msg": "成功",
            "data": {
                "accessToken": "IszKGVnxUpqvQiovNWt2llk1FzQmeJuSueaquzmP9axyUMMQkFZetTRfvpauJJ5r",
                "expiresIn": 2592000,
                "refreshToken": "paNGHEdxo3BZqR6V3is9r82PyOhFqtjQLWKNrkTqFA2JISSe3KDqU9Ac44qI7NJfoyBgoWPP5r2JCBY6uv5HVKzq3XLKsGpoUNxYPJQcOlFaDT9gR8b6mG5RlUZBHlHr",
                "userId": "5a50eae4a24c4ebfbdf16b7c537b81aa"
            }
        }
    """

    // 刷新 Token 方法
    fun refreshToken(
        refreshTokenRequest: RefreshTokenRequest,
        callback: (RefreshTokenResponse?, Throwable?) -> Unit
    ) {
        // 设置请求头
        val headers = mapOf(
            "Content-Type" to "application/json",
            "Accept" to "*/*"
        )

        // 构建请求体
        val requestBody = mapOf(
            "refreshToken" to refreshTokenRequest.refreshToken
        )

        // 调用 ApiServiceS 的 POST 方法
        ApiServiceS.post(
            BASE_URL_AUTH,
            "v1/refresh_token",
            requestBody,
            headers
        ) { response, error ->
            if (error != null) {
                // 网络请求失败时，返回本地数据
                val localParsedResponse = parseRefreshTokenResponse(localResponse)
                callback(localParsedResponse, null)
            } else {
                // 网络请求成功时，解析响应
                val parsedResponse = response?.let { parseRefreshTokenResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun refreshTokenBlocking(request: RefreshTokenRequest): RefreshTokenResponse {
        return suspendCancellableCoroutine { cont ->
            refreshToken(request) { response, error ->
                if (error != null) {
                    // 网络请求失败时，返回本地数据
                    val localParsedResponse = parseRefreshTokenResponse(localResponse)
                    cont.resume(localParsedResponse!!, null)
                } else {
                    // 网络请求成功时，返回响应
                    cont.resume(response!!, null)
                }
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