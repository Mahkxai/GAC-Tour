package com.example.gactour.ui.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.gactour.utils.MAP_BOX_MAP
import com.example.gactour.utils.MapStyles
import com.example.gactour.utils.PULSING_RADIUS_METRES
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.viewport.ViewportStatus
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    // Coroutine's job to manage your tasks
    private val viewModelJob = SupervisorJob()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // StateFlow for tracking location
    private val _isTrackingLocation = MutableStateFlow(false)
    val isTrackingLocation: StateFlow<Boolean> = _isTrackingLocation

    // StateFlow for the annotated point
    private val _annotatedPoint = MutableStateFlow<Point?>(null)
    val annotatedPoint: StateFlow<Point?> = _annotatedPoint

    // StateFlow for the zoom level
    private val _zoomLevel = MutableStateFlow(0.0)
    val zoomLevel: StateFlow<Double> = _zoomLevel

    // StateFlow for animation status
    private val _isAnimating = MutableStateFlow(false)
    val isAnimating: StateFlow<Boolean> = _isAnimating

    // StateFlow for the current map style
    private val _mapStyle = MutableStateFlow(MapStyles.MAPBOX_STREET.url)
    val mapStyle: StateFlow<String> = _mapStyle

    // StateFlow for the pulsing radius
    private val _pulsingRadius = MutableStateFlow(PULSING_RADIUS_METRES)
    val pulsingRadius: StateFlow<Float> = _pulsingRadius

    // Setters
    fun setIsTrackingLocation(isTrackingLocation: Boolean) {
        _isTrackingLocation.value = isTrackingLocation
    }

    fun setAnnotatedPoint(point: Point?) {
        _annotatedPoint.value = point
    }

    fun setIsAnimating(isAnimating: Boolean) {
        _isAnimating.value = isAnimating
    }

    fun setMapStyle(style: String) {
        _mapStyle.value = style
    }

    fun setPulsingRadius(pulsingRadius: Float) {
        _pulsingRadius.value = pulsingRadius
    }

    // Update isTracking state as a SHaredFlow
    /*fun setLocationTracking(isTrackingLocation: Boolean) {
        viewModelScope.launch {
            Log.d(VIEW_MODEL, "MapViewModel Emitted $isTrackingLocation")
            _isTrackingLocation.emit(isTrackingLocation)
        }
    }*/

    fun cleanup() {
        TODO("Stop any ongoing operations or listeners")
    }

    // Cleanup
    override fun onCleared() {
        super.onCleared()

        // Cancel all ongoing coroutine tasks
        viewModelJob.cancel()

        // Close or release any other resources if necessary
    }
}
