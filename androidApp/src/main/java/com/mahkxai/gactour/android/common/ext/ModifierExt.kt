package com.mahkxai.gactour.android.common.ext

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.forceDisable(): Modifier = this.pointerInteropFilter {
    // Return true to consume the event and prevent other handlers from receiving it
    true
}

fun Modifier.ignoreNextModifiers(): Modifier {
    return object : Modifier by this {
        override fun then(other: Modifier): Modifier {
            return this
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.disableInteraction(disabled: Boolean): Modifier =
    if (disabled) this.pointerInteropFilter { true }
    else this


fun Modifier.overlay(): Modifier = this
    .fillMaxSize()
    .background(Color.Black.copy(alpha = 0.7f))
    .blur(radius = 20.dp)

fun Modifier.blurredBackground(): Modifier = this
    .background(Color.Black.copy(alpha = 0.7f))
    .blur(radius = 20.dp)