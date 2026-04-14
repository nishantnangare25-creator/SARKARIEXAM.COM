package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserProfile(
    val exam: String = "",
    val language: String = "en",
    val studyHours: Int = 4,
    val level: String = "beginner",
    val weakSubjects: List<String> = emptyList(),
    val strongSubjects: List<String> = emptyList(),
    val isOnboarded: Boolean = false
)

class UserViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    fun updateExam(exam: String) {
        _userProfile.value = _userProfile.value.copy(exam = exam)
    }

    fun updateLanguage(language: String) {
        _userProfile.value = _userProfile.value.copy(language = language)
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
