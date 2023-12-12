package com.mahkxai.gactour.android.common.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object MapUtil {
    fun calculateFABContainerOffset(
        mapScreenHeight: Dp,
        bottomSheetOffsetFromTop: Dp
    ): Dp {
        val bottomSheetVisibleHeight = mapScreenHeight - bottomSheetOffsetFromTop

        val buttonContainerOffset =
            if (bottomSheetVisibleHeight > MapConstants.PEEK_HEIGHT) {
                (bottomSheetVisibleHeight - MapConstants.PEEK_HEIGHT)
                    .coerceAtMost(20.dp + 56.dp + 8.dp)
            } else {
                0.dp
            }

        return (-bottomSheetVisibleHeight)

    }
}