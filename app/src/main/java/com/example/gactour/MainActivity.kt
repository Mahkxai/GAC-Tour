package com.example.gactour

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import com.example.gactour.ui.theme.GACTourTheme
import android.Manifest
import android.content.Intent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.example.gactour.location.LocationService
import com.example.gactour.ui.presentation.screens.MapScreen
import com.example.gactour.ui.presentation.viewModels.LocationViewModel
import com.example.gactour.ui.presentation.viewModels.MapViewModel
import com.mapbox.maps.plugin.Plugin
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@ExperimentalMaterial3Api
@FlowPreview
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val locationViewModel: LocationViewModel by viewModels()
    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestLocationPermissions()
        setContent {
            GACTourTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MapScreen()

                }
            }
        }
        startLocationService()
    }

    override fun onDestroy() {
        super.onDestroy()
        locationViewModel.cleanup()
        mapViewModel.cleanup()
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
    }

    private fun startLocationService() {
        Intent(applicationContext, LocationService::class.java).apply {
            action = LocationService.ACTION_START
            startService(this)
        }
    }
}

/*
@FlowPreview
@ExperimentalMaterialApi
@Composable
fun MapContent(
    viewModel: MainViewModel,
) {
    Log.d(MAIN_ACTIVITY, "MapContent Created")
    val currentLocation by viewModel.location.collectAsState()
    Log.d(MAIN_ACTIVITY, "Cur Loc $currentLocation")
    viewModel.toastEvent.collectAsState().value?.let { message ->
        Toast.makeText(LocalContext.current, message, Toast.LENGTH_SHORT).show()
        viewModel.clearToastEvent()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        val context = LocalContext.current
        val hasLocationPermission = context.hasLocationPermission()

        var mapStyle by remember { mutableStateOf(MapStyles.Minimo.url) }
        var isTrackingLocation by remember { mutableStateOf(false) }

        Log.d(MAIN_ACTIVITY, "Location Tracking? $isTrackingLocation")

        // Drawables for Current Location FAB
        val trackingEnabledIcon = painterResource(id = R.drawable.map_my_location_enabled)
        val trackingDisabledIcon = painterResource(id = R.drawable.map_my_location_disabled)

        Column(Modifier.fillMaxSize()) {
            MapStyleButtons(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
            ) {
                mapStyle = it
            }

            Box(modifier = Modifier.fillMaxSize()) {

                MapScreen(
                    mapStyle = mapStyle,
//                    currentLocation = currentLocation,
                    isTrackingLocation = isTrackingLocation
                ) { isTracking ->
                    // isTrackingLocation = isTracking
                }

            currentLocation?.let {
                    MapScreen(
                        mapStyle = mapStyle,
                        currentLocation = it,
                        isTrackingLocation = isTrackingLocation
                    ) { isTracking ->
                        // isTrackingLocation = isTracking
                    }
                }

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-16).dp)
                        .size(56.dp),
                    onClick = {
                        // Handle location button click
                        isTrackingLocation = !isTrackingLocation
                    },
                    backgroundColor = Color.White,
                    content = {
                        Icon(
                            if (isTrackingLocation) trackingEnabledIcon else trackingDisabledIcon,
                            contentDescription = "Current Location",
                            tint = Color.Unspecified
                        )
                    },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 16.dp,
                        pressedElevation = 8.dp,
                    ),
                )
            }
        }
    }
}
*/
