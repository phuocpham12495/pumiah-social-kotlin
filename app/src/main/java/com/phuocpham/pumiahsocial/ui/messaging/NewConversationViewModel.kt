package com.phuocpham.pumiahsocial.ui.messaging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.FriendsRepository
import com.phuocpham.pumiahsocial.data.repository.MessagesRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewConversationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val friendsRepository: FriendsRepository,
    private val messagesRepository: MessagesRepository
) : ViewModel() {

    private val _friends = MutableStateFlow<UiState<List<Profile>>>(UiState.Loading)
    val friends: StateFlow<UiState<List<Profile>>> = _friends.asStateFlow()

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _friends.value = UiState.Loading
            val userId = authRepository.getCurrentUserId() ?: return@launch
            friendsRepository.getFriendsList(userId).fold(
                onSuccess = { _friends.value = UiState.Success(it) },
                onFailure = { _friends.value = UiState.Error(it.message ?: "Lỗi tải danh sách bạn bè") }
            )
        }
    }

    fun startConversation(otherUserId: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            messagesRepository.getOrCreateConversation(otherUserId).fold(
                onSuccess = { onSuccess(it) },
                onFailure = { }
            )
        }
    }
}
