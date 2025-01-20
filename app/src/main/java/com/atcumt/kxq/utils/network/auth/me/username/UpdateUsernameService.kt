package com.atcumt.kxq.utils.network.auth.me.username

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义修改用户名请求服务，继承 ApiService
class UpdateUsernameService : ApiService() {

    // 定义修改用户名请求的数据类
    data class UpdateUsernameRequest(
        val username: String  // 新用户名
    )

    // 定义修改用户名响应的数据类
    data class UpdateUsernameResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 修改用户名方法
    fun updateUsername(
        updateUsernameRequest: UpdateUsernameRequest,
        callback: (UpdateUsernameResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("username", updateUsernameRequest.username)
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/username")

        // 调用父类的 PATCH 方法
        patch(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseUpdateUsernameResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析修改用户名响应
    private fun parseUpdateUsernameResponse(jsonString: String): UpdateUsernameResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            UpdateUsernameResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
