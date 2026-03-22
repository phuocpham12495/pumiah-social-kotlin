package com.phuocpham.pumiahsocial.di

import com.phuocpham.pumiahsocial.data.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindFriendsRepository(impl: FriendsRepositoryImpl): FriendsRepository

    @Binds
    @Singleton
    abstract fun bindPostsRepository(impl: PostsRepositoryImpl): PostsRepository

    @Binds
    @Singleton
    abstract fun bindInteractionsRepository(impl: InteractionsRepositoryImpl): InteractionsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository

    @Binds
    @Singleton
    abstract fun bindMessagesRepository(impl: MessagesRepositoryImpl): MessagesRepository
}
