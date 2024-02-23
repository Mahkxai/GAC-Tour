package com.mahkxai.gactour.android.presentation.screen.explore.map

import com.mahkxai.gactour.android.common.ext.calculatePixelRadius
import com.mahkxai.gactour.android.common.util.CameraConstants
import com.mahkxai.gactour.android.common.util.MapConstants
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.android.gestures.ShoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.OnShoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.gestures.setGesturesManager
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class MapListeners(
    private val mapView: MapView,
    private val currentLocation: Point?,
    private val setCurrentBearing: (Double) -> Unit,
    private val setIsTrackingLocation: (Boolean) -> Unit,
    private val setIsTrackingBearing: (Boolean) -> Unit,
    private val setIs3DView: (Boolean) -> Unit,
    private val setFetchRingRadius: (Float) -> Unit
) {

    private val onIndicatorPositionChangedListener =
        OnIndicatorPositionChangedListener { point ->
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(point)
                    .build()
            )
            mapView.gestures.focalPoint = mapView.mapboxMap.pixelForCoordinate(point)
        }

    private val onIndicatorBearingChangedListener =
        OnIndicatorBearingChangedListener { bearing ->
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .bearing(bearing)
                    .build()
            )
        }

    private val onMoveListener = object : OnMoveListener {
        override fun onMove(detector: MoveGestureDetector): Boolean { return false }

        override fun onMoveBegin(detector: MoveGestureDetector) { onTrackingDismissed() }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    private val onShoveListener = object : OnShoveListener {
        override fun onShove(detector: ShoveGestureDetector) {
            val pitch = mapView.mapboxMap.cameraState.pitch
            setIs3DView(pitch > 0)
        }

        override fun onShoveBegin(detector: ShoveGestureDetector) {}

        override fun onShoveEnd(detector: ShoveGestureDetector) {}
    }

    private val onCameraZoomChangeListener = {
        val fetchRingRadius = mapView.mapboxMap
            .calculatePixelRadius(currentLocation?.latitude() ?: MapConstants.GAC_LOCATION.latitude())
            .coerceAtLeast(1f)
       setFetchRingRadius(fetchRingRadius)
    }

    fun onLocationTrackingStarted() {
        mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    fun onBearingTrackingStarted() {
        mapView.location.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    fun onBearingTrackingDismissed() {
        mapView.location.removeOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    fun onTrackingDismissed() {
        mapView.location.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        mapView.location.removeOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
        mapView.gestures.removeOnMoveListener(onMoveListener)
        setIsTrackingLocation(false)
        setIsTrackingBearing(false)
    }

    fun onCameraShoved() {
        mapView.gestures.addOnShoveListener(
            onShoveListener
        )
    }

    fun onCameraZoomChanged() {
        mapView.camera.addCameraZoomChangeListener {
            onCameraZoomChangeListener()
        }
    }

    fun onPuckBearingChanged() {
        mapView.location.addOnIndicatorBearingChangedListener {
            setCurrentBearing(it)
        }
    }
}