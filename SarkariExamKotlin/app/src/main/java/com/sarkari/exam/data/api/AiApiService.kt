package com.sarkari.exam.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

// Data classes for request and response
data class OpenRouterRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
    val max_tokens: Int = 1500
)

data class Message(
    val role: String,
    val content: String
)

data class OpenRouterResponse(
    val choices: List<Choice>
) {
    data class Choice(val message: Message)
}

// Retrofit Interface
interface AiApiService {
    
    // Primary API endpoint for Groq / OpenRouter
    @POST("v1/chat/completions")
    suspend fun generateCompletion(
        @Header("Authorization") authHeader: String,
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>
    
}
