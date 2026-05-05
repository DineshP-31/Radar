package com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.ApiResult
import com.dp.radar.domain.CreateUserUseCase
import com.dp.radar.domain.model.UserRequestDto
import com.dp.radar.ui.SignUpState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val createUserUseCase: CreateUserUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState.Initial)
    val state: StateFlow<SignUpState> = _state.asStateFlow()

    fun createUser(userName: String, email: String, latLong: LatLong) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            when (
                val result = createUserUseCase(
                    UserRequestDto(
                        username = userName,
                        email = email,
                        latLong = latLong
                    )
                )
            ) {
                is ApiResult.Success -> {
                    _state.update {
                        it.copy(user = result.data, isLoading = false, error = null)
                    }
                }

                is ApiResult.Error -> {
                    _state.update {
                        it.copy(
                            user = null,
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }
}
