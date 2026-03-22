package com.phuocpham.pumiahsocial.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val id: String = "",
    @SerialName("post_id") val postId: String = "",
    @SerialName("profile_id") val authorId: String = "",
    val content: String = "",
    @SerialName("created_at") val createdAt: String = ""
)

data class CommentWithDetails(
    val comment: Comment,
    val authorProfile: Profile? = null,
    val likeCount: Int = 0,
    val isLikedByMe: Boolean = false
)
