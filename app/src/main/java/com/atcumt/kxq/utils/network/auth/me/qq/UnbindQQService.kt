package com.atcumt.kxq.utils.network.auth.me.qq

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义解除 QQ 绑定请求服务，继承 ApiService
class UnbindQQService : ApiService() {

    // 定义解除 QQ 绑定响应的数据类
    data class UnbindQQResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
    )

    // 解除 QQ 绑定方法
    fun unbindQQ(
        callback: (UnbindQQResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "me/qq")

        // 调用父类的 DELETE 方法
        delete(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseUnbindQQResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析解除 QQ 绑定响应
    private fun parseUnbindQQResponse(jsonString: String): UnbindQQResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            UnbindQQResponse(code, msg)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
