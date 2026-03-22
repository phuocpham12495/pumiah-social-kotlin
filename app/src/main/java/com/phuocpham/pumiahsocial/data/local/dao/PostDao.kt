package com.phuocpham.pumiahsocial.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.phuocpham.pumiahsocial.data.local.entity.PostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PostDao {
    @Query("SELECT * FROM posts ORDER BY created_at DESC")
    fun getFeedPosts(): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE author_id = :userId ORDER BY created_at DESC")
    fun getUserPosts(userId: String): Flow<List<PostEntity>>

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPostById(postId: String): PostEntity?

    @Upsert
    suspend fun upsertPosts(posts: List<PostEntity>)

    @Upsert
    suspend fun upsertPost(post: PostEntity)

    @Query("DELETE FROM posts")
    suspend fun clearAll()
}
