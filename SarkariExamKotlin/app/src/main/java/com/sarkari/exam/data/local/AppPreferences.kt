package com.sarkari.exam.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "sarkari_prefs")

class AppPreferences(private val context: Context) {

    companion object {
        val LANGUAGE_CODE = stringPreferencesKey("language_code")
        val STATE_CODE = stringPreferencesKey("state_code")
        val EXAM_CODE = stringPreferencesKey("exam_code")
        val IS_LOGGED_IN = androidx.datastore.preferences.core.booleanPreferencesKey("is_logged_in")
    }

    val languageCode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE] ?: "en"
    }

    val stateCode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[STATE_CODE] ?: "DL" // Default to Delhi
    }

    val examCode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[EXAM_CODE] ?: "UPSC Civil Services" // Default exam
    }

    suspend fun saveLanguageCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE] = code
        }
    }

    suspend fun saveStateCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[STATE_CODE] = code
        }
    }

    suspend fun saveExamCode(code: String) {
        context.dataStore.edit { preferences ->
            preferences[EXAM_CODE] = code
        }
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun setLoggedIn(loggedIn: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = loggedIn
        }
    }
}
