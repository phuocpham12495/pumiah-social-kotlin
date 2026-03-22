package com.phuocpham.pumiahsocial.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.phuocpham.pumiahsocial.data.local.dao.FriendDao
import com.phuocpham.pumiahsocial.data.local.dao.MessageDao
import com.phuocpham.pumiahsocial.data.local.dao.PostDao
import com.phuocpham.pumiahsocial.data.local.dao.ProfileDao
import com.phuocpham.pumiahsocial.data.local.entity.ConversationEntity
import com.phuocpham.pumiahsocial.data.local.entity.FriendEntity
import com.phuocpham.pumiahsocial.data.local.entity.MessageEntity
import com.phuocpham.pumiahsocial.data.local.entity.PostEntity
import com.phuocpham.pumiahsocial.data.local.entity.ProfileEntity

@Database(
    entities = [
        ProfileEntity::class,
        PostEntity::class,
        FriendEntity::class,
        ConversationEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun postDao(): PostDao
    abstract fun friendDao(): FriendDao
    abstract fun messageDao(): MessageDao
}
