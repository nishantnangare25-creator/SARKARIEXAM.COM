package com.sarkari.exam.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class CurrentAffairsItem(
    val id: String,
    val title: String,
    val content: String,
    val date: String
)

data class PdfItem(
    val id: String,
    val title: String,
    val url: String,
    val category: String
)

data class BlogItem(
    val id: String,
    val title: String,
    val content: String,
    val author: String
)

data class AiMessage(
    val role: String,
    val content: String
)

data class AiOptions(
    val temperature: Double? = 0.7,
    val max_tokens: Int? = 4000
)

data class CloudflareAiRequest(
    val action: String = "ai",
    val messages: List<AiMessage>,
    val options: AiOptions = AiOptions()
)

data class CloudflareAiResponse(
    val content: String?,
    val error: String?
)

interface SarkariApiService {

    @GET("api/v1/current-affairs")
    suspend fun getCurrentAffairs(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<CurrentAffairsItem>>

    @GET("api/v1/pdfs")
    suspend fun getPdfs(
        @Query("category") category: String? = null
    ): Response<List<PdfItem>>

    @GET("api/v1/blogs")
    suspend fun getBlogs(): Response<List<BlogItem>>

    // Cloudflare Worker AI Endpoint
    @POST("/")
    suspend fun getAiCompletion(
        @Body request: CloudflareAiRequest
    ): Response<CloudflareAiResponse>
}
