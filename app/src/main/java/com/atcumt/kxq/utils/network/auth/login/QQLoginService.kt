package com.atcumt.kxq.utils.network.auth.login

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// QQ 登录请求服务，继承 ApiService
class QQLoginService : ApiService() {

    // 定义登录响应的数据类
    data class QQLoginResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: QQLoginData?     // 登录数据
    )

    data class QQLoginData(
        val accessToken: String?,  // 访问令牌
        val expiresIn: Long?,      // 过期时间
        val refreshToken: String?, // 刷新令牌
        val userId: String?        // 用户ID
    )

    // QQ 登录方法
    fun loginWithQQ(
        deviceType: String,
        qqAuthorizationCode: String,
        callback: (QQLoginResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Device-Type", deviceType)
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "login/qq")

        // 构建请求体
        val formbody = FormBody.Builder()
            .add("qqAuthorizationCode", qqAuthorizationCode)
            .build()

        // 调用父类的 POST 方法
        post(url, headers, formbody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseQQLoginResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析 QQ 登录响应
    private fun parseQQLoginResponse(jsonString: String): QQLoginResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", "")
            val dataJson = jsonObject.optJSONObject("data")
            val data = dataJson?.let {
                QQLoginData(
                    accessToken = it.optString("accessToken"),
                    expiresIn = it.optLong("expiresIn"),
                    refreshToken = it.optString("refreshToken"),
                    userId = it.optString("userId")
                )
            }

            QQLoginResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
