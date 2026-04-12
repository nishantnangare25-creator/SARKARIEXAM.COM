package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch

class PastPaperAnalyzerViewModel : ViewModel() {
    private val API_KEY = "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"

    val textInput = mutableStateOf("")
    val examInput = mutableStateOf("upsc")
    val analysisReport = mutableStateOf<String?>(null)
    val isAnalyzing = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun analyzePaper() {
        if (textInput.value.isBlank()) return
        isAnalyzing.value = true
        error.value = null
        
        viewModelScope.launch {
            try {
                val prompt = "Analyze this past paper structure and provide insights on topic weightings, difficulty level, and important areas to focus on for the ${examInput.value} exam:\n\n${textInput.value.take(1000)}" // Truncated for safety

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = listOf(
                        Message(role = "system", content = "You are an expert exam analyzer for Sarkari jobs in India."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 1000
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val rawText = response.body()!!.choices.firstOrNull()?.message?.content ?: "Analysis failed."
                    analysisReport.value = rawText.replace("**", "").replace("##", "")
                } else {
                    error.value = "Failed to fetch AI analysis."
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isAnalyzing.value = false
            }
        }
    }
}
