package com.phuocpham.pumiahsocial.ui.friends

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsListScreen(
    onNavigateToProfile: (String) -> Unit,
    onNavigateToRequests: () -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val friendsList by viewModel.friendsList.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadFriends() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bạn bè") },
                actions = {
                    IconButton(onClick = onNavigateToRequests) {
                        Icon(Icons.Default.PersonAdd, "Lời mời kết bạn")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = friendsList) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadFriends() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    ErrorMessage(
                        message = "Bạn chưa có bạn bè nào",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(state.data, key = { it.id }) { friend ->
                            FriendListItem(
                                friend = friend,
                                onProfileClick = { onNavigateToProfile(friend.id) },
                                onRemoveFriend = { viewModel.removeFriend(friend.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendListItem(
    friend: Profile,
    onProfileClick: () -> Unit,
    onRemoveFriend: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    ListItem(
        headlineContent = { Text(friend.name ?: friend.username) },
        supportingContent = friend.bio?.let { { Text(it, maxLines = 1) } },
        leadingContent = {
            UserAvatar(avatarUrl = friend.avatarUrl, size = 48.dp)
        },
        trailingContent = {
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Default.MoreVert, "Tùy chọn")
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Hủy kết bạn") },
                        onClick = {
                            showMenu = false
                            onRemoveFriend()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.PersonRemove, null)
                        }
                    )
                }
            }
        },
        modifier = Modifier.clickable(onClick = onProfileClick)
    )
}
