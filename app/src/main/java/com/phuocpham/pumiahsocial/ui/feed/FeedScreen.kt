package com.phuocpham.pumiahsocial.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.PostCard
import com.phuocpham.pumiahsocial.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onNavigateToCreatePost: () -> Unit,
    onNavigateToPostDetail: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,

    viewModel: FeedViewModel = hiltViewModel()
) {
    val feedPosts by viewModel.feedPosts.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pumiah Social") },
                scrollBehavior = scrollBehavior,
                actions = {}
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToCreatePost) {
                Icon(Icons.Default.Add, contentDescription = "Tạo bài viết")
            }
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshFeed() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = feedPosts) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadFeed() }
                )
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        ErrorMessage(message = "Chưa có bài viết nào. Hãy kết bạn để xem bảng tin!")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.data, key = { it.post.id }) { post ->
                                PostCard(
                                    post = post,
                                    onPostClick = { onNavigateToPostDetail(post.post.id) },
                                    onProfileClick = { onNavigateToProfile(post.post.authorId) },
                                    onLikeClick = { viewModel.toggleLike(post.post.id, post.post.authorId) },
                                    onCommentClick = { onNavigateToPostDetail(post.post.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
