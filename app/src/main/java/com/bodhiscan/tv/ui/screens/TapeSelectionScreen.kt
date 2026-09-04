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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodhiscan.tv.R
import com.bodhiscan.tv.data.model.VideoItem
import com.bodhiscan.tv.ui.UiState
import com.bodhiscan.tv.ui.theme.BodhiBackground
import com.bodhiscan.tv.ui.theme.BodhiCardBg
import com.bodhiscan.tv.ui.theme.BodhiCardFocused
import com.bodhiscan.tv.ui.theme.BodhiCardInner
import com.bodhiscan.tv.ui.theme.BodhiCyan
import com.bodhiscan.tv.ui.theme.BodhiTextDarkGrey
import com.bodhiscan.tv.ui.theme.BodhiTextGrey
import com.bodhiscan.tv.ui.theme.BodhiTextWhite

@Composable
fun TapeSelectionScreen(
    state: UiState,
    onVideoFocused: (Int) -> Unit,
    onVideoSelected: (VideoItem) -> Unit,
    onBackToAuth: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentFocusedVideo = state.videos.getOrNull(state.focusedIndex)
        ?: state.videos.firstOrNull()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BodhiBackground)
            .testTag("selection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Header & Focused Detail Banner
            Column(modifier = Modifier.fillMaxWidth()) {
                // Top Row: Collection Title & Back button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.collectionTitle.ifBlank { "Digitized Memories" },
                        color = BodhiCyan,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .testTag("collection_title")
                    )

                    BackToPinButton(onClick = onBackToAuth)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Focused Detail Banner
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(BodhiCardBg.copy(alpha = 0.5f))
                        .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(8.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .testTag("focused_detail_banner")
                ) {
                    Text(
                        text = currentFocusedVideo?.title.orEmpty().ifBlank { "Select a digitized tape to watch" },
                        color = BodhiTextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("focused_title")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentFocusedVideo?.description?.ifBlank { null }
                            ?: stringResource(R.string.default_video_description),
                        color = BodhiTextDarkGrey,
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("focused_description")
                    )
                }
            }

            // Middle Section: Cassette Tape Grid
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 250.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = 14.dp)
                    .testTag("video_grid"),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                itemsIndexed(state.videos) { index, video ->
                    CassetteTapeCard(
                        video = video,
                        index = index + 1,
                        isSelected = index == state.focusedIndex,
                        onFocused = { onVideoFocused(index) },
                        onSelected = { onVideoSelected(video) }
                    )
                }
            }

            // Footer Section: Bodhi Industries Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.bodhi_industries_logo),
                    contentDescription = stringResource(R.string.brand_footer_description),
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("selection_footer_logo")
                )
            }
        }
    }
}

@Composable
private fun CassetteTapeCard(
    video: VideoItem,
    index: Int,
    isSelected: Boolean,
    onFocused: () -> Unit,
    onSelected: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) {
            onFocused()
        }
    }

    val tapeNumber = video.getTapeNumber(index)

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.05f else 1.0f,
        label = "tapeCardScale"
    )

    val cardInnerBg = if (isFocused) BodhiCardFocused else BodhiCardInner
    val tapeNumColor = if (isFocused) BodhiCyan else BodhiTextWhite
    val tapeHeaderColor = if (isFocused) BodhiCyan else BodhiTextDarkGrey
    val tapeTitleColor = if (isFocused) BodhiTextWhite else BodhiTextGrey

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .scale(scale)
            .shadow(if (isFocused) 12.dp else 2.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .background(BodhiCardBg)
            .border(
                width = if (isFocused) 2.5.dp else 1.dp,
                color = if (isFocused) BodhiCyan else Color(0xFF222B3B),
                shape = RoundedCornerShape(10.dp)
            )
            .focusable(interactionSource = interactionSource)
            .clickable { onSelected() }
            .testTag("tape_card_$index")
    ) {
        // Inner Container
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(cardInnerBg)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top decorative cassette line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(BodhiCyan)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // TAPE header
                Text(
                    text = stringResource(R.string.tape_header),
                    color = tapeHeaderColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )

                // Giant Tape Number & Cassette Spool Decor
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left spool reel
                    CassetteReel(isSpinning = isFocused)

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = tapeNumber,
                        color = tapeNumColor,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.SansSerif,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Right spool reel
                    CassetteReel(isSpinning = isFocused)
                }

                // Bottom Title / Description
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (isFocused) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = BodhiCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = video.title.ifBlank { "Tape $tapeNumber" },
                        color = tapeTitleColor,
                        fontSize = 13.sp,
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CassetteReel(isSpinning: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(Color(0xFF0F151F))
            .border(
                1.5.dp,
                if (isSpinning) BodhiCyan.copy(alpha = 0.8f) else Color(0xFF2A3445),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (isSpinning) BodhiCyan else Color(0xFF475569))
        )
    }
}

@Composable
private fun BackToPinButton(
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
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
            .testTag("back_to_pin_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = if (isFocused) BodhiCyan else BodhiTextGrey,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = stringResource(R.string.action_back_to_pin),
            color = if (isFocused) BodhiCyan else BodhiTextGrey,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
