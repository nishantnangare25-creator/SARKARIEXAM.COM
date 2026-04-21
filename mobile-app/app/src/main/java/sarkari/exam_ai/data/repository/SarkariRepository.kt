package sarkari.exam_ai.data.repository

import sarkari.exam_ai.data.api.RetrofitClient
import sarkari.exam_ai.data.models.BlogItem
import sarkari.exam_ai.data.models.CurrentAffairsItem
import sarkari.exam_ai.data.models.PdfItem

class SarkariRepository {
    private val apiService = RetrofitClient.sarkariApiService

    suspend fun getCurrentAffairs(examCategory: String? = null, language: String? = null): Result<List<CurrentAffairsItem>> {
        return try {
            val response = apiService.getCurrentAffairs(examCategory = examCategory, language = language)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching Current Affairs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPdfs(examCategory: String? = null, language: String? = null, type: String? = null): Result<List<PdfItem>> {
        return try {
            val response = apiService.getPdfs(examCategory = examCategory, language = language, type = type)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching PDFs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBlogs(examCategory: String? = null, language: String? = null): Result<List<BlogItem>> {
        return try {
            val response = apiService.getBlogs(examCategory = examCategory, language = language)
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("Error fetching Blogs: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
