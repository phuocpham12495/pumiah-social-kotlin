package com.phuocpham.pumiahsocial.data.repository

import com.phuocpham.pumiahsocial.data.local.dao.PostDao
import com.phuocpham.pumiahsocial.data.local.entity.PostEntity
import com.phuocpham.pumiahsocial.data.model.Comment
import com.phuocpham.pumiahsocial.data.model.Friend
import com.phuocpham.pumiahsocial.data.model.Like
import com.phuocpham.pumiahsocial.data.model.Post
import com.phuocpham.pumiahsocial.data.model.PostWithDetails
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.util.safeApiCall
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val auth: Auth,
    private val postDao: PostDao
) : PostsRepository {

    private val currentUserId: String
        get() = auth.currentUserOrNull()?.id ?: ""

    override suspend fun getFeedPosts(): Result<List<PostWithDetails>> = safeApiCall {
        // Get friend IDs
        val friends1 = postgrest.from("friendships")
            .select { filter { eq("user1_id", currentUserId) } }
            .decodeList<Friend>()
        val friends2 = postgrest.from("friendships")
            .select { filter { eq("user2_id", currentUserId) } }
            .decodeList<Friend>()

        val friendIds = friends1.map { it.user2Id } + friends2.map { it.user1Id } + currentUserId

        if (friendIds.isEmpty()) return@safeApiCall emptyList()

        val posts = postgrest.from("posts")
            .select {
                filter { isIn("profile_id", friendIds) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                limit(50)
            }
            .decodeList<Post>()

        // Cache posts
        postDao.upsertPosts(posts.map { PostEntity.fromPost(it) })

        enrichPosts(posts)
    }

    override suspend fun getUserPosts(userId: String): Result<List<PostWithDetails>> = safeApiCall {
        val posts = postgrest.from("posts")
            .select {
                filter { eq("profile_id", userId) }
                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            }
            .decodeList<Post>()

        enrichPosts(posts)
    }

    override suspend fun createPost(
        contentText: String?,
        imageBytes: ByteArray?,
        imageName: String?,
        linkUrl: String?
    ): Result<Post> = safeApiCall {
        var imageUrl: String? = null
        if (imageBytes != null && imageName != null) {
            val path = "$currentUserId/$imageName"
            storage.from("post_images").upload(path, imageBytes) { upsert = true }
            imageUrl = storage.from("post_images").publicUrl(path)
        }

        val postType = when {
            imageUrl != null -> "image"
            linkUrl != null -> "link"
            else -> "text"
        }
        val post = Post(
            authorId = currentUserId,
            postType = postType,
            content = contentText,
            imageUrl = imageUrl,
            linkUrl = linkUrl
        )

        postgrest.from("posts").insert(post) {
            select()
        }.decodeSingle<Post>()
    }

    override suspend fun createPostMulti(
        contentText: String?,
        images: List<Pair<ByteArray, String>>,
        linkUrl: String?
    ): Result<Post> = safeApiCall {
        val limited = images.take(15)
        val urls = limited.map { (bytes, name) ->
            val path = "$currentUserId/$name"
            storage.from("post_images").upload(path, bytes) { upsert = true }
            storage.from("post_images").publicUrl(path)
        }
        val joined = urls.joinToString("\n").ifBlank { null }
        val postType = when {
            joined != null -> "image"
            linkUrl != null -> "link"
            else -> "text"
        }
        val post = Post(
            authorId = currentUserId,
            postType = postType,
            content = contentText,
            imageUrl = joined,
            linkUrl = linkUrl
        )
        postgrest.from("posts").insert(post) { select() }.decodeSingle<Post>()
    }

    override suspend fun getPostDetail(postId: String): Result<PostWithDetails> = safeApiCall {
        val post = postgrest.from("posts")
            .select { filter { eq("id", postId) } }
            .decodeSingle<Post>()

        enrichPosts(listOf(post)).first()
    }

    private suspend fun enrichPosts(posts: List<Post>): List<PostWithDetails> {
        if (posts.isEmpty()) return emptyList()

        val authorIds = posts.map { it.authorId }.distinct()
        val profiles = postgrest.from("profiles")
            .select { filter { isIn("id", authorIds) } }
            .decodeList<Profile>()
            .associateBy { it.id }

        val postIds = posts.map { it.id }
        val likes = postgrest.from("likes")
            .select {
                filter {
                    isIn("target_id", postIds)
                    eq("target_type", "post")
                }
            }
            .decodeList<Like>()

        val comments = postgrest.from("comments")
            .select { filter { isIn("post_id", postIds) } }
            .decodeList<Comment>()

        return posts.map { post ->
            val postLikes = likes.filter { it.targetId == post.id }
            val postComments = comments.filter { it.postId == post.id }
            PostWithDetails(
                post = post,
                authorProfile = profiles[post.authorId],
                likeCount = postLikes.size,
                commentCount = postComments.size,
                isLikedByMe = postLikes.any { it.userId == currentUserId }
            )
        }
    }
}
