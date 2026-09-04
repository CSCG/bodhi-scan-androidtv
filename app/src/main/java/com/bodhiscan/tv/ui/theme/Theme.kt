package com.bodhiscan.tv.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BodhiCyan = Color(0xFF00A8FF)
val BodhiBackground = Color(0xFF0F1115)
val BodhiCardBg = Color(0xFF1B2230)
val BodhiCardInner = Color(0xFF121824)
val BodhiCardFocused = Color(0xFF1E2B3C)
val BodhiErrorRed = Color(0xFFFF3333)
val BodhiTextWhite = Color(0xFFFFFFFF)
val BodhiTextGrey = Color(0xFFAAAAAA)
val BodhiTextLightGrey = Color(0xFFCCCCCC)
val BodhiTextDarkGrey = Color(0xFF888888)

private val DarkColorScheme = darkColorScheme(
    primary = BodhiCyan,
    onPrimary = Color.Black,
    primaryContainer = BodhiCardFocused,
    onPrimaryContainer = BodhiCyan,
    background = BodhiBackground,
    onBackground = BodhiTextWhite,
    surface = BodhiCardBg,
    onSurface = BodhiTextWhite,
    error = BodhiErrorRed,
    onError = Color.White
)

@Composable
fun BodhiScanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
