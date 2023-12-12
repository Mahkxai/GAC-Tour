package com.mahkxai.gactour.android.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

@Composable
fun PlayerListener(
    player: ExoPlayer,
    onEvent: (Int) -> Unit = {}
) {
    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY && player.playWhenReady)
                    onEvent(Player.STATE_READY)
            }

            override fun onPlayerError(error: PlaybackException) {
                onEvent(Player.EVENT_PLAYER_ERROR)
            }
        }
        player.addListener(listener)

        onDispose { player.removeListener(listener) }
    }
}