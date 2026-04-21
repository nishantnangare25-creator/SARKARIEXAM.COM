package sarkari.exam_ai.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_settings")

data class UserSettings(
    val language: String,
    val examCategory: String
)

class UserPreferencesRepository(private val context: Context) {

    private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    private val EXAM_CATEGORY_KEY = stringPreferencesKey("exam_category")

    // Default values matching the website
    private val defaultLanguage = "English"
    private val defaultExam = "UPSC Civil Services"

    val userSettingsFlow: Flow<UserSettings> = context.dataStore.data.map { preferences ->
        val lang = preferences[LANGUAGE_KEY] ?: defaultLanguage
        val exam = preferences[EXAM_CATEGORY_KEY] ?: defaultExam
        UserSettings(language = lang, examCategory = exam)
    }

    suspend fun updateLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    suspend fun updateExamCategory(category: String) {
        context.dataStore.edit { preferences ->
            preferences[EXAM_CATEGORY_KEY] = category
        }
    }
}
