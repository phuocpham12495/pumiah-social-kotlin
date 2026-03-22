package com.phuocpham.pumiahsocial.ui.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.ErrorMessage
import com.phuocpham.pumiahsocial.ui.components.LoadingIndicator
import com.phuocpham.pumiahsocial.ui.components.UserAvatar
import com.phuocpham.pumiahsocial.util.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: FriendsViewModel = hiltViewModel()
) {
    val pendingRequests by viewModel.pendingRequests.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadPendingRequests() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lời mời kết bạn") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        when (val state = pendingRequests) {
            is UiState.Loading -> LoadingIndicator(modifier = Modifier.padding(padding))
            is UiState.Error -> ErrorMessage(
                message = state.message,
                onRetry = { viewModel.loadPendingRequests() },
                modifier = Modifier.padding(padding)
            )
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    ErrorMessage(
                        message = "Không có lời mời kết bạn nào",
                        modifier = Modifier.padding(padding)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        items(state.data, key = { it.request.id }) { request ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    UserAvatar(
                                        avatarUrl = request.senderProfile?.avatarUrl,
                                        size = 48.dp,
                                        onClick = { onNavigateToProfile(request.request.senderId) }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = request.senderProfile?.name
                                                ?: request.senderProfile?.username
                                                ?: "Người dùng",
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            Button(
                                                onClick = {
                                                    viewModel.acceptRequest(
                                                        request.request.id,
                                                        request.request.senderId
                                                    )
                                                },
                                                modifier = Modifier.weight(1f)
                                            ) { Text("Chấp nhận") }
                                            OutlinedButton(
                                                onClick = { viewModel.declineRequest(request.request.id) },
                                                modifier = Modifier.weight(1f)
                                            ) { Text("Từ chối") }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
