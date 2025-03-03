package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.Exception

class RegisterService {
    // region 数据结构
    data class RegisterRequest(
        @SerializedName("deviceType") val deviceType: String,
        @SerializedName("unifiedAuthToken") val unifiedAuthToken: String,
        @SerializedName("username") val username: String,
        @SerializedName("password") val password: String,
        @SerializedName("qqAuthorizationCode") val qqAuthorizationCode: String?
    )

    data class RegisterResponse(
        @SerializedName("code") val code: Int?,
        @SerializedName("msg") val msg: String?,
        @SerializedName("data") val data: RegisterData?
    ) {
        val isSuccess: Boolean get() = code == 200
    }

    data class RegisterData(
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
                "accessToken": "zwqGZ3oD1oynglqJOxIJOyxljB5h32PQ7bI8rth5cwMEAmhbWPONxP8p75W3zZaA",
                "expiresIn": 2592000,
                "refreshToken": "VCzpo9jNRnXVEhV8gyIko2TAn9qEnGfxAX7yJ8lFsR6gtv1x9oMQEpaQo6rnio5WVuawqPhflMJss0jVhW12OvdPOUEZCrux3gxyCNKn4nBigXUsbCpO38HuDR2o22nr",
                "userId": "5a50eae4a24c4ebfbdf16b7c537b81aa"
            }
        }
    """
    // endregion

    // region 网络请求
    fun register(
        request: RegisterRequest,
        callback: (RegisterResponse?, Throwable?) -> Unit
    ) {
        val requestBody = mutableMapOf<String, String>().apply {
            put("unifiedAuthToken", request.unifiedAuthToken)
            put("username", request.username)
            put("password", request.password)
            // 只在字段不为空时添加
            request.qqAuthorizationCode?.let {
                put("qqAuthorizationCode", it)
            }
        }

        ApiServiceS.post(
            baseUrl = BASE_URL_AUTH,
            endpoint = "v1/register",
            params = requestBody, // 自动序列化对象
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
    suspend fun registerBlocking(request: RegisterRequest): RegisterResponse {
        return suspendCancellableCoroutine { cont ->
            register(request) { response, error ->
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
        callback: (RegisterResponse?, Throwable?) -> Unit
    ) {
        when {
            error != null -> callback(parseLocalData(), null)
            response != null -> {
                try {
                    callback(Gson().fromJson(response, RegisterResponse::class.java), null)
                } catch (e: Exception) {
                    callback(parseLocalData(), null)
                }
            }
            else -> callback(parseLocalData(), null)
        }
    }

    private fun parseLocalData(): RegisterResponse {
        return Gson().fromJson(localResponse, RegisterResponse::class.java)
    }
    // endregion
}