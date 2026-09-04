package com.bodhiscan.tv.ui.screens

import android.media.MediaPlayer
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.bodhiscan.tv.R
import com.bodhiscan.tv.data.model.VideoItem
import com.bodhiscan.tv.data.network.SentryLogger
import com.bodhiscan.tv.ui.theme.BodhiCardFocused
import com.bodhiscan.tv.ui.theme.BodhiCyan
import com.bodhiscan.tv.ui.theme.BodhiTextGrey
import com.bodhiscan.tv.ui.theme.BodhiTextWhite
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    video: VideoItem,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onDismiss()
    }

    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var showControls by remember { mutableStateOf(true) }
    var currentPositionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(0) }
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }

    // Auto-hide controls timer
    LaunchedEffect(showControls, isPlaying) {
        if (showControls && isPlaying) {
            delay(4500)
            showControls = false
        }
    }

    // Playback position polling
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            videoViewRef?.let { vv ->
                if (vv.isPlaying) {
                    currentPositionMs = vv.currentPosition
                    durationMs = vv.duration.coerceAtLeast(0)
                    isBuffering = false
                }
            }
            delay(500)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { showControls = !showControls }
            .testTag("video_player_screen")
    ) {
        // Native VideoView embedded in Compose
        AndroidView(
            factory = { context ->
                VideoView(context).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setOnPreparedListener { mp ->
                        mp.isLooping = false
                        isBuffering = false
                        durationMs = duration.coerceAtLeast(0)
                        start()
                        isPlaying = true
                    }
                    setOnCompletionListener {
                        isPlaying = false
                        onDismiss()
                    }
                    setOnErrorListener { _, what, extra ->
                        SentryLogger.logError(
                            "Android VideoView Error",
                            mapOf("url" to video.videoUrl, "what" to what, "extra" to extra)
                        )
                        isBuffering = false
                        onDismiss()
                        true
                    }
                    setVideoURI(Uri.parse(video.videoUrl))
                    videoViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        DisposableEffect(Unit) {
            onDispose {
                videoViewRef?.stopPlayback()
                videoViewRef = null
            }
        }

        // Buffering Indicator
        if (isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(54.dp)
                    .align(Alignment.Center)
                    .testTag("video_buffering_spinner"),
                color = BodhiCyan,
                strokeWidth = 3.dp
            )
        }

        // TV Player Controls Overlay
        AnimatedVisibility(
            visible = showControls,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.75f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
                    .padding(32.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PlayerIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to selection",
                        onClick = onDismiss
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = video.title,
                            color = BodhiTextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.testTag("player_video_title")
                        )
                        if (!video.description.isNullOrBlank()) {
                            Text(
                                text = video.description,
                                color = BodhiTextGrey,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Bottom Control Center
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Timeline Progress Bar
                    val progress = if (durationMs > 0) {
                        (currentPositionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .testTag("player_progress_bar"),
                        color = BodhiCyan,
                        trackColor = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Time Stamps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = formatTimeMs(currentPositionMs),
                            color = BodhiTextGrey,
                            fontSize = 12.sp
                        )
                        Text(
                            text = formatTimeMs(durationMs),
                            color = BodhiTextGrey,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons Row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        PlayerIconButton(
                            icon = Icons.Default.Replay10,
                            contentDescription = stringResource(R.string.player_rewind_10),
                            onClick = {
                                videoViewRef?.let { vv ->
                                    val newPos = (vv.currentPosition - 10_000).coerceAtLeast(0)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos
                                }
                            }
                        )

                        // Play/Pause
                        PlayerIconButton(
                            icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) stringResource(R.string.player_pause) else stringResource(R.string.player_play),
                            isLarge = true,
                            onClick = {
                                videoViewRef?.let { vv ->
                                    if (vv.isPlaying) {
                                        vv.pause()
                                        isPlaying = false
                                    } else {
                                        vv.start()
                                        isPlaying = true
                                    }
                                }
                            }
                        )

                        // Forward 10s
                        PlayerIconButton(
                            icon = Icons.Default.Forward10,
                            contentDescription = stringResource(R.string.player_forward_10),
                            onClick = {
                                videoViewRef?.let { vv ->
                                    val newPos = (vv.currentPosition + 10_000).coerceAtMost(vv.duration)
                                    vv.seekTo(newPos)
                                    currentPositionMs = newPos
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    isLarge: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val size = if (isLarge) 56.dp else 42.dp
    val iconSize = if (isLarge) 32.dp else 22.dp

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isFocused) BodhiCardFocused else Color.Black.copy(alpha = 0.5f))
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) BodhiCyan else Color(0xFF475569),
                shape = CircleShape
            )
            .focusable(interactionSource = interactionSource)
            .clickable { onClick() }
            .testTag("player_btn_$contentDescription"),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (isFocused) BodhiCyan else BodhiTextWhite,
            modifier = Modifier.size(iconSize)
        )
    }
}

private fun formatTimeMs(ms: Int): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val hours = minutes / 60
    return if (hours > 0) {
        String.format(java.util.Locale.US, "%d:%02d:%02d", hours, minutes % 60, seconds)
    } else {
        String.format(java.util.Locale.US, "%02d:%02d", minutes, seconds)
    }
}
