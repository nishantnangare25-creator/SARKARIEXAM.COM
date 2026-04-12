package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch

class InteractiveTutorViewModel : ViewModel() {
    private val API_KEY = "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" // Testing key

    val messages = mutableStateListOf<Message>()
    val currentInput = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun sendMessage() {
        val userText = currentInput.value.trim()
        if (userText.isEmpty()) return

        messages.add(Message(role = "user", content = userText))
        currentInput.value = ""
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            try {
                // Construct message history for AI context
                val aiMessages = mutableListOf(
                    Message(role = "system", content = "You are Riya AI, an expert exam tutor for Indian government exams. Provide helpful, structured, and encouraging responses.")
                )
                aiMessages.addAll(messages)

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = aiMessages,
                    maxTokens = 1500
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val aiResponseContent = response.body()!!.choices.firstOrNull()?.message?.content ?: "Sorry, I could not process that request."
                    messages.add(Message(role = "assistant", content = aiResponseContent.replace("**", ""))) // basic markdown strip
                } else {
                    error.value = "Failed to connect to Riya AI."
                    messages.removeLast() // remove user message if failed
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
                messages.removeLast()
            } finally {
                isLoading.value = false
            }
        }
    }

    fun clearChat() {
        messages.clear()
        error.value = null
    }
}
