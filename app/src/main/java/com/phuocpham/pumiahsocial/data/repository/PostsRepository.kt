package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.model.Post
import com.phuocpham.pumiahsocial.data.model.PostWithDetails

interface PostsRepository {
    suspend fun getFeedPosts(): Result<List<PostWithDetails>>
    suspend fun getUserPosts(userId: String): Result<List<PostWithDetails>>
    suspend fun createPost(contentText: String?, imageBytes: ByteArray?, imageName: String?, linkUrl: String?): Result<Post>
    suspend fun createPostMulti(contentText: String?, images: List<Pair<ByteArray, String>>, linkUrl: String?): Result<Post>
    suspend fun getPostDetail(postId: String): Result<PostWithDetails>
}
