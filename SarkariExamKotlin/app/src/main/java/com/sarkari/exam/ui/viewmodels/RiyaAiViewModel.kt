package com.sarkari.exam.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.repository.AiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class RiyaAiViewModel(private val aiRepository: AiRepository = AiRepository()) : ViewModel() {

    // Dynamic Options
    val examsList = listOf("SSC CGL 2024", "UPSC Civil Services", "Banking", "Railway")
    private val subjectMap = mapOf(
        "SSC CGL 2024" to listOf("Quantitative Aptitude", "Reasoning", "English", "General Awareness"),
        "UPSC Civil Services" to listOf("History", "Geography", "Polity", "Economy", "CSAT"),
        "Banking" to listOf("Quant", "Reasoning", "English", "Banking Awareness"),
        "Railway" to listOf("Maths", "Reasoning", "General Science")
    )

    private val _selectedExam = MutableStateFlow(examsList[0])
    val selectedExam: StateFlow<String> = _selectedExam.asStateFlow()

    private val _availableSubjects = MutableStateFlow(subjectMap[examsList[0]] ?: emptyList())
    val availableSubjects: StateFlow<List<String>> = _availableSubjects.asStateFlow()

    private val _selectedSubject = MutableStateFlow(_availableSubjects.value.firstOrNull() ?: "")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    // Chat State
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    val quickActions = listOf("Explain PYQ", "Generate Notes", "Create Quiz", "Study Plan")

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

    fun sendQuickAction(action: String) {
        sendMessage(action)
    }

    fun sendMessage(text: String = _inputText.value) {
        if (text.isBlank()) return

        // Add user message
        val userMsg = ChatMessage(text = text.trim(), isFromUser = true)
        _messages.value = _messages.value + userMsg
        _inputText.value = ""

        // Get AI response
        viewModelScope.launch {
            _isAiTyping.value = true
            
            val aiResponseText = try {
                aiRepository.getAiResponse(text, _selectedExam.value, _selectedSubject.value)
            } catch (e: Exception) {
                "Sorry, I am having trouble connecting to the AI service."
            }
            
            val aiMsg = ChatMessage(text = aiResponseText, isFromUser = false)
            
            _messages.value = _messages.value + aiMsg
            _isAiTyping.value = false
        }
    }

    fun clearChat() {
        _messages.value = emptyList()
    }
}
