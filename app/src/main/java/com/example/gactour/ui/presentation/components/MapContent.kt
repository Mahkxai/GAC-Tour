package com.example.gactour.ui.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import kotlinx.coroutines.CoroutineScope

@MapboxExperimental
@Composable
fun MapContent(
    boundsOptions: CameraBoundsOptions,
    showSheet: () -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        MapStyleButtons(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            MapBoxMap(
                modifier = Modifier.fillMaxSize(),
                boundsOptions = boundsOptions,
                showSheet = showSheet,
            )

            LocationFAB(
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}