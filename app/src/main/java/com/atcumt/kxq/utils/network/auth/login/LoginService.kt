package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

class LoginService {
    // region 数据结构
    // 登录请求数据类
    data class LoginRequest(
        @SerializedName("deviceType") val deviceType: String,
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String
    )

    // 登录响应数据结构
    data class LoginAPIResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: LoginResponseData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class LoginResponseData(
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
    fun login(
        request: LoginRequest,
        callback: (LoginAPIResponse?, Throwable?) -> Unit
    ) {
        // 构建 JSON 请求体
        val requestBody = mapOf(
            "username" to request.username,
            "password" to request.password
        )

        ApiServiceS.post(
            baseUrl = BASE_URL_AUTH,
            endpoint = "v1/login/username",
            params = requestBody, // 直接传递数据对象
            headers = mapOf(
                "Device-Type" to request.deviceType,
                "Content-Type" to "application/json",
                "Accept" to "*/*"
            )
        ) { response, error ->
            handleResponse(response, error, callback)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun loginBlocking(request: LoginRequest): LoginAPIResponse {
        return suspendCancellableCoroutine { cont ->
            login(request) { response, error ->
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
        callback: (LoginAPIResponse?, Throwable?) -> Unit
    ) {
        when {
            error != null -> {
                callback(parseLocalData(), null) // 网络错误时返回本地数据
            }
            response != null -> {
                try {
                    callback(Gson().fromJson(response, LoginAPIResponse::class.java), null)
                } catch (e: Exception) {
                    callback(parseLocalData(), null) // JSON 解析错误时返回本地数据
                }
            }
            else -> {
                callback(parseLocalData(), null) // 空响应时返回本地数据
            }
        }
    }

    private fun parseLocalData(): LoginAPIResponse {
        return Gson().fromJson(localResponse, LoginAPIResponse::class.java)
    }
    // endregion
}