package com.example.simplenote.network

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.simplenote.local.RefreshRequest

class TokenRefreshWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val prefs = applicationContext.getSharedPreferences("auth", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null) ?: return Result.failure()

        return try {
            val response = RetrofitClient.apiService.refreshToken(RefreshRequest(refreshToken))
            if (response.isSuccessful) {
                val newAccess = response.body()?.access ?: return Result.failure()
                RetrofitClient.refreshAccessToken(newAccess)
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
