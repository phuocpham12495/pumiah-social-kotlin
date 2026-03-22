package com.phuocpham.pumiahsocial.di

import android.content.Context
import androidx.room.Room
import com.phuocpham.pumiahsocial.data.local.AppDatabase
import com.phuocpham.pumiahsocial.data.local.dao.FriendDao
import com.phuocpham.pumiahsocial.data.local.dao.MessageDao
import com.phuocpham.pumiahsocial.data.local.dao.PostDao
import com.phuocpham.pumiahsocial.data.local.dao.ProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "pumiah_social.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideProfileDao(db: AppDatabase): ProfileDao = db.profileDao()

    @Provides
    fun providePostDao(db: AppDatabase): PostDao = db.postDao()

    @Provides
    fun provideFriendDao(db: AppDatabase): FriendDao = db.friendDao()

    @Provides
    fun provideMessageDao(db: AppDatabase): MessageDao = db.messageDao()
}
