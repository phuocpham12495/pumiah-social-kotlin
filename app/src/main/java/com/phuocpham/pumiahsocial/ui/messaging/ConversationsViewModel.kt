package com.phuocpham.pumiahsocial.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.ConversationWithDetails
import com.phuocpham.pumiahsocial.data.repository.MessagesRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationsViewModel @Inject constructor(
    private val messagesRepository: MessagesRepository
) : ViewModel() {

    private val _conversations = MutableStateFlow<UiState<List<ConversationWithDetails>>>(UiState.Loading)
    val conversations: StateFlow<UiState<List<ConversationWithDetails>>> = _conversations.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadConversations()
    }

    fun loadConversations() {
        viewModelScope.launch {
            _conversations.value = UiState.Loading
            messagesRepository.getConversations().fold(
                onSuccess = { _conversations.value = UiState.Success(it) },
                onFailure = { _conversations.value = UiState.Error(it.message ?: "Lỗi tải hội thoại") }
            )
        }
    }

    fun refreshConversations() {
        viewModelScope.launch {
            _isRefreshing.value = true
            messagesRepository.getConversations().fold(
                onSuccess = { _conversations.value = UiState.Success(it) },
                onFailure = { }
            )
            _isRefreshing.value = false
        }
    }
}
