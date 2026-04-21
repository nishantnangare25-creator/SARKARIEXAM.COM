package sarkari.exam_ai.data.api

import sarkari.exam_ai.data.models.BlogItem
import sarkari.exam_ai.data.models.CurrentAffairsItem
import sarkari.exam_ai.data.models.PdfItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SarkariApiService {

    @GET("api/v1/current-affairs")
    suspend fun getCurrentAffairs(
        @Query("exam") examCategory: String? = null,
        @Query("lang") language: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<List<CurrentAffairsItem>>

    @GET("api/v1/pdfs")
    suspend fun getPdfs(
        @Query("exam") examCategory: String? = null,
        @Query("lang") language: String? = null,
        @Query("category") type: String? = null
    ): Response<List<PdfItem>>

    @GET("api/v1/blogs")
    suspend fun getBlogs(
        @Query("exam") examCategory: String? = null,
        @Query("lang") language: String? = null
    ): Response<List<BlogItem>>

}
