package com.sarkari.exam.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiService {
    @POST("chat/completions")
    suspend fun generateMockQuestions(
        @Header("Authorization") authHeader: String,
        @Body request: OpenRouterRequest
    ): Response<OpenRouterResponse>
}
