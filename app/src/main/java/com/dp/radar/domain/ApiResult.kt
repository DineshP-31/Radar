package com.dp.radar.com.dp.radar.domain

import androidx.compose.ui.Modifier

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val message: String) : ApiResult<Nothing>
}