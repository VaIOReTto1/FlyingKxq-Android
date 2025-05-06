package com.atcumt.kxq.utils.Store.FlyKeyChain

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

// 对应 iOS 的 FlyKeyChainType
enum class FlyKeyChainType(val key: String) {
    TOKEN("com.atcumt.kxq.token"),
    REFRESH_TOKEN("com.atcumt.kxq.refreshToken")
}

// 抽象 KeyChain 接口，方便 Mock 测试
interface FlyKeyChain {
    fun saveToken(accessToken: String?, refreshToken: String?)
    fun save(key: FlyKeyChainType, value: String)
    fun read(key: FlyKeyChainType): String?
    fun delete(key: FlyKeyChainType)
}

/**
 * 基于 EncryptedSharedPreferences 的加密存储实现
 */
@Singleton
class EncryptedFlyKeyChain @Inject constructor(
    @ApplicationContext private val context: Context
) : FlyKeyChain {

    // 显式指定类型，确保 lazy delegate 正确推断
    private val prefs: SharedPreferences by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            "fly_keystore_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun saveToken(accessToken: String?, refreshToken: String?) {
        accessToken?.let { save(FlyKeyChainType.TOKEN, it) }
        refreshToken?.let { save(FlyKeyChainType.REFRESH_TOKEN, it) }
    }

    override fun save(key: FlyKeyChainType, value: String) {
        runCatching {
            prefs.edit()
                .putString(key.key, value)
                .apply()
        }.onFailure { it.printStackTrace() }
    }

    override fun read(key: FlyKeyChainType): String? =
        runCatching { prefs.getString(key.key, null) }
            .getOrNull()

    override fun delete(key: FlyKeyChainType) {
        runCatching {
            prefs.edit()
                .remove(key.key)
                .apply()
        }.onFailure { it.printStackTrace() }
    }
}

// 抽象 TokenProvider，方便注入与 Mock
interface TokenProvider {
    fun getToken(): String?
}

/**
 * 从 FlyKeyChain 中读取当前 Token
 */
@Singleton
class KeyChainTokenProvider @Inject constructor(
    private val flyKeyChain: FlyKeyChain
) : TokenProvider {
    override fun getToken(): String? =
        flyKeyChain.read(FlyKeyChainType.TOKEN)
}

/**
 * Hilt Module：绑定接口到实现
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class FlyKeyChainModule {
    @Binds
    abstract fun bindFlyKeyChain(
        impl: EncryptedFlyKeyChain
    ): FlyKeyChain

    @Binds
    abstract fun bindTokenProvider(
        impl: KeyChainTokenProvider
    ): TokenProvider
}
