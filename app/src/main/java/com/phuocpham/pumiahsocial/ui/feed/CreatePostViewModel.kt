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

data class SelectedImage(val bytes: ByteArray, val name: String, val uriString: String)

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postsRepository: PostsRepository
) : ViewModel() {

    private val _contentText = MutableStateFlow("")
    val contentText: StateFlow<String> = _contentText.asStateFlow()

    private val _linkUrl = MutableStateFlow("")
    val linkUrl: StateFlow<String> = _linkUrl.asStateFlow()

    private val _selectedImages = MutableStateFlow<List<SelectedImage>>(emptyList())
    val selectedImages: StateFlow<List<SelectedImage>> = _selectedImages.asStateFlow()

    private val _isPosting = MutableStateFlow(false)
    val isPosting: StateFlow<Boolean> = _isPosting.asStateFlow()

    private val _postSuccess = MutableStateFlow(false)
    val postSuccess: StateFlow<Boolean> = _postSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateContentText(value: String) { _contentText.value = value }
    fun updateLinkUrl(value: String) { _linkUrl.value = value }

    fun addImages(newImages: List<SelectedImage>) {
        val combined = (_selectedImages.value + newImages).take(15)
        _selectedImages.value = combined
        if ((_selectedImages.value + newImages).size > 15) {
            _errorMessage.value = "Tối đa 15 ảnh mỗi bài viết"
        }
    }

    fun removeImage(uriString: String) {
        _selectedImages.value = _selectedImages.value.filterNot { it.uriString == uriString }
    }

    fun clearImages() { _selectedImages.value = emptyList() }

    fun createPost() {
        val text = _contentText.value.ifBlank { null }
        val link = _linkUrl.value.ifBlank { null }
        val images = _selectedImages.value

        if (text == null && images.isEmpty() && link == null) {
            _errorMessage.value = "Vui lòng nhập nội dung, ảnh hoặc liên kết"
            return
        }

        viewModelScope.launch {
            _isPosting.value = true
            _errorMessage.value = null
            postsRepository.createPostMulti(
                contentText = text,
                images = images.map { it.bytes to it.name },
                linkUrl = link
            ).fold(
                onSuccess = { _postSuccess.value = true },
                onFailure = { _errorMessage.value = it.message ?: "Lỗi đăng bài" }
            )
            _isPosting.value = false
        }
    }
}
