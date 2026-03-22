package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id: String = "",
    @SerialName("profile_id") val authorId: String = "",
    @SerialName("post_type") val postType: String = "text",
    val content: String? = null,
    @SerialName("media_url") val imageUrl: String? = null,
    @SerialName("link_url") val linkUrl: String? = null,
    @SerialName("created_at") val createdAt: String = ""
)

data class PostWithDetails(
    val post: Post,
    val authorProfile: Profile? = null,
    val likeCount: Int = 0,
    val commentCount: Int = 0,
    val isLikedByMe: Boolean = false
)
