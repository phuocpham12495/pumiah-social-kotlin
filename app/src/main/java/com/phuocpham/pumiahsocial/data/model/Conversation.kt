package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Conversation(
    val id: String = "",
    @SerialName("user1_id") val user1Id: String = "",
    @SerialName("user2_id") val user2Id: String = "",
    @SerialName("last_message_at") val lastMessageAt: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

data class ConversationWithDetails(
    val conversation: Conversation,
    val otherUserProfile: Profile? = null,
    val lastMessage: Message? = null,
    val unreadCount: Int = 0
)
