package com.phuocpham.pumiahsocial.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.NotificationWithProfile
import com.phuocpham.pumiahsocial.data.repository.NotificationsRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationsRepository: NotificationsRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<UiState<List<NotificationWithProfile>>>(UiState.Loading)
    val notifications: StateFlow<UiState<List<NotificationWithProfile>>> = _notifications.asStateFlow()

    val unreadCount: StateFlow<Int> = notificationsRepository.getUnreadCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _notifications.value = UiState.Loading
            notificationsRepository.getNotifications().fold(
                onSuccess = { _notifications.value = UiState.Success(it) },
                onFailure = { _notifications.value = UiState.Error(it.message ?: "Lỗi tải thông báo") }
            )
        }
    }

    fun refreshNotifications() {
        viewModelScope.launch {
            _isRefreshing.value = true
            notificationsRepository.getNotifications().fold(
                onSuccess = { _notifications.value = UiState.Success(it) },
                onFailure = { }
            )
            notificationsRepository.refreshUnreadCount()
            _isRefreshing.value = false
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            notificationsRepository.markAsRead(notificationId)
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationsRepository.markAllAsRead().fold(
                onSuccess = { loadNotifications() },
                onFailure = { }
            )
        }
    }

    fun deleteAllNotifications() {
        viewModelScope.launch {
            notificationsRepository.deleteAllNotifications().fold(
                onSuccess = { _notifications.value = UiState.Success(emptyList()) },
                onFailure = { }
            )
        }
    }
}
