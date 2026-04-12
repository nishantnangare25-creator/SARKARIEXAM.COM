package com.sarkari.exam.ui.screens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarkari.exam.data.api.Message
import com.sarkari.exam.data.api.MockQuestion
import com.sarkari.exam.data.api.OpenRouterRequest
import com.sarkari.exam.data.api.RetrofitClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class MockTestViewModel : ViewModel() {
    private val API_KEY = "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" // Testing key

    private val _questions = mutableStateOf<List<MockQuestion>>(emptyList())
    val questions: State<List<MockQuestion>> = _questions

    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: State<Int> = _currentQuestionIndex

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _isStarted = mutableStateOf(false)
    val isStarted: State<Boolean> = _isStarted

    private val _isFinished = mutableStateOf(false)
    val isFinished: State<Boolean> = _isFinished

    private val _score = mutableStateOf(0)
    val score: State<Int> = _score

    private val _selectedAnswer = mutableStateOf<String?>(null)
    val selectedAnswer: State<String?> = _selectedAnswer

    private val _showExplanation = mutableStateOf(false)
    val showExplanation: State<Boolean> = _showExplanation

    fun startQuiz(exam: String, subject: String) {
        _isLoading.value = true
        _error.value = null
        _isFinished.value = false
        _score.value = 0
        
        viewModelScope.launch {
            try {
                val prompt = "Generate exactly 5 practice MCQ questions for $exam exam. Subject: $subject. Return strictly valid JSON object with an array 'questions' containing objects with fields 'question', array 'options', 'correctAnswer', and 'explanation'."

                val request = OpenRouterRequest(
                    model = "google/gemini-2.5-flash",
                    messages = listOf(
                        Message(role = "system", content = "You are an API returning valid JSON."),
                        Message(role = "user", content = prompt)
                    ),
                    maxTokens = 1500
                )

                val response = RetrofitClient.openRouterApi.generateMockQuestions("Bearer $API_KEY", request)
                if (response.isSuccessful && response.body() != null) {
                    val rawText = response.body()!!.choices.firstOrNull()?.message?.content ?: "{}"
                    val parsedQuestions = parseRawJsonToQuestions(rawText)
                    
                    if (parsedQuestions.isNotEmpty()) {
                        _questions.value = parsedQuestions
                        _currentQuestionIndex.value = 0
                        _isStarted.value = true
                        _selectedAnswer.value = null
                        _showExplanation.value = false
                    } else {
                        _error.value = "Failed to extract valid questions from AI."
                    }
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

    fun submitAnswer(answer: String) {
        if (_selectedAnswer.value != null || _questions.value.isEmpty()) return
        
        _selectedAnswer.value = answer
        _showExplanation.value = true
        
        val currentQ = _questions.value[_currentQuestionIndex.value]
        if (answer == currentQ.correctAnswer) {
            _score.value += 1
        }
    }

    fun nextQuestion() {
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.value += 1
            _selectedAnswer.value = null
            _showExplanation.value = false
        } else {
            _isFinished.value = true
        }
    }

    fun resetQuiz() {
        _isStarted.value = false
        _isFinished.value = false
        _questions.value = emptyList()
        _currentQuestionIndex.value = 0
        _score.value = 0
    }

    private fun parseRawJsonToQuestions(text: String): List<MockQuestion> {
        val resultList = mutableListOf<MockQuestion>()
        try {
            var rawJson = text
            if (rawJson.contains("```json")) rawJson = rawJson.substringAfter("```json").substringBefore("```")
            else if (rawJson.contains("```")) rawJson = rawJson.replace("```", "")
            
            val jsonObject = JSONObject(rawJson)
            val jsonArray = jsonObject.optJSONArray("questions")
            if (jsonArray != null) {
                for (i in 0 until jsonArray.length()) {
                    val qObj = jsonArray.getJSONObject(i)
                    val optsArray = qObj.getJSONArray("options")
                    val optsList = mutableListOf<String>()
                    for (j in 0 until optsArray.length()) {
                        optsList.add(optsArray.getString(j))
                    }
                    resultList.add(
                        MockQuestion(
                            id = i.toString(),
                            question = qObj.getString("question"),
                            options = optsList,
                            correctAnswer = qObj.getString("correctAnswer"),
                            explanation = qObj.getString("explanation")
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultList
    }
}
