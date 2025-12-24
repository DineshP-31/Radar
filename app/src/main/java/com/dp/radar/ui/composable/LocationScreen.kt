package com.dp.radar.com.dp.radar.ui.composable

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dp.radar.com.dp.radar.ui.viewmodel.LocationViewModel
import com.dp.radar.com.dp.radar.ui.viewmodel.SignUpViewModel
import com.dp.radar.data.datasources.remote.dto.LatLong
import com.dp.radar.ui.viewmodel.RadarViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@SuppressLint("ContextCastToActivity")
@Composable
fun LocationFetchScreen(
    locationViewModel: LocationViewModel = hiltViewModel(),
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    radarViewModel: RadarViewModel = hiltViewModel(),
    goToSuccessScreen: () -> Unit,
    username: String,
    email: String,
) {
    val activity = LocalContext.current as Activity
    val locationState by locationViewModel.uiState.collectAsState()
    val signUpState by signUpViewModel.state.collectAsState()
    val enableContinue = locationState.location!= null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "We need your location to continue..",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        when {
            locationState.isLoading -> {
                CircularProgressIndicator()
            }

            locationState.location != null -> {
                Text("Latitude: ${locationState.location!!.latitude}")
                Text("Longitude: ${locationState.location!!.longitude}")
            }

            locationState.error != null -> {
                Text(text = locationState.error!!, color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LocationPermissionScreen {
            locationViewModel.fetchLocation()

        }

            Button(
                onClick = { locationState.location?.let { location ->
                    signUpViewModel.createUser(
                        userName = username,
                        email = email,
                        latLong = LatLong(
                            lat = location.latitude,
                            lon = location.longitude
                        )
                    )
                } },
                enabled = enableContinue,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }



    BackHandler {
        activity.finish()
    }

    LaunchedEffect(signUpState.user) {
        signUpState.user?.let {
            with(radarViewModel){
                onLoginSuccess(email)
                onBoardingCompleted(userId = it.id)
            }
            goToSuccessScreen()
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LocationPermissionScreen(
    onPermissionGranted: () -> Unit
) {
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(permissionState.status.isGranted) {
        if (permissionState.status.isGranted) {
            onPermissionGranted()
        }
    }

    if (!permissionState.status.isGranted) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Location permission is required to continue")
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { permissionState.launchPermissionRequest() }
            ) {
                Text("Allow Permission")
            }
        }
    }
}



