package com.example.gac_tour.examples.basic

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.gac_tour.ExampleScaffold
import com.example.gac_tour.examples.utils.CityLocations
import com.example.gac_tour.ui.theme.GACTourTheme
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState

/**
 * Example to showcase usage of MapboxMap.
 */
@OptIn(MapboxExperimental::class)
public class SimpleMapActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GACTourTheme {
        ExampleScaffold {
          MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = MapViewportState().apply {
              setCameraOptions {
                zoom(ZOOM)
                center(CityLocations.HELSINKI)
              }
            },
          )
        }
      }
    }
  }

  private companion object {
    const val ZOOM: Double = 9.0
  }
}