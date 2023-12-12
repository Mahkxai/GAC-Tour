package com.example.gac_tour.examples.gactour

import android.Manifest
import android.animation.ValueAnimator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.gac_tour.R
import com.example.gac_tour.examples.gactour.composables.AudioPlayer
import com.example.gac_tour.examples.utils.CityLocations
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.JsonObject
import com.mapbox.bindgen.Value
import com.mapbox.common.location.*
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.dsl.cameraOptions
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.animation.MapAnimationOptions.Companion.mapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.compass.generated.CompassSettings
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings
import com.mapbox.maps.plugin.locationcomponent.location
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.OnNeverAskAgain
import permissions.dispatcher.OnPermissionDenied
import permissions.dispatcher.RuntimePermissions

@UnstableApi
@RuntimePermissions
@MapboxExperimental
class GACTourActivity : ComponentActivity() {
    private val locationHelper = LocationHelper(this)

    @ExperimentalAnimationApi
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableMyLocationWithPermissionCheck()

        setContent {
            val firestore = FirebaseFirestore.getInstance()
            val storage = FirebaseStorage.getInstance()

            val context = LocalContext.current
            val gacPin = MapUtils.resizedBitmap(context, R.drawable.gac_pin, 80)
            val mapPin = MapUtils.resizedBitmap(context, R.drawable.mapbox_red_marker, 32)
            /*val mapPin = remember(context) {
                context.getDrawable(R.drawable.map_pin)!!.toBitmap()
            }*/
            val trackingEnabledIcon = painterResource(id = R.drawable.map_user_enabled)
            val trackingDisabledIcon = painterResource(id = R.drawable.map_user_disabled)

            val mapViewportState = rememberMapViewportState()
            var showSheet by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState()
            val sheetScope = rememberCoroutineScope()
            val scaffoldState = rememberBottomSheetScaffoldState()
            var scaffoldHeight by remember { mutableStateOf(0.dp) }
            var scaffoldSheetOffsetFromTop by remember { mutableStateOf(0.dp) }
            var visibleScaffoldSheetHeight by remember { mutableStateOf(0.dp) }
            var buttonContainerOffset by remember { mutableStateOf(0.dp) }

            var annotatedPoint by remember { mutableStateOf<Point?>(null) }
            var oldAnnotation by remember { mutableStateOf<PointAnnotation?>(null) }
            var pointAnnotationManager: PointAnnotationManager? by remember { mutableStateOf(null) }

            val pinSize: Float by animateFloatAsState(
                targetValue = if (annotatedPoint != null) 1f else 0.5f,
                animationSpec = tween(
                    durationMillis = 200,
                    easing = LinearOutSlowInEasing
                )
            )

            val mapScope = rememberCoroutineScope()
            var textStory by remember { mutableStateOf("") }
            var selectedMultiplePhotoUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
            var selectedMultipleVideoMediaUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
            var selectedMultipleAudioUri by remember { mutableStateOf<List<Uri>>(emptyList()) }
            val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { uris ->
                    selectedMultiplePhotoUri = uris
                    lifecycleScope.launch {
                        MapUtils.uploadMedia(firestore, storage, uris, "photos", annotatedPoint!!)
                    }
                }
            )
            val multipleVideoPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.PickMultipleVisualMedia(),
                onResult = { uris ->
                    selectedMultipleVideoMediaUri = uris
                    lifecycleScope.launch {
                        MapUtils.uploadMedia(firestore, storage, uris, "videos", annotatedPoint!!)
                    }
                }
            )
            val multipleAudioPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetMultipleContents(),
                onResult = { uris ->
                    selectedMultipleAudioUri = uris
                    lifecycleScope.launch {
                        MapUtils.uploadMedia(firestore, storage, uris, "audios", annotatedPoint!!)
                    }
                }
            )
            var currentLocation by remember { mutableStateOf<Point?>(null) }
            var mapListeners by remember { mutableStateOf<MapBoxListeners?>(null) } // Step 1
            var isTrackingLocation by remember { mutableStateOf(false) }
            var is3DView by remember { mutableStateOf(false) }

            var heatmapPoints by remember { mutableStateOf<List<Point>>(emptyList()) }

            val firebaseScope = rememberCoroutineScope()
            var activeQueryListener by remember { mutableStateOf<ListenerRegistration?>(null) }
            var annotatedMediaSet by remember { mutableStateOf<Map<String, MediaInfo>>(emptyMap()) }
            val density = LocalDensity.current
            var isRowLayout by remember { mutableStateOf(true) }
            var mapLightPreset by remember { mutableStateOf("dawn") }
            var activeButton by remember { mutableStateOf(0) }
            val buttonStyles = listOf(
                "dawn" to R.drawable.mapbox_lightpreset_dawn,
                "day" to R.drawable.mapbox_lightpreset_day,
                "dusk" to R.drawable.mapbox_lightpreset_dusk,
                "night" to R.drawable.mapbox_lightpreset_night
            )
            val roundedCornerShape = RoundedCornerShape(15)
            val listState = rememberLazyListState()
            var currentMediaIndex by remember { mutableStateOf(0) }
            val annotationScope = rememberCoroutineScope()

            LaunchedEffect(Unit) {
                if (locationHelper.hasLocationPermission()) {
                    locationHelper.createLocationCallback {
                        currentLocation = it
                        println(currentLocation)
                    }
                    locationHelper.startLocationUpdates()
                }
            }

            LaunchedEffect(currentLocation) {
                currentLocation?.let { point ->
                    // Listener for Firestore to identify new media uploads
                    if (activeQueryListener == null) {
                        // Initial Map Loading Animation
                        mapScope.launch {
                            mapViewportState.flyTo(
                                cameraOptions = cameraOptions {
                                    center(currentLocation)
                                    zoom(ZOOM)
                                    bearing(BEARING)
                                },
                                animationOptions = mapAnimationOptions { duration(2000) }
                            )
                        }

                        activeQueryListener = firestore.collection("media_uploads")
                            .addSnapshotListener { snapshots, e ->
                                if (e != null) {
                                    Log.w(TAG, "Listen failed.", e)
                                    return@addSnapshotListener
                                }

                                val changes = snapshots?.documentChanges
                                changes?.forEach { change ->
                                    if (change.type == DocumentChange.Type.ADDED) {
                                        val document = change.document
                                        val uploadLat = document.getDouble("latitude")
                                        val uploadLng = document.getDouble("longitude")
                                        val mediaUrl = document.getString("url")
                                        val mediaType = document.getString("type")

                                        if (uploadLat != null && uploadLng != null) {
                                            heatmapPoints = heatmapPoints + (Point.fromLngLat(
                                                uploadLng,
                                                uploadLat
                                            ))

                                            val uploadLocation = GeoLocation(uploadLat, uploadLng)
                                            val center = GeoLocation(
                                                currentLocation!!.latitude(),
                                                currentLocation!!.longitude()
                                            )

                                            if (GeoFireUtils.getDistanceBetween(
                                                    uploadLocation,
                                                    center
                                                ) <= 50
                                            ) {
                                                if (!annotatedMediaSet.contains(document.id)) {
                                                    annotatedMediaSet =
                                                        annotatedMediaSet +
                                                                (document.id to MediaInfo(
                                                                    mediaType,
                                                                    mediaUrl
                                                                ))
                                                    val jsonObject = JsonObject()
                                                    jsonObject.addProperty(
                                                        "documentId",
                                                        document.id
                                                    )
                                                    val jsonElement = jsonObject

//                                                    Log.d(
//                                                        TAG,
//                                                        "New media uploaded nearby: ${document.id}"
//                                                    )
                                                    // Annotate the map with the new media
                                                    pointAnnotationManager?.let { manager ->
                                                        val docPoint =
                                                            Point.fromLngLat(uploadLng, uploadLat)
                                                        val annotationOptions =
                                                            PointAnnotationOptions()
                                                                .withPoint(docPoint)
                                                                .withIconImage(mapPin)
                                                                .withIconAnchor(IconAnchor.BOTTOM)
                                                                .withData(jsonElement)
                                                        val annotation =
                                                            manager.create(annotationOptions)

                                                        annotation.let {
                                                            manager.addClickListener { clickedAnnotation ->
                                                                if (clickedAnnotation == annotation) {
//                                                                    manager.delete(annotation)
                                                                    oldAnnotation?.let { ann ->
                                                                        ann.iconSize = 1.0
                                                                        manager.update(ann)
                                                                    }

                                                                    if (clickedAnnotation == oldAnnotation) {
                                                                        oldAnnotation = null
                                                                    } else {
                                                                        oldAnnotation =
                                                                            clickedAnnotation

//                                                                        // Check if the annotation is already scaled up
                                                                        val valueAnimator =
                                                                            ValueAnimator.ofFloat(
                                                                                0.75f,
                                                                                1.5f
                                                                            )
                                                                        valueAnimator.duration =
                                                                            200 // Duration in milliseconds
                                                                        valueAnimator.addUpdateListener { animation ->
                                                                            val scale =
                                                                                animation.animatedValue as Float
                                                                            clickedAnnotation.iconSize =
                                                                                scale.toDouble()
                                                                            manager.update(
                                                                                clickedAnnotation
                                                                            )
                                                                        }
                                                                        valueAnimator.start()
                                                                    }
                                                                    true
                                                                } else {
                                                                    false
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                    }

                    val center = GeoLocation(point.latitude(), point.longitude())
                    val radiusInM = 50.0
                    val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
                    val tasks = bounds.map { b ->
                        val query = firestore.collection("media_uploads")
                            .orderBy("geohash")
                            .startAt(b.startHash)
                            .endAt(b.endHash)
                        firebaseScope.async { query.get().await() }
                    }

                    val snapshots = tasks.awaitAll()
                    val documents = snapshots.flatMap { it.documents }
                    val matchingDocs = documents.filter { doc ->
                        val lat = doc.getDouble("latitude") ?: return@filter false
                        val lng = doc.getDouble("longitude") ?: return@filter false
                        val docLocation = GeoLocation(lat, lng)
                        GeoFireUtils.getDistanceBetween(docLocation, center) <= radiusInM
                    }

                    // Update map annotations
                    pointAnnotationManager?.let { manager ->
                        // Remove annotations that are not within the current location's radius
                        manager.annotations.filter { annotation ->
                            val annotationLocation = GeoLocation(
                                annotation.point.latitude(),
                                annotation.point.longitude()
                            )
                            GeoFireUtils.getDistanceBetween(annotationLocation, center) > radiusInM
                        }.forEach { annotation ->
                            annotatedMediaSet =
                                annotatedMediaSet - (annotation.getData()?.asJsonObject?.get("documentId")?.asString)!!
                            manager.delete(annotation)
                        }

                        // Add new annotations for the matching documents
                        matchingDocs.forEach { doc ->
                            if (!annotatedMediaSet.contains(doc.id)) {
                                val jsonObject = JsonObject()
                                jsonObject.addProperty("documentId", doc.id)
                                val jsonElement = jsonObject

                                val docPoint = Point.fromLngLat(
                                    doc.getDouble("longitude")!!,
                                    doc.getDouble("latitude")!!
                                )
                                val mediaType = doc.getString("type")
                                val mediaUrl = doc.getString("url")
                                val annotationOptions = PointAnnotationOptions()
                                    .withPoint(docPoint)
                                    .withIconImage(mapPin)
                                    .withIconAnchor(IconAnchor.BOTTOM)
                                    .withData(jsonElement)
                                val annotation = manager.create(annotationOptions)

                                annotatedMediaSet =
                                    annotatedMediaSet + (doc.id to MediaInfo(mediaType, mediaUrl))

                                annotation.let {
                                    manager.addClickListener { clickedAnnotation ->
                                        if (clickedAnnotation == annotation) {
                                            oldAnnotation?.let { ann ->
                                                ann.iconSize = 1.0
                                                manager.update(ann)
                                            }

                                            if (clickedAnnotation == oldAnnotation) {
                                                oldAnnotation = null
                                            } else {
                                                oldAnnotation = clickedAnnotation
                                                // Check if the annotation is already scaled up
                                                val valueAnimator =
                                                    ValueAnimator.ofFloat(0.75f, 1.5f)
                                                valueAnimator.duration = 200
                                                valueAnimator.addUpdateListener { animation ->
                                                    val scale = animation.animatedValue as Float
                                                    clickedAnnotation.iconSize = scale.toDouble()
                                                    manager.update(clickedAnnotation)
                                                }
                                                valueAnimator.start()
                                            }
                                            true
                                        } else {
                                            false
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }

            BottomSheetScaffold(
                modifier = Modifier
                    .onGloballyPositioned {
                        scaffoldHeight = with(density) {
                            it.size.height.toDp()
                        }
                    },
                scaffoldState = scaffoldState,
                sheetPeekHeight = PEEK_HEIGHT,
                sheetContent = {
                    Column(Modifier.wrapContentHeight()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "Streaming ${annotatedMediaSet.size} " +
                                        if (annotatedMediaSet.size == 1) "Item!" else "Items!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(
                                    Font(R.font.jetbrainsmono_regular, FontWeight.Normal)
                                )
                            )
                            IconButton(
                                onClick = {
                                    sheetScope.launch { scaffoldState.bottomSheetState.partialExpand() }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Sheet",
                                )
                            }
                        }

                        var currentCategory by remember { mutableStateOf("photos") }
                        var activeCategory by remember { mutableStateOf(0) }
                        val categoryList = listOf("photos", "videos", "audios", "text")

                        Row {
                            categoryList.forEachIndexed { index, category ->
                                Button(
                                    onClick = {
                                        currentCategory = category
                                        activeCategory = index
                                    },
                                    modifier = Modifier
                                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (activeCategory == index) RichGold else Color.White,
                                        contentColor = if (activeCategory == index) Color.White else Color.Black
                                    ),
                                ) {
                                    Text(
                                        text = category,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        val previewMediaContainerModifier = Modifier
                            .padding(8.dp)
                            .width(150.dp)
                            .aspectRatio(9f / 16f)
                            .clip(roundedCornerShape)
                            .border(1.dp, Color.Black, roundedCornerShape)

                        val fullscreenMediaContainerModifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                            .clip(roundedCornerShape)
                            .border(1.dp, Color.Black, roundedCornerShape)

                        LaunchedEffect(currentMediaIndex) {
                            delay(350)
                            listState.scrollToItem(index = currentMediaIndex)
                        }

                        AnimatedContent(
                            targetState = isRowLayout,
                            transitionSpec = {
                                if (isRowLayout) {
                                    slideInHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = 300,
                                        ),
                                        initialOffsetX = { fullWidth -> fullWidth }
                                    ) with slideOutVertically(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = 300,
                                        ),
                                        targetOffsetY = { fullHeight -> fullHeight }
                                    )
                                } else {
                                    slideInVertically(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = 300,
                                        ),
                                        initialOffsetY = { fullHeight -> fullHeight }
                                    ) with slideOutHorizontally(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            delayMillis = 300,
                                        ),
                                        targetOffsetX = { fullWidth -> -fullWidth }
                                    )
                                }
                            }, label = ""
                        ) { isRow ->

                            val filteredItems = annotatedMediaSet
                                .entries
                                .filter {
                                    it.value.mediaType == currentCategory
                                }
                                .toList()

                            if (isRow) {
                                LazyRow(
                                    state = listState,
                                    modifier = Modifier.fillMaxWidth(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    itemsIndexed(filteredItems) { index, (mediaId, mediaInfo) ->
                                        when (mediaInfo.mediaType) {
                                            "photos" -> {
                                                Card(
                                                    modifier = previewMediaContainerModifier
                                                        .clickable {
                                                            currentMediaIndex = index
                                                            isRowLayout = !isRowLayout
                                                        },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.Black
                                                    ),
                                                    elevation = CardDefaults.cardElevation(
                                                        defaultElevation = 10.dp
                                                    )
                                                ) {
                                                    AsyncImage(
                                                        modifier = Modifier.fillMaxSize(),
                                                        model = mediaInfo.mediaUrl,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }

                                            "videos" -> {
                                                val player = remember {
                                                    ExoPlayer.Builder(context).build().apply {
                                                        setMediaItem(MediaItem.fromUri(mediaInfo.mediaUrl!!))
                                                        prepare()
                                                        playWhenReady = false
                                                        repeatMode = Player.REPEAT_MODE_OFF
                                                    }
                                                }
                                                var isPlaying by remember { mutableStateOf(false) }

                                                DisposableEffect(player) {
                                                    // Listening to player events
                                                    val listener = object : Player.Listener {
                                                        override fun onPlaybackStateChanged(
                                                            state: Int
                                                        ) {
                                                            super.onPlaybackStateChanged(state)
                                                            if (state == Player.STATE_ENDED) {
                                                                player.seekTo(0)
                                                                player.playWhenReady = false
                                                                isPlaying = false
                                                            }
                                                        }
                                                    }
                                                    player.addListener(listener)
                                                    onDispose {
                                                        player.removeListener(listener)
                                                        player.release()
                                                    }
                                                }
                                                Card(
                                                    modifier = previewMediaContainerModifier
                                                        .clickable {
                                                            currentMediaIndex = index
                                                            isRowLayout = !isRowLayout
                                                        },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.Black
                                                    ),
                                                    elevation = CardDefaults.cardElevation(
                                                        defaultElevation = 10.dp
                                                    )
                                                ) {
                                                    Box(Modifier.fillMaxSize()) {
                                                        AndroidView(
                                                            factory = { context ->
                                                                PlayerView(context).apply {
                                                                    this.player = player
                                                                    useController = false
                                                                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                                                    resizeMode =
                                                                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                                                    layoutParams =
                                                                        FrameLayout.LayoutParams(
                                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                                            ViewGroup.LayoutParams.MATCH_PARENT
                                                                        )
                                                                }
                                                            },
                                                            modifier = Modifier.matchParentSize(),
                                                        )

                                                        IconButton(
                                                            onClick = {
                                                                isPlaying = true
                                                                player.playWhenReady = true
                                                            },
                                                            modifier = Modifier
                                                                .align(Alignment.BottomStart)
                                                                .padding(4.dp)
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.size(64.dp),
                                                                imageVector = Icons.Default.PlayArrow,
                                                                contentDescription = "Play Video",
                                                                tint = if (isPlaying) Color.Transparent else Color.White
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            "audios" -> {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(Color.Black)
                                                ) {
                                                    mediaInfo.mediaUrl?.let {
                                                        AudioPlayer(
                                                            it,
                                                            Modifier.fillMaxWidth()
                                                        )
                                                    }
                                                }
                                            }

                                            else -> {
                                                Text(
                                                    text = "${mediaInfo.mediaType}:\n$mediaId",
                                                    color = Color.White,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxHeight(),
                                    contentPadding = PaddingValues(8.dp)
                                ) {
                                    itemsIndexed(filteredItems) { index, (mediaId, mediaInfo) ->

                                        when (mediaInfo.mediaType) {
                                            "photos" -> {
                                                Card(
                                                    modifier = fullscreenMediaContainerModifier
                                                        .clickable {
                                                            currentMediaIndex = index
                                                            isRowLayout = !isRowLayout
                                                        },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.Black
                                                    ),
                                                    elevation = CardDefaults.cardElevation(
                                                        defaultElevation = 10.dp
                                                    )
                                                ) {
                                                    AsyncImage(
                                                        modifier = Modifier.fillMaxSize(),
                                                        model = mediaInfo.mediaUrl,
                                                        contentDescription = null,
                                                        contentScale = ContentScale.Fit
                                                    )
                                                }
                                            }

                                            "videos" -> {
                                                val player = remember {
                                                    ExoPlayer.Builder(context).build().apply {
                                                        setMediaItem(MediaItem.fromUri(mediaInfo.mediaUrl!!))
                                                        prepare()
                                                        playWhenReady = false
                                                        repeatMode = Player.REPEAT_MODE_OFF
                                                    }
                                                }
                                                var isPlaying by remember { mutableStateOf(false) }

                                                DisposableEffect(player) {
                                                    // Listening to player events
                                                    val listener = object : Player.Listener {
                                                        override fun onPlaybackStateChanged(
                                                            state: Int
                                                        ) {
                                                            super.onPlaybackStateChanged(state)
                                                            if (state == Player.STATE_ENDED) {
                                                                player.seekTo(0)
                                                                player.playWhenReady = false
                                                                isPlaying = false
                                                            }
                                                        }
                                                    }
                                                    player.addListener(listener)
                                                    onDispose {
                                                        player.removeListener(listener)
                                                        player.release()
                                                    }
                                                }

                                                Card(
                                                    modifier = fullscreenMediaContainerModifier
                                                        .clickable {
                                                            currentMediaIndex = index
                                                            isRowLayout = !isRowLayout
                                                        },
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = Color.Black
                                                    ),
                                                    elevation = CardDefaults.cardElevation(
                                                        defaultElevation = 10.dp
                                                    )
                                                ) {
                                                    Box(Modifier.fillMaxSize()) {
                                                        AndroidView(
                                                            factory = { context ->
                                                                PlayerView(context).apply {
                                                                    this.player = player
                                                                    useController = false
                                                                    setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                                                                    resizeMode =
                                                                        AspectRatioFrameLayout.RESIZE_MODE_FIT
                                                                    layoutParams =
                                                                        FrameLayout.LayoutParams(
                                                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                                                            ViewGroup.LayoutParams.MATCH_PARENT
                                                                        )
                                                                }
                                                            },
                                                            modifier = Modifier.matchParentSize(),
                                                        )

                                                        IconButton(
                                                            onClick = {
                                                                isPlaying = true
                                                                player.playWhenReady = true
                                                            },
                                                            modifier = Modifier
                                                                .align(Alignment.BottomStart)
                                                                .padding(16.dp)
                                                                .size(64.dp)
                                                        ) {
                                                            Icon(
                                                                modifier = Modifier.size(64.dp),
                                                                imageVector = Icons.Default.PlayArrow,
                                                                contentDescription = "Play Video",
                                                                tint = if (isPlaying) Color.Transparent else Color.White
                                                            )
                                                        }
                                                    }
                                                }

                                            }

                                            "audios" -> {
                                                mediaInfo.mediaUrl?.let {
                                                    AudioPlayer(
                                                        it,
                                                        Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }

                                            else -> {
                                                Text(
                                                    text = "${mediaInfo.mediaType}:\n$mediaId",
                                                    color = Color.White,
                                                    fontSize = 16.sp
                                                )
                                            }
                                        }


                                    }
                                }
                            }
                        }

                    }
                },
            ) {
                scaffoldSheetOffsetFromTop = with(density) {
                    scaffoldState.bottomSheetState.requireOffset().toDp()
                }

                visibleScaffoldSheetHeight = scaffoldHeight - scaffoldSheetOffsetFromTop

                buttonContainerOffset =
                    if (visibleScaffoldSheetHeight > PEEK_HEIGHT) {
                        (visibleScaffoldSheetHeight - PEEK_HEIGHT)
                            .coerceAtMost(20.dp + 56.dp + 8.dp)
                    } else {
                        0.dp
                    }

                DisposableEffect(scaffoldState.bottomSheetState.currentValue) {
                    onDispose {
                        if (scaffoldState.bottomSheetState.currentValue == SheetValue.PartiallyExpanded) {
                            isRowLayout = true
                        }
                    }
                }

                // MainScreenContent
                Box(modifier = Modifier.fillMaxSize()) {

                    // FABs
                    Column(
                        modifier = Modifier
                            .zIndex(999f)
                            .offset(
                                y = (-(scaffoldHeight - scaffoldSheetOffsetFromTop) + buttonContainerOffset)
                                    .coerceAtLeast(-scaffoldHeight / 2)
                            )
                            .align(Alignment.BottomEnd)
//                            .background(Color(0x50FFFFFF))
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Toggle 3D View
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(8.dp, 12.dp)
                                .size(56.dp)
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = CircleShape
                                ),
                            onClick = {
                                val pitch = if (is3DView) 0.0 else 45.0
                                mapViewportState.flyTo(
                                    cameraOptions {
                                        pitch(pitch)
                                    }
                                )
                                is3DView = !is3DView
                            },
                            shape = CircleShape,
                            containerColor = Color.White,
                            content = {
                                val dimension = if (is3DView) "3" else "2"
                                val color = if (is3DView) Color.Red else Color.Blue
                                Text(
                                    buildAnnotatedString {
                                        withStyle(style = SpanStyle(color = color)) {
                                            append(dimension)
                                        }
                                        append("D")
                                    },
                                    fontSize = 20.sp
                                )
                            },
                        )

                        // Toggle Location Tracking
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(8.dp, 12.dp)
                                .size(56.dp)
                                .border(
                                    width = 1.dp,
                                    color = when {
                                        locationHelper.hasLocationPermission() && isTrackingLocation ->
                                            Color(0xFF286DA8)

                                        locationHelper.hasLocationPermission() ->
                                            Color(0xFF000000)

                                        else ->
                                            Color(0x77000000)
                                    },
                                    shape = CircleShape
                                ),
                            onClick = {
                                isTrackingLocation = !isTrackingLocation
                            },
                            shape = CircleShape,
                            containerColor = Color.White,
                            contentColor = when {
                                locationHelper.hasLocationPermission() && isTrackingLocation ->
                                    Color(0xFF286DA8)

                                locationHelper.hasLocationPermission() ->
                                    Color(0xFF000000)

                                else ->
                                    Color.Gray
                            },
                            content = {
                                Icon(
                                    painter = if (isTrackingLocation) trackingEnabledIcon else trackingDisabledIcon,
                                    contentDescription = "Current Location"
                                )
                            },
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = if (locationHelper.hasLocationPermission()) 6.dp else 0.dp,
                                pressedElevation = if (locationHelper.hasLocationPermission()) 12.dp else 0.dp,
                                hoveredElevation = if (locationHelper.hasLocationPermission()) 8.dp else 0.dp,
                                focusedElevation = if (locationHelper.hasLocationPermission()) 16.dp else 0.dp,
                            )
                        )

                        // Media Stream Viewer
                        BadgedBox(
                            badge = {
                                Badge(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .offset(x = (-24).dp, y = 24.dp)
                                ) {
                                    Text(text = annotatedMediaSet.size.toString(), fontSize = 16.sp)
                                }
                            }
                        ) {
                            FloatingActionButton(
                                modifier = Modifier
                                    .padding(8.dp, 12.dp)
                                    .size(56.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color.Black,
                                        shape = CircleShape
                                    ),
                                onClick = {
                                    sheetScope.launch { scaffoldState.bottomSheetState.expand() }
                                },
                                shape = CircleShape,
                                containerColor = Color.White,
                                content = {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Open Sheet",
                                    )
                                },
                            )
                        }
                    }

                    // Map
                    MapboxMap(
                        modifier = Modifier.fillMaxSize(),
                        mapInitOptionsFactory = { context ->
                            MapInitOptions(
                                context = context,
//                                styleUri = Style.STANDARD,
//                                styleUri = "mapbox://styles/mapbox/standard-beta",
                                cameraOptions = cameraOptions {
                                    center(CityLocations.GAC)
//                                    zoom(ZOOM)
                                    zoom(0.0)
                                },
                            )
                        },
                        compassSettings = CompassSettings.Builder()
                            .setMarginRight(with(density) { 16.dp.toPx() })
                            .setMarginTop(with(density) { 76.dp.toPx() })
                            .build(),
                        mapViewportState = mapViewportState,
                        onMapClickListener = { p ->
                            if (isTrackingLocation) mapListeners?.onCameraTrackingDismissed()
                            sheetScope.launch { scaffoldState.bottomSheetState.partialExpand() }
                            annotatedPoint = p
                            mapScope.launch {
                                mapViewportState.flyTo(
                                    cameraOptions = cameraOptions {
                                        center(p)
                                        zoom(ZOOM)
                                    },
                                    animationOptions = mapAnimationOptions { duration(500) }
                                )
                                showSheet = true
                            }
                            false
                        },
                    ) {

                        // Map Setup
                        MapEffect(Unit) { mapView ->
                            pointAnnotationManager =
                                mapView.annotations.createPointAnnotationManager()

                            mapListeners = MapBoxListeners(
                                mapView = mapView,
                                afterCameraTrackingDismissedAction = {
                                    if (isTrackingLocation) isTrackingLocation = false
                                }
                            )

                            mapListeners?.onCameraTrackingStarted()

                            var radius = MapUtils.calculatePixelRadius(
                                mapView.getMapboxMap(),
                                currentLocation?.latitude() ?: CityLocations.GAC.latitude()
                            )

                            val shadowBitmap = MapUtils.resizeBitmapFromVector(
                                context,
                                R.drawable.map_mylocation_fetch_ring,
                                radius
                            )

                            var shadowImage = ImageHolder.from(shadowBitmap)

                            mapView.location.updateSettings {
                                enabled = true
                                locationPuck = LocationPuck2D(
                                    topImage = topImage,
                                    bearingImage = bearingImage,
                                    shadowImage = shadowImage,
                                )
                            }

                            mapView.camera.addCameraZoomChangeListener { zoom ->
                                radius = MapUtils.calculatePixelRadius(
                                    mapView.getMapboxMap(),
                                    currentLocation?.latitude() ?: CityLocations.GAC.latitude()
                                )

                                shadowImage =
                                    ImageHolder.from(
                                        MapUtils.resizeBitmapFromVector(
                                            context,
                                            R.drawable.map_mylocation_fetch_ring,
                                            radius
                                        )
                                    )
                                mapView.location.updateSettings {
                                    locationPuck = LocationPuck2D(
                                        topImage = topImage,
                                        bearingImage = bearingImage,
                                        shadowImage = shadowImage,
                                    )
                                }

                                /*// Update Pulsing Radius
                                currentLocation?.latitude()?.let { lat ->
                                    mapView.location.updateSettings {
                                        locationPuck = LocationPuck2D(
                                            topImage = topImage,
                                            bearingImage = bearingImage,
                                            shadowImage = shadowImage,
                                            scaleExpression = SCALE_EXPRESSION,
                                        )
                                        pulsingEnabled = true
                                        pulsingMaxRadius =
                                            MapUtils.computePulsingRadiusPixels(lat, zoom)
                                    }
                                }*/
                            }
                        }

                        // Change Map Style
                        MapEffect(mapLightPreset) { mapView ->
                            mapView.getMapboxMap().loadStyle(Style.STANDARD) { style ->
                                style.setStyleImportConfigProperty(
                                    "basemap",
                                    "lightPreset",
                                    Value.valueOf(mapLightPreset)
                                )
                            }
                        }

                        // Toggle Location Tracker
                        MapEffect(isTrackingLocation) {
                            if (isTrackingLocation) {
                                if (currentLocation != null) {
                                    mapViewportState.flyTo(
                                        cameraOptions = cameraOptions {
                                            zoom(ZOOM)
                                            center(currentLocation)
                                        },
                                        animationOptions = mapAnimationOptions { duration(400) },
                                        completionListener = { mapListeners?.onCameraTrackingStarted() }
                                    )
                                }
                            } else {
                                mapListeners?.onCameraTrackingDismissed()
                            }
                        }

                        // Upload Marker
                        annotatedPoint?.let { p ->
                            PointAnnotation(
                                point = p,
                                iconImageBitmap = gacPin,
                                iconAnchor = IconAnchor.BOTTOM,
                                iconSize = pinSize.toDouble()
                            )
                        }
                    }

                    // MapStyles Bar (LightPresets)
                    Card(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.TopEnd),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                    ) {
                        Row {
                            buttonStyles.forEachIndexed { index, (styleName, styleId) ->
                                IconButton(
                                    onClick = {
                                        mapLightPreset = styleName
                                        activeButton = index
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (activeButton == index) RichGold else Color.White,
                                        contentColor = if (activeButton == index) Color.White else RichGold
                                    ),
                                ) {
                                    Icon(
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .size(56.dp),
                                        painter = painterResource(id = styleId),
                                        contentDescription = styleName
                                    )
                                }
                            }
                        }
                    }

                    // Upload Bottom Sheet content
                    if (showSheet) {
                        ModalBottomSheet(
                            sheetState = sheetState,
                            scrimColor = Color.Transparent,
                            onDismissRequest = {
                                textStory = ""
                                annotatedPoint = null
                                showSheet = false
                            },
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxHeight(0.4f)
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceAround,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "Upload Item here", fontSize = 24.sp)

                                Row {
                                    Button(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        onClick = {
                                            multiplePhotoPickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                                )
                                            )
                                        }
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.upload_photo),
                                                contentDescription = "Upload Photos Icon"
                                            )
                                            Text(text = "Photos", fontSize = 12.sp)
                                        }
                                    }
                                    Button(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        onClick = {
                                            multipleVideoPickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                    ActivityResultContracts.PickVisualMedia.VideoOnly
                                                )
                                            )
                                        }
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.upload_video),
                                                contentDescription = "Upload Videos Icon"
                                            )
                                            Text(text = "Videos", fontSize = 12.sp)
                                        }
                                    }
                                    Button(
                                        modifier = Modifier
                                            .padding(16.dp),
                                        onClick = {
                                            multipleAudioPickerLauncher.launch("audio/*")
                                        }
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.upload_audio),
                                                contentDescription = "Upload Audios Icon"
                                            )
                                            Text(text = "Audios", fontSize = 12.sp)
                                        }
                                    }
                                }
                                TextField(
                                    textStyle = TextStyle(color = Color.Black),
                                    value = textStory,
                                    onValueChange = { newText ->
                                        if (newText.length <= 200) {
                                            textStory = newText
                                        }
                                    },
                                    placeholder = { Text("Or Share your Story!") },
                                )

                            }
                        }
                    }
                }

            }
        }
    }

    // TODO: "Location Based Tasks"
    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun enableMyLocation() {
    }

    // TODO: "Handle Permission Denied"
    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onLocationPermissionDenied() {
    }

    // TODO: "Handle 'Never ask again' choice"
    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    fun onLocationPermissionNeverAskAgain() {
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }

    private companion object {
        val RichGold = Color(0xFFECBE07)

        //        val RichGold = Color(0xFFA97142)
        val PEEK_HEIGHT = 48.dp
        const val ZOOM = 17.0
        const val BEARING = -58.0
        const val PITCH = 30.0
        const val TAG = "GACTourActivity"
        const val PULSING_RADIUS_METRES = 100f
        const val EARTH_EQUATOR_CIRCUMFERENCE = 40075017.0
        const val SCALE_EXPRESSION = """
                ["interpolate", ["linear"], ["zoom"],
                    0, 0.5,
                    15, 1,
                    20, 2
                ]
            """
        val topImage = ImageHolder.from(com.mapbox.maps.R.drawable.mapbox_mylocation_icon_bearing)
        val bearingImage = ImageHolder.from(com.mapbox.maps.R.drawable.mapbox_user_stroke_icon)
        //        val bearingImage = ImageHolder.from(com.mapbox.maps.R.drawable.mapbox_user_puck_icon)
    }
}
