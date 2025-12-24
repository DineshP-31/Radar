package com.dp.radar.data.repositories.login

import android.content.SharedPreferences
import javax.inject.Inject
import androidx.core.content.edit
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository

class LoginRepository @Inject constructor(
    private val sharedPrefs: SharedPreferences
) : ILoginRepository {

    companion object {
        private const val KEY_EMAIL = "user_email"
        private const val KEY_USER_ID = "user_id"
    }

    override fun saveEmail(email: String) {
        sharedPrefs.edit { putString(KEY_EMAIL, email) }
    }

    override fun saveUserId(userId: Long) {
        sharedPrefs.edit { putLong(KEY_USER_ID, userId) }
    }

    override fun getUserId(): Long {

        return sharedPrefs.getLong(KEY_USER_ID, -1)
    }

    override fun clearEmail() {
        sharedPrefs.edit { remove(KEY_EMAIL) }
    }

    override fun isLoggedIn(): Boolean = sharedPrefs.getString(KEY_EMAIL, null) != null
}
