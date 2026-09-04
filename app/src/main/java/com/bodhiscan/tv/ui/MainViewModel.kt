package com.bodhiscan.tv.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bodhiscan.tv.data.model.VideoItem
import com.bodhiscan.tv.data.network.BodhiScanApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    AUTH,
    SELECTION,
    PLAYER
}

data class UiState(
    val currentScreen: AppScreen = AppScreen.AUTH,
    val announcementHeadline: String = "WELCOME TO BODHISCAN",
    val announcementMessage: String = "Stream your high-definition digitized family memories.",
    val pin: String = "",
    val isLoading: Boolean = false,
    val statusError: String? = null,
    val collectionTitle: String = "",
    val videos: List<VideoItem> = emptyList(),
    val focusedIndex: Int = 0,
    val selectedVideo: VideoItem? = null
)

class MainViewModel(
    private val apiService: BodhiScanApiService = BodhiScanApiService()
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            val config = apiService.fetchConfig()
            _uiState.update { state ->
                state.copy(
                    announcementHeadline = config.headline
                        ?: state.announcementHeadline,
                    announcementMessage = config.message
                        ?: config.announcement
                        ?: state.announcementMessage
                )
            }
        }
    }

    fun onDigitEntered(digit: Char) {
        val currentPin = _uiState.value.pin
        if (currentPin.length < 6 && digit.isDigit()) {
            val newPin = currentPin + digit
            _uiState.update { it.copy(pin = newPin, statusError = null) }
            if (newPin.length == 6) {
                submitPin(newPin)
            }
        }
    }

    fun onDeleteDigit() {
        val currentPin = _uiState.value.pin
        if (currentPin.isNotEmpty()) {
            _uiState.update {
                it.copy(
                    pin = currentPin.dropLast(1),
                    statusError = null
                )
            }
        }
    }

    fun onClearDigits() {
        _uiState.update {
            it.copy(
                pin = "",
                statusError = null
            )
        }
    }

    fun submitPin(pinToSubmit: String = _uiState.value.pin) {
        if (pinToSubmit.length < 6) return

        _uiState.update { it.copy(isLoading = true, statusError = null) }

        viewModelScope.launch {
            val response = apiService.authenticate(pinToSubmit)
            _uiState.update { it.copy(isLoading = false) }

            if (response.success && !response.videos.isNullOrEmpty()) {
                _uiState.update { state ->
                    state.copy(
                        currentScreen = AppScreen.SELECTION,
                        collectionTitle = response.title ?: "Digitized Memories",
                        videos = response.videos,
                        focusedIndex = 0,
                        selectedVideo = null
                    )
                }
            } else {
                // If demo passcode "000000" or special preview is entered, provide sample tapes
                if (pinToSubmit == "000000") {
                    loadDemoCollection()
                } else {
                    val errorMsg = response.message
                        ?: "No digitized memories found for this code."
                    _uiState.update { state ->
                        state.copy(
                            statusError = errorMsg,
                            pin = ""
                        )
                    }
                }
            }
        }
    }

    fun loadDemoCollection() {
        val sampleVideos = listOf(
            VideoItem(
                title = "Tape 1: Summer Vacation 1994",
                description = "Family road trip to Yellowstone and mountain camping footage.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            ),
            VideoItem(
                title = "Tape 2: High School Graduation & Prom",
                description = "Class of 1998 graduation ceremony and celebrations.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            ),
            VideoItem(
                title = "Tape 3: Christmas Morning 1991",
                description = "Opening presents around the tree and family holiday brunch.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
            ),
            VideoItem(
                title = "Tape 4: Little League Championship 1996",
                description = "Championship finals game and trophy presentation banquet.",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
            )
        )
        _uiState.update { state ->
            state.copy(
                currentScreen = AppScreen.SELECTION,
                collectionTitle = "The Miller Family Archive (1991 - 1998)",
                videos = sampleVideos,
                focusedIndex = 0,
                selectedVideo = null,
                statusError = null
            )
        }
    }

    fun onVideoFocused(index: Int) {
        if (index in _uiState.value.videos.indices) {
            _uiState.update { it.copy(focusedIndex = index) }
        }
    }

    fun onVideoSelected(video: VideoItem) {
        _uiState.update {
            it.copy(
                selectedVideo = video,
                currentScreen = AppScreen.PLAYER
            )
        }
    }

    fun onPlayerDismissed() {
        _uiState.update {
            it.copy(
                selectedVideo = null,
                currentScreen = AppScreen.SELECTION
            )
        }
    }

    fun onBackToAuth() {
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.AUTH,
                pin = "",
                statusError = null,
                selectedVideo = null
            )
        }
    }
}
