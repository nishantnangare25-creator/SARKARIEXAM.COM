package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch

class NotesGeneratorViewModel : ViewModel() {
    private val API_KEY = "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"

    val exam = mutableStateOf("UPSC")
    val subject = mutableStateOf("Polity")
    val topics = mutableStateOf("")
    val notesResult = mutableStateOf<String?>(null)
    
    val isGenerating = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun generateNotes() {
        if (topics.value.isBlank()) return
        isGenerating.value = true
        error.value = null

        viewModelScope.launch {
            try {
                val prompt = "Create highly detailed, structured revision notes for ${exam.value} exam focusing on ${subject.value}. Specific topics: ${topics.value}. Include bullet points and easy-to-remember facts."

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = listOf(
                        Message(role = "system", content = "You are an expert exam note generator for Indian competitive exams."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 1200
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val rawText = response.body()!!.choices.firstOrNull()?.message?.content ?: "Generation failed."
                    notesResult.value = rawText.replace("**", "") // simplified markdown
                } else {
                    error.value = "Failed to fetch Notes."
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isGenerating.value = false
            }
        }
    }
}
