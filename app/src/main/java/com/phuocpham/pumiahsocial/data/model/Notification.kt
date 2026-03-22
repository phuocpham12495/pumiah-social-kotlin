package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String = "",
    @SerialName("recipient_id") val recipientId: String = "",
    @SerialName("sender_id") val senderId: String? = null,
    val type: String = "",
    @SerialName("target_url") val targetUrl: String? = null,
    val message: String? = null,
    @SerialName("is_read") val isRead: Boolean = false,
    @SerialName("created_at") val createdAt: String = ""
)

data class NotificationWithProfile(
    val notification: Notification,
    val senderProfile: Profile? = null
)
