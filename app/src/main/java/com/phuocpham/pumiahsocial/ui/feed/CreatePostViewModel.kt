package com.phuocpham.pumiahsocial.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.repository.PostsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _contentText = MutableStateFlow("")
    val contentText: StateFlow<String> = _contentText.asStateFlow()

    private val _linkUrl = MutableStateFlow("")
    val linkUrl: StateFlow<String> = _linkUrl.asStateFlow()

    private val _selectedImageBytes = MutableStateFlow<ByteArray?>(null)
    val selectedImageBytes: StateFlow<ByteArray?> = _selectedImageBytes.asStateFlow()

    private val _selectedImageName = MutableStateFlow<String?>(null)

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateContentText(value: String) { _contentText.value = value }
    fun updateLinkUrl(value: String) { _linkUrl.value = value }

    fun setSelectedImage(bytes: ByteArray, name: String) {
        _selectedImageBytes.value = bytes
        _selectedImageName.value = name
    }

    fun clearSelectedImage() {
        _selectedImageBytes.value = null
        _selectedImageName.value = null
    }

    fun createPost() {
        val text = _contentText.value.ifBlank { null }
        val link = _linkUrl.value.ifBlank { null }
        val imageBytes = _selectedImageBytes.value
        val imageName = _selectedImageName.value

        if (text == null && imageBytes == null && link == null) {
            _errorMessage.value = "Vui lòng nhập nội dung, ảnh hoặc liên kết"
            return
        }

        viewModelScope.launch {
            _isPosting.value = true
            _errorMessage.value = null
            postsRepository.createPost(text, imageBytes, imageName, link).fold(
                onSuccess = { _postSuccess.value = true },
                onFailure = { _errorMessage.value = it.message ?: "Lỗi đăng bài" }
            )
            _isPosting.value = false
        }
    }
}
