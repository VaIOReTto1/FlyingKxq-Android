package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.Headers
import org.json.JSONObject
import java.io.IOException

// 定义获取所有登录设备请求服务，继承 ApiService
class LoginDevicesService : ApiService() {

    // 定义获取所有登录设备请求的数据类
    data class LoginDevicesRequest(
        val device: String  // 设备信息
    )

    // 定义获取所有登录设备响应的数据类
    data class LoginDevicesResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: List<String>?    // 数据部分，设备列表
    )

    // 获取所有登录设备的方法
    fun getLoginDevices(
        loginDevicesRequest: LoginDevicesRequest,
        callback: (LoginDevicesResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Accept", "*/*")
            .build()

        // 构建 URL，添加查询参数
        val url = buildUrlWithParams(
            BASE_URL_AUTH,
            "loginDevices",
            mapOf("device" to loginDevicesRequest.device)
        )

        // 调用父类的 GET 方法
        get(url, headers) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseLoginDevicesResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析获取所有登录设备的响应
    private fun parseLoginDevicesResponse(jsonString: String): LoginDevicesResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataArray = jsonObject.optJSONArray("data")
            val data = dataArray?.let {
                List(it.length()) { index -> it.optString(index) }
            }
            LoginDevicesResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
