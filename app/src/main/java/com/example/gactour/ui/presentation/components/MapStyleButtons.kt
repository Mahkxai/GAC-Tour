package com.example.gactour.ui.presentation.components

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gactour.ui.presentation.viewModels.MapViewModel
import com.example.gactour.utils.MAP_SCREEN
import com.example.gactour.utils.MapStyles

val ButtonModifier = Modifier
    .padding(horizontal = 8.dp)

@Composable
fun MapStyleButtons(
    modifier: Modifier,
) {
    val mapViewModel: MapViewModel = hiltViewModel()

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
    ) {
        MapStyles.values.forEach { mapStyle ->
            Button(
                onClick = {
                    mapViewModel.setMapStyle(mapStyle.url)
                    Log.d(MAP_SCREEN, "MapStyleButtons: ${mapStyle.name}")
                },
                modifier = ButtonModifier
            ) {
                Text(text = mapStyle.name)
            }
        }
    }
}

