package com.atcumt.kxq.utils.network.interceptor

import com.atcumt.kxq.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

/**
 * 简易 Mock：在 BuildConfig.USE_MOCK == true 时拦截并返回本地 Json。
 * 你可以按 URL Path 建一个 Map<Regex, String> 维护 Mock 数据。
 */
class MockInterceptor(
    private val mockMap: Map<Regex, String>
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!BuildConfig.USE_MOCK) return chain.proceed(chain.request())

        val path = chain.request().url.encodedPath
        val mockBody = mockMap.entries.firstOrNull { path.matches(it.key) }?.value
            ?: return chain.proceed(chain.request()) // 未命中则走真网

        return Response.Builder()
            .code(200)
            .protocol(Protocol.HTTP_1_1)
            .message("OK-MOCK")
            .request(chain.request())
            .body(mockBody.toResponseBody("application/json".toMediaType()))
            .build()
    }
}
