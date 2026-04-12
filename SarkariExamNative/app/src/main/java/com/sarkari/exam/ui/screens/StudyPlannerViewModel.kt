package com.sarkari.exam.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch

class StudyPlannerViewModel : ViewModel() {
    private val API_KEY = "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" // Testing key

    private val _exam = mutableStateOf("ssc_cgl")
    val exam: State<String> = _exam

    private val _hours = mutableStateOf(4)
    val hours: State<Int> = _hours

    private val _level = mutableStateOf("beginner")
    val level: State<String> = _level

    private val _plan = mutableStateOf<String?>(null)
    val plan: State<String?> = _plan

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun setExam(value: String) { _exam.value = value }
    fun setHours(value: Int) { _hours.value = value }
    fun setLevel(value: String) { _level.value = value }

    fun generatePlan() {
        _isLoading.value = true
        _error.value = null
        _plan.value = null
        
        viewModelScope.launch {
            try {
                val prompt = "Create a structured daily study plan for the ${_exam.value} exam. The student can study for ${_hours.value} hours a day and is at a ${_level.value} level. Provide a concise markdown schedule."

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = listOf(
                        Message(role = "system", content = "You are an expert exam counselor."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 1500
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val rawText = response.body()!!.choices.firstOrNull()?.message?.content ?: "Plan generation failed."
                    _plan.value = rawText.replace("**", "").replace("##", "") // simple markdown cleanup for native display
                } else {
                    _error.value = "API Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Network Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
