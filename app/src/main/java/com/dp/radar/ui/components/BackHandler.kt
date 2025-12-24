package com.dp.radar.com.dp.radar.ui.components

import android.annotation.SuppressLint
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@SuppressLint("ContextCastToActivity")
@Composable
fun BackHandler() {
    val activity = LocalContext.current as Activity
    var backPressedOnce by remember { mutableStateOf(false) }
    BackHandler {
        if (backPressedOnce) {
            activity.finish()
        } else {
            Toast.makeText(activity, "Press back again to exit", Toast.LENGTH_SHORT).show()
        }
    }
}