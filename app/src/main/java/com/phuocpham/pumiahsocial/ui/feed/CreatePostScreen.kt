package com.phuocpham.pumiahsocial.ui.feed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreatePostViewModel = hiltViewModel()
) {
    val contentText by viewModel.contentText.collectAsState()
    val linkUrl by viewModel.linkUrl.collectAsState()
    val isPosting by viewModel.isPosting.collectAsState()
    val postSuccess by viewModel.postSuccess.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val selectedImages by viewModel.selectedImages.collectAsState()

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val ts = System.currentTimeMillis()
            val items = uris.mapIndexedNotNull { idx, uri ->
                val bytes = context.contentResolver.openInputStream(uri)?.readBytes()
                bytes?.let {
                    SelectedImage(
                        bytes = it,
                        name = "post_${ts}_$idx.jpg",
                        uriString = uri.toString()
                    )
                }
            }
            viewModel.addImages(items)
        }
    }

    LaunchedEffect(postSuccess) {
        if (postSuccess) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tạo bài viết") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.createPost() },
                        enabled = !isPosting
                    ) {
                        if (isPosting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Đăng")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)
        ) {
            OutlinedTextField(
                value = contentText,
                onValueChange = { viewModel.updateContentText(it) },
                label = { Text("Bạn đang nghĩ gì?") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp),
                maxLines = 10
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = linkUrl,
                onValueChange = { viewModel.updateLinkUrl(it) },
                label = { Text("Liên kết (tùy chọn)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedImages.isNotEmpty()) {
                Text(
                    text = "Đã chọn ${selectedImages.size}/15 ảnh",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(selectedImages, key = { it.uriString }) { img ->
                        Box(modifier = Modifier.size(110.dp)) {
                            AsyncImage(
                                model = img.uriString,
                                contentDescription = "Ảnh đã chọn",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            IconButton(
                                onClick = { viewModel.removeImage(img.uriString) },
                                modifier = Modifier.align(Alignment.TopEnd).size(28.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Xóa ảnh",
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                enabled = selectedImages.size < 15,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedImages.isEmpty()) "Thêm ảnh (tối đa 15)" else "Thêm ảnh khác")
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
