package com.atcumt.kxq.utils

import androidx.lifecycle.ViewModel
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaultsKey
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChainType
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChain
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val keyChain: FlyKeyChain,
    private val userDefaults: FlyUserDefaults
) : ViewModel() {

    /**
     * true: 本地有 token 且还没过期
     * false: token 不存在或已过期，需要重新登录
     */
    val isTokenValid: Boolean
        get() {
            val token = keyChain.read(FlyKeyChainType.TOKEN)
            val expiresAt = userDefaults.get<Long>(FlyUserDefaultsKey.TOKEN_EXPIRES_AT) ?: 0L
            return token != null && System.currentTimeMillis() < expiresAt
        }
}
