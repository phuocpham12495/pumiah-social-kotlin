package com.phuocpham.pumiahsocial.ui.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    companion object {
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        val NOTIFY_LIKES_KEY = booleanPreferencesKey("notify_likes")
        val NOTIFY_COMMENTS_KEY = booleanPreferencesKey("notify_comments")
        val NOTIFY_FRIEND_REQUESTS_KEY = booleanPreferencesKey("notify_friend_requests")
        val NOTIFY_MESSAGES_KEY = booleanPreferencesKey("notify_messages")
    }

    private val _profile = MutableStateFlow<Profile?>(null)
    val profile: StateFlow<Profile?> = _profile.asStateFlow()

    val isDarkMode: StateFlow<Boolean> = dataStore.data
        .map { it[DARK_MODE_KEY] ?: false }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val notifyLikes: StateFlow<Boolean> = dataStore.data
        .map { it[NOTIFY_LIKES_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyComments: StateFlow<Boolean> = dataStore.data
        .map { it[NOTIFY_COMMENTS_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyFriendRequests: StateFlow<Boolean> = dataStore.data
        .map { it[NOTIFY_FRIEND_REQUESTS_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val notifyMessages: StateFlow<Boolean> = dataStore.data
        .map { it[NOTIFY_MESSAGES_KEY] ?: true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val userId = authRepository.getCurrentUserId() ?: return@launch
            profileRepository.fetchProfile(userId).fold(
                onSuccess = { _profile.value = it },
                onFailure = { }
            )
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            dataStore.edit { it[DARK_MODE_KEY] = enabled }
        }
    }

    fun toggleNotifyLikes(enabled: Boolean) {
        viewModelScope.launch { dataStore.edit { it[NOTIFY_LIKES_KEY] = enabled } }
    }

    fun toggleNotifyComments(enabled: Boolean) {
        viewModelScope.launch { dataStore.edit { it[NOTIFY_COMMENTS_KEY] = enabled } }
    }

    fun toggleNotifyFriendRequests(enabled: Boolean) {
        viewModelScope.launch { dataStore.edit { it[NOTIFY_FRIEND_REQUESTS_KEY] = enabled } }
    }

    fun toggleNotifyMessages(enabled: Boolean) {
        viewModelScope.launch { dataStore.edit { it[NOTIFY_MESSAGES_KEY] = enabled } }
    }

    fun logout(onLogout: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onLogout()
        }
    }
}
