package com.mahkxai.gactour.android.presentation.screen.explore

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.fadeOut
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahkxai.gactour.android.common.ext.disableInteraction
import com.mahkxai.gactour.android.presentation.MainViewModel
import com.mahkxai.gactour.android.presentation.navigation.BottomBarNavGraph
import com.mahkxai.gactour.android.presentation.permission.MultiplePermissionsHandler
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapScreenContent
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapViewModel
import com.mahkxai.gactour.android.presentation.screen.explore.stream.StreamScreenContent
import com.mahkxai.gactour.android.presentation.screen.explore.stream.StreamViewModel
import com.mapbox.maps.MapboxExperimental
import com.ramcosta.composedestinations.annotation.Destination

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(
    ExperimentalMaterial3Api::class,
    MapboxExperimental::class
)
@BottomBarNavGraph(start = true)
@Destination
@Composable
fun ExploreScreen(
    mainViewModel: MainViewModel,
    mapViewModel: MapViewModel = hiltViewModel(),
    streamViewModel: StreamViewModel = hiltViewModel(),
    showBottomBar: (Boolean) -> Unit,
) {
    val locationState by mainViewModel.locationState.collectAsStateWithLifecycle()
    val streamState by streamViewModel.streamState.collectAsStateWithLifecycle()
    val uploadState by streamViewModel.uploadState.collectAsStateWithLifecycle()
    val mapUiState by mapViewModel.uiState

    var allLocationPermissionsGranted by remember { mutableStateOf(false) }

    LaunchedEffect(allLocationPermissionsGranted) {
        if (allLocationPermissionsGranted) mainViewModel.startLocationUpdate()
    }

    LaunchedEffect(locationState.location) {
        locationState.apply {
            location?.let { currentLocation ->
                mapViewModel.setCurrentLocation(currentLocation)
                streamViewModel.fetchNearbyMedia(currentLocation, 50.0)
            }
        }
    }

    LaunchedEffect(mapUiState.isMiniMap) {
        showBottomBar(!mapUiState.isMiniMap)
    }

    AnimatedVisibility(
        visible = mapUiState.isMiniMap,
        enter = EnterTransition.None,
        exit = fadeOut()
    ) {
        StreamScreenContent(
            activeMediaCategory = streamState.activeMediaCategory,
            activeCategoryMediaItems = streamState.activeCategoryMediaItems,
            mediaCategoryIndices = streamState.mediaCategoryIndices,
            setMediaCategory = streamViewModel::setMediaCategory,
            setSelectedMediaIndex = streamViewModel::setSelectedMediaIndex,
            closeStream = { mapViewModel.setIsMiniMap(false) }
        )
    }

    MapScreenContent(
        modifier = Modifier.disableInteraction(!allLocationPermissionsGranted),
        mapUiState = mapUiState,
        mapViewModel = mapViewModel,
        mediaItems = streamViewModel.getAllMedias(),
        activeMediaCategory = streamState.activeMediaCategory,
        activeCategoryMediaItems = streamState.activeCategoryMediaItems,
        activeMediaIndex = streamState.mediaCategoryIndices[streamState.activeMediaCategory],
        setMediaCategory = streamViewModel::setMediaCategory,
        setSelectedMediaIndex = streamViewModel::setSelectedMediaIndex,
        hasLocationPermission = allLocationPermissionsGranted,
        uploadState = uploadState,
        uploadMedia = streamViewModel::uploadMedia
    )

    MultiplePermissionsHandler(
        permissionKeys = listOf(PermissionKey.FINE_LOCATION, PermissionKey.COARSE_LOCATION),
        isFeatureOverlay = true
    ) { allLocationPermissionsGranted = it }
}