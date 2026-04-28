package com.dp.radar.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext

@Composable
fun BackHandler(
    exitDelayMillis: Long = 2000L
) {
    val context = LocalContext.current
    val activity = context.findActivity() ?: return

    var backPressedOnce by rememberSaveable { mutableStateOf(false) }

    BackHandler {
        if (backPressedOnce) {
            activity.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(
                context,
                "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
