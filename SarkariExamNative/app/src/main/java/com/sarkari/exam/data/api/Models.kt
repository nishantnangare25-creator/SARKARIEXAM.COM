package com.sarkari.exam.data.api

import com.google.gson.annotations.SerializedName

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    @SerializedName("max_tokens") val maxTokens: Int = 1500,
    val temperature: Double = 0.7
)

data class OpenRouterResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

data class MockTestResult(
    val questions: List<MockQuestion>
)

data class MockQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String
)
