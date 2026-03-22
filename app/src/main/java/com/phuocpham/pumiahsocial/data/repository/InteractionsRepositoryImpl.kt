package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.Comment
import com.phuocpham.pumiahsocial.data.model.CommentWithDetails
import com.phuocpham.pumiahsocial.data.model.Like
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InteractionsRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val auth: Auth
) : InteractionsRepository {

    private val currentUserId: String
        get() = auth.currentUserOrNull()?.id ?: ""

    override suspend fun togglePostLike(postId: String, authorId: String): Result<Boolean> = safeApiCall {
        val existing = postgrest.from("likes")
            .select {
                filter {
                    eq("profile_id", currentUserId)
                    eq("target_id", postId)
                    eq("target_type", "post")
                }
            }
            .decodeList<Like>()

        if (existing.isNotEmpty()) {
            postgrest.from("likes").delete {
                filter {
                    eq("profile_id", currentUserId)
                    eq("target_id", postId)
                    eq("target_type", "post")
                }
            }
            false
        } else {
            postgrest.from("likes").insert(
                mapOf(
                    "profile_id" to currentUserId,
                    "target_id" to postId,
                    "target_type" to "post"
                )
            )
            // Notify post author
            if (authorId != currentUserId) {
                postgrest.from("notifications").insert(
                    mapOf(
                        "recipient_id" to authorId,
                        "sender_id" to currentUserId,
                        "type" to "like_post",
                        "target_url" to postId,
                        "message" to "đã thích bài viết của bạn"
                    )
                )
            }
            true
        }
    }

    override suspend fun toggleCommentLike(commentId: String): Result<Boolean> = safeApiCall {
        val existing = postgrest.from("likes")
            .select {
                filter {
                    eq("profile_id", currentUserId)
                    eq("target_id", commentId)
                    eq("target_type", "comment")
                }
            }
            .decodeList<Like>()

        if (existing.isNotEmpty()) {
            postgrest.from("likes").delete {
                filter {
                    eq("profile_id", currentUserId)
                    eq("target_id", commentId)
                    eq("target_type", "comment")
                }
            }
            false
        } else {
            postgrest.from("likes").insert(
                mapOf(
                    "profile_id" to currentUserId,
                    "target_id" to commentId,
                    "target_type" to "comment"
                )
            )
            true
        }
    }

    override suspend fun getCommentsForPost(postId: String): Result<List<CommentWithDetails>> = safeApiCall {
        val comments = postgrest.from("comments")
            .select {
                filter { eq("post_id", postId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.ASCENDING)
            }
            .decodeList<Comment>()

        if (comments.isEmpty()) return@safeApiCall emptyList()

        val authorIds = comments.map { it.authorId }.distinct()
        val profiles = postgrest.from("profiles")
            .select { filter { isIn("id", authorIds) } }
            .decodeList<Profile>()
            .associateBy { it.id }

        val commentIds = comments.map { it.id }
        val likes = postgrest.from("likes")
            .select {
                filter {
                    isIn("target_id", commentIds)
                    eq("target_type", "comment")
                }
            }
            .decodeList<Like>()

        comments.map { comment ->
            val commentLikes = likes.filter { it.targetId == comment.id }
            CommentWithDetails(
                comment = comment,
                authorProfile = profiles[comment.authorId],
                likeCount = commentLikes.size,
                isLikedByMe = commentLikes.any { it.userId == currentUserId }
            )
        }
    }

    override suspend fun addComment(postId: String, content: String, postAuthorId: String): Result<Unit> = safeApiCall {
        postgrest.from("comments").insert(
            mapOf(
                "post_id" to postId,
                "profile_id" to currentUserId,
                "content" to content
            )
        )
        // Notify post author
        if (postAuthorId != currentUserId) {
            postgrest.from("notifications").insert(
                mapOf(
                    "recipient_id" to postAuthorId,
                    "sender_id" to currentUserId,
                    "type" to "comment_post",
                    "target_url" to postId,
                    "message" to "đã bình luận về bài viết của bạn"
                )
            )
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit> = safeApiCall {
        // Delete associated likes first
        postgrest.from("likes").delete {
            filter {
                eq("target_id", commentId)
                eq("target_type", "comment")
            }
        }
        postgrest.from("comments").delete {
            filter { eq("id", commentId) }
        }
    }
}
