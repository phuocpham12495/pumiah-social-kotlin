package com.phuocpham.pumiahsocial.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileCheckViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _hasProfile = MutableStateFlow<Boolean?>(null)
    val hasProfile: StateFlow<Boolean?> = _hasProfile.asStateFlow()

    fun checkProfile(userId: String) {
        viewModelScope.launch {
            profileRepository.fetchProfile(userId).fold(
                onSuccess = { profile ->
                    _hasProfile.value = !profile.name.isNullOrBlank()
                },
                onFailure = {
                    // Profile doesn't exist or error — treat as no profile
                    _hasProfile.value = false
                }
            )
        }
    }
}
