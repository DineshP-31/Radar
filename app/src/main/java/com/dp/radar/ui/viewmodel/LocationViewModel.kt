package com.dp.radar.com.dp.radar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dp.radar.com.dp.radar.domain.GetCurrentLocationUseCase
import com.dp.radar.com.dp.radar.domain.model.LocationData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationUiState())
    val uiState: StateFlow<LocationUiState> = _uiState

    fun fetchLocation() {
        viewModelScope.launch {
            _uiState.value = LocationUiState(isLoading = true)
            try {
                val location = getCurrentLocationUseCase()
                _uiState.value = LocationUiState(location = location)
            } catch (e: Exception) {
                _uiState.value = LocationUiState(
                    error = "Unable to fetch location"
                )
            }
        }
    }
}


data class LocationUiState(
    val isLoading: Boolean = false,
    val location: LocationData? = null,
    val error: String? = null
)