package com.phuocpham.pumiahsocial.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.data.model.NotificationWithProfile
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState
import com.phuocpham.pumiahsocial.util.formatRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    onNavigateToPost: (String) -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToFriendRequests: () -> Unit,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.DoneAll, "Đánh dấu tất cả đã đọc")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = notifications) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadNotifications() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    ErrorMessage(
                        message = "Chưa có thông báo nào",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding)
                    ) {
                        items(state.data, key = { it.notification.id }) { item ->
                            NotificationItem(
                                item = item,
                                onClick = {
                                    viewModel.markAsRead(item.notification.id)
                                    when (item.notification.type) {
                                        "like_post", "comment_post" -> {
                                            item.notification.targetUrl?.let { onNavigateToPost(it) }
                                        }
                                        "friend_request" -> onNavigateToFriendRequests()
                                        else -> item.notification.senderId?.let { onNavigateToProfile(it) }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    item: NotificationWithProfile,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = {
            Text(
                text = "${item.senderProfile?.name ?: "Ai đó"} ${item.notification.message ?: ""}",
                fontWeight = if (!item.notification.isRead) FontWeight.Bold else FontWeight.Normal
            )
        },
        supportingContent = {
            Text(formatRelativeTime(item.notification.createdAt))
        },
        leadingContent = {
            UserAvatar(avatarUrl = item.senderProfile?.avatarUrl, size = 40.dp)
        },
        modifier = Modifier.clickable(onClick = onClick),
        colors = ListItemDefaults.colors(
            containerColor = if (!item.notification.isRead)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        )
    )
}
