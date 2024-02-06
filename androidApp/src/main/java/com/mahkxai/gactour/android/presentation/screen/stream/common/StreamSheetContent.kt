package com.mahkxai.gactour.android.presentation.screen.stream.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.stream.image.ImageStreamPreview
import com.mahkxai.gactour.android.presentation.screen.stream.video.VideoStreamPreview

@Composable
fun StreamSheetContent(
    mediaItems: List<GACTourMediaItem>,
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    setMediaCategory: (GACTourMediaType) -> Unit,
    setStreamInfoHeight: (Dp) -> Unit,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit,
) {
    LaunchedEffect(mediaItems) {
        setMediaCategory(activeMediaCategory)
    }

    StreamSheetHeader(
        setStreamInfoHeight = setStreamInfoHeight,
        mediaItemsSize = mediaItems.size,
    )

    FilterList(
        activeMediaCategory = activeMediaCategory,
        setMediaCategory = setMediaCategory
    )

    StreamPreview(
        activeMediaCategory = activeMediaCategory,
        activeCategoryMediaItems = activeCategoryMediaItems,
        setSelectedMediaIndex = setSelectedMediaIndex,
    )
}

@Composable
fun StreamSheetHeader(
    mediaItemsSize: Int,
    setStreamInfoHeight: (Dp) -> Unit,
) {
    val density = LocalDensity.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { with(density) { setStreamInfoHeight(it.size.height.toDp()) } }
    ) {
        Text(
            text = "Streaming $mediaItemsSize Items",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterList(
    activeMediaCategory: GACTourMediaType,
    setMediaCategory: (GACTourMediaType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GACTourMediaType.values().forEach { mediaCategory ->
            FilterChip(
                onClick = { setMediaCategory(mediaCategory) },
                label = { Text(mediaCategory.title()) },
                selected = (mediaCategory == activeMediaCategory),
            )
        }
    }
}

@Composable
fun StreamPreview(
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit,
) {
    StreamContentContainer(
        isMediaListEmpty = activeCategoryMediaItems.isEmpty(),
        isStreamPreview = true,
        activeMediaCategory = activeMediaCategory
    ) {
        when (activeMediaCategory) {
            GACTourMediaType.IMAGE -> ImageStreamPreview(activeCategoryMediaItems) { index ->
                setSelectedMediaIndex(GACTourMediaType.IMAGE, index)
            }

            GACTourMediaType.VIDEO -> VideoStreamPreview(activeCategoryMediaItems) { index ->
                setSelectedMediaIndex(GACTourMediaType.VIDEO, index)
            }

            GACTourMediaType.AUDIO -> AudioStream(activeCategoryMediaItems)
            GACTourMediaType.TEXT -> TextStream(activeCategoryMediaItems)
        }
    }
}

@Composable
fun StreamContentContainer(
    isMediaListEmpty: Boolean,
    isStreamPreview: Boolean,
    activeMediaCategory: GACTourMediaType,
    streamContent: @Composable (BoxScope.() -> Unit)
) {
    val modifier =
        if (isStreamPreview) Modifier
            .fillMaxWidth()
            .height(300.dp)
        else Modifier.fillMaxSize()

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (!isMediaListEmpty) streamContent()
        else Text(text = "No ${activeMediaCategory.title()} found near current location.")
    }
}

@Composable
fun AudioStream(mediaUrls: List<GACTourMediaItem>) {
}

@Composable
fun AudioContainer() {
}

@Composable
fun TextStream(mediaUrls: List<GACTourMediaItem>) {
}

@Composable
fun TextContainer() {
}

@Composable
fun VisualMediaPreviewCard(
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit),
) {
    val roundedCornerShape = RoundedCornerShape(15.dp)
    Card(
        modifier = modifier
            .fillMaxHeight()
            .aspectRatio(9f / 16f)
            .clip(roundedCornerShape)
            .border(1.dp, Color.Black, roundedCornerShape),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
        content = content,
    )
}

