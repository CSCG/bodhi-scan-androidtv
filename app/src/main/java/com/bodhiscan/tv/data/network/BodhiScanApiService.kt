package com.bodhiscan.tv.data.network

import android.util.Log
import com.bodhiscan.tv.data.model.AuthResponse
import com.bodhiscan.tv.data.model.ConfigResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class BodhiScanApiService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build(),
    private val gson: Gson = Gson()
) {
    companion object {
        private const val TAG = "BodhiScanApi"
        private const val BASE_CONFIG_URL = "https://streaming.bodhiscan.com/api/config.php"
        private const val BASE_AUTH_URL = "https://streaming.bodhiscan.com/api/auth.php"
    }

    suspend fun fetchConfig(): ConfigResponse = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(BASE_CONFIG_URL)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank()) {
                    try {
                        gson.fromJson(body, ConfigResponse::class.java)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse config JSON", e)
                        SentryLogger.logError("Config JSON Parsing Failed", mapOf("raw_response" to body))
                        fallbackConfig()
                    }
                } else {
                    fallbackConfig()
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Config request failed", e)
            fallbackConfig()
        }
    }

    suspend fun authenticate(code: String): AuthResponse = withContext(Dispatchers.IO) {
        val encodedCode = URLEncoder.encode(code, "UTF-8")
        val fullUrl = "$BASE_AUTH_URL?code=$encodedCode"

        val request = Request.Builder()
            .url(fullUrl)
            .get()
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string()
                if (response.isSuccessful && !body.isNullOrBlank()) {
                    try {
                        val authResp = gson.fromJson(body, AuthResponse::class.java)
                        authResp
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse auth JSON: $body", e)
                        SentryLogger.logError("JSON Parsing Failed", mapOf("raw_response" to body))
                        AuthResponse(
                            success = false,
                            message = "Invalid server response format."
                        )
                    }
                } else {
                    SentryLogger.logError("Network Transfer Failed", mapOf("url" to fullUrl, "code" to response.code))
                    AuthResponse(
                        success = false,
                        message = "Unable to connect to BodhiScan servers."
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Network exception during auth", e)
            SentryLogger.logError("Network Transfer Failed", mapOf("url" to fullUrl, "exception" to (e.message ?: "Unknown")))
            AuthResponse(
                success = false,
                message = "Unable to connect to BodhiScan servers."
            )
        }
    }

    private fun fallbackConfig(): ConfigResponse {
        return ConfigResponse(
            headline = "WELCOME TO BODHISCAN",
            message = "Stream your high-definition digitized family memories."
        )
    }
}
