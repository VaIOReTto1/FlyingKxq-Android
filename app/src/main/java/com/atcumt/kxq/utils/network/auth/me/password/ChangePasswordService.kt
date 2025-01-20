package com.atcumt.kxq.utils.network.auth.me.password

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义修改密码请求服务，继承 ApiService
class ChangePasswordService : ApiService() {

    // 定义修改密码请求的数据类
    data class ChangePasswordRequest(
        val oldPassword: String,  // 旧密码
        val newPassword: String   // 新密码
    )

    // 定义修改密码响应的数据类
    data class ChangePasswordResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 修改密码方法
    fun changePassword(
        changePasswordRequest: ChangePasswordRequest,
        callback: (ChangePasswordResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("oldPassword", changePasswordRequest.oldPassword)
            .add("newPassword", changePasswordRequest.newPassword)
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/password")

        // 调用父类的 PATCH 方法
        patch(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseChangePasswordResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析修改密码响应
    private fun parseChangePasswordResponse(jsonString: String): ChangePasswordResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            ChangePasswordResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
