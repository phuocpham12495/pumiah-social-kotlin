package com.phuocpham.pumiahsocial.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.FriendshipStatus
import com.phuocpham.pumiahsocial.data.model.PostWithDetails
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.FriendsRepository
import com.phuocpham.pumiahsocial.data.repository.MessagesRepository
import com.phuocpham.pumiahsocial.data.repository.PostsRepository
import com.phuocpham.pumiahsocial.util.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: com.phuocpham.pumiahsocial.data.repository.ProfileRepository,
    private val friendsRepository: FriendsRepository,
    private val postsRepository: PostsRepository,
    private val messagesRepository: MessagesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _profileState = MutableStateFlow<UiState<Profile>>(UiState.Loading)
    val profileState: StateFlow<UiState<Profile>> = _profileState.asStateFlow()

    private val _friendshipStatus = MutableStateFlow(FriendshipStatus.NONE)
    val friendshipStatus: StateFlow<FriendshipStatus> = _friendshipStatus.asStateFlow()

    private val _userPosts = MutableStateFlow<UiState<List<PostWithDetails>>>(UiState.Loading)
    val userPosts: StateFlow<UiState<List<PostWithDetails>>> = _userPosts.asStateFlow()

    private val _friendCount = MutableStateFlow(0)
    val friendCount: StateFlow<Int> = _friendCount.asStateFlow()

    private val _isOwnProfile = MutableStateFlow(true)
    val isOwnProfile: StateFlow<Boolean> = _isOwnProfile.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun clearError() { _errorMessage.value = null }

    private var profileUserId: String? = null

    val userId: String? = savedStateHandle.get<String>("userId")

    fun loadProfile(targetUserId: String?) {
        viewModelScope.launch {
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch
            val uid = targetUserId ?: currentUserId
            profileUserId = uid
            _isOwnProfile.value = uid == currentUserId

            _profileState.value = UiState.Loading
            profileRepository.fetchProfile(uid).fold(
                onSuccess = { profile ->
                    _profileState.value = UiState.Success(profile)
                },
                onFailure = { _profileState.value = UiState.Error(it.message ?: "Lỗi tải hồ sơ") }
            )

            // Load friendship status if viewing someone else's profile
            if (uid != currentUserId) {
                _friendshipStatus.value = friendsRepository.getFriendshipStatus(uid)
            }

            // Load friend count
            friendsRepository.getFriendsList(uid).fold(
                onSuccess = { _friendCount.value = it.size },
                onFailure = { }
            )

            // Load user posts
            postsRepository.getUserPosts(uid).fold(
                onSuccess = { _userPosts.value = UiState.Success(it) },
                onFailure = { _userPosts.value = UiState.Error(it.message ?: "Lỗi tải bài viết") }
            )
        }
    }

    fun sendFriendRequest() {
        val uid = profileUserId ?: return
        viewModelScope.launch {
            friendsRepository.sendFriendRequest(uid).fold(
                onSuccess = { _friendshipStatus.value = FriendshipStatus.PENDING_SENT },
                onFailure = { }
            )
        }
    }

    fun removeFriend() {
        val uid = profileUserId ?: return
        viewModelScope.launch {
            friendsRepository.removeFriend(uid).fold(
                onSuccess = { _friendshipStatus.value = FriendshipStatus.NONE },
                onFailure = { }
            )
        }
    }

    fun startConversation(onNavigateToChat: (String) -> Unit) {
        val uid = profileUserId ?: return
        viewModelScope.launch {
            messagesRepository.getOrCreateConversation(uid).fold(
                onSuccess = { conversationId -> onNavigateToChat(conversationId) },
                onFailure = { }
            )
        }
    }
}
