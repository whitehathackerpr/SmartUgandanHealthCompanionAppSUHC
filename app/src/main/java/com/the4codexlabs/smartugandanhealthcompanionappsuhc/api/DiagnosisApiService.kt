package com.the4codexlabs.smartugandanhealthcompanionappsuhc.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Data class for the diagnosis request.
 */
data class DiagnosisRequest(
    val symptoms: List<String>
)

/**
 * Data class for the diagnosis response.
 */
data class DiagnosisResponse(
    val condition: String,
    val recommendation: String,
    val confidence: Int
)

/**
 * Retrofit service interface for the diagnosis API.
 */
interface DiagnosisApiService {
    @POST("diagnose")
    suspend fun diagnose(@Body request: DiagnosisRequest): Response<DiagnosisResponse>
}

/**
 * Singleton object to provide the API service.
 */
object DiagnosisApi {
    private const val BASE_URL = "http://10.0.2.2:5000/" // Android emulator localhost
    
    private val retrofit by lazy {
        retrofit2.Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .client(okhttp3.OkHttpClient.Builder()
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build())
            .build()
    }
    
    val service: DiagnosisApiService by lazy {
        retrofit.create(DiagnosisApiService::class.java)
    }
}