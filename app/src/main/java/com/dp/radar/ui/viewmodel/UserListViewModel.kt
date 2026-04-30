package com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.GetUsersUseCase
import com.dp.radar.domain.ObserveUsersUseCase
import com.dp.radar.domain.login.GetUserIdUseCase
import com.dp.radar.domain.model.Chat
import com.dp.radar.ui.UserListIntent
import com.dp.radar.ui.UserListState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val observeUsersUseCase: ObserveUsersUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState.Initial)
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        startObservingUsers()
        refreshFromNetwork()
    }

    fun handleIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.LoadUsers, UserListIntent.RetryLoad -> refreshFromNetwork()
        }
    }

    private fun startObservingUsers() {
        viewModelScope.launch(dispatcher) {
            val currentUserId = getUserIdUseCase().first()
            observeUsersUseCase().collect { users ->
                _state.update { it.copy(users = users.filter { u -> u.id != currentUserId }) }
            }
        }
    }

    private fun refreshFromNetwork() {
        viewModelScope.launch(dispatcher) {
            _state.update { it.copy(isLoading = true, error = null) }
            when (val result = getUsersUseCase()) {
                is ApiResult.Success -> _state.update { it.copy(isLoading = false, error = null) }
                is ApiResult.Error -> _state.update { state ->
                    state.copy(
                        isLoading = false,
                        error = if (state.users.isEmpty()) result.message else null,
                    )
                }
            }
        }
    }
}