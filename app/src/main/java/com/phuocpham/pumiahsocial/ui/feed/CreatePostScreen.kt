package com.phuocpham.pumiahsocial.ui.feed

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val selectedImageBytes by viewModel.selectedImageBytes.collectAsState()

    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            val bytes = context.contentResolver.openInputStream(it)?.readBytes()
            if (bytes != null) {
                val fileName = "post_${System.currentTimeMillis()}.jpg"
                viewModel.setSelectedImage(bytes, fileName)
            }
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

            if (selectedImageBytes != null && selectedImageUri != null) {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Box {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Ảnh đã chọn",
                            modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                viewModel.clearSelectedImage()
                                selectedImageUri = null
                            },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Xóa ảnh",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (selectedImageBytes != null) "Đổi ảnh" else "Thêm ảnh")
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
