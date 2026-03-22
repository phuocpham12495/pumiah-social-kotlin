package com.phuocpham.pumiahsocial.ui.feed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.CommentWithDetails
import com.phuocpham.pumiahsocial.data.model.PostWithDetails
import com.phuocpham.pumiahsocial.data.repository.InteractionsRepository
import com.phuocpham.pumiahsocial.data.repository.PostsRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostDetailViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val interactionsRepository: InteractionsRepository,
    private val authRepository: com.phuocpham.pumiahsocial.data.repository.AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val postId: String = savedStateHandle.get<String>("postId") ?: ""

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    private val _postState = MutableStateFlow<UiState<PostWithDetails>>(UiState.Loading)
    val postState: StateFlow<UiState<PostWithDetails>> = _postState.asStateFlow()

    private val _comments = MutableStateFlow<UiState<List<CommentWithDetails>>>(UiState.Loading)
    val comments: StateFlow<UiState<List<CommentWithDetails>>> = _comments.asStateFlow()

    private val _commentText = MutableStateFlow("")
    val commentText: StateFlow<String> = _commentText.asStateFlow()

    init {
        viewModelScope.launch {
            _currentUserId.value = authRepository.getCurrentUserId()
        }
        loadPost()
        loadComments()
    }

    fun loadPost() {
        viewModelScope.launch {
            _postState.value = UiState.Loading
            postsRepository.getPostDetail(postId).fold(
                onSuccess = { _postState.value = UiState.Success(it) },
                onFailure = { _postState.value = UiState.Error(it.message ?: "Lỗi tải bài viết") }
            )
        }
    }

    fun loadComments() {
        viewModelScope.launch {
            interactionsRepository.getCommentsForPost(postId).fold(
                onSuccess = { _comments.value = UiState.Success(it) },
                onFailure = { _comments.value = UiState.Error(it.message ?: "Lỗi tải bình luận") }
            )
        }
    }

    fun updateCommentText(value: String) { _commentText.value = value }

    fun addComment() {
        val content = _commentText.value.trim()
        if (content.isBlank()) return
        val postAuthorId = (_postState.value as? UiState.Success)?.data?.post?.authorId ?: return
        viewModelScope.launch {
            interactionsRepository.addComment(postId, content, postAuthorId).fold(
                onSuccess = {
                    _commentText.value = ""
                    loadComments()
                    loadPost() // refresh comment count
                },
                onFailure = { }
            )
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            interactionsRepository.deleteComment(commentId).fold(
                onSuccess = {
                    loadComments()
                    loadPost()
                },
                onFailure = { }
            )
        }
    }

    fun togglePostLike() {
        val post = (_postState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            interactionsRepository.togglePostLike(postId, post.post.authorId).fold(
                onSuccess = { isLiked ->
                    _postState.value = UiState.Success(
                        post.copy(
                            isLikedByMe = isLiked,
                            likeCount = if (isLiked) post.likeCount + 1 else post.likeCount - 1
                        )
                    )
                },
                onFailure = { }
            )
        }
    }

    fun toggleCommentLike(commentId: String) {
        viewModelScope.launch {
            interactionsRepository.toggleCommentLike(commentId).fold(
                onSuccess = { isLiked ->
                    val current = (_comments.value as? UiState.Success)?.data ?: return@fold
                    _comments.value = UiState.Success(
                        current.map { c ->
                            if (c.comment.id == commentId) {
                                c.copy(
                                    isLikedByMe = isLiked,
                                    likeCount = if (isLiked) c.likeCount + 1 else c.likeCount - 1
                                )
                            } else c
                        }
                    )
                },
                onFailure = { }
            )
        }
    }
}
