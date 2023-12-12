package com.example.gac_tour.examples.gactour.composables

import android.media.MediaPlayer
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import com.example.gac_tour.R
import kotlin.math.roundToInt

@Composable
fun AudioPlayer(url: String, modifier: Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0f) }
    var maxProgress by remember { mutableStateOf(1f) }

    val mediaPlayer = remember {
        MediaPlayer().apply {
            setDataSource(url)
            prepareAsync()
            setOnPreparedListener {
                maxProgress = it.duration.toFloat()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
        }
    }

    // Play/Pause toggle
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            mediaPlayer.start()
        } else {
            mediaPlayer.pause()
        }
    }

    // SeekBar update
    LaunchedEffect(mediaPlayer) {
        while (true) {
            if (isPlaying) {
                progress = mediaPlayer.currentPosition.toFloat()
            }
            kotlinx.coroutines.delay(1000)
        }
    }

    Row(modifier = modifier) {
        // Play/Pause Button
        Button(onClick = { isPlaying = !isPlaying }) {
            Icon(
                imageVector =
                if (isPlaying) {painterResource(id = R.drawable.baseline_pause_24) as ImageVector
                } else {Icons.Filled.PlayArrow},
                contentDescription = if (isPlaying) "Pause Button" else "Play Button"
            )
        }

        // SeekBar
        Slider(
            value = progress,
            onValueChange = { newPosition ->
                mediaPlayer.seekTo(newPosition.roundToInt())
                progress = newPosition
            },
            valueRange = 0f..maxProgress,
            modifier = Modifier.weight(1f)
        )
    }
}