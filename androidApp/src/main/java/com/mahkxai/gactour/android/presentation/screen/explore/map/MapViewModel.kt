package com.mahkxai.gactour.android.presentation.screen.explore.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.mahkxai.gactour.android.common.util.MapConstants
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(): ViewModel() {
    var uiState = mutableStateOf(MapUiState())
        private set

    fun setCurrentLocation(newLocation : Point) {
        uiState.value = uiState.value.copy(currentLocation = newLocation)
    }

    fun setCurrentBearing(newBearing : Double) {
        uiState.value = uiState.value.copy(currentBearing = newBearing)
    }

    fun setZoomLevel(newZoom : Double) {
        uiState.value = uiState.value.copy(zoomLevel = newZoom)
    }

    fun setMapStyle(newStyle : String) {
        uiState.value = uiState.value.copy(mapStyle = newStyle)
    }

    fun setIsMiniMap(newFlag: Boolean) {
        uiState.value = uiState.value.copy(isMiniMap = newFlag)
    }

    fun setIsFullScreenMapReady(newFlag: Boolean) {
        uiState.value = uiState.value.copy(isFullScreenMapReady = newFlag)
    }

    fun setIsTrackingLocation(newFlag: Boolean) {
        uiState.value = uiState.value.copy(isTrackingLocation = newFlag)
    }

    fun setIsTrackingBearing(newFlag: Boolean) {
        uiState.value = uiState.value.copy(isTrackingBearing = newFlag)
    }

    fun setIs3DView(newFlag: Boolean) {
        uiState.value = uiState.value.copy(is3DView = newFlag)
    }

    fun setUploadPointAnnotation(newPoint : Point?) {
        uiState.value = uiState.value.copy(uploadPointAnnotation = newPoint)
    }

    fun setIsUploadPinVisible(newFlag : Boolean) {
        uiState.value = uiState.value.copy(isUploadPinVisible = newFlag)
    }

}

data class MapUiState(
    val currentLocation: Point? = null,
    val currentBearing: Double? = null,
    val mapStyle: String = MapConstants.MapStyle.DAWN.styleName,
    val zoomLevel: Double = 0.0,
    val isMiniMap: Boolean = false,
    val isFullScreenMapReady: Boolean = true,
    val isTrackingLocation: Boolean = false,
    val isTrackingBearing: Boolean = false,
    val is3DView: Boolean = false,
    val uploadPointAnnotation: Point? = null,
    val isUploadPinVisible: Boolean = false,
)