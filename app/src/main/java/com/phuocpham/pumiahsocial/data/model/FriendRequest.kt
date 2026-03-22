package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: String = "",
    @SerialName("sender_id") val senderId: String = "",
    @SerialName("receiver_id") val receiverId: String = "",
    val status: String = "pending",
    @SerialName("created_at") val createdAt: String = ""
)

data class FriendRequestWithProfile(
    val request: FriendRequest,
    val senderProfile: Profile? = null
)
