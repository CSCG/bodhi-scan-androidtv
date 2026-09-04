package com.bodhiscan.tv

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.bodhiscan.tv.ui.AppScreen
import com.bodhiscan.tv.ui.MainViewModel
import com.bodhiscan.tv.ui.screens.AuthScreen
import com.bodhiscan.tv.ui.screens.TapeSelectionScreen
import com.bodhiscan.tv.ui.screens.VideoPlayerScreen
import com.bodhiscan.tv.ui.theme.BodhiBackground
import com.bodhiscan.tv.ui.theme.BodhiScanTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BodhiScanTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BodhiBackground
                ) {
                    BodhiScanApp(viewModel = viewModel)
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val currentScreen = viewModel.uiState.value.currentScreen

        // Hardware numeric keypad & remote number keys support
        if (currentScreen == AppScreen.AUTH) {
            when (keyCode) {
                KeyEvent.KEYCODE_0, KeyEvent.KEYCODE_NUMPAD_0 -> {
                    viewModel.onDigitEntered('0')
                    return true
                }
                KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_NUMPAD_1 -> {
                    viewModel.onDigitEntered('1')
                    return true
                }
                KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_NUMPAD_2 -> {
                    viewModel.onDigitEntered('2')
                    return true
                }
                KeyEvent.KEYCODE_3, KeyEvent.KEYCODE_NUMPAD_3 -> {
                    viewModel.onDigitEntered('3')
                    return true
                }
                KeyEvent.KEYCODE_4, KeyEvent.KEYCODE_NUMPAD_4 -> {
                    viewModel.onDigitEntered('4')
                    return true
                }
                KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_NUMPAD_5 -> {
                    viewModel.onDigitEntered('5')
                    return true
                }
                KeyEvent.KEYCODE_6, KeyEvent.KEYCODE_NUMPAD_6 -> {
                    viewModel.onDigitEntered('6')
                    return true
                }
                KeyEvent.KEYCODE_7, KeyEvent.KEYCODE_NUMPAD_7 -> {
                    viewModel.onDigitEntered('7')
                    return true
                }
                KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_NUMPAD_8 -> {
                    viewModel.onDigitEntered('8')
                    return true
                }
                KeyEvent.KEYCODE_9, KeyEvent.KEYCODE_NUMPAD_9 -> {
                    viewModel.onDigitEntered('9')
                    return true
                }
                KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_CLEAR -> {
                    viewModel.onDeleteDigit()
                    return true
                }
            }
        }

        // Back key behavior matching Roku's onKeyEvent
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when (currentScreen) {
                AppScreen.PLAYER -> {
                    viewModel.onPlayerDismissed()
                    return true
                }
                AppScreen.SELECTION -> {
                    viewModel.onBackToAuth()
                    return true
                }
                AppScreen.AUTH -> {
                    // Standard system back
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }
}

@Composable
fun BodhiScanApp(viewModel: MainViewModel) {
    val state by viewModel.uiState.collectAsState()

    when (state.currentScreen) {
        AppScreen.AUTH -> {
            AuthScreen(
                state = state,
                onDigitEntered = viewModel::onDigitEntered,
                onDeleteDigit = viewModel::onDeleteDigit,
                onClearDigits = viewModel::onClearDigits,
                onLoadDemo = viewModel::loadDemoCollection
            )
        }
        AppScreen.SELECTION -> {
            TapeSelectionScreen(
                state = state,
                onVideoFocused = viewModel::onVideoFocused,
                onVideoSelected = viewModel::onVideoSelected,
                onBackToAuth = viewModel::onBackToAuth
            )
        }
        AppScreen.PLAYER -> {
            state.selectedVideo?.let { video ->
                VideoPlayerScreen(
                    video = video,
                    onDismiss = viewModel::onPlayerDismissed
                )
            } ?: run {
                TapeSelectionScreen(
                    state = state,
                    onVideoFocused = viewModel::onVideoFocused,
                    onVideoSelected = viewModel::onVideoSelected,
                    onBackToAuth = viewModel::onBackToAuth
                )
            }
        }
    }
}
