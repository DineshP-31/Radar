package com.dp.radar.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.com.dp.radar.domain.ApiResult
import com.dp.radar.com.dp.radar.domain.login.GetUserIdUseCase
import com.dp.radar.com.dp.radar.domain.model.Chat
import com.dp.radar.com.dp.radar.ui.UserListIntent
import com.dp.radar.com.dp.radar.ui.UserListState
import com.dp.radar.domain.GetUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val getUsersUseCase: GetUsersUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val dispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val _state = MutableStateFlow(UserListState.Initial)
    val state: StateFlow<UserListState> = _state.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        Log.e("Init:", "Called")
        handleIntent(UserListIntent.LoadUsers)
    }

    fun handleIntent(intent: UserListIntent) {
        when (intent) {
            UserListIntent.LoadUsers, UserListIntent.RetryLoad -> loadUsers()
        }
    }

    private fun loadUsers() {
        viewModelScope.launch(dispatcher) {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (val result = getUsersUseCase()) {
                is ApiResult.Success -> {
                    val data = result.data.filter {
                        it.id != getUserIdUseCase()
                    }
                    _state.update {
                        it.copy(users = data, isLoading = false, error = null)
                    }
                }

                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            users = emptyList(),
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}