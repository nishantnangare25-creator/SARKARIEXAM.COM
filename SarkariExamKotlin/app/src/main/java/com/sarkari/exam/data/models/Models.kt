package com.sarkari.exam.data.models

import com.google.gson.annotations.SerializedName

// --- Artificial Intelligence Models ---

data class ChatMessage(
    @SerializedName("role") val role: String,
    @SerializedName("content") val content: String
)

data class AiRequest(
    @SerializedName("model") val model: String,
    @SerializedName("messages") val messages: List<ChatMessage>,
    @SerializedName("temperature") val temperature: Double = 0.7,
    @SerializedName("max_tokens") val maxTokens: Int = 1500
)

data class AiResponse(
    @SerializedName("choices") val choices: List<Choice>
)

data class Choice(
    @SerializedName("message") val message: ChatMessage
)

// --- Mock Test Models ---

data class Question(
    @SerializedName("id") val id: Int? = null,
    @SerializedName("question") val question: String,
    @SerializedName("options") val options: List<String>,
    @SerializedName("correctAnswer") val correctAnswer: String,
    @SerializedName("explanation") val explanation: String? = null
)

data class MockQuestionsResponse(
    val questions: List<Question>
)

data class TestResult(
    val exam: String,
    val subject: String,
    val score: Int,
    val total: Int,
    val timestamp: Long = System.currentTimeMillis()
)
