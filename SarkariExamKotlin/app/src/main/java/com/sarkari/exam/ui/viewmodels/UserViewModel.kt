package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.local.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UserProfile(
    val exam: String = "",
    val language: String = "en",
    val studyHours: Int = 4,
    val level: String = "beginner",
    val weakSubjects: List<String> = emptyList(),
    val strongSubjects: List<String> = emptyList(),
    val isOnboarded: Boolean = false
)

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val appPreferences = AppPreferences(application)
    
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        viewModelScope.launch {
            appPreferences.languageCode.collectLatest { lang ->
                _userProfile.value = _userProfile.value.copy(language = lang)
            }
        }
        viewModelScope.launch {
            appPreferences.examCode.collectLatest { exam ->
                _userProfile.value = _userProfile.value.copy(exam = exam)
            }
        }
    }

    fun updateExam(exam: String) {
        viewModelScope.launch {
            appPreferences.saveExamCode(exam)
            _userProfile.value = _userProfile.value.copy(exam = exam)
        }
    }

    fun updateLanguage(language: String) {
        viewModelScope.launch {
            appPreferences.saveLanguageCode(language)
            _userProfile.value = _userProfile.value.copy(language = language)
        }
    }

    fun updateStudyHours(hours: Int) {
        _userProfile.value = _userProfile.value.copy(studyHours = hours)
    }

    fun updateLevel(level: String) {
        _userProfile.value = _userProfile.value.copy(level = level)
    }

    fun setOnboarded(onboarded: Boolean) {
        _userProfile.value = _userProfile.value.copy(isOnboarded = onboarded)
    }
}

