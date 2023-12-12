package com.example.gactour.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModalSheetContent(
    markerText: String,
    closeBottomSheet: () -> Unit)
{
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        /*DragHandle()*/

        Text(
            text = markerText,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            color = Color.LightGray,
            modifier = Modifier.align(Alignment.Center)
        )

        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close Icon",
            tint = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .size(32.dp)
                .align(Alignment.TopEnd)
                .clickable { closeBottomSheet() }
        )
    }
}

@Composable
fun DragHandle() {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .width(32.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.LightGray),
    )
}