package com.mahkxai.gactour.android.presentation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahkxai.gactour.android.domain.usecase.GetLocationUseCase
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getLocationUseCase: GetLocationUseCase
) : ViewModel() {
    private val _locationState = MutableStateFlow(LocationState())
    val locationState = _locationState.asStateFlow()

    fun startLocationUpdate() {
        viewModelScope.launch {
            getLocationUseCase.invoke().collect { location ->
                location?.let { currentLocation ->
                    _locationState.value = LocationState(
                        location = currentLocation,
                    )
                }
            }
        }
    }

    private val _viewState: MutableStateFlow<ViewState> = MutableStateFlow(ViewState.Loading)
    val viewState = _viewState.asStateFlow()

    fun handle(event: PermissionEvent) {
        when (event) {
            PermissionEvent.Granted -> {
                viewModelScope.launch {
                    getLocationUseCase.invoke().collect { location ->
                        location?.let { currentLocation ->
                            _viewState.value = ViewState.Success(currentLocation)
                        }
                    }
                }
            }
            PermissionEvent.Revoked -> {
                _viewState.value = ViewState.RevokedPermissions
            }
        }
    }

    // fun getFusedLocationProvider(): LocationProvider = fusedLocationManager
}

data class LocationState(
    val location: Point? = null,
    val bearing: Double? = null
)

sealed interface ViewState {
    data object Loading : ViewState
    data class Success(val location: Point?) : ViewState
    data object RevokedPermissions : ViewState
}

sealed interface PermissionEvent {
    data object Granted : PermissionEvent
    data object Revoked : PermissionEvent
}