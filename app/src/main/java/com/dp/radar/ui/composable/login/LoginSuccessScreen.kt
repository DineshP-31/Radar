package com.dp.radar.ui.composable.login

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("ContextCastToActivity", "ConfigurationScreenWidthHeight")
@Composable
fun LoginSuccessScreen(
    goToHome: () -> Unit
) {
    val activity = LocalContext.current as Activity

    BackHandler {
        activity.finish()
    }

    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    // Animation state
    val tickOffsetY = remember { Animatable(screenHeight.value) }

    LaunchedEffect(Unit) {
        tickOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 1400,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Center content
        Column(
            modifier = Modifier
                .offset(y = tickOffsetY.value.dp)
                .padding(top = 70.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Green Tick
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFFEA4335),
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Successfully Onboarded!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Fixed bottom button
        Button(
            onClick = goToHome,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Text("Go to Home")
        }
    }
}
