package com.atcumt.kxq.utils.network

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

// 通用的网络请求服务类
open class ApiService {

    companion object {
        const val BASE_URL_USER = "http://119.45.93.228:8080/api/user/"
        const val BASE_URL_AUTH = "http://119.45.93.228:8080/api/auth/"
        const val BASE_URL_MAIN = "http://119.45.93.228:8080/api/"
        const val BASE_URL_POST = "http://119.45.93.228:8080/api//post/"
        const val BASE_URL_LIKE = "http://119.45.93.228:8080/api//like/"
    }

    private val client = OkHttpClient()

    /**
     * 构建带有查询参数的 URL
     * @param baseUrl 基础 URL
     * @param endpoint 接口的具体路径
     * @param params 查询参数 (可选)
     * @return 完整的 URL
     */
    open fun buildUrlWithParams(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>? = null
    ): String {
        var url = "$baseUrl$endpoint"
        params?.let {
            val paramString = it.map { (key, value) ->
                "$key=${URLEncoder.encode(value, StandardCharsets.UTF_8.toString())}"
            }.joinToString("&")
            url += "?$paramString"
        }
        return url
    }

    // GET 方法
    open fun get(
        url: String,
        headers: Headers = Headers.Builder()
            .build(),
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }

    // POST 方法
    open fun post(
        url: String,
        headers: Headers = Headers.Builder().build(),
        body: RequestBody = FormBody.Builder().build(),
        callback: (String?, IOException?) -> Unit,
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .post(body)
            .build()
        Log.d("NetworkLog", "Received login request: $request")


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                Log.d("NetworkLog", "Received login response: $response")
                callback(response.body?.string(), null)
            }
        })
    }

    // DELETE 方法
    open fun delete(
        url: String,
        headers: Headers,
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .delete()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }

    // PATCH 方法
    open fun patch(
        url: String,
        headers: Headers,
        body: RequestBody = FormBody.Builder().build(),
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .patch(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }

    // PUT 方法
    open fun put(
        url: String,
        headers: Headers,
        body: RequestBody,
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .put(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }

    // OPTIONS 方法
    open fun options(
        url: String,
        headers: Headers,
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .method("OPTIONS", null)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }

    // HEAD 方法
    open fun head(
        url: String,
        headers: Headers,
        callback: (String?, IOException?) -> Unit
    ) {
        val request = Request.Builder()
            .url(url)
            .headers(headers)
            .head()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(null, e)
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.body?.string(), null)
            }
        })
    }
}