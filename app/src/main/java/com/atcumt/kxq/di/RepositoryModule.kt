package com.atcumt.kxq.di

import com.atcumt.kxq.page.component.FlyMarkdownTextView.MarkdownRepository
import com.atcumt.kxq.page.component.FlyMarkdownTextView.MarkwonMarkdownRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindMarkdownRepository(
        impl: MarkwonMarkdownRepository
    ): MarkdownRepository
}
