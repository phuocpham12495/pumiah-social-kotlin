package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AppSettings(
    val friendRequestNotifications: Boolean = true,
    val likeNotifications: Boolean = true,
    val commentNotifications: Boolean = true,
    val messageNotifications: Boolean = true
)
