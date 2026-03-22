package com.phuocpham.pumiahsocial.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class FeedViewModel @Inject constructor(
    private val postsRepository: PostsRepository,
    private val interactionsRepository: InteractionsRepository
) : ViewModel() {

    private val _feedPosts = MutableStateFlow<UiState<List<PostWithDetails>>>(UiState.Loading)
    val feedPosts: StateFlow<UiState<List<PostWithDetails>>> = _feedPosts.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        viewModelScope.launch {
            _feedPosts.value = UiState.Loading
            postsRepository.getFeedPosts().fold(
                onSuccess = { _feedPosts.value = UiState.Success(it) },
                onFailure = { _feedPosts.value = UiState.Error(it.message ?: "Lỗi tải bảng tin") }
            )
        }
    }

    fun refreshFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            postsRepository.getFeedPosts().fold(
                onSuccess = { _feedPosts.value = UiState.Success(it) },
                onFailure = { }
            )
            _isRefreshing.value = false
        }
    }

    fun toggleLike(postId: String, authorId: String) {
        viewModelScope.launch {
            interactionsRepository.togglePostLike(postId, authorId).fold(
                onSuccess = { isLiked ->
                    val current = (_feedPosts.value as? UiState.Success)?.data ?: return@fold
                    _feedPosts.value = UiState.Success(
                        current.map { post ->
                            if (post.post.id == postId) {
                                post.copy(
                                    isLikedByMe = isLiked,
                                    likeCount = if (isLiked) post.likeCount + 1 else post.likeCount - 1
                                )
                            } else post
                        }
                    )
                },
                onFailure = { }
            )
        }
    }
}
