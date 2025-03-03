package com.atcumt.kxq.utils.network.auth.authentication

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException

class UnifiedAuthService {
    // region 数据结构
    data class UnifiedAuthRequest(
        @SerializedName("Cookie") val cookie: String
    )

    data class UnifiedAuthResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: UnifiedAuthData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class UnifiedAuthData(
        @SerializedName("type") val type: String?,
        @SerializedName("token") val token: String?,
        @SerializedName("expiresIn") val expiresIn: Long?
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
    fun unifiedAuth(
        request: UnifiedAuthRequest,
        callback: (UnifiedAuthResponse?, Throwable?) -> Unit
    ) {
        ApiServiceS.post(
            baseUrl = BASE_URL_AUTH,
            endpoint = "v1/authentication/unifiedAuth",
            params = mapOf(), // 无请求体
            headers = mapOf(
                "Cookie" to request.cookie,
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, callback)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun unifiedAuthBlocking(request: UnifiedAuthRequest): UnifiedAuthResponse {
        return suspendCancellableCoroutine { cont ->
            unifiedAuth(request) { response, error ->
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
    // endregion
}