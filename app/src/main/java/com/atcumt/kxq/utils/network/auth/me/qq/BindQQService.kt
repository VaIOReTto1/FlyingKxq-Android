package com.atcumt.kxq.utils.network.auth.me.qq

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义绑定 QQ 请求服务，继承 ApiService
class BindQQService : ApiService() {

    // 定义绑定 QQ 请求的数据类
    data class BindQQRequest(
        val qqAuthorizationCode: String  // QQ 授权码
    )

    // 定义绑定 QQ 响应的数据类
    data class BindQQResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 绑定 QQ 方法
    fun bindQQ(
        bindQQRequest: BindQQRequest,
        callback: (BindQQResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("qqAuthorizationCode", bindQQRequest.qqAuthorizationCode)
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/qq")

        // 调用父类的 PATCH 方法
        patch(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseBindQQResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析绑定 QQ 响应
    private fun parseBindQQResponse(jsonString: String): BindQQResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            BindQQResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
