package com.example.gac_tour.examples.annotation

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.gac_tour.ExampleScaffold
import com.example.gac_tour.examples.utils.CityLocations.HELSINKI
import com.example.gac_tour.ui.theme.GACTourTheme
import com.mapbox.maps.*
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
//import com.mapbox.maps.viewannotation.annotationAnchors
//import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import java.util.*

/**
 * Example to showcase usage of MapboxMap.
 */
@OptIn(MapboxExperimental::class)
public class ViewAnnotationActivity : ComponentActivity() {
    private val random = Random()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var buttonColor by remember {
                mutableStateOf(Color.Blue)
            }
            val animatedColor by animateColorAsState(buttonColor, label = "ButtonAnnotationColor")
            GACTourTheme {
                ExampleScaffold {
                    MapboxMap(
                        Modifier.fillMaxSize(),
                        mapViewportState = MapViewportState().apply {
                            setCameraOptions {
                                zoom(ZOOM)
                                center(HELSINKI)
                            }
                        }
                    ) {
                        ViewAnnotation(
                            options = viewAnnotationOptions {
                                geometry(HELSINKI)
                                viewAnnotationOptions {
                                    anchor(ViewAnnotationAnchor.BOTTOM)
                                }
                                /*annotationAnchors(
                                    {
                                        anchor(ViewAnnotationAnchor.BOTTOM)

                                    }
                                )*/
                                allowOverlap(false)
                            }
                        ) {
                            Button(
                                onClick = {
                                    buttonColor = Color(
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(256)
                                    )
                                    Toast.makeText(applicationContext, "Click", LENGTH_SHORT).show()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = animatedColor
                                ),
                            ) {
                                Text(
                                    "Click me"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private companion object {
        const val ZOOM: Double = 9.0
    }
}