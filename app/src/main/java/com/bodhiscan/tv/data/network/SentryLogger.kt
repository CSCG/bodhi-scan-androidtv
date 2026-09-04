package com.bodhiscan.tv.data.network

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object SentryLogger {
    private const val TAG = "BodhiScanSentry"
    private const val SENTRY_URL =
        "https://o4506537273393152.ingest.us.sentry.io/api/4512023646240768/store/?sentry_key=69210eb4f5b937ecc5b25f5f5abf2143&sentry_version=7"

    private val httpClient by lazy { OkHttpClient() }
    private val gson by lazy { Gson() }
    private val scope = CoroutineScope(Dispatchers.IO)

    fun logError(message: String, extraData: Map<String, Any?> = emptyMap()) {
        Log.e(TAG, "Logging error to Sentry: $message | $extraData")
        scope.launch {
            try {
                val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                val eventId = UUID.randomUUID().toString().replace("-", "")

                val payload = mapOf(
                    "event_id" to eventId,
                    "timestamp" to isoFormat.format(Date()),
                    "platform" to "android",
                    "level" to "error",
                    "logger" to "android_tv_app",
                    "message" to mapOf("formatted" to message),
                    "extra" to extraData
                )

                val requestBody = gson.toJson(payload)
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url(SENTRY_URL)
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .build()

                httpClient.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "Sentry response not successful: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Failed to send error to Sentry: ${e.message}")
            }
        }
    }
}
