package com.atcumt.kxq.utils.network.interceptor

import android.util.Log
import com.atcumt.kxq.utils.network.TokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val token = tokenProvider.accessToken()
        Log.d("AuthInterceptor", "token: $token")
//        val token =
//            "NOmdUNImd5sEmpEzLF1Z3Y6T3rNUH1KHsTA95oHsRRAXYazXvRand2F1RU14QLMzySUu104A8mcp6N1blRMXlhKro92UR2f0RGzQB5QMpcG2NcDFvptt5TU7Pjo7xKUW1TuTquIGwZ9htX9zNRDkDX1GoNPkUrEPCXd1NPxODobIhkgHkJQfFKbpLqRqVkE78RsgmQTc4WN2ZfR2oAN2aoylHzr55busFGYtIAda7NCQFaqtBLlKjygj0zsYoAcZ"
        val req = if (token != null)

            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        else chain.request()
        chain.proceed(req)
    }
}
