package com.atcumt.kxq.utils.Store.UserDefaults

import android.content.Context
import android.content.SharedPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 对应 Swift 里的 FlyUserDefaults.Key
 */
enum class FlyUserDefaultsKey(val key: String) {
    IS_LOGGED_IN("isLoggedIn"),
    NEWS_TYPE("newsType"),
    TOKEN_EXPIRES_AT("tokenExpiresAt")
}

/**
 * 抽象接口，方便 Mock & 替换为 DataStore 实现
 */
interface FlyUserDefaults {
    // 原有的枚举 key
    fun <T> set(value: T, forKey: FlyUserDefaultsKey)
    fun <T> get(key: FlyUserDefaultsKey): T?
    fun remove(key: FlyUserDefaultsKey)
    fun contains(key: FlyUserDefaultsKey): Boolean
    fun clearAll()

    // 新增：字符串 key
    fun setString(key: String, value: String)
    fun getString(key: String): String?
    fun remove(key: String)
}

/**
 * 基于 SharedPreferences 的实现
 */
@Singleton
class SharedPrefsUserDefaults @Inject constructor(
    private val prefs: SharedPreferences
) : FlyUserDefaults {

    override fun <T> set(value: T, forKey: FlyUserDefaultsKey) {
        prefs.edit().apply {
            when (value) {
                is String  -> putString(forKey.key, value)
                is Int     -> putInt(forKey.key, value)
                is Boolean -> putBoolean(forKey.key, value)
                is Float   -> putFloat(forKey.key, value)
                is Long    -> putLong(forKey.key, value)
                is Set<*>  -> @Suppress("UNCHECKED_CAST")
                putStringSet(forKey.key, value as Set<String>)
                else       -> throw IllegalArgumentException("不支持的类型：${value}")
            }
        }.apply() // 异步提交
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(key: FlyUserDefaultsKey): T? {
        // 直接从 all 里拿，类型转换
        return prefs.all[key.key] as? T
    }

    override fun remove(key: FlyUserDefaultsKey) {
        prefs.edit().remove(key.key).apply()
    }

    override fun contains(key: FlyUserDefaultsKey): Boolean =
        prefs.contains(key.key)

    override fun clearAll() {
        val editor = prefs.edit()
        FlyUserDefaultsKey.entries.forEach { editor.remove(it.key) }
        editor.apply()
    }

    override fun setString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun getString(key: String): String? =
        prefs.getString(key, null)

    override fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}

/**
 * Hilt Module：提供 SharedPreferences & FlyUserDefaults
 */
@Module
@InstallIn(SingletonComponent::class)
object FlyUserDefaultsModule {

    @Provides @Singleton
    fun provideSharedPreferences(
        @ApplicationContext ctx: Context
    ): SharedPreferences =
        ctx.getSharedPreferences("fly_user_defaults", Context.MODE_PRIVATE)

    @Provides @Singleton
    fun provideFlyUserDefaults(
        impl: SharedPrefsUserDefaults
    ): FlyUserDefaults = impl
}
