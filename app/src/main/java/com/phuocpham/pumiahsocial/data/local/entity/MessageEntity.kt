package com.phuocpham.pumiahsocial.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phuocpham.pumiahsocial.data.model.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "conversation_id") val conversationId: String,
    @ColumnInfo(name = "sender_id") val senderId: String,
    val content: String,
    @ColumnInfo(name = "created_at") val createdAt: String,
    @ColumnInfo(name = "is_read") val isRead: Boolean
) {
    fun toMessage() = Message(
        id = id,
        conversationId = conversationId,
        senderId = senderId,
        content = content,
        createdAt = createdAt,
        isRead = isRead
    )

    companion object {
        fun fromMessage(message: Message) = MessageEntity(
            id = message.id,
            conversationId = message.conversationId,
            senderId = message.senderId,
            content = message.content,
            createdAt = message.createdAt,
            isRead = message.isRead
        )
    }
}
