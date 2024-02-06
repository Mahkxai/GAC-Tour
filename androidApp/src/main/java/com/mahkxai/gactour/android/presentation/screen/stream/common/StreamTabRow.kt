package com.mahkxai.gactour.android.presentation.screen.stream.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType

@Composable
fun StreamTabRow(
    activeMediaCategory: GACTourMediaType,
    setMediaCategory: (GACTourMediaType) -> Unit,
) {
    val selectedTabIndex = activeMediaCategory.ordinal
    TabRow(
        modifier = Modifier.fillMaxWidth(0.9f),
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent,
        indicator = { tabPositions ->  StreamTabIndicator(selectedTabIndex, tabPositions)},
        divider = {},
    ) {
        GACTourMediaType.values().forEach { mediaCategory ->
            Tab(
                selected = (mediaCategory == activeMediaCategory),
                onClick = { setMediaCategory(mediaCategory) },
                text = { Text(mediaCategory.title(), color = Color.White) },
            )
        }
    }
}

@Composable
fun StreamTabIndicator(selectedTabIndex: Int, tabPositions: List<TabPosition>) {
    if (selectedTabIndex in tabPositions.indices) {
        val currentTabPosition = tabPositions[selectedTabIndex]

        Box(
            modifier = Modifier
                .tabIndicatorOffset(currentTabPosition)
                .width(2.dp)
                .height(3.dp)
                .clip(CircleShape)
                .background(color = Color.White)
        )
    }
}