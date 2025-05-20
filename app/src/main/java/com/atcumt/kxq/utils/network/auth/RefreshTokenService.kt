package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenService @Inject constructor() {
    // region 数据结构
    data class RefreshTokenRequest(
        @SerializedName("refreshToken") val refreshToken: String
    )

    data class RefreshTokenResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: RefreshTokenData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class RefreshTokenData(
        @SerializedName("accessToken") val accessToken: String?,
        @SerializedName("expiresIn") val expiresIn: Long?,
        @SerializedName("refreshToken") val refreshToken: String?,
        @SerializedName("userId") val userId: String?
    )
    // endregion

    // region 本地数据
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
    // endregion

    // region 网络请求
    fun refreshToken(
        request: RefreshTokenRequest,
        callback: (RefreshTokenResponse?, Throwable?) -> Unit
    ) {
        ApiServiceS.post(
            baseUrl = BASE_URL_AUTH,
            endpoint = "v1/refresh_token",
            params = mapOf(
                "refreshToken" to request.refreshToken
            ), // 自动序列化对象
            headers = mapOf(
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, callback)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun refreshTokenBlocking(request: RefreshTokenRequest): RefreshTokenResponse {
        return suspendCancellableCoroutine { cont ->
            refreshToken(request) { response, error ->
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
        callback: (RefreshTokenResponse?, Throwable?) -> Unit
    ) {
        when {
            error != null -> callback(parseLocalData(), null)
            response != null -> {
                try {
                    callback(Gson().fromJson(response, RefreshTokenResponse::class.java), null)
                } catch (e: Exception) {
                    callback(parseLocalData(), null)
                }
            }

            else -> callback(parseLocalData(), null)
        }
    }

    private fun parseLocalData(): RefreshTokenResponse {
        return Gson().fromJson(localResponse, RefreshTokenResponse::class.java)
    }
    // endregion
}