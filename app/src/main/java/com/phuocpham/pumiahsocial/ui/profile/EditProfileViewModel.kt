package com.phuocpham.pumiahsocial.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _location = MutableStateFlow("")
    val location: StateFlow<String> = _location.asStateFlow()

    private val _avatarUrl = MutableStateFlow<String?>(null)
    val avatarUrl: StateFlow<String?> = _avatarUrl.asStateFlow()

    private val _coverPhotoUrl = MutableStateFlow<String?>(null)
    val coverPhotoUrl: StateFlow<String?> = _coverPhotoUrl.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var currentProfile: Profile? = null

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            profileRepository.fetchProfile(userId).fold(
                onSuccess = { profile ->
                    currentProfile = profile
                    _name.value = profile.name ?: ""
                    _bio.value = profile.bio ?: ""
                    _location.value = profile.location ?: ""
                    _avatarUrl.value = profile.avatarUrl
                    _coverPhotoUrl.value = profile.coverPhotoUrl
                },
                onFailure = { _errorMessage.value = it.message }
            )
        }
    }

    fun updateName(value: String) { _name.value = value }
    fun updateBio(value: String) { _bio.value = value }
    fun updateLocation(value: String) { _location.value = value }

    fun uploadAvatar(imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            profileRepository.uploadAvatar(userId, imageBytes, fileName).fold(
                onSuccess = { url -> _avatarUrl.value = url },
                onFailure = { _errorMessage.value = "Lỗi tải ảnh đại diện" }
            )
        }
    }

    fun uploadCoverPhoto(imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            profileRepository.uploadCoverPhoto(userId, imageBytes, fileName).fold(
                onSuccess = { url -> _coverPhotoUrl.value = url },
                onFailure = { _errorMessage.value = "Lỗi tải ảnh bìa" }
            )
        }
    }

    fun saveProfile() {
        val profile = currentProfile ?: return
        viewModelScope.launch {
            _isSaving.value = true
            _errorMessage.value = null
            val updated = profile.copy(
                name = _name.value.ifBlank { null },
                bio = _bio.value.ifBlank { null },
                location = _location.value.ifBlank { null },
                avatarUrl = _avatarUrl.value,
                coverPhotoUrl = _coverPhotoUrl.value
            )
            profileRepository.updateProfile(updated).fold(
                onSuccess = { _saveSuccess.value = true },
                onFailure = { _errorMessage.value = it.message ?: "Lỗi lưu hồ sơ" }
            )
            _isSaving.value = false
        }
    }
}
