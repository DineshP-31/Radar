package com.dp.radar.ui.composable

import android.Manifest
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dp.radar.R
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.domain.model.LocationData
import com.dp.radar.ui.viewmodel.LocationUiState
import com.dp.radar.ui.viewmodel.LocationViewModel
import com.dp.radar.ui.viewmodel.RadarViewModel
import com.dp.radar.ui.viewmodel.SignUpViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationFetchRoute(
    goToSuccessScreen: () -> Unit,
    username: String,
    email: String,
) {
    val locationViewModel: LocationViewModel = hiltViewModel()
    val signUpViewModel: SignUpViewModel = hiltViewModel()
    val radarViewModel: RadarViewModel = hiltViewModel()

    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    val locationState by locationViewModel.uiState.collectAsState()
    val signUpState by signUpViewModel.state.collectAsState()
    val isPermissionGranted = permissionState.status.isGranted

    LaunchedEffect(signUpState.user) {
        signUpState.user?.let {
            radarViewModel.onLoginSuccess(email)
            radarViewModel.onBoardingCompleted(userId = it.id)
            goToSuccessScreen()
        }
    }

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            locationViewModel.fetchLocation()
        }
    }

    LocationFetchScreen(
        locationState = locationState,
        isSignUpLoading = signUpState.isLoading,
        allowPermission = {
            permissionState.launchPermissionRequest()
        },
        isPermissionGranted = isPermissionGranted,
        onContinue = { location ->
            signUpViewModel.createUser(
                userName = username,
                email = email,
                latLong = LatLong(
                    lat = location.latitude,
                    lon = location.longitude
                )
            )
        }
    )
}

@Composable
fun LocationFetchScreen(
    locationState: LocationUiState,
    isSignUpLoading: Boolean,
    onContinue: (LocationData) -> Unit,
    isPermissionGranted: Boolean,
    allowPermission: () -> Unit,
) {
    val enableContinue = locationState.location != null

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_gps),
                contentDescription = "Location illustration",
                modifier = Modifier
                    .size(120.dp)
            )

            Text(
                text = "We need to know where you are in order to find near by friends",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            when {
                locationState.isLoading -> CircularProgressIndicator()
                locationState.location != null -> {
                    Text("Latitude: ${locationState.location.latitude}")
                    Text("Longitude: ${locationState.location.longitude}")
                }

                locationState.error != null -> {
                    Text(locationState.error, color = Color.Red)
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Location permission is required to continue",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(16.dp))
                Button(
                    enabled = !isPermissionGranted,
                    onClick = { allowPermission() }
                ) {
                    Text("Allow Permission")
                }
            }
        }

        Button(
            enabled = enableContinue && !isSignUpLoading,
            onClick = {
                locationState.location?.let {
                    onContinue(it)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text("Continue")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LocationFetchScreenPreview() {
    LocationFetchScreen(
        locationState = LocationUiState(
            isLoading = false,
            location = null,
            error = null
        ),
        isSignUpLoading = false,
        onContinue = {},
        isPermissionGranted = false,
        allowPermission = {
        }
    )
}
