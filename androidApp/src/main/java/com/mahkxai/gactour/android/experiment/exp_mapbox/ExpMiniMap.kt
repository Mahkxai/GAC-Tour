package com.mahkxai.gactour.android.experiment.exp_mapbox

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.common.ext.resize
import com.mahkxai.gactour.android.common.util.CameraConstants
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.DefaultSettingsProvider
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapboxMapScope
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.PuckBearing
import com.mapbox.maps.plugin.attribution.generated.AttributionSettings
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.gestures.generated.GesturesSettings
import com.mapbox.maps.plugin.locationcomponent.createDefault2DPuck
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings
import com.mapbox.maps.plugin.logo.generated.LogoSettings
import com.mapbox.maps.plugin.scalebar.generated.ScaleBarSettings
import com.mapbox.maps.plugin.viewport.data.DefaultViewportTransitionOptions
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateBearing
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions
import kotlinx.coroutines.delay

@OptIn(MapboxExperimental::class)
@Composable
fun BoxScope.ExpMiniMap(
    isMinimap: Boolean,
    mediaAnnotatedPoint: Point?
) {
    var shouldDisplayMap by remember { mutableStateOf(false) }
    var isNewPin by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val sizeState by animateDpAsState(
        targetValue = if (isMinimap) 150.dp else maxOf(
            LocalConfiguration.current.screenWidthDp.dp,
            LocalConfiguration.current.screenHeightDp.dp
        ),
        animationSpec = tween(durationMillis = 2000, easing = FastOutSlowInEasing)
    )

    val miniMapSize by animateFloatAsState(
        targetValue = if (shouldDisplayMap) 1.0f else 0.0f,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "Mini Map Expansion Animation",
    )

    val locationComponentSettings = remember {
        DefaultSettingsProvider.defaultLocationComponentSettings(context)
    }

    LaunchedEffect(mediaAnnotatedPoint) {
        if (mediaAnnotatedPoint != null) {
            isNewPin = false
            delay(500)
            isNewPin = true
        }
    }

    Box(
        modifier =
        Modifier
            .size(sizeState)
            .padding(if (isMinimap) 16.dp else 0.dp)
            .clip(if (isMinimap) CircleShape else RectangleShape)
            .background(Color.Gray)
            .border(1.dp, Color.Black, if (isMinimap) CircleShape else RectangleShape)
            .align(Alignment.TopStart)
            .animateContentSize()
        // Modifier
        //     .scale(miniMapSize)
        //     .miniMap()
        //     .align(Alignment.TopStart)
    ) {
        MapboxMap(
            locationComponentSettings = LocationComponentSettings
                .Builder(
                    createDefault2DPuck(withBearing = true)
                )
                .setEnabled(true)
                .setPuckBearingEnabled(true)
                .setPuckBearing(PuckBearing.HEADING)
                .build(),

            /*locationComponentSettings
                .toBuilder()
                .setLocationPuck(createDefault2DPuck(true))
                .setEnabled(true)
                .setPulsingEnabled(true)
                .setPuckBearingEnabled(true)
                .setPuckBearing(PuckBearing.HEADING)
                .setPulsingMaxRadius(100f)
                .build(),*/
            mapViewportState = MapViewportState().apply {
                transitionToFollowPuckState(
                    followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                        .pitch(CameraConstants.PITCH_2D)
                        .zoom(CameraConstants.ZOOM_FOCUSED - 1.0)
                        .bearing(FollowPuckViewportStateBearing.SyncWithLocationPuck)
                        .build(),
                    defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
                        .maxDurationMs(200)
                        .build(),
                    completionListener = { shouldDisplayMap = true }
                )
            },

            gesturesSettings = GesturesSettings {
                doubleTapToZoomInEnabled = false
                doubleTouchToZoomOutEnabled = false
                increasePinchToZoomThresholdWhenRotating = false
                increaseRotateThresholdWhenPinchingToZoom = false
                pinchScrollEnabled = false
                pinchToZoomDecelerationEnabled = false
                pinchToZoomEnabled = false
                pitchEnabled = false
                quickZoomEnabled = false
                rotateDecelerationEnabled = false
                rotateEnabled = false
                scrollDecelerationEnabled = false
                scrollEnabled = false
                simultaneousRotateAndPinchToZoomEnabled = false
                scrollEnabled = false
            },
            compassSettings = CompassSettings { visibility = false; marginTop = -999f },
            scaleBarSettings = ScaleBarSettings { enabled = false },
            attributionSettings = AttributionSettings { enabled = false },
            logoSettings = LogoSettings { enabled = false },
            // mapEvents = MapEvents(onStyleLoaded = {})
        ) {
            if (mediaAnnotatedPoint != null) {
                locationComponentSettings.locationPuck
                CurrentMediaMarker(
                    point = mediaAnnotatedPoint,
                    isNewPin = isNewPin
                )
            }

            MapEffect(Unit) { mapView ->
                // mapView.location.apply {
                //     enabled = true
                //     locationPuck = createDefault2DPuck(withBearing = true)
                //     puckBearingEnabled = true
                //     puckBearing = PuckBearing.HEADING
                // }
            }
        }
    }

    /*var locationComponentSettings by remember {
        mutableStateOf(DefaultSettingsProvider.defaultLocationComponentSettings(context))
    }

    var gesturesSettings by remember {
        mutableStateOf(DefaultSettingsProvider.defaultGesturesSettings)
    }

    val mapViewPortState = rememberMapViewportState {
        setCameraOptions {
            zoom(CameraConstants.ZOOM_DEFAULT)
            pitch(CameraConstants.PITCH_2D)
        }
    }*/
}

@OptIn(MapboxExperimental::class)
@Composable
fun MapboxMapScope.CurrentMediaMarker(
    point: Point,
    isNewPin: Boolean,
) {
    val context = LocalContext.current
    val mapPin = remember(context) {
        (R.drawable.gac_pin).resize(context, 48f)
    }
    val mapPinSize by animateFloatAsState(
        targetValue = if (isNewPin) 1f else 0.0f,
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing),
        label = "New Media Pin Animation",
    )

    PointAnnotation(
        point = point,
        iconImageBitmap = mapPin,
        iconAnchor = IconAnchor.BOTTOM,
        iconSize = mapPinSize.toDouble(),
    )
}

fun Modifier.miniMap(): Modifier =
    this
        .size(150.dp)
        .padding(16.dp)
        .clip(CircleShape)
        .background(Color.Gray)
        .border(1.dp, Color.Black, CircleShape)
