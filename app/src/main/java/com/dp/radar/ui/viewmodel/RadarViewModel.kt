package com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.com.dp.radar.domain.login.ClearEmailUseCase
import com.dp.radar.com.dp.radar.domain.login.GetIsLoggedInUseCase
import com.dp.radar.com.dp.radar.domain.login.GetUserIdUseCase
import com.dp.radar.com.dp.radar.domain.login.SaveEmailUseCase
import com.dp.radar.com.dp.radar.domain.login.SaveUserIdUseCase
import com.dp.radar.ui.navigation.RadarScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    getIsLoggedInUseCase: GetIsLoggedInUseCase,
    private val saveEmailUseCase: SaveEmailUseCase,
    private val clearEmailUseCase: ClearEmailUseCase,
    getUserIdUseCase: GetUserIdUseCase,
    private val saveUserIdUseCase: SaveUserIdUseCase,
) : ViewModel() {

    private val _bottomBarState = MutableStateFlow(BottomBarState(false, RadarScreen.HomeScreen))
    val bottomBarState: StateFlow<BottomBarState> = _bottomBarState.asStateFlow()

    private val _topBarTitle = MutableStateFlow("")
    val topBarTitle = _topBarTitle.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = getIsLoggedInUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val userId: StateFlow<Long> = getUserIdUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, -1L)

    fun updateBottomBar(show: Boolean, screen: RadarScreen) {
        _bottomBarState.value = BottomBarState(show, screen)
    }

    fun updateTopBarTitle(title: String) {
        _topBarTitle.value = title
    }

    fun onLoginSuccess(email: String) {
        viewModelScope.launch {
            saveEmailUseCase(email)
        }
    }

    fun updateLoginState(isLoggedIn: Boolean) { }

    fun onBoardingCompleted(userId: Long) {
        viewModelScope.launch {
            saveUserIdUseCase(userId)
        }
    }

    fun getUserId(): Long = userId.value

    fun logout() {
        viewModelScope.launch {
            clearEmailUseCase()
        }
    }
}

data class BottomBarState(val show: Boolean, val route: RadarScreen)