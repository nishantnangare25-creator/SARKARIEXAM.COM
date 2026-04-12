package com.sarkari.exam.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL_OPENROUTER = "https://openrouter.ai/api/v1/"
    private const val BASE_URL_GROQ = "https://api.groq.com/openai/v1/"

    val openRouterApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_OPENROUTER)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val groqApi: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL_GROQ)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
