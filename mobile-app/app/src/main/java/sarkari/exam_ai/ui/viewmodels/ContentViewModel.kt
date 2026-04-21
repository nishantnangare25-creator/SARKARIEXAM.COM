package sarkari.exam_ai.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import sarkari.exam_ai.data.models.BlogItem
import sarkari.exam_ai.data.models.CurrentAffairsItem
import sarkari.exam_ai.data.models.PdfItem
import sarkari.exam_ai.data.repository.SarkariRepository
import sarkari.exam_ai.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

class ContentViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SarkariRepository()
    private val prefsRepo = UserPreferencesRepository(application.applicationContext)

    private val _currentAffairsState = MutableStateFlow<UiState<List<CurrentAffairsItem>>>(UiState.Loading)
    val currentAffairsState: StateFlow<UiState<List<CurrentAffairsItem>>> = _currentAffairsState.asStateFlow()

    private val _pdfsState = MutableStateFlow<UiState<List<PdfItem>>>(UiState.Loading)
    val pdfsState: StateFlow<UiState<List<PdfItem>>> = _pdfsState.asStateFlow()

    private val _blogsState = MutableStateFlow<UiState<List<BlogItem>>>(UiState.Loading)
    val blogsState: StateFlow<UiState<List<BlogItem>>> = _blogsState.asStateFlow()

    init {
        // Automatically fetch initial data when ViewModel is created
        fetchCurrentAffairs()
        fetchPdfs()
        fetchBlogs()
    }

    fun fetchCurrentAffairs() {
        viewModelScope.launch {
            _currentAffairsState.value = UiState.Loading
            val prefs = prefsRepo.userSettingsFlow.first()
            val langCode = if (prefs.language == "Hindi") "hi" else "en"
            
            repository.getCurrentAffairs(examCategory = prefs.examCategory, language = langCode).fold(
                onSuccess = { _currentAffairsState.value = UiState.Success(it) },
                onFailure = { _currentAffairsState.value = UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun fetchPdfs(category: String? = null) {
        viewModelScope.launch {
            _pdfsState.value = UiState.Loading
            val prefs = prefsRepo.userSettingsFlow.first()
            val langCode = if (prefs.language == "Hindi") "hi" else "en"
            
            repository.getPdfs(examCategory = prefs.examCategory, language = langCode, type = category).fold(
                onSuccess = { _pdfsState.value = UiState.Success(it) },
                onFailure = { _pdfsState.value = UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun fetchBlogs() {
        viewModelScope.launch {
            _blogsState.value = UiState.Loading
            val prefs = prefsRepo.userSettingsFlow.first()
            val langCode = if (prefs.language == "Hindi") "hi" else "en"
            
            repository.getBlogs(examCategory = prefs.examCategory, language = langCode).fold(
                onSuccess = { _blogsState.value = UiState.Success(it) },
                onFailure = { _blogsState.value = UiState.Error(it.message ?: "Unknown error") }
            )
        }
    }
}
