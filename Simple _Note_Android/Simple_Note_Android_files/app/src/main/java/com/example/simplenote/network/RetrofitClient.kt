package com.example.simplenote.network

import com.example.simplenote.local.RefreshRequest
import com.example.simplenote.local.RefreshResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://simple.darkube.app/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    /**
     * Helper function to refresh the access token using a refresh token.
     * Returns the new access token as a string, or null if failed.
     */
    suspend fun refreshAccessToken(refreshToken: String): String? {
        return try {
            val response: Response<RefreshResponse> = apiService.refreshToken(RefreshRequest(refreshToken))
            if (response.isSuccessful) {
                response.body()?.access
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
