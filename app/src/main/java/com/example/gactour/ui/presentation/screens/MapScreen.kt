package com.example.gactour.ui.presentation.screens

import android.util.Log
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gactour.ui.presentation.components.MapContent
import com.example.gactour.ui.presentation.components.ModalSheetContent
import com.example.gactour.ui.presentation.viewModels.MapViewModel
import com.example.gactour.utils.*
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraBoundsOptions
import com.mapbox.maps.CoordinateBounds
import kotlinx.coroutines.*


@ExperimentalMaterial3Api
@FlowPreview
@Composable
fun MapScreen() {
    val mapViewModel: MapViewModel = hiltViewModel()

    val campusBounds = getCampusBounds()
    val sheetState = rememberModalBottomSheetState()
    val sheetScope = rememberCoroutineScope()
    val markerText by remember { mutableStateOf("") }

    var isSheetOpen by rememberSaveable { mutableStateOf(false) }
//    var isSheetOpen by remember { mutableStateOf(false) }

    Log.d(MAP_SCREEN, "MapScreen Created")

    MapContent(
        boundsOptions = campusBounds,
        showSheet = {
            isSheetOpen = true

//            sheetScope.launch { sheetState.show() }
//                .invokeOnCompletion {
//                    if (sheetState.isVisible) {
//                        isSheetOpen = true
//                    }
//                }

        }
    )

    if (isSheetOpen) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                isSheetOpen = false
                Log.d(MAP_SCREEN, "Removed Annotation")
//                mapViewModel.setAnnotatedPoint(null)
            },
        ) {
            ModalSheetContent(
                markerText = markerText,
                closeBottomSheet = {
                    sheetScope.launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                isSheetOpen = false
                            }
                        }
                })
        }
    }

}

@Composable
fun getCampusBounds(): CameraBoundsOptions {
    return CameraBoundsOptions.Builder()
        .bounds(
            CoordinateBounds(
                Point.fromLngLat(
                    CAMPUS_BOUNDARY.first.longitude,
                    CAMPUS_BOUNDARY.first.latitude
                ),
                Point.fromLngLat(
                    CAMPUS_BOUNDARY.second.longitude,
                    CAMPUS_BOUNDARY.second.latitude
                ),
                false
            )
        )
        .minZoom(5.0)
        .build()
}


/*ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = Color(0xFF3B3B3B),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color(0x11000000),
        sheetContent = {
            ModalSheetContent(markerText = markerText)
            {
                sheetScope.launch {
                    mapViewModel.setAnnotatedPoint(null)
                    sheetState.hide()
                }
            }
        },

        )
    {
        MapContent(
            boundsOptions = campusBounds,
            sheetState = sheetState,
            sheetScope = sheetScope,
            annotatedPoint = annotatedPointState,
            showSheet = {
                sheetScope.launch {
                    Log.d(MAP_BOX_MAP, "BottomSheet shown")
                    sheetState.show()
                }
            },
            setAnnotatedPoint = { annotatedPointState = it }
        )
    }*/


/*
@FlowPreview
@ExperimentalMaterialApi
@Composable
fun MapScreen(
    viewModel: MainViewModel,
) {
    Log.d(MAP_SCREEN, "MapScreen created")

    val annotatedPoint = remember { mutableStateOf<Point?>(null) }
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val markerText by remember { mutableStateOf("") }
    val sheetScope = rememberCoroutineScope()
    val campusBounds: CameraBoundsOptions =
        CameraBoundsOptions.Builder()
            .bounds(
                CoordinateBounds(
                    Point.fromLngLat(
                        CAMPUS_BOUNDARY.first.longitude,
                        CAMPUS_BOUNDARY.first.latitude
                    ),
                    Point.fromLngLat(
                        CAMPUS_BOUNDARY.second.longitude,
                        CAMPUS_BOUNDARY.second.latitude
                    ),
                    false
                )
            )
            .minZoom(14.0)
            .build()

    LaunchedEffect(annotatedPoint.value) {
        if (annotatedPoint.value != null && sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            sheetScope.launch {
                sheetState.show()
            }
        }
    }

    LaunchedEffect(sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.Hidden && annotatedPoint.value != null) {
            annotatedPoint.value = null
        }
    }

    val context = LocalContext.current
    val hasLocationPermission = context.hasLocationPermission()

    var mapStyle by remember { mutableStateOf(MapStyles.Minimo.url) }
    var isTrackingLocation by remember { mutableStateOf(false) }

    Log.d(MAIN_ACTIVITY, "Location Tracking? $isTrackingLocation")

    // Drawables for Current Location FAB
    val trackingEnabledIcon = painterResource(id = R.drawable.map_my_location_enabled)
    val trackingDisabledIcon = painterResource(id = R.drawable.map_my_location_disabled)


    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetBackgroundColor = Color(0xFF3B3B3B),
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        scrimColor = Color(0x11000000),
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                DragHandle()

                Text(
                    text = markerText,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.Center)
                )

                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    tint = Color.White,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-16).dp, y = 16.dp)
                        .clickable {
                            sheetScope.launch {
                                if (sheetState.isVisible) {
                                    sheetScope.launch {
                                        sheetState.hide()
                                        annotatedPoint.value = null
                                    }
                                }
                            }
                        }
                )
            }

        },
    ) {


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

                MapBoxMap(
                    annotatedPoint = annotatedPoint,
                    modifier = Modifier.fillMaxSize(),
                    boundsOptions = campusBounds,
                )

                FloatingActionButton(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-16).dp, y = (-16).dp)
                        .size(56.dp),
                    onClick = {
                        if (!hasLocationPermission) {
                            Toast.makeText(context, "Location is Disabled", Toast.LENGTH_SHORT)
                                .show()
                            isTrackingLocation = !isTrackingLocation
                        }
                    },
                    backgroundColor = Color.White,
                    content = {
                        Icon(
                            if (isTrackingLocation) trackingEnabledIcon else trackingDisabledIcon,
                            contentDescription = "Current Location",
                            tint = if (!hasLocationPermission) Color.Unspecified else Color.DarkGray
                        )
                    },
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 16.dp,
                        pressedElevation = 8.dp,
                    ),
                    contentColor = if (!hasLocationPermission) Color.White else Color.Unspecified
                )
            }

        }

    }
}
*/


/*
@FlowPreview
@Composable
fun MapScreen(
    mapStyle: String,
//    currentLocation: Point?,
    isTrackingLocation: Boolean,
    onToggleTracking: (Boolean) -> Unit
) {
    Log.d(MAP_SCREEN, "MapScreen created")

    val campusBounds: CameraBoundsOptions =
        CameraBoundsOptions.Builder()
            .bounds(
                CoordinateBounds(
                    Point.fromLngLat(
                        CAMPUS_BOUNDARY.first.longitude,
                        CAMPUS_BOUNDARY.first.latitude
                    ),
                    Point.fromLngLat(
                        CAMPUS_BOUNDARY.second.longitude,
                        CAMPUS_BOUNDARY.second.latitude
                    ),
                    false
                )
            )
            .minZoom(14.0)
            .build()

    MapBoxMap(
        modifier = Modifier.fillMaxSize(),
//        currentLocPoint = latestLocation,
        mapStyle = mapStyle,
        boundsOptions = campusBounds,
        isTrackingLocation = isTrackingLocation,
        onToggleTracking = onToggleTracking,
    )
}*/

