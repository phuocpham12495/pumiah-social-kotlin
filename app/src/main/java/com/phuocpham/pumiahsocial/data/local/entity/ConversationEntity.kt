package com.phuocpham.pumiahsocial.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "user1_id") val user1Id: String,
    @ColumnInfo(name = "user2_id") val user2Id: String,
    @ColumnInfo(name = "created_at") val createdAt: String
)
