package com.atcumt.kxq.page.ai.utils

sealed class ChatError(message: String? = null) : Throwable(message) {
    data class NetworkError(val detail: String) : ChatError("网络错误：$detail")
    data class StorageError(val detail: String) : ChatError("存储错误：$detail")
    data object InvalidSession : ChatError("无效的会话")
    data object MessageEmpty : ChatError("消息不能为空")
    data class Unknown(val throwable: Throwable) : ChatError(throwable.localizedMessage)
}