package com.example.gac_tour.examples.gactour

import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location

class MapBoxListeners(
    private val mapView: MapView,
    private val afterCameraTrackingDismissedAction: () -> Unit = {}  // to disable LocationFAB
) {
    val onIndicatorPositionChangedListener =
        OnIndicatorPositionChangedListener { point ->
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .zoom(ZOOM)
                    .center(point)
                    .build()
            )
            mapView.gestures.focalPoint = mapView.getMapboxMap().pixelForCoordinate(point)
        }

    val onIndicatorBearingChangedListener =
        OnIndicatorBearingChangedListener { bearing ->
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .bearing(bearing)
                    .build()
            )
        }

    val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    fun onCameraTrackingStarted(trackBearing: Boolean = false) {
        mapView.location.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )

        if (trackBearing) {
            mapView.location.addOnIndicatorBearingChangedListener(
                onIndicatorBearingChangedListener
            )
        }

        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    fun onCameraTrackingDismissed(trackBearing: Boolean = false) {
        mapView.location.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )

        if (trackBearing) {
            mapView.location.removeOnIndicatorBearingChangedListener(
                onIndicatorBearingChangedListener
            )
        }

        mapView.gestures.removeOnMoveListener(onMoveListener)

        afterCameraTrackingDismissedAction.invoke()     // disable LocationFAB

    }

    private companion object {
        const val ZOOM = 17.0
        const val BEARING = -58.0
        const val PITCH = 60.0
    }
}