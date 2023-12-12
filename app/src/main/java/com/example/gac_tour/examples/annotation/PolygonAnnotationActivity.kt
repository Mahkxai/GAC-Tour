package com.example.gac_tour.examples.annotation

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.gac_tour.ExampleScaffold
import com.example.gac_tour.ui.theme.GACTourTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation

/**
 * Example to showcase usage of PolygonAnnotation with Jetpack Compose.
 */
@OptIn(MapboxExperimental::class)
public class PolygonAnnotationActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      GACTourTheme {
        ExampleScaffold {
          MapboxMap(
            Modifier.fillMaxSize(),
            mapInitOptionsFactory = { context ->
              MapInitOptions(
                context,
                styleUri = Style.LIGHT
              )
            },
            mapViewportState = MapViewportState().apply {
              setCameraOptions {
                zoom(ZOOM)
                center(CAMERA_CENTER)
              }
            }
          ) {
            PolygonAnnotation(
              points = POLYGON_POINTS,
              fillColorInt = Color.RED,
              onClick = {
                Toast.makeText(
                  this@PolygonAnnotationActivity,
                  "Clicked on Polygon Annotation: $it",
                  Toast.LENGTH_SHORT
                ).show()
                true
              }
            )
          }
        }
      }
    }
  }

  private companion object {
    const val ZOOM: Double = 5.0
    val CAMERA_CENTER: Point = Point.fromLngLat(-88.90136, 25.04579)
    val POLYGON_POINTS = listOf(
      listOf(
        Point.fromLngLat(-89.857177734375, 24.51713945052515),
        Point.fromLngLat(-87.967529296875, 24.51713945052515),
        Point.fromLngLat(-87.967529296875, 26.244156283890756),
        Point.fromLngLat(-89.857177734375, 26.244156283890756),
        Point.fromLngLat(-89.857177734375, 24.51713945052515)
      )
    )
  }
}