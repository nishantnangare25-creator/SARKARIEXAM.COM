package com.sarkari.exam.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch

data class SubjectScores(val name: String, val score: Int, val color: Long)

class AnalyticsViewModel : ViewModel() {
    private val API_KEY = "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"

    val accuracy = mutableStateOf(78)
    val consistency = mutableStateOf(92)
    val completion = mutableStateOf(45)

    val subjects = listOf(
        SubjectScores("Indian Polity", 85, 0xFF2563EB),
        SubjectScores("History & Culture", 62, 0xFFF97316),
        SubjectScores("Geography", 74, 0xFF10B981),
        SubjectScores("Economy", 48, 0xFFEF4444),
        SubjectScores("Current Affairs", 91, 0xFF8B5CF6)
    )

    val analysisReport = mutableStateOf<String?>(null)
    val isAnalyzing = mutableStateOf(false)
    val error = mutableStateOf<String?>(null)

    fun fetchDeepDiveAnalysis(exam: String = "UPSC") {
        isAnalyzing.value = true
        error.value = null

        viewModelScope.launch {
            try {
                // Mock generating an AI payload
                val summaryData = subjects.joinToString { "${it.name}: ${it.score}%" }
                val prompt = "Analyze the student's performance for $exam exam based on these scores: $summaryData. Accuracy is ${accuracy.value}%. Provide a 2 paragraph study motivation and tactical advice."

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = listOf(
                        Message(role = "system", content = "You are a top-tier Indian Sarkari Exam counselor."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 800
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val rawText = response.body()!!.choices.firstOrNull()?.message?.content ?: "Report generation failed."
                    analysisReport.value = rawText.replace("**", "").replace("##", "")
                } else {
                    error.value = "Failed to fetch AI Deep Dive."
                }
            } catch (e: Exception) {
                error.value = "Network Error: ${e.message}"
            } finally {
                isAnalyzing.value = false
            }
        }
    }
}
