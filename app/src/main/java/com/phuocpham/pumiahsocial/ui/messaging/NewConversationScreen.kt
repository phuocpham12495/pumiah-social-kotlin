package com.phuocpham.pumiahsocial.ui.messaging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
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
    var searchQuery by remember { mutableStateOf("") }

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
                    val filteredFriends = remember(searchQuery, state.data) {
                        if (searchQuery.isBlank()) state.data
                        else state.data.filter {
                            (it.name ?: "").contains(searchQuery, ignoreCase = true) ||
                                it.username.contains(searchQuery, ignoreCase = true)
                        }
                    }
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        item {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Tìm bạn bè...") },
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = TextFieldDefaults.colors(
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent
                                )
                            )
                        }
                        items(filteredFriends, key = { it.id }) { profile ->
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
