package com.dp.radar.utils

import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavType
import com.dp.radar.domain.model.User
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object UserNavType : NavType<User>(
    isNullableAllowed = false,
) {
    override fun get(bundle: Bundle, key: String): User? =
        bundle.getString(key)?.let { parseValue(it) }

    override fun put(bundle: Bundle, key: String, value: User) {
        bundle.putString(
            key,
            serializeAsValue(
                value
            )
        )
    }

    override fun parseValue(value: String): User =
        Json.decodeFromString(Uri.decode(value))

    override fun serializeAsValue(value: User): String =
        Uri.encode(Json.encodeToString(value))
}
