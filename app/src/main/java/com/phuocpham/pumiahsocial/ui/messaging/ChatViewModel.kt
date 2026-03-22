package com.phuocpham.pumiahsocial.ui.messaging

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.Message
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.MessagesRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messagesRepository: MessagesRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val conversationId: String = savedStateHandle.get<String>("conversationId") ?: ""

    private val _messages = MutableStateFlow<UiState<List<Message>>>(UiState.Loading)
    val messages: StateFlow<UiState<List<Message>>> = _messages.asStateFlow()

    private val _messageText = MutableStateFlow("")
    val messageText: StateFlow<String> = _messageText.asStateFlow()

    private val _currentUserId = MutableStateFlow("")
    val currentUserId: StateFlow<String> = _currentUserId.asStateFlow()

    init {
        viewModelScope.launch {
            _currentUserId.value = authRepository.getCurrentUserId() ?: ""
        }
        loadMessages()
        startPolling()
    }

    fun loadMessages() {
        viewModelScope.launch {
            messagesRepository.getMessages(conversationId).fold(
                onSuccess = { _messages.value = UiState.Success(it) },
                onFailure = { _messages.value = UiState.Error(it.message ?: "Lỗi tải tin nhắn") }
            )
            messagesRepository.markConversationRead(conversationId)
        }
    }

    fun updateMessageText(value: String) { _messageText.value = value }

    fun sendMessage() {
        val content = _messageText.value.trim()
        if (content.isBlank()) return
        viewModelScope.launch {
            _messageText.value = ""
            messagesRepository.sendMessage(conversationId, content).fold(
                onSuccess = { loadMessages() },
                onFailure = { _messageText.value = content }
            )
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
                delay(3000)
                messagesRepository.getMessages(conversationId).fold(
                    onSuccess = { _messages.value = UiState.Success(it) },
                    onFailure = { }
                )
            }
        }
    }
}
