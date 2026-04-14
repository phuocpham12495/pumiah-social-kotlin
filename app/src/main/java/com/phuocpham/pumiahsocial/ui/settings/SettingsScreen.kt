package com.phuocpham.pumiahsocial.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val profile by viewModel.profile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val notifyLikes by viewModel.notifyLikes.collectAsState()
    val notifyComments by viewModel.notifyComments.collectAsState()
    val notifyFriendRequests by viewModel.notifyFriendRequests.collectAsState()
    val notifyMessages by viewModel.notifyMessages.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cài đặt") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Profile section
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(16.dp)) {
                    UserAvatar(avatarUrl = profile?.avatarUrl, size = 56.dp)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = profile?.name ?: profile?.username ?: "Người dùng",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "@${profile?.username ?: ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account section
            Text("Tài khoản", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Hồ sơ") },
                leadingContent = { Icon(Icons.Default.Person, null) }
            )
            ListItem(
                headlineContent = { Text("Tên người dùng") },
                supportingContent = { Text("@${profile?.username ?: ""}") },
                leadingContent = { Icon(Icons.Default.Email, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Appearance section
            Text("Giao diện", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Chế độ tối") },
                supportingContent = { Text(if (isDarkMode) "Đang bật" else "Đang tắt") },
                leadingContent = {
                    Icon(
                        if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Notifications section
            Text("Thông báo", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Lượt thích") },
                supportingContent = { Text("Thông báo khi có người thích bài viết") },
                leadingContent = { Icon(Icons.Default.Favorite, null) },
                trailingContent = {
                    Switch(
                        checked = notifyLikes,
                        onCheckedChange = { viewModel.toggleNotifyLikes(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Bình luận") },
                supportingContent = { Text("Thông báo khi có bình luận mới") },
                leadingContent = { Icon(Icons.Default.ChatBubble, null) },
                trailingContent = {
                    Switch(
                        checked = notifyComments,
                        onCheckedChange = { viewModel.toggleNotifyComments(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Lời mời kết bạn") },
                supportingContent = { Text("Thông báo khi nhận lời mời kết bạn") },
                leadingContent = { Icon(Icons.Default.PersonAdd, null) },
                trailingContent = {
                    Switch(
                        checked = notifyFriendRequests,
                        onCheckedChange = { viewModel.toggleNotifyFriendRequests(it) }
                    )
                }
            )
            ListItem(
                headlineContent = { Text("Tin nhắn") },
                supportingContent = { Text("Thông báo khi nhận tin nhắn mới") },
                leadingContent = { Icon(Icons.Default.Chat, null) },
                trailingContent = {
                    Switch(
                        checked = notifyMessages,
                        onCheckedChange = { viewModel.toggleNotifyMessages(it) }
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // App section
            Text("Ứng dụng", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))

            ListItem(
                headlineContent = { Text("Phiên bản") },
                supportingContent = { Text("1.0") },
                leadingContent = { Icon(Icons.Default.Info, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Logout
            Button(
                onClick = { viewModel.logout(onLogout) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
