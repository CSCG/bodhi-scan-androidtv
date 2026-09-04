package com.bodhiscan.tv.data.model

import com.google.gson.annotations.SerializedName

data class ConfigResponse(
    @SerializedName("headline")
    val headline: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("announcement")
    val announcement: String? = null
)

data class AuthResponse(
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("title")
    val title: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("videos")
    val videos: List<VideoItem>? = null
)

data class VideoItem(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("video_url")
    val videoUrl: String = ""
) {
    /**
     * Extracts tape number faithfully matching the original Roku BrightScript logic:
     * e.g., "Tape 4: High School Graduation" -> "4"
     * or leading digits -> "1", or fallbackIndex
     */
    fun getTapeNumber(fallbackIndex: Int): String {
        if (title.isBlank()) return fallbackIndex.toString()
        val titleLower = title.lowercase()
        val tapePos = titleLower.indexOf("tape")
        if (tapePos >= 0) {
            val remStr = titleLower.substring(tapePos + 4)
            val numBuilder = StringBuilder()
            var foundDigit = false
            for (ch in remStr) {
                if (ch.isDigit()) {
                    numBuilder.append(ch)
                    foundDigit = true
                } else if (foundDigit) {
                    break
                }
            }
            if (numBuilder.isNotEmpty()) {
                return numBuilder.toString()
            }
        }

        // Check if title starts with digits
        val leadingDigits = StringBuilder()
        for (ch in title.trim()) {
            if (ch.isDigit()) {
                leadingDigits.append(ch)
            } else if (leadingDigits.isNotEmpty()) {
                break
            }
        }
        if (leadingDigits.isNotEmpty()) {
            return leadingDigits.toString()
        }

        return fallbackIndex.toString()
    }
}
