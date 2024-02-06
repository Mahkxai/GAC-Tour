package com.mahkxai.gactour.android.presentation.screen.stream.video

import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.mahkxai.gactour.android.R

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