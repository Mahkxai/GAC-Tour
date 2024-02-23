package com.mahkxai.gactour.android.presentation.screen.explore.stream.common

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.explore.stream.image.ImageStreamFeed
import com.mahkxai.gactour.android.presentation.screen.explore.stream.video.VideoStreamFeed

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StreamFeedContainer(
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    setMediaCategory: (GACTourMediaType) -> Unit,
    mediaCategoryIndices: Map<GACTourMediaType, Int>,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit
) {
    val activeMediaCategoryPage = activeMediaCategory.ordinal
    val totalCategories = GACTourMediaType.values().size
    val streamCategoryPagerState = rememberPagerState(initialPage = activeMediaCategoryPage) {
        totalCategories
    }

    LaunchedEffect(streamCategoryPagerState.currentPage) {
        val currentCategoryIndex = streamCategoryPagerState.currentPage
        val currentCategory = GACTourMediaType.values()[currentCategoryIndex]
        setMediaCategory(currentCategory)
    }

    LaunchedEffect(activeMediaCategory) {
        streamCategoryPagerState.scrollToPage(activeMediaCategory.ordinal)
    }

    HorizontalPager(state = streamCategoryPagerState) { categoryIndex ->
        val currentMediaCategory = GACTourMediaType.values()[categoryIndex]
        StreamFeed(
            activeMediaCategory = currentMediaCategory,
            activeCategoryMediaItems = activeCategoryMediaItems,
            mediaCategoryIndices = mediaCategoryIndices,
            setSelectedMediaIndex = setSelectedMediaIndex
        )
    }
}


@Composable
fun StreamFeed(
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    mediaCategoryIndices: Map<GACTourMediaType, Int>,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit
) {
    StreamContentContainer(
        isMediaListEmpty = activeCategoryMediaItems.isEmpty(),
        isStreamPreview = false,
        activeMediaCategory = activeMediaCategory
    ) {
        val currentCategoryInitialPage = mediaCategoryIndices[activeMediaCategory] ?: 0

        when (activeMediaCategory) {
            GACTourMediaType.IMAGE ->
                ImageStreamFeed(
                    photoItems = activeCategoryMediaItems,
                    initialPage = currentCategoryInitialPage
                ) {
                    setSelectedMediaIndex(GACTourMediaType.IMAGE, it)
                }

            GACTourMediaType.VIDEO ->
                VideoStreamFeed(
                    videoItems = activeCategoryMediaItems,
                    initialPage = currentCategoryInitialPage
                ) {
                    setSelectedMediaIndex(GACTourMediaType.VIDEO, it)
                }

            GACTourMediaType.AUDIO ->
                AudioStream(activeCategoryMediaItems)

            GACTourMediaType.TEXT ->
                TextStream(activeCategoryMediaItems)
        }
    }
}


