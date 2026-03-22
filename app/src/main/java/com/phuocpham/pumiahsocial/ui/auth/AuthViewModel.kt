package com.phuocpham.pumiahsocial.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.AuthState
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    val authState: StateFlow<AuthState> = authRepository.observeAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Loading)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập email và mật khẩu"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.signIn(email, password).fold(
                onSuccess = { /* Auth state will update automatically */ },
                onFailure = { _errorMessage.value = it.message ?: "Đăng nhập thất bại" }
            )
            _isLoading.value = false
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Vui lòng nhập email và mật khẩu"
            return
        }
        if (password != confirmPassword) {
            _errorMessage.value = "Mật khẩu xác nhận không khớp"
            return
        }
        if (password.length < 6) {
            _errorMessage.value = "Mật khẩu phải có ít nhất 6 ký tự"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            authRepository.signUp(email, password).fold(
                onSuccess = { /* Auth state will update automatically */ },
                onFailure = { _errorMessage.value = it.message ?: "Đăng ký thất bại" }
            )
            _isLoading.value = false
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
