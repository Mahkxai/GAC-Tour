package com.mahkxai.gactour.android.presentation.screen.map.content

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import coil.compose.AsyncImage
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.common.composables.ComposableLifecycle
import com.mahkxai.gactour.android.common.composables.PlayerListener
import com.mahkxai.gactour.android.common.util.LogTags
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.mock.MockData
import com.mahkxai.gactour.android.presentation.screen.stream.content.rememberPlayerView
import com.mapbox.maps.logE

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
            GACTourMediaType.IMAGE -> PhotoStreamPreview(activeCategoryMediaItems) { index ->
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
fun PhotoStreamPreview(
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
            PhotoPreviewContainer(photoUrl = photoUrl) { setSelectedMediaIndex(index) }
        }
    }
}

@Composable
fun PhotoPreviewContainer(
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

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoStreamPreview(
    videoItems: List<GACTourMediaItem>,
    setSelectedMediaIndex: (Int) -> Unit,
) {
    // val videoUrls = MockData.videoUrls
    // val thumbnailUrls = MockData.thumbnailUrls

    val videoUrls = remember(videoItems) { videoItems.map { it.url } }
    val thumbnailUrls = remember(videoItems) { videoItems.map { it.thumbnailUrl } }

    val listState = rememberLazyListState()
    var currentlyPlayingItem by remember { mutableStateOf<Int?>(null) }
    val playbackOffsetPx = with(LocalDensity.current) { 30.dp.toPx() }

    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // Setup ExoPlayer as Playlist
    // LaunchedEffect(Unit) {
    //     val mediaItems = videoUrls.map { MediaItem.fromUri(it) }
    //     exoPlayer.setMediaItems(mediaItems, false)
    //     exoPlayer.prepare()
    // }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (visibleItemsInfo.isNotEmpty()) {
                    val firstItem = visibleItemsInfo.first()
                    val nextIndex = firstItem.index + 1

                    currentlyPlayingItem =
                        if (-firstItem.offset > playbackOffsetPx && nextIndex in videoItems.indices) {
                            nextIndex
                        } else {
                            firstItem.index
                        }

                    currentlyPlayingItem?.let { currentIndex ->
                        // exoPlayer.seekTo(currentIndex, C.TIME_UNSET)
                        // exoPlayer.playWhenReady = true

                        val currentlyPlayingUrl = videoUrls[currentIndex]
                        exoPlayer.setMediaItem(MediaItem.fromUri(currentlyPlayingUrl))
                        exoPlayer.prepare()
                        exoPlayer.playWhenReady = true
                    }
                }
            }
    }

    LazyRow(
        state = listState,
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(videoUrls) { currentIndex, videoUrl ->
            val thumbnailUrl = thumbnailUrls[currentIndex]

            if (currentlyPlayingItem == currentIndex)
                VideoPreviewContainer(exoPlayer, thumbnailUrl) {
                    setSelectedMediaIndex(currentIndex)
                    exoPlayer.stop()
                }
            else
                PhotoPreviewContainer(photoUrl = thumbnailUrl) {
                    setSelectedMediaIndex(currentIndex)
                    exoPlayer.stop()
                }
        }
    }

    ComposableLifecycle(onComposableDisposed = exoPlayer::release) { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> exoPlayer.playWhenReady = false
            Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
            else -> Unit
        }
    }

}

@androidx.annotation.OptIn(UnstableApi::class) @Composable
fun VideoPreviewContainer(
    exoPlayer: ExoPlayer,
    thumbnailUrl: String?,
    onClick: () -> Unit,
) {
    val playerView = rememberPlayerView(exoPlayer) {
        useController = false
        resizeMode = RESIZE_MODE_ZOOM
    }
    var isVideoReady by remember { mutableStateOf(false) }

    PlayerListener(player = exoPlayer) { event ->
        when (event) {
            Player.STATE_READY -> isVideoReady = true
            Player.EVENT_PLAYER_ERROR -> logE(LogTags.ExoPlayer, "Exo Player Error")
        }
    }

    VisualMediaPreviewCard(modifier = Modifier.clickable { onClick() }) {
        Box {
            AndroidView(factory = { playerView }, modifier = Modifier.fillMaxSize())
            if (!isVideoReady) PhotoPreviewContainer(photoUrl = thumbnailUrl)

            if (isVideoReady) VideoPreviewPauseIcon()
            else VideoPreviewPlayIcon()
        }
    }
}

@Composable
fun BoxScope.VideoPreviewPlayIcon() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.White, CircleShape)
            .align(Alignment.BottomEnd)
    ) {
        Icon(
            imageVector = Icons.Default.PlayArrow,
            tint = Color.White,
            contentDescription = "Video Preview Play"
        )
    }
}

@Composable
fun BoxScope.VideoPreviewPauseIcon() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .border(1.dp, Color.White, CircleShape)
            .align(Alignment.BottomEnd)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.stream_pause_icon),
            tint = Color.White,
            contentDescription = "Video Preview Pause"
        )
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

