package com.atcumt.kxq.di

import com.atcumt.kxq.utils.network.ai.user.ConversationsService
import com.atcumt.kxq.utils.network.ai.user.conversation.ConversationService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module
@InstallIn(SingletonComponent::class)
class AIConversationModule {
    @Provides
    fun provideConversationService(): ConversationService = ConversationService()

    @Provides
    fun provideConversationsService(): ConversationsService = ConversationsService()
}