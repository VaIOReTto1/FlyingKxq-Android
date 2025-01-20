package com.atcumt.kxq.utils.network.auth.me.username

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义获取用户名请求服务，继承 ApiService
class GetUsernameService : ApiService() {

    // 定义获取用户名响应的数据类
    data class GetUsernameResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: String?          // 用户名
    )

    // 获取用户名方法
    fun getUsername(
        callback: (GetUsernameResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/username")

        // 调用父类的 GET 方法
        get(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseGetUsernameResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析获取用户名响应
    private fun parseGetUsernameResponse(jsonString: String): GetUsernameResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())
            val data = jsonObject.optString("data", null.toString())

            GetUsernameResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
