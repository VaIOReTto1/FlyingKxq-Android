package com.atcumt.kxq.utils.network

import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap
import retrofit2.http.Url
import java.io.IOException

// Retrofit 服务接口，用于定义网络请求的接口方法
interface ApiServiceInterface {
    @GET
    fun get(
        @Url endpoint: String,  // 接口路径
        @QueryMap params: Map<String, String>? = null,  // 可选查询参数
        @HeaderMap headers: Map<String, String> = mapOf()  // 请求头部信息
    ): Call<ResponseBody>

    @POST
    fun post(
        @Url endpoint: String,
        @Body body: RequestBody,  // 请求体（JSON 格式）
        @HeaderMap headers: Map<String, String> = mapOf()  // 请求头部信息
    ): Call<ResponseBody>

    @DELETE
    fun delete(
        @Url endpoint: String,  // 接口路径
        @HeaderMap headers: Map<String, String> = mapOf()  // 请求头部信息
    ): Call<ResponseBody>

    @FormUrlEncoded
    @PATCH
    fun patch(
        @Url endpoint: String,
        @FieldMap params: Map<String, String>? = null,  // 表单参数
        @HeaderMap headers: Map<String, String> = mapOf()  // 请求头部信息
    ): Call<ResponseBody>

    @PUT
    fun put(
        @Url endpoint: String,
        @Body body: RequestBody,  // 请求体（JSON 格式）
        @HeaderMap headers: Map<String, String> = mapOf()  // 请求头部信息
    ): Call<ResponseBody>
}

// Retrofit 管理类，用于创建和缓存 Retrofit 实例
object RetrofitClient {
    private val retrofitInstances = mutableMapOf<String, Retrofit>()

    // 获取 Retrofit 实例，使用缓存来避免重复创建
    private fun getRetrofit(baseUrl: String): Retrofit {
        return retrofitInstances.getOrPut(baseUrl) {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 解析器
                .build()
        }
    }

    // 获取指定 baseUrl 的 ApiServiceInterface 实例
    fun getService(baseUrl: String): ApiServiceInterface {
        return getRetrofit(baseUrl).create(ApiServiceInterface::class.java)
    }
}

// 网络请求服务类，用于处理不同 HTTP 方法的请求
object ApiServiceS {
    // 各个接口的基础 URL
    const val BASE_URL_USER = "http://119.45.93.228:8080/api/user/"
    const val BASE_URL_AUTH = "http://119.45.93.228:8080/api/auth/"
    const val BASE_URL_MAIN = "http://119.45.93.228:8080/api/"
    const val BASE_URL_POST = "http://119.45.93.228:8080/api/post/"
    const val BASE_URL_LIKE = "http://119.45.93.228:8080/api/like/"

    // 获取指定 baseUrl 的服务实例
    private fun getService(baseUrl: String) = RetrofitClient.getService(baseUrl)

    // GET 请求方法
    fun get(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>? = null,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        getService(baseUrl).get(endpoint, params, headers).enqueue(createCallback(callback))
    }

    // POST 请求方法
    fun post(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String> = mapOf(),
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        Log.d("NetworkLog", "Sending POST to: ${baseUrl + endpoint}")
        getService(baseUrl).post(endpoint, createJsonBody(params), headers).enqueue(createCallback(callback))
    }

    // DELETE 请求方法
    fun delete(
        baseUrl: String,
        endpoint: String,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        getService(baseUrl).delete(endpoint, headers).enqueue(createCallback(callback))
    }

    // PATCH 请求方法
    fun patch(
        baseUrl: String,
        endpoint: String,
        params: Map<String, String>? = null,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        getService(baseUrl).patch(endpoint, params, headers).enqueue(createCallback(callback))
    }

    // PUT 请求方法
    fun put(
        baseUrl: String,
        endpoint: String,
        body: RequestBody,
        headers: Map<String, String> = mapOf(),
        callback: (String?, Throwable?) -> Unit
    ) {
        getService(baseUrl).put(endpoint, body, headers).enqueue(createCallback(callback))
    }

    // 创建回调函数，处理请求的响应和失败情况
    private fun createCallback(callback: (String?, Throwable?) -> Unit) = object : Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: retrofit2.Response<ResponseBody>) {
            Log.d("NetworkLog", "Received response from: ${call.request()}")
            Log.d("NetworkLog", "Response body: $response")
            try {
                if (response.isSuccessful) {
                    callback(response.body()?.string(), null)  // 请求成功，返回响应体
                } else {
                    callback(null, IOException("HTTP error: ${response.code()}"))  // 请求失败，返回错误信息
                }
            } catch (e: Exception) {
                callback(null, e)  // 处理异常情况
            }
        }

        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            callback(null, t)  // 请求失败，返回异常
        }
    }

    // 创建 JSON 格式的请求体
    fun createJsonRequestBody(json: String): RequestBody {
        return json.toRequestBody("application/json".toMediaTypeOrNull())  // 将 JSON 字符串转为 RequestBody
    }

    // 直接使用 JSON 格式的请求体，而不是使用 FormBody
    fun createJsonBody(params: Map<String, String>): RequestBody {
        val json = params.entries.joinToString(",") { "\"${it.key}\":\"${it.value}\"" }
        val jsonBody = "{ $json }"
        return createJsonRequestBody(jsonBody)  // 创建 JSON 请求体
    }
}
