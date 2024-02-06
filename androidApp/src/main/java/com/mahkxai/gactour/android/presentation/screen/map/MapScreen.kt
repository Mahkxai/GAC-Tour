package com.mahkxai.gactour.android.presentation.screen.map

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.presentation.screen.stream.common.StreamSheetContent
import com.mahkxai.gactour.android.common.util.MapConstants
import com.mahkxai.gactour.android.presentation.MainViewModel
import com.mahkxai.gactour.android.common.ext.disableInteraction
import com.mahkxai.gactour.android.common.ext.ignoreNextModifiers
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.presentation.navigation.BottomBarNavGraph
import com.mahkxai.gactour.android.presentation.permission.MultiplePermissionsHandler
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import com.mahkxai.gactour.android.presentation.screen.map.content.MapContent
import com.mahkxai.gactour.android.presentation.screen.map.content.MapFABs
import com.mahkxai.gactour.android.presentation.screen.map.content.MapStyleControls
import com.mahkxai.gactour.android.presentation.screen.map.content.MapUploadContentSheet
import com.mahkxai.gactour.android.presentation.screen.stream.StreamViewModel
import com.mahkxai.gactour.android.presentation.screen.stream.UploadState
import com.mahkxai.gactour.android.presentation.screen.stream.common.StreamFeedContainer
import com.mahkxai.gactour.android.presentation.screen.stream.common.StreamItemCounter
import com.mahkxai.gactour.android.presentation.screen.stream.common.StreamTabRow
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
fun MapScreen(
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

@Destination
@ExperimentalMaterial3Api
@MapboxExperimental
@Composable
fun MapScreenContent(
    modifier: Modifier = Modifier,
    mapUiState: MapUiState,
    mapViewModel: MapViewModel,
    mediaItems: List<GACTourMediaItem>,
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    activeMediaIndex: Int?,
    setMediaCategory: (GACTourMediaType) -> Unit,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit,
    uploadState: UploadState,
    uploadMedia: (GACTourMediaType, List<GACTourUploadItem>) -> Unit,
    // uploadMedia: (GACTourMediaType, GACTourMediaItem, Uri, Uri?) -> Unit,
    hasLocationPermission: Boolean,
    requestLocationPermission: () -> Unit = {},
) {
    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState()
    var streamInfoHeight by remember { mutableStateOf(0.dp) }
    var isShowUploadSheet by remember { mutableStateOf(false) }
    val sheetPeekHeight = remember(mapUiState.isMiniMap, streamInfoHeight) {
        if (mapUiState.isMiniMap) 0.dp
        else MapConstants.PEEK_HEIGHT + streamInfoHeight
    }

    LaunchedEffect(mapUiState.isMiniMap, isShowUploadSheet) {
        if (mapUiState.isMiniMap || isShowUploadSheet) {
            bottomSheetScaffoldState.bottomSheetState.partialExpand()
        }
    }

    BottomSheetScaffold(
        modifier = modifier.ignoreNextModifiers(),
        scaffoldState = bottomSheetScaffoldState,
        containerColor = Color.Transparent,
        sheetPeekHeight = sheetPeekHeight,
        sheetContent = {
            StreamSheetContent(
                mediaItems = mediaItems,
                activeMediaCategory = activeMediaCategory,
                activeCategoryMediaItems = activeCategoryMediaItems,
                setMediaCategory = setMediaCategory,
                setStreamInfoHeight = { streamInfoHeight = it },
                setSelectedMediaIndex = { mediaCategory, currentMediaIndex ->
                    mapViewModel.setIsMiniMap(true)
                    setSelectedMediaIndex(mediaCategory, currentMediaIndex)
                },
            )
        },
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val bottomSheetOffsetFromTop = with(LocalDensity.current) {
                bottomSheetScaffoldState.bottomSheetState.requireOffset().toDp()
            }
            val fabContainerOffset = remember(bottomSheetOffsetFromTop) {
                bottomSheetOffsetFromTop - maxHeight
            }

            MapContent(
                mapUiState = mapUiState,
                mapViewModel = mapViewModel,
                mediaItems = mediaItems,
                activeCategoryMediaItems = activeCategoryMediaItems,
                activeMediaIndex = activeMediaIndex,
                hasLocationPermission = hasLocationPermission,
                showUploadSheet = { isShowUploadSheet = true }
            )

            AnimatedVisibility(
                visible = !mapUiState.isMiniMap,
                enter = AnimationDefaults.mapControlsEnterTransition,
                exit = AnimationDefaults.mapControlsExitTransition
            ) {
                Box(Modifier.fillMaxSize()) {
                    MapFABs(
                        modifier = Modifier.align(Alignment.BottomEnd),
                        isTrackingLocation = mapUiState.isTrackingLocation,
                        isTrackingBearing = mapUiState.isTrackingBearing,
                        is3DView = mapUiState.is3DView,
                        setIsTrackingLocation = mapViewModel::setIsTrackingLocation,
                        setIsTrackingBearing = mapViewModel::setIsTrackingBearing,
                        setIs3DView = mapViewModel::setIs3DView,
                        hasLocationPermission = hasLocationPermission,
                        onDisabledLocationFABClick = requestLocationPermission,
                        fabContainerOffset = fabContainerOffset,
                    )
                    MapStyleControls(
                        modifier = Modifier.align(Alignment.TopEnd),
                        currentMapStyle = mapUiState.mapStyle,
                        setMapStyle = mapViewModel::setMapStyle
                    )
                    if (isShowUploadSheet) {
                        MapUploadContentSheet(
                            setIsUploadPinVisible = mapViewModel::setIsUploadPinVisible,
                            hideUploadSheet = { isShowUploadSheet = false },
                            contentPoint = mapUiState.uploadPointAnnotation,
                            uploadState = uploadState,
                            uploadMedia = uploadMedia,
                            // setUploadPointAnnotation = mapViewModel::setUploadPointAnnotation
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun StreamScreenContent(
    activeMediaCategory: GACTourMediaType,
    activeCategoryMediaItems: List<GACTourMediaItem>,
    mediaCategoryIndices: Map<GACTourMediaType, Int>,
    setMediaCategory: (GACTourMediaType) -> Unit,
    setSelectedMediaIndex: (GACTourMediaType, Int) -> Unit,
    closeStream: () -> Unit
) {
    BackHandler { closeStream() }

    Box(
        modifier = Modifier.background(Color.Black),
        contentAlignment = Alignment.TopCenter
    ) {
        StreamFeedContainer(
            activeMediaCategory = activeMediaCategory,
            activeCategoryMediaItems = activeCategoryMediaItems,
            mediaCategoryIndices = mediaCategoryIndices,
            setMediaCategory = setMediaCategory,
            setSelectedMediaIndex = setSelectedMediaIndex
        )

        StreamTabRow(
            activeMediaCategory = activeMediaCategory,
            setMediaCategory = setMediaCategory
        )

        if (activeCategoryMediaItems.isNotEmpty()) {
            val activeItem = mediaCategoryIndices[activeMediaCategory] ?: 0
            val totalItems = activeCategoryMediaItems.size

            StreamItemCounter(
                activeItem = activeItem,
                totalItems = totalItems,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

    }
}


object AnimationDefaults {
    private const val animationDuration = 500

    val mapControlsEnterTransition = fadeIn() + expandVertically()

    val mapControlsExitTransition =
        fadeOut(tween(delayMillis = animationDuration)) +
                shrinkVertically(tween(delayMillis = animationDuration))
}

