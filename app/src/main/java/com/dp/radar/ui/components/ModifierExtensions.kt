package com.dp.radar.com.dp.radar.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.IntSize
import com.dp.radar.R
import com.dp.radar.com.dp.radar.ui.style.DesignConstants.ALPHA_MED
import com.dp.radar.com.dp.radar.ui.style.DesignConstants.ALPHA_VERY_LOW

fun Modifier.shimmerEffect(): Modifier = composed {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val transition = rememberInfiniteTransition(label = "shimmer")
    val startOffsetX by transition.animateFloat(
        initialValue = -2 * size.width.toFloat(),
        targetValue = 2 * size.width.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
        ),
        label = "shimmer",
    )
    background(
        brush = Brush.linearGradient(
            colors = listOf(
                colorResource(id = R.color.LightestSlate).copy(alpha = ALPHA_VERY_LOW),
                colorResource(id = R.color.LightestSlate).copy(alpha = ALPHA_MED),
                colorResource(id = R.color.LightestSlate).copy(alpha = ALPHA_VERY_LOW),
            ),
            start = Offset(startOffsetX, 0f),
            end = Offset(
                startOffsetX + size.width.toFloat(),
                size.height.toFloat(),
            ),
        ),
    ).onGloballyPositioned {
        size = it.size
    }
}