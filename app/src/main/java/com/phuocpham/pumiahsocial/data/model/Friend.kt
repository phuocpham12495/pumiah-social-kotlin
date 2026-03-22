package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Friend(
    @SerialName("user1_id") val user1Id: String = "",
    @SerialName("user2_id") val user2Id: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

enum class FriendshipStatus {
    NONE,
    PENDING_SENT,
    PENDING_RECEIVED,
    FRIENDS
}
