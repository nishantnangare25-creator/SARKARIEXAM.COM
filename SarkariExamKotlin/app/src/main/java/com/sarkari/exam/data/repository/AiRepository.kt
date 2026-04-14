package com.sarkari.exam.data.repository

import com.google.gson.Gson
import com.sarkari.exam.data.api.*
import com.sarkari.exam.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AiRepository {
    
    private val groqRetrofit = Retrofit.Builder()
        .baseUrl("https://api.groq.com/openai/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(AiApiService::class.java)

    private val geminiRetrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApiService::class.java)

    private val gson = Gson()
    
    // Fallback logic implemented natively
    suspend fun getAiResponse(messages: List<ChatMessage>, apiKey: String, provider: String = "groq"): String? = withContext(Dispatchers.IO) {
        try {
            if (provider == "groq") {
                val response = groqRetrofit.getGroqCompletion("Bearer $apiKey", AiRequest("llama-3.3-70b-versatile", messages))
                if (response.isSuccessful) {
                    return@withContext response.body()?.choices?.firstOrNull()?.message?.content
                }
            } else {
                val geminiMsg = messages.map { GeminiContent(listOf(GeminiPart(it.content))) }
                val response = geminiRetrofit.getGeminiCompletion(apiKey, GeminiRequest(geminiMsg))
                if (response.isSuccessful) {
                    return@withContext response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

    // Logic to parse AI text into Question objects (Native version of parseTextToQuestions)
    fun parseQuestions(text: String): List<Question> {
        val questions = mutableListOf<Question>()
        try {
            val blocks = text.split(Regex("(?:^|\\n)\\s*(?:Q|Question)\\s*\\d*[:.]?\\s*", RegexOption.IGNORE_CASE))
                .filter { it.isNotBlank() }
            
            blocks.forEachIndexed { index, block ->
                val lines = block.lines().filter { it.isNotBlank() }
                if (lines.size < 3) return@forEachIndexed

                var questionStr = ""
                val options = mutableListOf<String>()
                var correctAnswer = ""
                var explanation = ""
                var mode = "Q"

                lines.forEach { line ->
                    val trimmed = line.trim()
                    val optionMatch = Regex("^[\(]?([A-E])[\).:]\\s*(.+)").find(trimmed)
                    
                    if (optionMatch != null) {
                        mode = "O"
                        options.add(optionMatch.groupValues[2].trim())
                    } else if (trimmed.startsWith("Answer:", ignoreCase = true)) {
                        mode = "A"
                        val ansStr = trimmed.removePrefix("Answer:").trim()
                        val letterMatch = Regex("([A-E])").find(ansStr)
                        if (letterMatch != null && options.isNotEmpty()) {
                            val letterIndex = letterMatch.groupValues[1].uppercase()[0] - 'A'
                            if (letterIndex in options.indices) {
                                correctAnswer = options[letterIndex]
                            } else {
                                correctAnswer = ansStr
                            }
                        } else {
                            correctAnswer = ansStr
                        }
                    } else if (trimmed.startsWith("Explanation:", ignoreCase = true) || mode == "E") {
                        if (mode != "E") {
                            mode = "E"
                            explanation = trimmed.removePrefix("Explanation:").trim()
                        } else {
                            explanation += "\n$trimmed"
                        }
                    } else {
                        if (mode == "Q") questionStr += (if (questionStr.isEmpty()) "" else "\n") + trimmed
                        else if (mode == "E") explanation += "\n$trimmed"
                    }
                }

                if (questionStr.isNotBlank() && options.size >= 2 && correctAnswer.isNotBlank()) {
                    questions.add(Question(index + 1, questionStr, options, correctAnswer, explanation))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return questions
    }
}
