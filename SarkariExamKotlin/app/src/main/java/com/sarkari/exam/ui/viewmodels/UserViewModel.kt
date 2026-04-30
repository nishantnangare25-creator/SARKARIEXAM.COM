package com.sarkari.exam.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.local.AppPreferences
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.sarkari.exam.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth

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
    private val userRepository = UserRepository()
    private val firebaseAuth = FirebaseAuth.getInstance()
    
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
        
        // Fetch from Firestore if logged in
        firebaseAuth.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                userRepository.getUserProfile(uid).onSuccess { profile ->
                    if (profile != null) {
                        // Assuming you want to sync some fields from profile
                        // e.g. displayName, but the current UI uses default local mock.
                        // You can expand UserProfile to hold the displayName later.
                    }
                }
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

