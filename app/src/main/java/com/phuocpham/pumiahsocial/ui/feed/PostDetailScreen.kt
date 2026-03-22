package com.phuocpham.pumiahsocial.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.phuocpham.pumiahsocial.ui.components.CommentItem
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState
import com.phuocpham.pumiahsocial.util.formatRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: PostDetailViewModel = hiltViewModel()
) {
    val postState by viewModel.postState.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val commentText by viewModel.commentText.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bài viết") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        },
        bottomBar = {
            Surface(tonalElevation = 3.dp) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { viewModel.updateCommentText(it) },
                        placeholder = { Text("Viết bình luận...") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { viewModel.addComment() }) {
                        Icon(Icons.AutoMirrored.Filled.Send, "Gửi", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    ) { padding ->
        when (val state = postState) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadPost() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                val post = state.data
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(12.dp)
                ) {
                    // Post header
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            UserAvatar(
                                avatarUrl = post.authorProfile?.avatarUrl,
                                size = 48.dp,
                                onClick = { onNavigateToProfile(post.post.authorId) }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = post.authorProfile?.name ?: "Người dùng",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = formatRelativeTime(post.post.createdAt),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Content
                    if (!post.post.content.isNullOrBlank()) {
                        item {
                            Text(post.post.content, style = MaterialTheme.typography.bodyLarge)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    if (!post.post.imageUrl.isNullOrBlank()) {
                        item {
                            AsyncImage(
                                model = post.post.imageUrl,
                                contentDescription = "Ảnh",
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Like button
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            TextButton(onClick = { viewModel.togglePostLike() }) {
                                Icon(
                                    imageVector = if (post.isLikedByMe) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Thích",
                                    tint = if (post.isLikedByMe) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${post.likeCount} lượt thích")
                            }
                        }
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Bình luận (${post.commentCount})", style = MaterialTheme.typography.titleSmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Comments
                    when (val commentsState = comments) {
                        is UiState.Success -> {
                            items(commentsState.data, key = { it.comment.id }) { comment ->
                                CommentItem(
                                    comment = comment,
                                    isOwnComment = comment.comment.authorId == currentUserId,
                                    onLikeClick = { viewModel.toggleCommentLike(comment.comment.id) },
                                    onDeleteClick = { viewModel.deleteComment(comment.comment.id) },
                                    onProfileClick = { onNavigateToProfile(comment.comment.authorId) }
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}
