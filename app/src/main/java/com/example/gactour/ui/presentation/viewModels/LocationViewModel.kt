package com.example.gactour.ui.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gactour.location.LocationManager
import com.example.gactour.models.Coordinate
import com.example.gactour.utils.*
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel() {

    private val _location = MutableStateFlow<Point?>(null)
    val location: StateFlow<Point?> = _location

    private var userPreviouslyInBounds = true

    // This flow will be used to notify the view of any one-time events, such as showing a Toast
    private val _toastEvent = MutableStateFlow<String?>(null)
    val toastEvent: StateFlow<String?> = _toastEvent

    init {
        locationManager.addLocationListener { point ->
            updateLocation(point)
        }
        locationManager.startLocationUpdates()
    }

    private fun updateLocation(point: Point) {
        viewModelScope.launch {
            Log.d(VIEW_MODEL,"Loc $point")
//            val updatedLocation = updateLocationBasedOnBoundary(point)
            val updatedLocation = point
            _location.emit(updatedLocation)
        }
    }

    private fun updateLocationBasedOnBoundary(newLocation: Point): Point {
        val coordinate = Coordinate(newLocation.latitude(), newLocation.longitude())
        val isWithin =
            coordinate.latitude in CAMPUS_BOUNDARY.first.latitude..CAMPUS_BOUNDARY.second.latitude &&
                    coordinate.longitude in CAMPUS_BOUNDARY.first.longitude..CAMPUS_BOUNDARY.second.longitude

        return if (isWithin) {
            userPreviouslyInBounds = true
            newLocation
        } else {
            if (userPreviouslyInBounds) {
                _toastEvent.value = "You're Off Campus"  // Notify the View
                userPreviouslyInBounds = false
            }
            Log.d(
                MAIN_APPLICATION,
                "User out of bounds, defaulting to Three Flags"
            )
            Point.fromLngLat(DEFAULT_POS.longitude, DEFAULT_POS.latitude)
        }
    }

    fun clearToastEvent() {
        _toastEvent.value = null
    }

    override fun onCleared() {
        super.onCleared()
        locationManager.stopLocationUpdates()
        locationManager.removeLocationListener { point -> updateLocation(point) }
    }

    fun cleanup() {
        locationManager.stopLocationUpdates()
        locationManager.cleanup()
    }

}
