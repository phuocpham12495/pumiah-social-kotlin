package com.phuocpham.pumiahsocial.ui.messaging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewConversationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToChat: (String) -> Unit,
    viewModel: NewConversationViewModel = hiltViewModel()
) {
    val friends by viewModel.friends.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tin nhắn mới") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = friends) {
            is UiState.Loading -> LoadingIndicator()
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadFriends() }
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    ErrorMessage(message = "Chưa có bạn bè nào. Hãy kết bạn trước!")
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(state.data, key = { it.id }) { profile ->
                            ListItem(
                                headlineContent = {
                                    Text(profile.name ?: profile.username)
                                },
                                supportingContent = {
                                    Text("@${profile.username}")
                                },
                                leadingContent = {
                                    UserAvatar(avatarUrl = profile.avatarUrl, size = 48.dp)
                                },
                                modifier = Modifier.clickable {
                                    viewModel.startConversation(profile.id) { conversationId ->
                                        onNavigateToChat(conversationId)
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
