package com.atcumt.kxq.utils.network.interceptor

import com.atcumt.kxq.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

/**
 * 简易 Mock：在 BuildConfig.USE_MOCK == true 时拦截并返回本地 Json。
 * 你可以按 URL Path 建一个 Map<Regex, String> 维护 Mock 数据。
 */
class MockInterceptor(
    private val mockMap: Map<Regex, String>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // 1. 如果没开启 Mock 或者没有对应 mock 数据，则直接走真网
        if (!BuildConfig.USE_MOCK) return chain.proceed(request)
        val path = request.url.encodedPath
        val mockBody = mockMap.entries
            .firstOrNull { it.key.matches(path) }
            ?.value
            ?: return chain.proceed(request)

        // 2. 尝试真网请求
        val realResponse = try {
            chain.proceed(request)
        } catch (e: IOException) {
            null  // 网络异常
        }

        // 3. 如果真网响应正常（2xx），就返回真值
        if (realResponse?.isSuccessful == true) {
            return realResponse
        }

        // 4. 真网失败或异常，回退到 Mock 响应
        return Response.Builder()
            .code(200)  // Mock 总是 200
            .protocol(Protocol.HTTP_1_1)
            .message("OK-MOCK-FALLBACK")
            .request(request)
            .body(
                mockBody
                    .toResponseBody("application/json".toMediaType())
            )
            .build()
    }
}