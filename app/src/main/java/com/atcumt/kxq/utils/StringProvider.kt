package com.atcumt.kxq.utils

import android.content.Context
import javax.inject.Inject
import javax.inject.Singleton

/** 通过接口抽象 Android 资源访问，方便 Mock */
interface StringProvider {
    fun getString(resId: Int, vararg args: Any): String
}

/** Hilt 自动注入的默认实现 */
@Singleton
class AndroidStringProvider @Inject constructor(
    private val context: Context
) : StringProvider {
    override fun getString(resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}
