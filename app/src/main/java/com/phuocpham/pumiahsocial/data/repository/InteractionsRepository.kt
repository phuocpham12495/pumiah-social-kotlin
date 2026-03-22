package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.CommentWithDetails

interface InteractionsRepository {
    suspend fun togglePostLike(postId: String, authorId: String): Result<Boolean>
    suspend fun toggleCommentLike(commentId: String): Result<Boolean>
    suspend fun getCommentsForPost(postId: String): Result<List<CommentWithDetails>>
    suspend fun addComment(postId: String, content: String, postAuthorId: String): Result<Unit>
    suspend fun deleteComment(commentId: String): Result<Unit>
}
