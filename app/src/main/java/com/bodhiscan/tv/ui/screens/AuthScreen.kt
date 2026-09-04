package com.bodhiscan.tv.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodhiscan.tv.R
import com.bodhiscan.tv.ui.UiState
import com.bodhiscan.tv.ui.theme.BodhiBackground
import com.bodhiscan.tv.ui.theme.BodhiCardBg
import com.bodhiscan.tv.ui.theme.BodhiCardFocused
import com.bodhiscan.tv.ui.theme.BodhiCardInner
import com.bodhiscan.tv.ui.theme.BodhiCyan
import com.bodhiscan.tv.ui.theme.BodhiErrorRed
import com.bodhiscan.tv.ui.theme.BodhiTextGrey
import com.bodhiscan.tv.ui.theme.BodhiTextLightGrey
import com.bodhiscan.tv.ui.theme.BodhiTextWhite

@Composable
fun AuthScreen(
    state: UiState,
    onDigitEntered: (Char) -> Unit,
    onDeleteDigit: () -> Unit,
    onClearDigits: () -> Unit,
    onLoadDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BodhiBackground)
            .testTag("auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Server Announcement Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.announcementHeadline.isNotBlank()) {
                    Text(
                        text = state.announcementHeadline.uppercase(),
                        color = BodhiCyan,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("announcement_headline")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                if (state.announcementMessage.isNotBlank()) {
                    Text(
                        text = state.announcementMessage,
                        color = BodhiTextGrey,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.testTag("announcement_message")
                    )
                }
            }

            // Main Brand and Prompt
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.auth_title),
                    color = BodhiCyan,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("auth_title")
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_subtitle),
                    color = BodhiTextLightGrey,
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("auth_subtitle")
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 6-digit PIN Slot Display
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("pin_display_row")
                ) {
                    for (i in 0 until 6) {
                        val isFilled = i < state.pin.length
                        val isCurrent = i == state.pin.length && !state.isLoading
                        val charAtSlot = if (isFilled) state.pin[i].toString() else ""

                        PinSlot(
                            digit = charAtSlot,
                            isFilled = isFilled,
                            isCurrent = isCurrent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Loading Spinner or Error Message
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.isLoading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = BodhiCyan,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.status_authenticating),
                                color = BodhiCyan,
                                fontSize = 13.sp
                            )
                        }
                    } else if (!state.statusError.isNullOrBlank()) {
                        Text(
                            text = state.statusError,
                            color = BodhiErrorRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.testTag("status_error_label")
                        )
                    }
                }

                // 3x4 PinPad Grid (Remote D-pad friendly)
                PinPadGrid(
                    isEnabled = !state.isLoading,
                    onDigitClick = onDigitEntered,
                    onDeleteClick = onDeleteDigit,
                    onClearClick = onClearDigits
                )
            }

            // Footer with Bodhi Industries Logo and Demo option
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Demo button on the left for convenient testing
                DemoButton(onClick = onLoadDemo)

                // Bodhi Industries Logo at Center
                Image(
                    painter = painterResource(R.drawable.bodhi_industries_logo),
                    contentDescription = stringResource(R.string.brand_footer_description),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("bodhi_footer_logo")
                )

                // Spacer to balance layout
                Spacer(modifier = Modifier.width(90.dp))
            }
        }
    }
}

@Composable
private fun PinSlot(
    digit: String,
    isFilled: Boolean,
    isCurrent: Boolean
) {
    val borderColor = when {
        isFilled -> BodhiCyan
        isCurrent -> BodhiCyan.copy(alpha = 0.8f)
        else -> Color(0xFF2A3445)
    }

    val bgColor = if (isFilled) BodhiCardFocused else BodhiCardInner

    Box(
        modifier = Modifier
            .size(width = 46.dp, height = 54.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = if (isFilled || isCurrent) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isFilled) {
            Text(
                text = digit,
                color = BodhiCyan,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        } else if (isCurrent) {
            Box(
                modifier = Modifier
                    .size(width = 16.dp, height = 2.dp)
                    .background(BodhiCyan.copy(alpha = 0.7f))
            )
        }
    }
}

@Composable
private fun PinPadGrid(
    isEnabled: Boolean,
    onDigitClick: (Char) -> Unit,
    onDeleteClick: () -> Unit,
    onClearClick: () -> Unit
) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("CLR", "0", "DEL")
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.testTag("pinpad_grid")
    ) {
        keys.forEachIndexed { rowIndex, rowKeys ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowKeys.forEach { keyLabel ->
                    PinPadKey(
                        label = keyLabel,
                        isEnabled = isEnabled,
                        onClick = {
                            when (keyLabel) {
                                "CLR" -> onClearClick()
                                "DEL" -> onDeleteClick()
                                else -> onDigitClick(keyLabel[0])
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PinPadKey(
    label: String,
    isEnabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.08f else 1.0f,
        label = "keyScale"
    )

    val bgColor = when {
        isFocused -> BodhiCardFocused
        else -> BodhiCardBg
    }

    val borderColor = when {
        isFocused -> BodhiCyan
        else -> Color(0xFF263242)
    }

    Box(
        modifier = Modifier
            .size(width = 90.dp, height = 44.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .focusable(enabled = isEnabled, interactionSource = interactionSource)
            .clickable(enabled = isEnabled) { onClick() }
            .testTag("pin_key_$label"),
        contentAlignment = Alignment.Center
    ) {
        when (label) {
            "DEL" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = stringResource(R.string.button_backspace),
                    tint = if (isFocused) BodhiCyan else BodhiTextLightGrey,
                    modifier = Modifier.size(18.dp)
                )
            }
            "CLR" -> {
                Text(
                    text = stringResource(R.string.button_clear),
                    color = if (isFocused) BodhiCyan else BodhiTextLightGrey,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            else -> {
                Text(
                    text = label,
                    color = if (isFocused) BodhiCyan else BodhiTextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DemoButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isFocused) BodhiCardFocused else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isFocused) BodhiCyan else Color(0xFF334155),
                shape = RoundedCornerShape(6.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("demo_mode_button"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Demo Archive",
            color = if (isFocused) BodhiCyan else BodhiTextGrey,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
