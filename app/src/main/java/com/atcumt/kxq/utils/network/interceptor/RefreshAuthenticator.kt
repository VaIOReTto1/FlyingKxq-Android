package com.atcumt.kxq.utils.network.interceptor

import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaultsKey
import com.atcumt.kxq.utils.network.TokenProvider
import com.atcumt.kxq.utils.network.auth.RefreshTokenService
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshAuthenticator @Inject constructor(
    private val refreshService: RefreshTokenService,
    private val tokenProvider: TokenProvider,
    private val userDefaults: FlyUserDefaults
) : Authenticator {

    private val mutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? = runBlocking {
        // 同一个请求只重试一次
        if (responseCount(response) >= 2) return@runBlocking null

        mutex.withLock {
            val refreshToken = tokenProvider.refreshToken() ?: return@withLock null
            val resp = refreshService.refreshTokenBlocking(
                RefreshTokenService.RefreshTokenRequest(refreshToken)
            )
            if (resp.isSuccess && resp.data != null) {
                tokenProvider.saveToken(resp.data.accessToken!!, resp.data.refreshToken!!)
                userDefaults.set(true, FlyUserDefaultsKey.IS_LOGGED_IN)
                return@runBlocking response.request.newBuilder()
                    .header("Authorization", "Bearer ${resp.data.accessToken}")
                    .build()
            } else {
                tokenProvider.clear()
                userDefaults.set(false, FlyUserDefaultsKey.IS_LOGGED_IN)
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response.priorResponse
        var c = 1
        while (r != null) { c++; r = r.priorResponse }
        return c
    }
}
