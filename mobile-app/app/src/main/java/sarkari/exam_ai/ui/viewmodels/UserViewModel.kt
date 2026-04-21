package sarkari.exam_ai.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import sarkari.exam_ai.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val authRepository = AuthRepository(application.applicationContext)
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        authRepository.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                authRepository.getUserProfile(uid)?.let { data ->
                    _userProfile.value = UserProfile(
                        exam = data["exam"] as? String ?: "",
                        language = data["language"] as? String ?: "en",
                        studyHours = (data["studyHours"] as? Long)?.toInt() ?: 4,
                        level = data["level"] as? String ?: "beginner",
                        weakSubjects = (data["weakSubjects"] as? List<String>) ?: emptyList(),
                        strongSubjects = (data["strongSubjects"] as? List<String>) ?: emptyList(),
                        isOnboarded = data["isOnboarded"] as? Boolean ?: false
                    )
                }
            }
        }
    }

    private fun saveToFirestore() {
        authRepository.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                val profile = _userProfile.value
                val data = mapOf(
                    "exam" to profile.exam,
                    "language" to profile.language,
                    "studyHours" to profile.studyHours,
                    "level" to profile.level,
                    "weakSubjects" to profile.weakSubjects,
                    "strongSubjects" to profile.strongSubjects,
                    "isOnboarded" to profile.isOnboarded
                )
                authRepository.saveUserProfile(uid, data)
            }
        }
    }

    fun updateExam(exam: String) {
        _userProfile.value = _userProfile.value.copy(exam = exam)
        saveToFirestore()
    }

    fun updateLanguage(language: String) {
        _userProfile.value = _userProfile.value.copy(language = language)
        saveToFirestore()
    }

    fun updateStudyHours(hours: Int) {
        _userProfile.value = _userProfile.value.copy(studyHours = hours)
        saveToFirestore()
    }

    fun updateLevel(level: String) {
        _userProfile.value = _userProfile.value.copy(level = level)
        saveToFirestore()
    }

    fun setOnboarded(onboarded: Boolean) {
        _userProfile.value = _userProfile.value.copy(isOnboarded = onboarded)
        saveToFirestore()
    }
}
