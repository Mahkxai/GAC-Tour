package com.mahkxai.gactour.android.experiment.exp_stream

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BoxScope.ExpStreamFeed(
    initialPage: Int = 0,
    mediaItems: List<GACTourMediaItem>,
    onMediaChange: (Int) -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialPage) { mediaItems.size }
    val scope = rememberCoroutineScope()

    val leftPressInteractionSource = remember { MutableInteractionSource() }
    val isLeftPressed by leftPressInteractionSource.collectIsPressedAsState()
    val leftPressGradient = Brush.horizontalGradient(
        listOf(Color.Black.copy(alpha = 0.4f), Color.Transparent)
    )

    val rightPressInteractionSource = remember { MutableInteractionSource() }
    val isRightPressed by rightPressInteractionSource.collectIsPressedAsState()
    val rightPressGradient = Brush.horizontalGradient(
        listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
    )
    val defaultGradient = Brush.horizontalGradient(
        listOf(Color.Transparent, Color.Transparent)
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { currentPage ->
            onMediaChange(currentPage)
        }
    }

    HorizontalPager(state = pagerState) { index ->
        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            model = mediaItems[index].url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.2f)
            .align(Alignment.CenterStart)
            .background( if (isLeftPressed) leftPressGradient else defaultGradient)
            .clickable(
                interactionSource = leftPressInteractionSource,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage - 1
                        )
                    }
                },
                indication = null
            )
    )

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.2f)
            .align(Alignment.CenterEnd)
            .background(if (isRightPressed) rightPressGradient else defaultGradient)
            .clickable(
                interactionSource = rightPressInteractionSource,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            pagerState.currentPage + 1
                        )
                    }
                },
                indication = null
            )
    )

}
