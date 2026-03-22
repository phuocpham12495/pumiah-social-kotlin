package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Like(
    val id: String = "",
    @SerialName("profile_id") val userId: String = "",
    @SerialName("target_id") val targetId: String = "",
    @SerialName("target_type") val targetType: String = "post",
    @SerialName("created_at") val createdAt: String = ""
)
