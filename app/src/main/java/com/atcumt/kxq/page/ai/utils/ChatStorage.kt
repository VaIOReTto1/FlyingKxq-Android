package com.atcumt.kxq.page.ai.utils

import com.atcumt.kxq.page.ai.viewmodel.ChatMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton
import com.atcumt.kxq.utils.Store.UserDefaults.FlyUserDefaults

data class ChatSession(
    val sessionId: String,
    val title: String,
    val lastUpdated: Long,
    val messages: List<ChatMessage>
)

interface ChatStorageService {
    suspend fun saveChatSessions(sessions: List<ChatSession>)
    suspend fun loadChatSessions(): List<ChatSession>
    suspend fun saveMessages(sessionId: String, messages: List<ChatMessage>)
    suspend fun loadMessages(sessionId: String): List<ChatMessage>
    suspend fun deleteMessages(sessionId: String)
}

private const val KEY_SESSIONS = "chat_sessions"
private fun keyMessages(sessionId: String) = "chat_messages_$sessionId"

@Singleton
class DefaultChatStorageService @Inject constructor(
    private val userDefaults: FlyUserDefaults,
    private val gson: Gson
) : ChatStorageService {
    private val sessionListType: Type = object : TypeToken<List<ChatSession>>() {}.type
    private val msgListType: Type = object : TypeToken<List<ChatMessage>>() {}.type

    override suspend fun saveChatSessions(sessions: List<ChatSession>): Unit =
        withContext(Dispatchers.IO) {
            runCatching {
                val json = gson.toJson(sessions)
                userDefaults.setString(KEY_SESSIONS, json)
            }.onFailure { throw ChatError.StorageError(it.message ?: "保存会话失败") }
        }

    override suspend fun loadChatSessions(): List<ChatSession> =
        withContext(Dispatchers.IO) {
            runCatching {
                userDefaults.getString(KEY_SESSIONS)
                    ?.let { gson.fromJson<List<ChatSession>>(it, sessionListType) }
                    ?: emptyList()
            }.getOrElse { throw ChatError.StorageError(it.message ?: "加载会话失败") }
        }

    override suspend fun saveMessages(sessionId: String, messages: List<ChatMessage>): Unit =
        withContext(Dispatchers.IO) {
            runCatching {
                val json = gson.toJson(messages)
                userDefaults.setString(keyMessages(sessionId), json)
            }.onFailure { throw ChatError.StorageError(it.message ?: "保存消息失败") }
        }

    override suspend fun loadMessages(sessionId: String): List<ChatMessage> =
        withContext(Dispatchers.IO) {
            runCatching {
                userDefaults.getString(keyMessages(sessionId))
                    ?.let { gson.fromJson<List<ChatMessage>>(it, msgListType) }
                    ?: emptyList()
            }.getOrElse { throw ChatError.StorageError(it.message ?: "加载消息失败") }
        }

    override suspend fun deleteMessages(sessionId: String): Unit =
        withContext(Dispatchers.IO) {
            runCatching {
                userDefaults.remove(keyMessages(sessionId))
            }.onFailure { throw ChatError.StorageError(it.message ?: "删除消息失败") }
        }
}

@Module
@InstallIn(SingletonComponent::class)
object ChatStorageModule {
    @Provides
    fun provideChatStorageService(
        userDefaults: FlyUserDefaults,
        gson: Gson
    ): ChatStorageService = DefaultChatStorageService(userDefaults, gson)

    @Provides
    fun provideGson(): Gson = Gson()
}
