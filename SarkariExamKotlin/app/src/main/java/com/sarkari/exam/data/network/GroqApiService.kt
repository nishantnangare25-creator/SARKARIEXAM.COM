package com.sarkari.exam.data.network

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class GroqMessage(
    val role: String, // "user", "assistant", "system"
    val content: String
)

data class GroqChatRequest(
    val model: String = "llama3-8b-8192",
    val messages: List<GroqMessage>,
    val temperature: Double = 0.7
)

data class GroqChoice(
    val message: GroqMessage
)

data class GroqChatResponse(
    val choices: List<GroqChoice>
)

interface GroqApiService {
    @POST("chat/completions")
    suspend fun createChatCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: GroqChatRequest
    ): GroqChatResponse
}
