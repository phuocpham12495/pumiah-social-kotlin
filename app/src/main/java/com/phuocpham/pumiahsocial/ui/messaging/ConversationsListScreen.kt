package com.phuocpham.pumiahsocial.ui.messaging

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState
import com.phuocpham.pumiahsocial.util.formatRelativeTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationsListScreen(
    onNavigateToChat: (String) -> Unit,
    viewModel: ConversationsViewModel = hiltViewModel()
) {
    val conversations by viewModel.conversations.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Tin nhắn") })
        }
    ) { padding ->
        when (val state = conversations) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadConversations() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    ErrorMessage(
                        message = "Chưa có cuộc hội thoại nào",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding)
                    ) {
                        items(state.data, key = { it.conversation.id }) { convo ->
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = convo.otherUserProfile?.name
                                            ?: convo.otherUserProfile?.username
                                            ?: "Người dùng",
                                        fontWeight = if (convo.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                supportingContent = {
                                    convo.lastMessage?.let { msg ->
                                        Text(
                                            text = msg.content,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontWeight = if (convo.unreadCount > 0) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                },
                                trailingContent = {
                                    Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                                        convo.lastMessage?.let {
                                            Text(
                                                formatRelativeTime(it.createdAt),
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                        if (convo.unreadCount > 0) {
                                            Badge { Text("${convo.unreadCount}") }
                                        }
                                    }
                                },
                                leadingContent = {
                                    UserAvatar(
                                        avatarUrl = convo.otherUserProfile?.avatarUrl,
                                        size = 48.dp
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onNavigateToChat(convo.conversation.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
