package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiServiceS
import com.atcumt.kxq.utils.network.ApiServiceS.BASE_URL_AUTH
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import org.json.JSONObject

// 定义注册请求服务
class RegisterService {

    // 定义注册请求的数据类
    data class RegisterRequest(
        val deviceType: String,               // 设备类型
        val unifiedAuthToken: String,         // 统一认证令牌
        val username: String,                 // 用户名
        val password: String,                 // 密码
        val qqAuthorizationCode: String?      // QQ 授权码，可选
    )

    // 定义注册响应的数据类
    data class RegisterResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: RegisterResponseData? // 数据部分
    )

    data class RegisterResponseData(
        val accessToken: String?,  // 访问令牌
        val expiresIn: Long?,      // 有效时间
        val refreshToken: String?, // 刷新令牌
        val userId: String?        // 用户 ID
    )

    // 本地数据
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

    // 注册方法
    fun register(
        registerRequest: RegisterRequest,
        callback: (RegisterResponse?, Throwable?) -> Unit
    ) {
        // 设置请求头
        val headers = mapOf(
            "Device-Type" to registerRequest.deviceType,
            "Content-Type" to "application/json",
            "Accept" to "*/*"
        )

        // 构建请求体
//        val requestBody = buildString {
//            append("{")
//            append("\"unifiedAuthToken\": \"${registerRequest.unifiedAuthToken}\",")
//            append("\"username\": \"${registerRequest.username}\",")
//            append("\"password\": \"${registerRequest.password}\"")
//
//            // 只在字段不为空时添加
//            registerRequest.qqAuthorizationCode?.let {
//                append(", \"qqAuthorizationCode\": \"$it\"")
//            }
//
//            append("}")
//        }

        val requestBody = mutableMapOf<String, String>().apply {
            put("unifiedAuthToken", registerRequest.unifiedAuthToken)
            put("username", registerRequest.username)
            put("password", registerRequest.password)
            // 只在字段不为空时添加
            registerRequest.qqAuthorizationCode?.let {
                put("qqAuthorizationCode", it)
            }
        }

        // 调用 ApiServiceS 的 POST 方法
        ApiServiceS.post(
            BASE_URL_AUTH,
            "v1/register",
            requestBody,
            headers
        ) { response, error ->
            if (error != null) {
                // 网络请求失败时，返回本地数据
                val localParsedResponse = parseRegisterResponse(localResponse)
                callback(localParsedResponse, null)
            } else {
                // 网络请求成功时，解析响应
                val parsedResponse = response?.let { parseRegisterResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun registerBlocking(request: RegisterRequest): RegisterResponse {
        return suspendCancellableCoroutine { cont ->
            register(request) { response, error ->
                if (error != null) {
                    // 网络请求失败时，返回本地数据
                    val localParsedResponse = parseRegisterResponse(localResponse)
                    cont.resume(localParsedResponse!!, null)
                } else {
                    // 网络请求成功时，返回响应
                    cont.resume(response!!, null)
                }
            }
        }
    }

    // 解析注册响应
    private fun parseRegisterResponse(jsonString: String): RegisterResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                RegisterResponseData(
                    accessToken = it.optString("accessToken", null.toString()),
                    expiresIn = it.optLong("expiresIn", -1),
                    refreshToken = it.optString("refreshToken", null.toString()),
                    userId = it.optString("userId", null.toString())
                )
            }
            RegisterResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}