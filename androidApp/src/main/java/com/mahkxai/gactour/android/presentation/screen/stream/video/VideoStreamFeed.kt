package com.mahkxai.gactour.android.presentation.screen.stream.video

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.mahkxai.gactour.android.common.composables.ComposableLifecycle
import com.mahkxai.gactour.android.common.composables.PlayerListener
import com.mahkxai.gactour.android.common.util.LogTags
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.presentation.screen.stream.image.ImageFeedContainer
import com.mapbox.maps.logE

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoStreamFeed(
    videoItems: List<GACTourMediaItem>,
    initialPage: Int = 0,
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

        if (currentPage == videoPagerState.currentPage) VideoFeedContainer(exoPlayer, thumbnailUrl)
        else ImageFeedContainer(url = thumbnailUrl)
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
fun VideoFeedContainer(
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
        if (!isVideoReady) ImageFeedContainer(url = thumbnailUrl)
    }


}