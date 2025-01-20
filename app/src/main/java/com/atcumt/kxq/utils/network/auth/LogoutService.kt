package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// 定义注销请求服务，继承 ApiService
class LogoutService : ApiService() {

    // 定义注销请求的数据类
    data class LogoutRequest(
        val device: String  // 设备信息
    )

    // 定义注销响应的数据类
    data class LogoutResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 注销方法
    fun logout(
        logoutRequest: LogoutRequest,
        callback: (LogoutResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL，带上设备参数
        val url =
            buildUrlWithParams(BASE_URL_AUTH, "logout", mapOf("device" to logoutRequest.device))

        // 调用父类的 POST 方法
        post(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseLogoutResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析注销响应
    private fun parseLogoutResponse(jsonString: String): LogoutResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            LogoutResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
