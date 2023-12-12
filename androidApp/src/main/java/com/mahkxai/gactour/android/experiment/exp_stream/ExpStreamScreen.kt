package com.mahkxai.gactour.android.experiment.exp_stream

/*
@RequiresApi(Build.VERSION_CODES.S)
// @BottomBarNavGraph
@Destination
@Composable
fun StreamScreen(
    mainViewModel: MainViewModel,
    streamViewModel: StreamViewModel = hiltViewModel(),
) {
    val viewState: ViewState by mainViewModel.viewState.collectAsStateWithLifecycle()
    val streamState by streamViewModel.streamState.collectAsStateWithLifecycle()
    var allLocationPermissionsGranted by remember { mutableStateOf(false) }

    // MultiplePermissionsHandler(
    //     permissionKeys = listOf(PermissionKey.FINE_LOCATION, PermissionKey.COARSE_LOCATION),
    //     isFeatureOverlay = false,
    // ) { allLocationPermissionsGranted = it }

    LaunchedEffect(allLocationPermissionsGranted) {
        // if (allLocationPermissionsGranted) mainViewModel.handle(PermissionEvent.Granted)
    }

    with(viewState) {
        when (this) {
            ViewState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ViewState.Success -> {
                LaunchedEffect(location) {
                    location?.let { loc ->
                        streamViewModel.fetchNearbyMedia(loc, 50.0)
                    }
                }
                ExpStreamScreenContent(mediaItems = streamViewModel.getAllMedias())
            }

            ViewState.RevokedPermissions -> {}
        }
    }
}

@Composable
fun ExpStreamScreenContent(
    initialMediaIndex: Int = 0,
    mediaItems: List<GACTourMediaItem>
) {
    var currentMediaIndex by remember { mutableIntStateOf(initialMediaIndex) }
    var currentMediaAnnotatedPoint by remember { mutableStateOf<Point?>(null) }
    var isMinimap by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()  ) {
        if (mediaItems.isNotEmpty()) {
            LaunchedEffect(currentMediaIndex) {
                val lat = mediaItems[currentMediaIndex].latitude
                val lng = mediaItems[currentMediaIndex].longitude
                currentMediaAnnotatedPoint = Point.fromLngLat(lng, lat)
            }
            ExpStreamFeed(mediaItems = mediaItems) { currentMediaIndex = it }
        }

        ExpMiniMap(isMinimap = isMinimap, mediaAnnotatedPoint = currentMediaAnnotatedPoint)

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            onClick = { isMinimap = !isMinimap }) {
            Icon(
                imageVector =
                    if (isMinimap) Icons.Default.Place
                    else Icons.Default.PlayArrow,
                contentDescription = ""
            )
        }
    }
}*/
