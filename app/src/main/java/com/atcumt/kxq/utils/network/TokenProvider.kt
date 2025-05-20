package com.atcumt.kxq.utils.network

import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChain
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChainType
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenProvider @Inject constructor(
    private val keyChain: FlyKeyChain
) {
    private val mutex = Mutex()

    suspend fun accessToken(): String? = mutex.withLock {
        keyChain.read(FlyKeyChainType.TOKEN)
    }

    suspend fun refreshToken(): String? = mutex.withLock {
        keyChain.read(FlyKeyChainType.REFRESH_TOKEN)
    }

    suspend fun saveToken(access: String, refresh: String) = mutex.withLock {
        keyChain.saveToken(access, refresh)
    }

    fun clear() {
        keyChain.delete(FlyKeyChainType.TOKEN)
        keyChain.delete(FlyKeyChainType.REFRESH_TOKEN)
    }
}
