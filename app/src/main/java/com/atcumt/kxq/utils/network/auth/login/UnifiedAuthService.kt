package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject
import java.io.IOException

// 统一身份登录请求服务
class UnifiedAuthService {

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
        val userId: String?       // 用户ID
    )

    // 本地数据
    private val localResponse = """
        {
            "code": 200,
            "msg": "成功",
            "data": {
                "type": "unified_auth",
                "token": "723e5af3de084a86a1fdf8ed771e79a5",
                "expiresIn": 900
            }
        }
    """

    // 统一身份登录方法
    fun loginWithUnifiedAuth(
        cookie: String,
        deviceType: String,
        callback: (UnifiedAuthResponse?, Throwable?) -> Unit
    ) {
        // 设置请求头
        val headers = mapOf(
            "Cookie" to cookie,
            "Device-Type" to deviceType,
            "Accept" to "*/*"
        )

        // 调用 ApiServiceS 的 POST 方法
        ApiServiceS.post(
            BASE_URL_AUTH,
            "login/unifiedAuth",
            mapOf(), // 无请求体
            headers
        ) { response, error ->
            if (error != null) {
                // 网络请求失败时，返回本地数据
                val localParsedResponse = parseUnifiedAuthResponse(localResponse)
                callback(localParsedResponse, null)
            } else {
                // 网络请求成功时，解析响应
                val parsedResponse = response?.let { parseUnifiedAuthResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun loginWithUnifiedAuthBlocking(
        cookie: String,
        deviceType: String
    ): UnifiedAuthResponse {
        return suspendCancellableCoroutine { cont ->
            loginWithUnifiedAuth(cookie, deviceType) { response, error ->
                if (error != null) {
                    // 网络请求失败时，返回本地数据
                    val localParsedResponse = parseUnifiedAuthResponse(localResponse)
                    cont.resume(localParsedResponse!!, null)
                } else {
                    // 网络请求成功时，返回响应
                    cont.resume(response!!, null)
                }
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