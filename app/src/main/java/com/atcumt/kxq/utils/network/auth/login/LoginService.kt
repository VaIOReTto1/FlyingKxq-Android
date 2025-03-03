package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject

// 定义登录请求服务，继承 ApiService
class LoginService {

    // 定义登录请求的数据类
    data class LoginRequest(
        val deviceType: String,  // 设备类型
        val username: String,    // 用户名
        val password: String     // 密码
    )

    // 定义登录响应的数据类
    data class LoginAPIResponse(
        val code: Int?,         // 响应码
        val msg: String?,       // 消息
        val data: LoginResponseData? // 数据部分
    )

    data class LoginResponseData(
        val accessToken: String?,    // 访问令牌
        val expiresIn: Long?,        // 有效期
        val refreshToken: String?,   // 刷新令牌
        val userId: String?          // 用户ID
    )

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

    // 登录方法
    fun login(
        loginRequest: LoginRequest,
        callback: (LoginAPIResponse?, Throwable?) -> Unit
    ) {
        // 设置请求头
        val headers = mapOf(
            "Device-Type" to loginRequest.deviceType,
            "Content-Type" to "application/json",
            "Accept" to "*/*"
        )

        // 构建 JSON 请求体
        val requestBody = mapOf(
            "username" to loginRequest.username,
            "password" to loginRequest.password
        )

        // 调用父类的 POST 方法
        ApiServiceS.post(
            BASE_URL_AUTH,
            "v1/login/username",
            requestBody,
            headers
        ) { response, error ->
            if (error != null) {
                val localParsedResponse = parseLoginResponse(localResponse)
                callback(localParsedResponse, null)
            } else {
                val parsedResponse = response?.let { parseLoginResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun loginBlocking(request: LoginRequest): LoginAPIResponse {
        return suspendCancellableCoroutine { cont ->
            login(request) { response, error ->
                if (error != null) {
                    // 网络请求失败时，返回本地数据
                    val localParsedResponse = parseLoginResponse(localResponse)
                    cont.resume(localParsedResponse!!, null)
                } else {
                    cont.resume(response!!, null)
                }
            }
        }
    }

    // 解析登录响应
    private fun parseLoginResponse(jsonString: String): LoginAPIResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                LoginResponseData(
                    accessToken = it.optString("accessToken", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1),
                    refreshToken = it.optString("refreshToken", null.toString()),
                    userId = it.optString("userId", null.toString())
                )
            }
            LoginAPIResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
