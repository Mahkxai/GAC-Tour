package com.mahkxai.gactour.android.presentation.screen.stream.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun StreamItemCounter(
    activeItem: Int,
    totalItems: Int,
    modifier: Modifier = Modifier,
) {
    val roundedCornerShape = RoundedCornerShape(20)
    Box(
        modifier = modifier
            .padding(16.dp)
            .border(2.dp, Color.White, roundedCornerShape)
            .clip(roundedCornerShape)
            .padding(8.dp),
    ) {
        Text(
            text = "${activeItem+1}/$totalItems",
            color = Color.White
        )
    }
}

@Preview
@Composable
fun PreviewStreamItemCounter() {
    StreamItemCounter(activeItem = 4, totalItems = 8)
}