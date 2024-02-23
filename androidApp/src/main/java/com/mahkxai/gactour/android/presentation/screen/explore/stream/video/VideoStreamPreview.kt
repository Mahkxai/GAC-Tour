package com.mahkxai.gactour.android.presentation.screen.explore.stream.video

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import com.mahkxai.gactour.android.MainApplication
import com.mahkxai.gactour.android.common.composables.ComposableLifecycle
import com.mahkxai.gactour.android.common.composables.PlayerListener
import com.mahkxai.gactour.android.common.util.LogTags
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.explore.stream.image.ImagePreviewContainer
import com.mahkxai.gactour.android.presentation.screen.explore.stream.common.VisualMediaPreviewCard
import com.mapbox.maps.logE

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoStreamPreview(
    videoItems: List<GACTourMediaItem>,
    setSelectedMediaIndex: (Int) -> Unit,
) {
    val videoUrls = remember(videoItems) { videoItems.map { it.url } }
    val thumbnailUrls = remember(videoItems) { videoItems.map { it.thumbnailUrl } }

    val listState = rememberLazyListState()
    var currentlyPlayingItem by remember { mutableStateOf<Int?>(null) }
    val playbackOffsetPx = with(LocalDensity.current) { 30.dp.toPx() }

    val context = LocalContext.current
    val appContext = context.applicationContext as MainApplication
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }
    val cacheManager = appContext.cacheManager
    val cacheDataSourceFactory = cacheManager.createCacheDataSourceFactory()
    val mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSourceFactory)

    /* Test Cache Manager*/
    // val cacheDir = File(context.cacheDir, "video_cache")
    // val databaseProvider: DatabaseProvider = StandaloneDatabaseProvider(context)
    // val cacheEvictor = LeastRecentlyUsedCacheEvictor(500 * 1024 * 1024) // 500 MB
    // val simpleCache = SimpleCache(cacheDir, cacheEvictor, databaseProvider)
    // val cacheDataSourceFactory = CacheDataSource.Factory().apply {
    //     setCache(simpleCache)
    //     setUpstreamDataSourceFactory(DefaultHttpDataSource.Factory())
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
                        val mediaItem = MediaItem.fromUri(currentlyPlayingUrl)
                        val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)

                        try {
                            exoPlayer.setMediaSource(mediaSource)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true
                        } catch (e: Exception) {
                            logE("VideoStream", "Error playing from cache: ${e.message}")
                        }

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
                ImagePreviewContainer(photoUrl = thumbnailUrl) {
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

    /*//Setup ExoPlayer as Playlist
    LaunchedEffect(Unit) {
        val mediaItems = videoUrls.map { MediaItem.fromUri(it) }
        exoPlayer.setMediaItems(mediaItems, false)
        exoPlayer.prepare()
    }*/

}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun VideoPreviewContainer(
    exoPlayer: ExoPlayer,
    thumbnailUrl: String?,
    onClick: () -> Unit,
) {
    val playerView = rememberPlayerView(exoPlayer) {
        useController = false
        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
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
            if (!isVideoReady) ImagePreviewContainer(photoUrl = thumbnailUrl)

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
            imageVector = Icons.Default.Pause,
            tint = Color.White,
            contentDescription = "Video Preview Pause"
        )
    }
}