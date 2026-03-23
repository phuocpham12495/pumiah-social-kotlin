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
                onFailure = { _errorMessage.value = toFriendlyError(it) }
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
                onFailure = { _errorMessage.value = toFriendlyError(it) }
            )
            _isLoading.value = false
        }
    }

    private fun toFriendlyError(error: Throwable): String {
        val msg = error.message?.lowercase() ?: return "Đã xảy ra lỗi, vui lòng thử lại"
        return when {
            "invalid_credentials" in msg || "invalid login" in msg ->
                "Email hoặc mật khẩu không đúng"
            "email_not_confirmed" in msg ->
                "Email chưa được xác nhận, vui lòng kiểm tra hộp thư"
            "user_already_exists" in msg || "already registered" in msg ->
                "Email này đã được đăng ký"
            "over_email_send_rate_limit" in msg || "rate limit" in msg ->
                "Gửi quá nhiều yêu cầu, vui lòng thử lại sau vài phút"
            "weak_password" in msg || "password" in msg && "short" in msg ->
                "Mật khẩu quá yếu, vui lòng chọn mật khẩu mạnh hơn"
            "network" in msg || "timeout" in msg || "connect" in msg ->
                "Không thể kết nối, vui lòng kiểm tra mạng"
            else -> "Đã xảy ra lỗi, vui lòng thử lại"
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
