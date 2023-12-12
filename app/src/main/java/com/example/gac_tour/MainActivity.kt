package com.example.gac_tour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.gac_tour.examples.utils.CityLocations
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

@MapboxExperimental
public class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExampleScaffold {
                MapboxMap(
                    Modifier.fillMaxSize(),
                    mapViewportState = MapViewportState().apply {
                        setCameraOptions {
                            center(CityLocations.HELSINKI)
                        }
                    },
                )
            }
        }
    }
}