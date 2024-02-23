package com.mahkxai.gactour.android.presentation.screen.explore.map.content

import android.content.Context
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.common.ext.resize
import com.mahkxai.gactour.android.common.util.CameraConstants
import com.mahkxai.gactour.android.common.util.MapConstants
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapListeners
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapUiState
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapViewModel
import com.mapbox.bindgen.Value
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ImageHolder
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotationGroup
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.logo.generated.LogoSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@MapboxExperimental
@Composable
fun BoxScope.MapContent(
    mapUiState: MapUiState,
    mapViewModel: MapViewModel,
    mediaItems: List<GACTourMediaItem>,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    activeMediaIndex: Int?,
    hasLocationPermission: Boolean,
    showUploadSheet: () -> Unit,
) {
    val mapContext = LocalContext.current
    val density = LocalDensity.current
    val mapViewportState = rememberMapViewportState()
    var mapListeners by remember { mutableStateOf<MapListeners?>(null) }
    var fetchRingRadius by rememberSaveable { mutableFloatStateOf(1f) }
    var currentBearing by remember { mutableDoubleStateOf(CameraConstants.BEARING_DEFAULT) }
    var pointAnnotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    val mapScope = rememberCoroutineScope()

    val currentMediaAnnotations = remember(mediaItems) {
        mediaItems.map { mediaItem ->
            val point = Point.fromLngLat(mediaItem.longitude, mediaItem.latitude)
            PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(MapDefaults.mediaItemPin(mapContext))
                .withIconAnchor(IconAnchor.BOTTOM)
        }
    }
    var activeMediaPoint by remember(activeMediaIndex, activeCategoryMediaItems) {
        mutableStateOf<Point?>(null)
    }

    /* Animation Variables */
    val miniMapAnimationDuration = 400
    val miniMapAnimationDelay = 400
    val mapSize by animateDpAsState(
        targetValue = if (mapUiState.isMiniMap) 125.dp else maxOf(
            LocalConfiguration.current.screenWidthDp.dp,
            LocalConfiguration.current.screenHeightDp.dp
        ),
        animationSpec = tween(
            durationMillis = miniMapAnimationDuration,
            // delayMillis = miniMapAnimationDelay,
            delayMillis = if (mapUiState.isMiniMap) miniMapAnimationDelay else 0,
            easing = LinearOutSlowInEasing
        ),
        label = "Mini Map Size Animation"
    )
    val mapShape by animateIntAsState(
        targetValue = if (mapUiState.isMiniMap) 100 else 0,
        animationSpec = tween(
            durationMillis = miniMapAnimationDuration,
            // delayMillis = miniMapAnimationDelay,
            // durationMillis = miniMapAnimationDuration * (if (mapUiState.isMiniMap) 3 else 1),
            delayMillis = if (mapUiState.isMiniMap) miniMapAnimationDelay else 0,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { if (it == 0) mapViewModel.setIsFullScreenMapReady(true) },
        label = "Mini Map Shape Animation"
    )
    val mapPadding by animateDpAsState(
        targetValue = if (mapUiState.isMiniMap) 16.dp else 0.dp,
        animationSpec = tween(
            durationMillis = miniMapAnimationDuration,
            // delayMillis = miniMapAnimationDelay,
            // durationMillis = miniMapAnimationDuration * (if (mapUiState.isMiniMap) 3 else 1),
            delayMillis = if (mapUiState.isMiniMap) miniMapAnimationDelay else 0,
            easing = FastOutSlowInEasing
        ),
        label = "Mini Map Padding Animation"
    )
    val uploadMarkerPinSize by animateFloatAsState(
        targetValue = if (mapUiState.isUploadPinVisible) 1.0f else 0.0f,
        animationSpec = tween(
            durationMillis = 150,
            easing = FastOutSlowInEasing
        ),
        finishedListener = { currentScale ->
            if (currentScale == 1.0f) {
                showUploadSheet()
                mapViewportState.easeTo(
                    cameraOptions = CameraOptions.Builder()
                        .center(mapUiState.uploadPointAnnotation)
                        .zoom(CameraConstants.ZOOM_FOCUSED)
                        .build(),
                    animationOptions = MapAnimationOptions.Builder()
                        .startDelay(50)
                        .duration(400)
                        .build(),
                )
            } else {
                mapViewModel.setUploadPointAnnotation(null)
            }
        },
        label = "Upload Marker Size Animation"
    )



    MapboxMap(
        modifier = Modifier
            .padding(horizontal = mapPadding, vertical = mapPadding)
            .size(mapSize)
            .clip(RoundedCornerShape(mapShape))
            .border(
                if (mapUiState.isMiniMap) 1.dp else 0.dp,
                Color.Black,
                RoundedCornerShape(mapShape)
            )
            .align(Alignment.BottomStart),
        // .align(Alignment.BottomEnd),
        // .align(Alignment.TopEnd),
        // .align(Alignment.TopStart),
        mapViewportState = mapViewportState,
        compassSettings = MapDefaults.getCompassSettings(mapUiState.isFullScreenMapReady, density),
        gesturesSettings = MapDefaults.getGesturesSettings(mapUiState.isFullScreenMapReady),
        scaleBarSettings = MapDefaults.getScaleBarSettings(
            mapUiState.isFullScreenMapReady,
            mapContext
        ),
        attributionSettings = MapDefaults.getAttributionSettings(
            mapUiState.isFullScreenMapReady,
            mapContext
        ),
        logoSettings = MapDefaults.getLogoSettings(mapUiState.isFullScreenMapReady, mapContext),
        mapInitOptionsFactory = { context -> MapDefaults.getMapInitOptions(context) },
        onMapClickListener = { point ->
            if (mapUiState.isMiniMap) {
                mapScope.launch {
                    delay(miniMapAnimationDelay / 4L)
                    mapViewModel.setIsMiniMap(false)
                }
            } else {
                mapListeners?.onTrackingDismissed()
                mapViewModel.setUploadPointAnnotation(point)
                mapViewModel.setIsUploadPinVisible(true)
                /*
                Flying to PointAnnotation and Showing Upload Sheet is done on
                uploadMarkerPinSize animation completion
                */
            }
            false
        },
    ) {


        mapUiState.uploadPointAnnotation?.let { point ->
            PointAnnotation(
                point = point,
                iconImageBitmap = MapDefaults.uploadMarkerPin(mapContext),
                iconAnchor = IconAnchor.BOTTOM,
                iconSize = uploadMarkerPinSize.toDouble()
            )
        }

        if (mapUiState.isFullScreenMapReady) {
            key(mediaItems) {
                PointAnnotationGroup(
                    annotations = currentMediaAnnotations,
                    iconAllowOverlap = true,
                    iconIgnorePlacement = true,
                )
            }
        }

        if (!mapUiState.isFullScreenMapReady && activeCategoryMediaItems.isNotEmpty()) {
            val activeMediaLat = activeCategoryMediaItems[activeMediaIndex!!].latitude
            val activeMediaLng = activeCategoryMediaItems[activeMediaIndex].longitude
            activeMediaPoint = Point.fromLngLat(activeMediaLng, activeMediaLat)

            PointAnnotation(
                point = activeMediaPoint!!,
                iconImageBitmap = MapDefaults.mediaItemPin(mapContext),
                iconAnchor = IconAnchor.BOTTOM,
                iconSize = 0.75,
            )
        }

        // State Change to MiniMap
        MapEffect(mapUiState.isMiniMap) { mapView ->
            if (mapUiState.isMiniMap) {
                mapViewModel.setIsFullScreenMapReady(false)
                if (mapUiState.is3DView) mapViewModel.setIs3DView(false)
                mapViewportState.flyTo(
                    cameraOptions = CameraOptions.Builder()
                        .zoom(CameraConstants.ZOOM_FOCUSED - 1.5)
                        .center(mapUiState.currentLocation)
                        .bearing(currentBearing)
                        .build(),
                    animationOptions = MapAnimationOptions.Builder()
                        .duration(miniMapAnimationDuration.toLong())
                        .build(),
                    completionListener = {
                        if (!mapUiState.isTrackingLocation) mapListeners?.onLocationTrackingStarted()
                        if (!mapUiState.isTrackingBearing) mapListeners?.onBearingTrackingStarted()
                    }
                )
            } else {
                if (mapUiState.currentLocation != null) {
                    mapListeners?.onBearingTrackingDismissed()
                    mapViewportState.easeTo(
                        cameraOptions = CameraOptions.Builder()
                            .zoom(CameraConstants.ZOOM_FOCUSED)
                            .build(),
                        animationOptions = MapAnimationOptions.Builder()
                            .startDelay(miniMapAnimationDelay.toLong())
                            .build(),
                        completionListener = {
                            mapViewModel.setIsTrackingBearing(false)
                            if (!mapUiState.isTrackingLocation) mapViewModel.setIsTrackingLocation(true)
                        }
                    )
                }
            }
        }

        // Map Puck Setup
        if (hasLocationPermission) {
            MapEffect(Unit) { mapView ->
                pointAnnotationManager = mapView.annotations.createPointAnnotationManager()

                // Set Up Listeners
                mapListeners = MapListeners(
                    mapView = mapView,
                    currentLocation = mapUiState.currentLocation,
                    setCurrentBearing = { currentBearing = it },
                    setIsTrackingLocation = mapViewModel::setIsTrackingLocation,
                    setIsTrackingBearing = mapViewModel::setIsTrackingBearing,
                    setIs3DView = mapViewModel::setIs3DView,
                    setFetchRingRadius = { fetchRingRadius = it }
                )
                mapListeners?.onCameraShoved()
                mapListeners?.onCameraZoomChanged()
                mapListeners?.onPuckBearingChanged()

                mapView.location.updateSettings {
                    enabled = true
                    puckBearingEnabled = true
                    puckBearing = PuckBearing.HEADING
                }
            }

            // Fly to Current Location the First Time it's Received
            if (mapUiState.currentLocation != null) {
                MapEffect(Unit) {
                    delay(500)
                    mapViewportState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .zoom(CameraConstants.ZOOM_FOCUSED)
                            .bearing(CameraConstants.BEARING_GAC)
                            .center(mapUiState.currentLocation)
                            .build(),
                        animationOptions = MapAnimationOptions.Builder().duration(800).build(),
                    )
                }
            }

            // Update Media Fetch Ring
            MapEffect(fetchRingRadius) { mapView ->
                val diameter = fetchRingRadius * 2
                val shadowBitmap = (R.drawable.map_media_fetch_ring).resize(mapContext, diameter)
                val shadowImage = ImageHolder.from(shadowBitmap)
                mapView.location.updateSettings {
                    locationPuck = LocationPuck2D(
                        topImage = MapDefaults.topImage,
                        bearingImage = MapDefaults.bearingImage,
                        shadowImage = shadowImage,
                    )
                }
            }

            // Start Location Tracking if Location FAB is Toggled On
            MapEffect(mapUiState.isTrackingLocation) {
                if (mapUiState.isTrackingLocation) {
                    val miniMapZoomOffset = if (mapUiState.isMiniMap) -2.0 else 0.0
                    mapViewportState.flyTo(
                        cameraOptions = CameraOptions.Builder()
                            .center(mapUiState.currentLocation)
                            .zoom(CameraConstants.ZOOM_FOCUSED + miniMapZoomOffset)
                            .build(),
                        animationOptions = MapAnimationOptions.Builder().duration(400).build(),
                        completionListener = { mapListeners?.onLocationTrackingStarted() }
                    )
                }
            }

            // Toggle Bearing Tracking
            MapEffect(mapUiState.isTrackingBearing) {
                if (mapUiState.isTrackingBearing) {
                    val miniMapZoomOffset = if (mapUiState.isMiniMap) -2.0 else 0.0
                    mapViewportState.easeTo(
                        cameraOptions = CameraOptions.Builder()
                            .bearing(currentBearing)
                            .zoom(CameraConstants.ZOOM_FOCUSED + miniMapZoomOffset)
                            .build(),
                        completionListener = { mapListeners?.onBearingTrackingStarted() }
                    )
                } else {
                    mapListeners?.onBearingTrackingDismissed()
                }
            }
        }

        // Toggle 3D View
        MapEffect(mapUiState.is3DView) {
            val pitch =
                if (mapUiState.is3DView) CameraConstants.PITCH_3D
                else CameraConstants.PITCH_2D
            mapViewportState.flyTo(
                cameraOptions = CameraOptions.Builder().pitch(pitch).build()
            )
        }

        // Change Map Style
        MapEffect(mapUiState.mapStyle) { mapView ->
            mapView.mapboxMap.loadStyle(Style.STANDARD) { style ->
                style.setStyleImportConfigProperty(
                    "basemap",
                    "lightPreset",
                    Value.valueOf(mapUiState.mapStyle)
                )
            }
        }

    }
}

@OptIn(MapboxExperimental::class)
object MapDefaults {
    val topImage =
        ImageHolder.from(R.drawable.mapbox_user_icon_with_bearing)

    val bearingImage =
        ImageHolder.from(com.mapbox.maps.R.drawable.mapbox_user_stroke_icon)

    fun mediaItemPin(context: Context) =
        (R.drawable.mapbox_red_marker).resize(context, 32f)

    fun uploadMarkerPin(context: Context) =
        (R.drawable.gac_pin).resize(context, 96f)

    private val defaultFollowPuckViewportStateBearing =
        FollowPuckViewportStateBearing.Constant(CameraConstants.BEARING_DEFAULT)

    private fun defaultCompassSettings(density: Density) = CompassSettings.Builder()
        .setMarginRight(with(density) { 16.dp.toPx() })
        .setMarginTop(with(density) { 76.dp.toPx() })
        .build()

    private val locationTrackingViewportStateOption =
        FollowPuckViewportStateOptions.Builder()
            .zoom(CameraConstants.ZOOM_FOCUSED)
            .pitch(CameraConstants.PITCH_2D)
            .bearing(defaultFollowPuckViewportStateBearing)
            .build()

    private val bearingTrackingViewportStateOption =
        FollowPuckViewportStateOptions.Builder()
            .zoom(CameraConstants.ZOOM_FOCUSED)
            .pitch(CameraConstants.PITCH_3D)
            .bearing(FollowPuckViewportStateBearing.SyncWithLocationPuck)
            .build()

    /* MinMap Defaults*/
    private const val miniMapSettingsOffset = -999f

    private val miniMapGesturesSettings = GesturesSettings {
        doubleTapToZoomInEnabled = false
        doubleTouchToZoomOutEnabled = false
        increasePinchToZoomThresholdWhenRotating = false
        increaseRotateThresholdWhenPinchingToZoom = false
        pinchScrollEnabled = false
        pinchToZoomEnabled = false
        pinchToZoomDecelerationEnabled = false
        pitchEnabled = false
        quickZoomEnabled = false
        rotateEnabled = false
        rotateDecelerationEnabled = false
        scrollEnabled = false
        scrollDecelerationEnabled = false
        simultaneousRotateAndPinchToZoomEnabled = false
    }

    private val miniMapCompassSettings =
        CompassSettings.Builder().setMarginTop(miniMapSettingsOffset).build()

    private val miniMapScaleBarSettings = ScaleBarSettings.Builder().setEnabled(false).build()

    private val miniMapAttributionSettings =
        AttributionSettings.Builder().setMarginBottom(miniMapSettingsOffset).build()

    private val miniMapLogoSettings =
        LogoSettings.Builder().setMarginBottom(miniMapSettingsOffset).build()

    /* Get appropriate settings */
    fun getCompassSettings(isFullScreenMap: Boolean, density: Density) =
        if (!isFullScreenMap) miniMapCompassSettings
        else defaultCompassSettings(density)

    fun getGesturesSettings(isFullScreenMap: Boolean) =
        if (!isFullScreenMap) miniMapGesturesSettings
        else DefaultSettingsProvider.defaultGesturesSettings

    fun getScaleBarSettings(isFullScreenMap: Boolean, context: Context) =
        if (!isFullScreenMap) miniMapScaleBarSettings
        else DefaultSettingsProvider.defaultScaleBarSettings(context)

    fun getAttributionSettings(isFullScreenMap: Boolean, context: Context) =
        if (!isFullScreenMap) miniMapAttributionSettings
        else DefaultSettingsProvider.defaultAttributionSettings(context)

    fun getLogoSettings(isFullScreenMap: Boolean, context: Context) =
        if (!isFullScreenMap) miniMapLogoSettings
        else DefaultSettingsProvider.defaultLogoSettings(context)

    fun getMapInitOptions(context: Context) =
        MapInitOptions(
            context = context,
            styleUri = Style.STANDARD,
            cameraOptions = CameraOptions.Builder()
                .center(MapConstants.GAC_LOCATION)
                .zoom(CameraConstants.ZOOM_DEFAULT)
                .bearing(CameraConstants.BEARING_DEFAULT)
                .pitch(CameraConstants.PITCH_2D)
                .build(),
        )

}