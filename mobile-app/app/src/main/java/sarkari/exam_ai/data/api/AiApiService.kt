package sarkari.exam_ai.data.api

import sarkari.exam_ai.data.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AiApiService {
    
    // Groq API Endpoint
    @POST("v1/chat/completions")
    suspend fun getGroqCompletion(
        @Header("Authorization") auth: String,
        @Body request: AiRequest
    ): Response<AiResponse>

    // Gemini API style (Simplified for GSON)
    // Note: Gemini has a different body structure, usually handled by a wrapper
}

// Minimal wrapper for Gemini's specific JSON structure
data class GeminiRequest(
    val contents: List<GeminiContent>
)
data class GeminiContent(
    val parts: List<GeminiPart>
)
data class GeminiPart(
    val text: String
)
data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)
data class GeminiCandidate(
    val content: GeminiContent?
)

interface GeminiApiService {
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    suspend fun getGeminiCompletion(
        @retrofit2.http.Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): Response<GeminiResponse>
}
