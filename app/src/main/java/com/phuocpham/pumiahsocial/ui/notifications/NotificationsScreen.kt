package com.phuocpham.pumiahsocial.ui.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Xóa tất cả thông báo") },
            text = { Text("Bạn có chắc muốn xóa tất cả thông báo? Hành động này không thể hoàn tác.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteAllNotifications()
                }) { Text("Xóa", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Hủy") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Thông báo") },
                actions = {
                    IconButton(onClick = { viewModel.markAllAsRead() }) {
                        Icon(Icons.Default.DoneAll, "Đánh dấu tất cả đã đọc")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.DeleteSweep, "Xóa tất cả thông báo")
                    }
                }
            )
        }
    ) { padding ->
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refreshNotifications() },
            state = pullToRefreshState,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when (val state = notifications) {
                is UiState.Loading -> LoadingIndicator()
                is UiState.Error -> ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadNotifications() }
                )
                is UiState.Success -> {
                    if (state.data.isEmpty()) {
                        ErrorMessage(message = "Chưa có thông báo nào")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.data, key = { it.notification.id }) { item ->
                                NotificationItem(
                                    item = item,
                                    onClick = {
                                        viewModel.markAsRead(item.notification.id)
                                        when (item.notification.type) {
                                            "like_post", "comment_post" -> {
                                                item.notification.targetUrl?.let { onNavigateToPost(it) }
                                            }
                                            "friend_request", "friend_request_received" -> onNavigateToFriendRequests()
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
