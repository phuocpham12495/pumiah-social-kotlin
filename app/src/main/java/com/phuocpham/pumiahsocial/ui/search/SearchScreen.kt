package com.phuocpham.pumiahsocial.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.phuocpham.pumiahsocial.ui.components.UserAvatar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { viewModel.updateQuery(it) },
                        placeholder = { Text("Tìm kiếm người dùng...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Search, null) }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (isSearching) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.TopCenter).padding(16.dp))
            } else if (results.isEmpty() && query.length >= 2) {
                Text(
                    "Không tìm thấy kết quả",
                    modifier = Modifier.align(Alignment.TopCenter).padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(8.dp)) {
                    items(results, key = { it.id }) { profile ->
                        ListItem(
                            headlineContent = { Text(profile.name ?: profile.username) },
                            supportingContent = profile.bio?.let { { Text(it, maxLines = 1) } },
                            leadingContent = {
                                UserAvatar(avatarUrl = profile.avatarUrl, size = 48.dp)
                            },
                            modifier = Modifier.clickable { onNavigateToProfile(profile.id) }
                        )
                    }
                }
            }
        }
    }
}
