package com.phuocpham.pumiahsocial.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.phuocpham.pumiahsocial.data.model.Profile
import com.phuocpham.pumiahsocial.data.repository.AuthRepository
import com.phuocpham.pumiahsocial.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _results = MutableStateFlow<List<Profile>>(emptyList())
    val results: StateFlow<List<Profile>> = _results.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private var searchJob: Job? = null
    private var currentUserId: String? = null

    init {
        loadAllUsers()
    }

    private fun loadAllUsers() {
        viewModelScope.launch {
            currentUserId = authRepository.getCurrentUserId()
            _isSearching.value = true
            profileRepository.getAllProfiles().fold(
                onSuccess = { _results.value = it.filter { p -> p.id != currentUserId } },
                onFailure = { _results.value = emptyList() }
            )
            _isSearching.value = false
        }
    }

    fun updateQuery(value: String) {
        _query.value = value
        searchJob?.cancel()
        if (value.isBlank()) {
            loadAllUsers()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            _isSearching.value = true
            profileRepository.searchProfiles(value).fold(
                onSuccess = { _results.value = it.filter { p -> p.id != currentUserId } },
                onFailure = { _results.value = emptyList() }
            )
            _isSearching.value = false
        }
    }
}
