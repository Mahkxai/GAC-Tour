package com.mahkxai.gactour.android.presentation.screen.stream.content

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.common.composables.ComposableLifecycle
import com.mahkxai.gactour.android.common.composables.PlayerListener
import com.mahkxai.gactour.android.common.util.LogTags
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.map.content.AudioStream
import com.mahkxai.gactour.android.presentation.screen.map.content.StreamContentContainer
import com.mahkxai.gactour.android.presentation.screen.map.content.TextStream
import com.mapbox.maps.logE

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
                PhotoStreamFeed(
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PhotoStreamFeed(
    photoItems: List<GACTourMediaItem>,
    initialPage: Int,
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
        PhotoContainer(url = photoUrls[index])
    }
}


@Composable
fun PhotoContainer(url: String?) {
    AsyncImage(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        model = url,
        contentDescription = null,
        contentScale = ContentScale.FillWidth,
    )
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoStreamFeed(
    videoItems: List<GACTourMediaItem>,
    initialPage: Int,
    setSelectedMediaIndex: (Int) -> Unit,
) {
    // val videoUrls = MockData.videoUrls
    // val thumbnailUrls = MockData.thumbnailUrls
    // val videoPagerState = rememberPagerState(initialPage = initialPage) { videoUrls.size }

    val videoUrls = remember(videoItems) { videoItems.map { it.url } }
    val thumbnailUrls = remember(videoItems) { videoItems.map { it.thumbnailUrl } }
    val videoPagerState = rememberPagerState(initialPage = initialPage) { videoItems.size }

    val context = LocalContext.current
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    // Setup ExoPlayer as Playlist
    // LaunchedEffect(Unit) {
    //     val mediaItems = videoUrls.map { MediaItem.fromUri(it) }
    //     exoPlayer.setMediaItems(mediaItems, false)
    //     exoPlayer.prepare()
    // }

    LaunchedEffect(videoPagerState) {
        snapshotFlow { videoPagerState.currentPage }
            .collect { currentPage ->
                setSelectedMediaIndex(currentPage)

                // exoPlayer.seekTo(currentPage, C.TIME_UNSET)
                // exoPlayer.playWhenReady = true


                val currentlyPlayingUrl = videoUrls[currentPage]
                exoPlayer.setMediaItem(MediaItem.fromUri(currentlyPlayingUrl))
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }
    }

    VerticalPager(state = videoPagerState) { currentPage ->
        val videoUrl = videoUrls[currentPage]
        val thumbnailUrl = thumbnailUrls[currentPage]

        if (currentPage == videoPagerState.currentPage) VideosContainer(exoPlayer, thumbnailUrl)
        else PhotoContainer(url = thumbnailUrl)
    }

    ComposableLifecycle(onComposableDisposed = exoPlayer::release) { _, event ->
        when (event) {
            Lifecycle.Event.ON_PAUSE -> exoPlayer.playWhenReady = false
            Lifecycle.Event.ON_RESUME -> exoPlayer.playWhenReady = true
            else -> Unit
        }
    }

}

@Composable
fun VideosContainer(
    exoPlayer: ExoPlayer,
    thumbnailUrl: String?,
) {
    val playerView = rememberPlayerView(exoPlayer)
    var isVideoReady by remember { mutableStateOf(false) }

    PlayerListener(player = exoPlayer) { event ->
        when (event) {
            Player.STATE_READY -> isVideoReady = true
            Player.EVENT_PLAYER_ERROR -> logE(LogTags.ExoPlayer, "Exo Player Error")
        }
    }

    Box {
        AndroidView(factory = { playerView }, modifier = Modifier.fillMaxSize())
        if (!isVideoReady) PhotoContainer(url = thumbnailUrl)
    }


}


@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberPlayerView(
    player: ExoPlayer,
    setupPlayerView: PlayerView.() -> Unit = {}
): PlayerView {
    val context = LocalContext.current
    val layoutInflater = LayoutInflater.from(context)
    val frameLayout = FrameLayout(context)

    val playerView = remember {
        val view = layoutInflater.inflate(R.layout.player_view, frameLayout, false) as PlayerView
        view.apply { setupPlayerView() }

        // layoutInflater
        //     .inflate(R.layout.player_view, frameLayout, false) as PlayerView

        // PlayerView impl but this uses SurfaceView which overlaps with MapboxMap's SurfaceView
        // PlayerView(context).apply {
        //     layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        //     useController = true
        //     this.player = player
        // }
    }

    DisposableEffect(player) {
        playerView.player = player

        onDispose { playerView.player = null }
    }
    return playerView
}
