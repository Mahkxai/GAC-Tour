package com.mahkxai.gactour.android.presentation.screen.explore.stream.image

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageStreamFeed(
    photoItems: List<GACTourMediaItem>,
    initialPage: Int = 0,
    setSelectedMediaIndex: (Int) -> Unit,
) {
    // val photoUrls = MockData.imageUrls
    // val photoPagerState = rememberPagerState(initialPage = initialPage) { photoUrls.size }

    val photoUrls = remember(photoItems) { photoItems.map { it.url } }
    val photoPagerState = rememberPagerState(initialPage = initialPage) { photoItems.size }

    LaunchedEffect(photoPagerState) {
        snapshotFlow { photoPagerState.currentPage }
            .collect { currentPage ->
                setSelectedMediaIndex(currentPage)
            }
    }

    VerticalPager(state = photoPagerState) { index ->
        ImageFeedContainer(url = photoUrls[index])
    }
}

@Composable
fun ImageFeedContainer(url: String?) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        model = url,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}