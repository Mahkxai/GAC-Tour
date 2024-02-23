package com.mahkxai.gactour.android.presentation.screen.explore.stream

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.explore.stream.common.StreamFeedContainer
import com.mahkxai.gactour.android.presentation.screen.explore.stream.common.StreamItemCounter
import com.mahkxai.gactour.android.presentation.screen.explore.stream.common.StreamTabRow

@Composable
fun StreamScreenContent(
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    mediaCategoryIndices: Map<GACTourMediaType, Int>,
    setMediaCategory: (GACTourMediaType) -> Unit,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit,
    closeStream: () -> Unit
) {
    BackHandler { closeStream() }

    Box(
        modifier = Modifier.background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        StreamFeedContainer(
            activeMediaCategory = activeMediaCategory,
            activeCategoryMediaItems = activeCategoryMediaItems,
            mediaCategoryIndices = mediaCategoryIndices,
            setMediaCategory = setMediaCategory,
            setSelectedMediaIndex = setSelectedMediaIndex
        )

        StreamTabRow(
            activeMediaCategory = activeMediaCategory,
            setMediaCategory = setMediaCategory
        )

        if (activeCategoryMediaItems.isNotEmpty()) {
            val activeItem = mediaCategoryIndices[activeMediaCategory] ?: 0
            val totalItems = activeCategoryMediaItems.size

            StreamItemCounter(
                activeItem = activeItem,
                totalItems = totalItems,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

    }
}