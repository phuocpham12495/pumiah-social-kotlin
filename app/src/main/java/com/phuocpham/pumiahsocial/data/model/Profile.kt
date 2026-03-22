package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id: String = "",
    val username: String = "",
    @SerialName("full_name") val name: String? = null,
    @SerialName("profile_photo_url") val avatarUrl: String? = null,
    @SerialName("cover_photo_url") val coverPhotoUrl: String? = null,
    val bio: String? = null,
    val location: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("created_at") val joinDate: String = ""
)
