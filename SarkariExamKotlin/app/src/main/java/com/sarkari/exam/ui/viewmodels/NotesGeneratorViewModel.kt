package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import com.sarkari.exam.data.repository.AiRepository

enum class InputTab { TEXT, FILE, URL }

data class GeneratedNote(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val summary: String,
    val bullets: List<String>,
    val highlights: List<String>
)

data class RecentNoteItem(
    val id: String,
    val title: String,
    val timeAgo: String,
    val subject: String,
    val exam: String
)

sealed class GenerationState {
    object Idle : GenerationState()
    object Loading : GenerationState()
    data class Success(val note: GeneratedNote) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

class NotesGeneratorViewModel : ViewModel() {

    private val aiRepository = AiRepository()
    // Input Tabs
    private val _currentTab = MutableStateFlow(InputTab.TEXT)
    val currentTab: StateFlow<InputTab> = _currentTab.asStateFlow()

    // Dynamic Options
    val examsList = listOf("UPSC", "SSC", "Banking", "Railway")
    private val subjectMap = mapOf(
        "UPSC" to listOf("History", "Geography", "Polity", "Economy", "CSAT"),
        "SSC" to listOf("Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "Banking" to listOf("Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway" to listOf("Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow(_availableSubjects.value.firstOrNull() ?: "")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Inputs
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _inputUrl = MutableStateFlow("")
    val inputUrl: StateFlow<String> = _inputUrl.asStateFlow()

    // Smart Options
    private val _examFocused = MutableStateFlow(true)
    val examFocused: StateFlow<Boolean> = _examFocused.asStateFlow()

    private val _convertFlashcards = MutableStateFlow(false)
    val convertFlashcards: StateFlow<Boolean> = _convertFlashcards.asStateFlow()

    private val _highlightPoints = MutableStateFlow(true)
    val highlightPoints: StateFlow<Boolean> = _highlightPoints.asStateFlow()

    private val _linkPyq = MutableStateFlow(false)
    val linkPyq: StateFlow<Boolean> = _linkPyq.asStateFlow()

    // Generation State
    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    // Recent Notes
    private val _recentNotes = MutableStateFlow<List<RecentNoteItem>>(emptyList())
    val recentNotes: StateFlow<List<RecentNoteItem>> = _recentNotes.asStateFlow()

    init {
        loadRecentNotes()
    }

    fun setTab(tab: InputTab) {
        _currentTab.value = tab
    }

    fun onExamSelected(exam: String) {
        _selectedExam.value = exam
        val subjects = subjectMap[exam] ?: emptyList()
        _availableSubjects.value = subjects
        if (subjects.isNotEmpty()) {
            _selectedSubject.value = subjects[0]
        }
    }

    fun onSubjectSelected(subject: String) {
        _selectedSubject.value = subject
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun onInputUrlChanged(url: String) {
        _inputUrl.value = url
    }

    fun toggleSmartOption(option: String) {
        when (option) {
            "examFocused" -> _examFocused.value = !_examFocused.value
            "flashcards" -> _convertFlashcards.value = !_convertFlashcards.value
            "highlight" -> _highlightPoints.value = !_highlightPoints.value
            "linkPyq" -> _linkPyq.value = !_linkPyq.value
        }
    }

    fun generateNotes() {
        val topicInput = when (_currentTab.value) {
            InputTab.TEXT -> _inputText.value.trim()
            InputTab.URL -> "Content from URL: ${_inputUrl.value.trim()}"
            InputTab.FILE -> "Uploaded file content"
        }

        if (topicInput.isEmpty()) {
            _generationState.value = GenerationState.Error("Please provide some input to generate notes.")
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState.Loading

            // Real Groq API call
            aiRepository.generateNotes(
                topic = topicInput,
                examContext = _selectedExam.value,
                subjectContext = _selectedSubject.value,
                includeFlashcards = _convertFlashcards.value,
                includePyqLink = _linkPyq.value
            ).onSuccess { rawNotes ->
                // Parse the raw AI text into structured GeneratedNote
                val lines = rawNotes.split("\n").filter { it.isNotBlank() }
                val bullets = lines.filter { it.trimStart().startsWith("•") || it.trimStart().startsWith("-") }
                    .map { it.trimStart('-', '•', ' ') }
                    .take(8)
                val summary = lines.firstOrNull { it.length > 50 && !it.startsWith("•") && !it.startsWith("-") }
                    ?: "AI-generated notes for ${_selectedSubject.value}."

                val generated = GeneratedNote(
                    title = "AI Notes: ${_selectedSubject.value} — ${_selectedExam.value}",
                    summary = summary,
                    bullets = bullets.ifEmpty { lines.take(5) },
                    highlights = listOf()
                )
                _generationState.value = GenerationState.Success(generated)

                // Add to recent
                val newRecent = RecentNoteItem(
                    id = UUID.randomUUID().toString(),
                    title = generated.title,
                    timeAgo = "Just now",
                    subject = _selectedSubject.value,
                    exam = _selectedExam.value
                )
                _recentNotes.value = listOf(newRecent) + _recentNotes.value

            }.onFailure { error ->
                _generationState.value = GenerationState.Error(
                    "Failed to generate notes: ${error.message ?: "Network error"}"
                )
            }
        }
    }

    private fun loadRecentNotes() {
        _recentNotes.value = listOf(
            RecentNoteItem("1", "Fundamental Rights Summary", "2 hours ago", "Polity", "UPSC"),
            RecentNoteItem("2", "Percentage Tricks", "1 day ago", "Quantitative Aptitude", "SSC")
        )
    }

    fun clearState() {
        _generationState.value = GenerationState.Idle
    }
}
