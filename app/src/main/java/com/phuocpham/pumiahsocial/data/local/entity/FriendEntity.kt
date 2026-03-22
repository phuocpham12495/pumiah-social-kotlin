package com.phuocpham.pumiahsocial.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(tableName = "friends", primaryKeys = ["user1_id", "user2_id"])
data class FriendEntity(
    @ColumnInfo(name = "user1_id") val user1Id: String,
    @ColumnInfo(name = "user2_id") val user2Id: String,
    @ColumnInfo(name = "friend_user_id") val friendUserId: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
