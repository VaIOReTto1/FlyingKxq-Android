package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

class UnifiedAuthService {
    // region 数据结构
    data class UnifiedAuthResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: UnifiedAuthData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class UnifiedAuthData(
        @SerializedName("type") val type: String?,        // 认证类型
        @SerializedName("token") val token: String?,      // 令牌
        @SerializedName("expiresIn") val expiresIn: Long?,// 有效期
        @SerializedName("accessToken") val accessToken: String?,   // 访问令牌 (兼容字段)
        @SerializedName("refreshToken") val refreshToken: String?, // 刷新令牌 (兼容字段)
        @SerializedName("userId") val userId: String?     // 用户ID (兼容字段)
    )
    // endregion

    // region 本地数据
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
    // endregion

    // region 网络请求
    fun loginWithUnifiedAuth(
        cookie: String,
        deviceType: String,
        callback: (UnifiedAuthResponse?, Throwable?) -> Unit
    ) {
        ApiServiceS.post(
            baseUrl = BASE_URL_AUTH,
            endpoint = "login/unifiedAuth",
            params = mapOf(), // 无请求体参数
            headers = mapOf(
                "Cookie" to cookie,
                "Device-Type" to deviceType,
                "Accept" to "application/json"
            )
        ) { response, error ->
            handleResponse(response, error, callback)
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
                    cont.resumeWith(Result.success(parseLocalData()))
                } else {
                    response?.let { cont.resumeWith(Result.success(it)) }
                        ?: cont.resumeWith(Result.success(parseLocalData()))
                }
            }
        }
    }
    // endregion

    // region 响应处理
    private fun handleResponse(
        response: String?,
        error: Throwable?,
        callback: (UnifiedAuthResponse?, Throwable?) -> Unit
    ) {
        when {
            error != null -> callback(parseLocalData(), null)
            response != null -> {
                try {
                    callback(Gson().fromJson(response, UnifiedAuthResponse::class.java), null)
                } catch (e: Exception) {
                    callback(parseLocalData(), null)
                }
            }
            else -> callback(parseLocalData(), null)
        }
    }

    private fun parseLocalData(): UnifiedAuthResponse {
        return Gson().fromJson(localResponse, UnifiedAuthResponse::class.java)
    }
}