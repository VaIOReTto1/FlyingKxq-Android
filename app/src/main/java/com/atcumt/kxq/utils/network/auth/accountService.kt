package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义注销账号请求服务，继承 ApiService
class AccountService : ApiService() {

    // 定义注销账号请求的数据类
    data class AccountRequest(
        val password: String  // 密码
    )

    // 定义注销账号响应的数据类
    data class AccountResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 注销账号方法
    fun deleteAccount(
        accountRequest: AccountRequest,
        callback: (AccountResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL，添加查询参数
        val url = buildUrlWithParams(BASE_URL_AUTH, "account", mapOf("password" to accountRequest.password))

        // 调用父类的 DELETE 方法
        delete(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseAccountResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析注销账号响应
    private fun parseAccountResponse(jsonString: String): AccountResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            AccountResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
