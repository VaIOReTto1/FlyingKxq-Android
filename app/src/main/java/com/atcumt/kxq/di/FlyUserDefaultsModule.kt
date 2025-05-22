package com.atcumt.kxq.di

import android.content.Context
import android.content.SharedPreferences
import com.atcumt.kxq.utils.Store.FlyKeyChain.EncryptedFlyKeyChain
import com.atcumt.kxq.utils.Store.FlyKeyChain.FlyKeyChain
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults
import com.atcumt.kxq.utils.Store.UserDefaults.SharedPrefsUserDefaults
import com.atcumt.kxq.utils.network.TokenProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module：提供 SharedPreferences & FlyUserDefaults
 */
@Module
@InstallIn(SingletonComponent::class)
object FlyUserDefaultsModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext ctx: Context
    ): SharedPreferences =
        ctx.getSharedPreferences("fly_user_defaults", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFlyUserDefaults(
        impl: SharedPrefsUserDefaults
    ): FlyUserDefaults = impl

    @Provides
    @Singleton
    fun provideTokenProvider(keyChain: FlyKeyChain): TokenProvider = TokenProvider(keyChain)
}