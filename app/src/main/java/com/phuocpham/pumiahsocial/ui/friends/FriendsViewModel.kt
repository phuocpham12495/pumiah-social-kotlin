package com.phuocpham.pumiahsocial.ui.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.FriendRequestWithProfile
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.FriendsRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val friendsRepository: FriendsRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _friendsList = MutableStateFlow<UiState<List<Profile>>>(UiState.Loading)
    val friendsList: StateFlow<UiState<List<Profile>>> = _friendsList.asStateFlow()

    private val _pendingRequests = MutableStateFlow<UiState<List<FriendRequestWithProfile>>>(UiState.Loading)
    val pendingRequests: StateFlow<UiState<List<FriendRequestWithProfile>>> = _pendingRequests.asStateFlow()

    fun loadFriends() {
        viewModelScope.launch {
            _friendsList.value = UiState.Loading
            val userId = authRepository.getCurrentUserId() ?: return@launch
            friendsRepository.getFriendsList(userId).fold(
                onSuccess = { _friendsList.value = UiState.Success(it) },
                onFailure = { _friendsList.value = UiState.Error(it.message ?: "Lỗi tải danh sách bạn bè") }
            )
        }
    }

    fun loadPendingRequests() {
        viewModelScope.launch {
            _pendingRequests.value = UiState.Loading
            friendsRepository.getPendingRequests().fold(
                onSuccess = { _pendingRequests.value = UiState.Success(it) },
                onFailure = { _pendingRequests.value = UiState.Error(it.message ?: "Lỗi tải lời mời") }
            )
        }
    }

    fun acceptRequest(requestId: String, senderId: String) {
        viewModelScope.launch {
            friendsRepository.acceptFriendRequest(requestId, senderId).fold(
                onSuccess = {
                    loadPendingRequests()
                    loadFriends()
                },
                onFailure = { }
            )
        }
    }

    fun declineRequest(requestId: String) {
        viewModelScope.launch {
            friendsRepository.declineFriendRequest(requestId).fold(
                onSuccess = { loadPendingRequests() },
                onFailure = { }
            )
        }
    }

    fun removeFriend(friendUserId: String) {
        viewModelScope.launch {
            friendsRepository.removeFriend(friendUserId).fold(
                onSuccess = { loadFriends() },
                onFailure = { }
            )
        }
    }
}
