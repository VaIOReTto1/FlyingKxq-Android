package com.atcumt.kxq.utils.network.auth.me.linkedAccount

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 获取账户信息请求服务，继承 ApiService
class LinkedAccountService : ApiService() {

    // 定义获取账户信息响应的数据类
    data class LinkedAccountResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: LinkedAccountData? // 账户数据
    )

    data class LinkedAccountData(
        val userId: String?,      // 用户ID
        val email: String?,       // 电子邮件
        val qq: Boolean?,         // QQ 是否绑定
        val apple: Boolean?       // Apple 是否绑定
    )

    // 获取账户信息方法
    fun getLinkedAccount(
        callback: (LinkedAccountResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/linkedAccount")

        // 调用父类的 GET 方法
        get(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseLinkedAccountResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析获取账户信息响应
    private fun parseLinkedAccountResponse(jsonString: String): LinkedAccountResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", "")
            val dataJson = jsonObject.optJSONObject("data")
            val data = dataJson?.let {
                LinkedAccountData(
                    userId = it.optString("userId"),
                    email = it.optString("email"),
                    qq = it.optBoolean("qq"),
                    apple = it.optBoolean("apple")
                )
            }

            LinkedAccountResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
