package com.atcumt.kxq.page.ai.utils

import android.content.Context
import androidx.annotation.StringRes
import com.atcumt.kxq.R
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

/**
 * 抽象：将 ChatError 格式化成用户可读文案
 */
interface ErrorMessageProvider {
    fun getMessage(error: ChatError): String
}

/**
 * 默认实现：从 strings.xml 里取文案，支持参数、国际化、单元测试 Mock
 */
@ActivityRetainedScoped
class DefaultErrorMessageProvider @Inject constructor(
    @ApplicationContext private val ctx: Context
): ErrorMessageProvider {

    private fun getString(@StringRes resId: Int, vararg args: Any): String =
        ctx.getString(resId, *args)

    override fun getMessage(error: ChatError): String =
        when (error) {
            is ChatError.NetworkError ->
                getString(R.string.error_network, error.detail)         // "网络错误：%s"

            is ChatError.StorageError ->
                getString(R.string.error_storage, error.detail)         // "存储错误：%s"

            ChatError.InvalidSession ->
                getString(R.string.error_invalid_session)               // "无效的会话"

            ChatError.MessageEmpty ->
                getString(R.string.error_message_empty)                 // "消息不能为空"

            is ChatError.Unknown ->
                getString(R.string.error_unknown, error.throwable.localizedMessage)
        }
}