package com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.dp.radar.com.dp.radar.domain.login.ClearEmailUseCase
import com.dp.radar.com.dp.radar.domain.login.GetIsLoggedInUseCase
import com.dp.radar.com.dp.radar.domain.login.GetUserIdUseCase
import com.dp.radar.com.dp.radar.domain.login.SaveEmailUseCase
import com.dp.radar.com.dp.radar.domain.login.SaveUserIdUseCase
import com.dp.radar.ui.navigation.RadarScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RadarViewModel @Inject constructor(
    getIsLoggedInUseCase: GetIsLoggedInUseCase,
    private val saveEmailUseCase: SaveEmailUseCase,
    private val clearEmailUseCase: ClearEmailUseCase,
    private val getUserIdUseCase: GetUserIdUseCase,
    private val saveUserIdUseCase: SaveUserIdUseCase,
) : ViewModel() {

    private val _bottomBarState = MutableStateFlow(BottomBarState(false, RadarScreen.HomeScreen))
    val bottomBarState: StateFlow<BottomBarState> = _bottomBarState.asStateFlow()

    private val _topBarTitle = MutableStateFlow("")
    val topBarTitle = _topBarTitle.asStateFlow()

    fun updateBottomBar(show: Boolean, screen: RadarScreen) {
        _bottomBarState.value = BottomBarState(show, screen)
    }

    fun updateTopBarTitle(title: String) {
        _topBarTitle.value = title
    }

    private val _isLoggedIn = MutableStateFlow(getIsLoggedInUseCase())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun onLoginSuccess(email: String) {
        saveEmailUseCase(email)
        //_isLoggedIn.value = true
    }

    fun updateLoginState(isLoggedIn: Boolean) {
        _isLoggedIn.value = isLoggedIn
    }

    fun onBoardingCompleted(userId: Long) {
        saveUserIdUseCase(userId)

    }

    fun getUserId(): Long = getUserIdUseCase()

    fun logout() {
        clearEmailUseCase()
        _isLoggedIn.value = false
    }
}

data class BottomBarState(val show: Boolean, val route: RadarScreen)