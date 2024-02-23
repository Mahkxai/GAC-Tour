package com.mahkxai.gactour.android.presentation.screen.explore.stream.image

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.presentation.screen.explore.stream.common.VisualMediaPreviewCard

@Composable
fun ImageStreamPreview(
    photoItems: List<GACTourMediaItem>,
    setSelectedMediaIndex: (Int) -> Unit,
) {
    // val photoUrls = MockData.imageUrls

    val photoUrls = remember(photoItems) { photoItems.map { it.url } }

    LazyRow(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(photoUrls) { index, photoUrl ->
            ImagePreviewContainer(photoUrl = photoUrl) { setSelectedMediaIndex(index) }
        }
    }
}

@Composable
fun ImagePreviewContainer(
    photoUrl: String?,
    onClick: () -> Unit = {},
) {
    VisualMediaPreviewCard(modifier = Modifier.clickable { onClick() }) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = photoUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
    }
}