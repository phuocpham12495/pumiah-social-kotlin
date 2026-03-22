package com.phuocpham.pumiahsocial.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.phuocpham.pumiahsocial.data.local.entity.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendDao {
    @Query("SELECT * FROM friends")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT friend_user_id FROM friends")
    suspend fun getFriendUserIds(): List<String>

    @Upsert
    suspend fun upsertFriends(friends: List<FriendEntity>)

    @Query("DELETE FROM friends")
    suspend fun clearAll()
}
