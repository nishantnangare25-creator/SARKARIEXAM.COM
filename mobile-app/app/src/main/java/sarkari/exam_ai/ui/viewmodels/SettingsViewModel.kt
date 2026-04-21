package sarkari.exam_ai.ui.viewmodels

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import sarkari.exam_ai.data.repository.UserPreferencesRepository
import sarkari.exam_ai.data.repository.UserSettings
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPrefsRepo = UserPreferencesRepository(application.applicationContext)

    val userSettings: StateFlow<UserSettings> = userPrefsRepo.userSettingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserSettings("English", "UPSC Civil Services")
    )

    /**
     * Updates the display language AND immediately applies it to the app's locale
     * so all stringResource() calls recompose with the new language — no restart needed.
     */
    fun updateLanguage(language: String) {
        viewModelScope.launch {
            userPrefsRepo.updateLanguage(language)
        }
        // Map selection label → BCP 47 locale tag
        val localeTag = when (language) {
            "Hindi" -> "hi"
            "Marathi" -> "mr"
            "Tamil" -> "ta"
            "Telugu" -> "te"
            "Kannada" -> "kn"
            "Bengali" -> "bn"
            "Gujarati" -> "gu"
            "Punjabi" -> "pa"
            else -> "en"
        }
        // This triggers instant UI recomposition — no app restart needed
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(localeTag)
        )
    }

    fun updateExamCategory(category: String) {
        viewModelScope.launch {
            userPrefsRepo.updateExamCategory(category)
        }
    }
}
