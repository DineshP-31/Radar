package com.dp.radar.data.repositories.login

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dp.radar.com.dp.radar.domain.repositories.ILoginRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ILoginRepository {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_ID = longPreferencesKey("user_id")
    }

    override val isLoggedIn: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[KEY_EMAIL] != null }

    override val userId: Flow<Long> = dataStore.data
        .map { prefs -> prefs[KEY_USER_ID] ?: -1L }

    override suspend fun saveEmail(email: String) {
        dataStore.edit { prefs -> prefs[KEY_EMAIL] = email }
    }

    override suspend fun saveUserId(userId: Long) {
        dataStore.edit { prefs -> prefs[KEY_USER_ID] = userId }
    }

    override suspend fun clearEmail() {
        dataStore.edit { prefs -> prefs.remove(KEY_EMAIL) }
    }
}