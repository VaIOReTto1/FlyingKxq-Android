package com.atcumt.kxq.utils.network.auth

import com.atcumt.kxq.utils.network.ApiService
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.RequestBody
import org.json.JSONObject
import java.io.IOException

// 定义获取敏感记录请求服务，继承 ApiService
class SensitiveRecordService : ApiService() {

    // 定义获取敏感记录请求的数据类
    data class SensitiveRecordRequest(
        val type: String,   // 记录类型
        val page: Long,     // 当前页
        val size: Long      // 每页记录数
    )

    // 定义获取敏感记录响应的数据类
    data class SensitiveRecordResponse(
        val code: Int?,            // 响应码
        val msg: String?,          // 消息
        val data: SensitiveRecordResponseData? // 数据部分
    )

    data class SensitiveRecordResponseData(
        val totalRecords: Long?,  // 总记录数
        val totalPages: Long?,    // 总页数
        val page: Long?,          // 当前页
        val size: Long?,          // 每页记录数
        val data: List<Record>?   // 记录数据
    )

    data class Record(
        val recordId: String?,    // 记录ID
        val userId: String?,      // 用户ID
        val type: String?,        // 类型
        val description: String?, // 描述
        val ip: String?,          // IP地址
        val region: String?,      // 地区
        val recordTime: String?   // 记录时间
    )

    // 获取敏感记录方法
    fun getSensitiveRecords(
        sensitiveRecordRequest: SensitiveRecordRequest,
        callback: (SensitiveRecordResponse?, IOException?) -> Unit
    ) {
        // 设置请求头
        val headers = Headers.Builder()
            .add("Content-Type", "application/json")
            .add("Accept", "*/*")
            .build()

        // 构建请求体
        val formBody = FormBody.Builder()
            .add("type", sensitiveRecordRequest.type)
            .add("page", sensitiveRecordRequest.page.toString())
            .add("size", sensitiveRecordRequest.size.toString())
            .build()

        // 构建 URL
        val url = buildUrlWithParams(BASE_URL_AUTH, "sensitiveRecord")

        // 调用父类的 POST 方法
        post(url, headers, formBody) { response, error ->
            if (error != null) {
                callback(null, error)
            } else {
                val parsedResponse = response?.let { parseSensitiveRecordResponse(it) }
                callback(parsedResponse, null)
            }
        }
    }

    // 解析获取敏感记录响应
    private fun parseSensitiveRecordResponse(jsonString: String): SensitiveRecordResponse? {
        return try {
            val jsonObject = JSONObject(jsonString)
            val code = jsonObject.optInt("code", -1)
            val msg = jsonObject.optString("msg", null.toString())

            val dataObject = jsonObject.optJSONObject("data")
            val data = dataObject?.let {
                val recordsArray = it.optJSONArray("data")
                val records = mutableListOf<Record>()
                for (i in 0 until (recordsArray?.length() ?: 0)) {
                    val recordObject = recordsArray?.optJSONObject(i)
                    records.add(
                        Record(
                            recordId = recordObject?.optString("recordId", null.toString()),
                            userId = recordObject?.optString("userId", null.toString()),
                            type = recordObject?.optString("type", null.toString()),
                            description = recordObject?.optString("description", null.toString()),
                            ip = recordObject?.optString("ip", null.toString()),
                            region = recordObject?.optString("region", null.toString()),
                            recordTime = recordObject?.optString("recordTime", null.toString())
                        )
                    )
                }

                SensitiveRecordResponseData(
                    totalRecords = it.optLong("totalRecords", -1),
                    totalPages = it.optLong("totalPages", -1),
                    page = it.optLong("page", -1),
                    size = it.optLong("size", -1),
                    data = records
                )
            }
            SensitiveRecordResponse(code, msg, data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
