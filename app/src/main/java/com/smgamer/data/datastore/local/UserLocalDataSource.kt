package com.smgamer.data.datastore.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.smgamer.domain.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserLocalDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore by preferencesDataStore(name = "user_prefs")

    private val KEY_ID = stringPreferencesKey("id")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_USERNAME = stringPreferencesKey("username")
    private val KEY_PHONE = stringPreferencesKey("phone")
    private val KEY_IMAGE = stringPreferencesKey("profileImage")
    private val KEY_COVER = stringPreferencesKey("coverImage")

    suspend fun saveUser(user: User) {
        context.dataStore.edit { prefs ->
            prefs[KEY_ID] = user.id
            prefs[KEY_EMAIL] = user.email
            prefs[KEY_USERNAME] = user.username
            prefs[KEY_PHONE] = user.phone
            prefs[KEY_IMAGE] = user.profileImage
            prefs[KEY_COVER] = user.coverImage
        }
    }

    suspend fun getUser(): User? {
        val prefs = context.dataStore.data.first()
        val id = prefs[KEY_ID] ?: return null
        return User(
            id = id,
            email = prefs[KEY_EMAIL] ?: "",
            username = prefs[KEY_USERNAME] ?: "",
            phone = prefs[KEY_PHONE] ?: "",
            profileImage = prefs[KEY_IMAGE] ?: "",
            coverImage = prefs[KEY_COVER] ?: "",
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun clearUser() {
        context.dataStore.edit { it.clear() }
    }
}