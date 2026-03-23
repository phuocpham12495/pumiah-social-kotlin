package com.phuocpham.pumiahsocial.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.phuocpham.pumiahsocial.data.model.FriendshipStatus
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.PostCard
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState
import com.phuocpham.pumiahsocial.util.formatDate
import androidx.compose.material.icons.filled.ChatBubbleOutline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String?,
    onNavigateToEditProfile: (() -> Unit)?,
    onNavigateToFriends: () -> Unit,
    onNavigateToUserProfile: (String) -> Unit,
    onNavigateToSettings: (() -> Unit)?,
    onNavigateToChat: ((String) -> Unit)? = null,
    onNavigateBack: (() -> Unit)?,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val friendshipStatus by viewModel.friendshipStatus.collectAsState()
    val userPosts by viewModel.userPosts.collectAsState()
    val friendCount by viewModel.friendCount.collectAsState()
    val isOwnProfile by viewModel.isOwnProfile.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ") },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                        }
                    }
                },
                actions = {
                    if (isOwnProfile && onNavigateToSettings != null) {
                        IconButton(onClick = onNavigateToSettings) {
                            Icon(Icons.Default.Settings, "Cài đặt")
                        }
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshProfile() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = profileState) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadProfile(userId) }
                )
                is UiState.Success -> {
                    val profile = state.data
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                ) {
                    // Cover photo
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(180.dp)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                        ) {
                            if (!profile.coverPhotoUrl.isNullOrBlank()) {
                                AsyncImage(
                                    model = profile.coverPhotoUrl,
                                    contentDescription = "Ảnh bìa",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    // Avatar + Info
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.offset(y = (-40).dp)) {
                                UserAvatar(
                                    avatarUrl = profile.avatarUrl,
                                    size = 80.dp
                                )
                            }
                            Text(
                                text = profile.name ?: profile.username,
                                style = MaterialTheme.typography.headlineSmall,
                                modifier = Modifier.offset(y = (-32).dp)
                            )
                            if (!profile.bio.isNullOrBlank()) {
                                Text(
                                    text = profile.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.offset(y = (-28).dp)
                                )
                            }

                            Row(
                                modifier = Modifier.offset(y = (-20).dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                if (!profile.location.isNullOrBlank()) {
                                    Text(
                                        text = profile.location,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = "Tham gia: ${formatDate(profile.joinDate)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Friend count + action buttons
                            Row(
                                modifier = Modifier.offset(y = (-12).dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = onNavigateToFriends) {
                                    Text("$friendCount bạn bè")
                                }

                                if (isOwnProfile && onNavigateToEditProfile != null) {
                                    OutlinedButton(onClick = onNavigateToEditProfile) {
                                        Icon(Icons.Default.Edit, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Chỉnh sửa")
                                    }
                                } else {
                                    when (friendshipStatus) {
                                        FriendshipStatus.NONE -> {
                                            Button(onClick = { viewModel.sendFriendRequest() }) {
                                                Icon(Icons.Default.PersonAdd, null, Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Kết bạn")
                                            }
                                        }
                                        FriendshipStatus.PENDING_SENT -> {
                                            OutlinedButton(onClick = {}, enabled = false) {
                                                Text("Đã gửi lời mời")
                                            }
                                        }
                                        FriendshipStatus.PENDING_RECEIVED -> {
                                            OutlinedButton(onClick = {}) {
                                                Text("Phản hồi lời mời")
                                            }
                                        }
                                        FriendshipStatus.FRIENDS -> {
                                            if (onNavigateToChat != null) {
                                                OutlinedButton(onClick = {
                                                    viewModel.startConversation(onNavigateToChat)
                                                }) {
                                                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(16.dp))
                                                    Spacer(Modifier.width(4.dp))
                                                    Text("Nhắn tin")
                                                }
                                            }
                                            OutlinedButton(onClick = { viewModel.removeFriend() }) {
                                                Icon(Icons.Default.PersonRemove, null, Modifier.size(16.dp))
                                                Spacer(Modifier.width(4.dp))
                                                Text("Hủy kết bạn")
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Bài viết", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // User posts
                    when (val postsState = userPosts) {
                        is UiState.Success -> {
                            if (postsState.data.isEmpty()) {
                                item {
                                    Text(
                                        "Chưa có bài viết nào",
                                        modifier = Modifier.padding(16.dp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            } else {
                                items(postsState.data, key = { it.post.id }) { post ->
                                    PostCard(
                                        post = post,
                                        onPostClick = {},
                                        onProfileClick = {},
                                        onLikeClick = {},
                                        onCommentClick = {},
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                        is UiState.Loading -> item { LoadingIndicator() }
                        is UiState.Error -> item {
                            Text(postsState.message, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            }
        }
    }
}
