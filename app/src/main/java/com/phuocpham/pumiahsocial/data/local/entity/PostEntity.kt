package com.phuocpham.pumiahsocial.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.phuocpham.pumiahsocial.data.model.Post

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "author_id") val authorId: String,
    @ColumnInfo(name = "post_type") val postType: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "image_url") val imageUrl: String?,
    @ColumnInfo(name = "link_url") val linkUrl: String?,
    @ColumnInfo(name = "created_at") val createdAt: String
) {
    fun toPost() = Post(
        id = id,
        authorId = authorId,
        postType = postType,
        content = content,
        imageUrl = imageUrl,
        linkUrl = linkUrl,
        createdAt = createdAt
    )

    companion object {
        fun fromPost(post: Post) = PostEntity(
            id = post.id,
            authorId = post.authorId,
            postType = post.postType,
            content = post.content,
            imageUrl = post.imageUrl,
            linkUrl = post.linkUrl,
            createdAt = post.createdAt
        )
    }
}
