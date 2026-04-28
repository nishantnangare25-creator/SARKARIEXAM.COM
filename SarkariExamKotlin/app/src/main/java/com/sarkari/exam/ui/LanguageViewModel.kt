package com.sarkari.exam.ui

import android.app.Application
import android.app.LocaleManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.local.AppPreferences
import com.sarkari.exam.data.local.AppStrings
import com.sarkari.exam.data.local.LocalisationManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LanguageViewModel(application: Application) : AndroidViewModel(application) {
    
    private val appPreferences = AppPreferences(application)

    val currentLanguageCode: StateFlow<String> = appPreferences.languageCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "en")

    val currentStateCode: StateFlow<String> = appPreferences.stateCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "DL")

    val currentExamCode: StateFlow<String> = appPreferences.examCode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "UPSC Civil Services")

    val strings: StateFlow<AppStrings> = currentLanguageCode.map { code ->
        LocalisationManager.getStrings(code)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LocalisationManager.getStrings("en"))

    val isLoggedIn: StateFlow<Boolean> = appPreferences.isLoggedIn
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateLanguage(code: String) {
        viewModelScope.launch {
            appPreferences.saveLanguageCode(code)
            
            // Dynamically change application locale
            try {
                val context = getApplication<Application>().applicationContext
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.getSystemService(LocaleManager::class.java)?.applicationLocales =
                        LocaleList.forLanguageTags(code)
                } else {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(code))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateState(code: String) {
        viewModelScope.launch {
            appPreferences.saveStateCode(code)
        }
    }

    fun updateExam(code: String) {
        viewModelScope.launch {
            appPreferences.saveExamCode(code)
        }
    }
}

