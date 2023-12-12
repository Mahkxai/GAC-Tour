package com.example.gactour.ui.presentation.components

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gactour.R
import com.example.gactour.ui.presentation.viewModels.LocationViewModel
import com.example.gactour.ui.presentation.viewModels.MapViewModel
import com.example.gactour.utils.*
import com.mapbox.maps.*
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CameraOptions
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.fillExtrusionLayer
import com.mapbox.maps.extension.style.layers.generated.fillLayer
import com.mapbox.maps.extension.style.layers.generated.lineLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.VectorSource
import com.mapbox.maps.extension.style.sources.generated.vectorSource
import com.mapbox.maps.extension.style.sources.getSourceAs
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*
import kotlin.math.cos
import kotlin.math.pow
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName
import com.mapbox.maps.extension.style.projection.generated.projection
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.style
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.gestures

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    boundsOptions: CameraBoundsOptions,
    showSheet: () -> Unit,
) {
    val locationViewModel: LocationViewModel = hiltViewModel()
    val mapViewModel: MapViewModel = hiltViewModel()

    val context = LocalContext.current

    // Defaults
    val gacMarker by rememberUpdatedState(newValue = resizedBitmap(R.drawable.gac_pin, 60))

    // Collect states from ViewModel
    val currentLocation by locationViewModel.location.collectAsState()
    val mapStyle by mapViewModel.mapStyle.collectAsState()
    val isTrackingLocation by mapViewModel.isTrackingLocation.collectAsState()
    val annotatedPoint by mapViewModel.annotatedPoint.collectAsState()
    val pulsingRadius by mapViewModel.pulsingRadius.collectAsState()
    val isAnimating by mapViewModel.isAnimating.collectAsState()

    // Composable-specific states
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var mapListeners by remember { mutableStateOf<MapBoxListeners?>(null) } // Step 1
    var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }
    var lastLoadedStyle by remember { mutableStateOf(mapStyle) }

//    var annotatedPoint by remember { mutableStateOf<Point?>(null) }

    val annotationScope = rememberCoroutineScope()
    val mapScope = rememberCoroutineScope()

    val animationOptions = MapAnimationOptions.Builder()
        .duration(500L)
        .animatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                mapViewModel.setIsAnimating(true)
//                Log.d(MAP_BOX_MAP, "Start $isAnimating")
            }

            override fun onAnimationEnd(animation: Animator) {
                mapViewModel.setIsAnimating(false)
//                Log.d(MAP_BOX_MAP, "End $isAnimating")
            }

            override fun onAnimationCancel(animation: Animator) {
                mapViewModel.setIsAnimating(false)
//                Log.d(MAP_BOX_MAP, "Cancel $isAnimating")
            }

            override fun onAnimationRepeat(animation: Animator) {}
        })
        .build()

    AndroidView(
        factory = {
            Log.d(MAP_BOX_MAP, "AndroidView Created")
            mapView = MapView(it)

            val annotationApi = mapView?.annotations
            pointAnnotationManager = annotationApi?.createPointAnnotationManager()
            mapView?.gestures?.rotateEnabled = false

            mapListeners = MapBoxListeners(
                mapView = mapView!!,
                afterCameraTrackingDismissedAction = {
                    if (isTrackingLocation) { mapViewModel.setIsTrackingLocation(false) }
                }
            )

            mapView?.getMapboxMap()?.addOnMapClickListener { p ->
                if (isTrackingLocation) mapListeners?.onCameraTrackingDismissed()
//                mapScope.launch {
//                }
                showSheet()
//                mapViewModel.setAnnotatedPoint(p)
                true
            }
            mapView?.camera?.addCameraZoomChangeListener { zoom ->
                val currentLatitude = currentLocation?.latitude()
                if (currentLatitude != null) {
                    mapView?.location?.updateSettings {
                        pulsingEnabled = false
                    }

                    mapScope.launch {
                        mapViewModel.setPulsingRadius(
                            computePulsingRadiusPixels(
                                currentLatitude,
                                zoom
                            )
                        )
                        delay(100L)
                        mapView!!.location.updateSettings {
                            pulsingMaxRadius = pulsingRadius
                            pulsingEnabled = true
                        }
                    }

                }
            }

            // Track Puck based on LocationFAB's state
            if (isTrackingLocation) {
                mapListeners?.onCameraTrackingStarted()
            }

            // Configure the location component
            val topImage =
                ContextCompat.getDrawable(
                    context,
                    com.mapbox.maps.R.drawable.mapbox_mylocation_icon_bearing
                )!!
            val bearingImage =
                ContextCompat.getDrawable(
                    context,
                    com.mapbox.maps.R.drawable.mapbox_user_puck_icon
                    /*com.mapbox.maps.R.drawable.mapbox_user_stroke_icon*/    // default with bearing
                )!!
            val shadowImage =
                ContextCompat.getDrawable(
                    context,
                    com.mapbox.maps.R.drawable.mapbox_user_icon_shadow
                )!!
            val scaleExpression = """
                ["interpolate", ["linear"], ["zoom"],
                    0, 0.5,
                    20, 1
                ]
            """
            val userLocationPuck = LocationPuck2D(
                topImage = null,
                bearingImage = bearingImage,
                shadowImage = shadowImage,
                scaleExpression = scaleExpression
            )
            mapView?.location?.updateSettings {
                enabled = true
                pulsingEnabled = true
                locationPuck = userLocationPuck
            }

            val worldBounds = listOf(
                Point.fromLngLat(-180.0, 90.0),
                Point.fromLngLat(180.0, 90.0),
                Point.fromLngLat(180.0, -90.0),
                Point.fromLngLat(-180.0, -90.0),
                Point.fromLngLat(-180.0, 90.0)
            )
            val campusBounds = listOf(
                /*// Calculated with reference to diagonal and bearing
                topLeft.toPoint(),
                bottomLeft.toPoint(),
                bottomRight.toPoint(),
                topRight.toPoint(),
                topLeft.toPoint(),*/
                TOP_LEFT_BOUND,
                BOTTOM_LEFT_BOUND,
                BOTTOM_RIGHT_BOUND,
                TOP_RIGHT_BOUND,
                TOP_LEFT_BOUND
            )
            val maskedCampusPolygon = Polygon.fromLngLats(listOf(worldBounds, campusBounds))
            val pad = 3000.0
            val padding = EdgeInsets(pad, pad, pad, pad)
            val geometricalCameraOptions =
                mapView!!.getMapboxMap()
                    .cameraForGeometry(maskedCampusPolygon, padding, BEARING_ORIENTED, null)

            val defaultCameraOptions =
                CameraOptions.Builder()
                    .center(
                        currentLocation ?: Point.fromLngLat(
                            DEFAULT_POS.longitude,
                            DEFAULT_POS.latitude
                        )
                    )
                    .build()

            mapView?.getMapboxMap()?.setCamera(defaultCameraOptions)

            mapView?.getMapboxMap()?.setBounds(
                CameraBoundsOptions.Builder()
                    .bounds(
                        CoordinateBounds(
                            // World
                            Point.fromLngLat(0.0, 0.0),
                            Point.fromLngLat(0.0, 0.0),
                            /*Point.fromLngLat(
                                CAMPUS_BOUNDARY.first.longitude,
                                CAMPUS_BOUNDARY.first.latitude
                            ),
                            Point.fromLngLat(
                                CAMPUS_BOUNDARY.second.longitude,
                                CAMPUS_BOUNDARY.second.latitude
                            ),*/
                            true
                        )
                    )
                    .build()
            )
            mapView!!.apply {
                getMapboxMap().loadStyle(
                    style(mapStyle) {
                        /*+projection(ProjectionName.GLOBE)*/   // 3D View for 2D Maps
                        +geoJsonSource("mask-source-id") { geometry(maskedCampusPolygon) }
                    }
                ) { style ->
                    updateMapStyle(style)
                }
            }
        },
        update = {
            Log.d(MAP_BOX_MAP, "AndroidView Updated")

            if (isTrackingLocation) {
                mapListeners?.onCameraTrackingStarted()
            } else {
                mapListeners?.onCameraTrackingDismissed()
            }

            if (lastLoadedStyle != mapStyle) {
                mapView?.getMapboxMap()?.loadStyleUri(mapStyle) {
                    updateMapStyle(it)
                }
                lastLoadedStyle = mapStyle
            }

            annotationScope.launch {
                pointAnnotationManager?.let { manager ->
                    manager.deleteAll()
                    annotatedPoint?.let { p ->
                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(p)
                            .withIconImage(gacMarker)
                        manager.create(pointAnnotationOptions)
                        mapScope.launch {
                            mapView?.getMapboxMap()?.flyTo(
                                CameraOptions.Builder()
                                    .center(p)
                                    .zoom(ZOOM_FOCUSED)
                                    .build(),
                                animationOptions
                            )
                        }
                    }
//                }
                }
            }

        },
        modifier = modifier
    )

    /*LaunchedEffect(sheetState.currentValue) {
        if (!sheetState.isVisible) {
            mapViewModel.setAnnotatedPoint(null)
        }
    }*/

    /* LaunchedEffect(annotatedPoint) {
         Log.d(MAP_BOX_MAP, "annotatedPoint $annotatedPoint")

         pointAnnotationManager?.let { manager ->
             manager.deleteAll()
             annotatedPoint?.let { p ->
                 val pointAnnotationOptions = PointAnnotationOptions()
                     .withPoint(p)
                     .withIconImage(gacMarker)
                 manager.create(pointAnnotationOptions)
                 mapView?.getMapboxMap()?.flyTo(
                     CameraOptions.Builder()
                         .center(p)
                         .zoom(ZOOM_FOCUSED)
                         .build(),
                     animationOptions
                 )
             }
         }

     }*/

    /*LaunchedEffect(isTrackingLocation) {
        Log.d(MAP_BOX_MAP, "isTrackingLocation $isTrackingLocation")
        if (isTrackingLocation) {
            mapListeners?.onCameraTrackingStarted()
        } else {
            mapListeners?.onCameraTrackingDismissed()
        }
    }*/

}

fun metersPerPixel(latitude: Double, zoomLevel: Double): Double {
    val scale = 2.0.pow(zoomLevel)
    return (EARTH_EQUATOR_CIRCUMFERENCE * cos(latitude * Math.PI / 180)) / (256 * scale)
}

fun computePulsingRadiusPixels(latitude: Double, zoomLevel: Double): Float {
    val mpp = metersPerPixel(latitude, zoomLevel)
    return (PULSING_RADIUS_METRES / mpp).toFloat()
}

@Suppress("DEPRECATION")
fun getPlaceName(
    latitude: Double,
    longitude: Double,
    context: Context,
    callback: (String?) -> Unit
) {
    val geocoder = Geocoder(context)
    val maxResults = 1

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android Tiramisu and later
        geocoder.getFromLocation(
            latitude,
            longitude,
            maxResults,
            object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        callback(addresses[0].getAddressLine(0))
                    } else {
                        Log.d(MAP_BOX_MAP, "Tiramisu: Empty Address")
                        callback(null)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e(MAP_BOX_MAP, errorMessage ?: "Unknown error")
                    callback(null)
                }
            })
    } else {
        Log.d(MAP_BOX_MAP, "Dep")
        // Use the deprecated method for earlier Android versions
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, maxResults)
            if (addresses != null && addresses.isNotEmpty()) {
                callback(addresses[0].getAddressLine(0))
            } else {
                Log.d(MAP_BOX_MAP, "Deprecated: Empty Address")
                callback(null)
            }
        } catch (e: IOException) {
            callback(null)
        }
    }
}

@Composable
fun resizedBitmap(@DrawableRes id: Int, widthDp: Int): Bitmap {
    val context = LocalContext.current
    val resources = context.resources
    val originalBitmap = BitmapFactory.decodeResource(resources, id)
    val aspectRatio = originalBitmap.height.toDouble() / originalBitmap.width.toDouble()
    val heightPx = (widthDp * resources.displayMetrics.density * aspectRatio).toInt()
    val widthPx = (widthDp * resources.displayMetrics.density).toInt()

    return Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, false)
}


fun buildCameraOptions(
    zoom: Double = ZOOM_DEFAULT,
    pitch: Double = PITCH_DEFAULT,
    bearing: Double = BEARING_ORIENTED,
    center: Point,
): CameraOptions {
    return CameraOptions.Builder()
        .zoom(zoom)
        .pitch(pitch)
        .bearing(bearing)
        .center(center)
        .build()
}

fun updateMapStyle(style: Style) {
    if (style.getSourceAs<VectorSource>("composite") == null) {
        style.addSource(vectorSource("composite") {
            url(style.styleURI)
            /*url("mapbox://mapbox.mapbox-streets-v8")*/
        })
    }

    // Create and add the fill-extrusion layer
    style.addLayer(
        fillExtrusionLayer("building-extrusion-layer", "composite") {
            sourceLayer("building")
            filter(Expression.eq(Expression.get("extrude"), Expression.literal("true")))
            fillExtrusionColor(Expression.literal("#000"))
            fillExtrusionHeight(Expression.get("height"))
            // fillExtrusionHeight(Expression.literal(BUILDING_HEIGHT))
            fillExtrusionBase(Expression.get("min_height"))
            fillExtrusionOpacity(0.0)
        }
    )

    // Add building labels layer
    style.addLayer(
        symbolLayer("building-layer", "composite") {
            sourceLayer("building")
            filter(Expression.eq(Expression.get("extrude"), Expression.literal("true")))
            textField(Expression.get("name"))
            textSize(12.0)
            textColor(Expression.literal("#FF0000")) // Red
            textTranslate(listOf(0.0, BUILDING_HEIGHT))
            textTranslateAnchor(Expression.literal("viewport"))
            symbolZOrder(Expression.literal("viewport-y"))
            visibility(visibility = Visibility.VISIBLE)
        }
    )

    // Make the world outside of campus bounds transparent
    style.addLayer(
        fillLayer("mask-layer-id", "mask-source-id") {
            fillColor("#000000")
            fillOpacity(0.2)
        }
    )

    // Campus region boundary
    style.addLayer(
        lineLayer("rectangle-line-layer", "mask-source-id") {
            lineColor("black")
            lineWidth(1.0)
            lineBlur(1.0)
        }
    )

}