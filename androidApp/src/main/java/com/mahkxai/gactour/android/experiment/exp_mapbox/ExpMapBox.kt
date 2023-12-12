package com.mahkxai.gactour.android.experiment.exp_mapbox

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.common.util.MapConstants

/* FAB Container Offset
fun calculateFABContainerOffset(
    mapScreenHeight: Dp,
    bottomSheetOffsetFromTop: Dp
): Dp {
    val bottomSheetVisibleHeight = mapScreenHeight - bottomSheetOffsetFromTop

    val buttonContainerOffset =
        if (bottomSheetVisibleHeight > MapConstants.PEEK_HEIGHT) {
            (bottomSheetVisibleHeight - MapConstants.PEEK_HEIGHT)
                .coerceAtMost(20.dp + 56.dp + 8.dp)
        } else {
            0.dp
        }

    return (-bottomSheetVisibleHeight
//                + buttonContainerOffset
            ).coerceAtLeast(-mapScreenHeight / 2)

}*/

/*// Viewport Navigation
LaunchedEffect(mapViewportState.mapViewportStatus) {
     val isFollowingPuck = mapViewportState.mapViewportStatus is ViewportStatus.State
             && (mapViewportState.mapViewportStatus as ViewportStatus.State).state is FollowPuckViewportState
     if (isFollowingPuck) {println("Tracking")}
     else println("Not Tracking")
 }

 // Start Location Tracking if Location FAB is Toggled On
 LaunchedEffect(mapUiState.isTrackingLocation) {
     if (mapUiState.isTrackingLocation) {
         mapViewportState.transitionToFollowPuckState(
             MapPresets.locationTrackingViewportStateOption
         )
     }
 }

 // Toggle Bearing Tracking
 LaunchedEffect(mapUiState.isTrackingBearing) {
     if (mapUiState.isTrackingBearing) {
         mapViewportState.transitionToFollowPuckState(
             MapPresets.bearingTrackingViewportStateOption
         )
     } else {
         mapViewportState.transitionToFollowPuckState(
             MapPresets.locationTrackingViewportStateOption
         )
     }
 }

 // Toggle 3D View
 LaunchedEffect(mapUiState.is3DView) {
     val pitch =
         if (mapUiState.is3DView) CameraConstants.PITCH_3D
         else CameraConstants.PITCH_2D
     mapViewportState.flyTo( cameraOptions = cameraOptions { pitch(pitch) } )
 }*/


/*// CameraTracking Using ViewPortState
if (mapUiState.isTrackingLocation) {
    mapViewportState.transitionToFollowPuckState(
        followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
            .zoom(MapCameraConstants.ZOOM_FOCUSED)
            .pitch(mapViewportState.cameraState.pitch)
            .bearing(
                FollowPuckViewportStateBearing
                    .Constant(mapViewportState.cameraState.bearing)
            )
            .build(),
        completionListener = {
            trackingSuccess = true
        },
        defaultTransitionOptions = DefaultViewportTransitionOptions.Builder()
            .maxDurationMs(500)
            .build()
    )
}*/


/*// Deactivate Location FAB when the map is panned
LaunchedEffect(mapViewportState.mapViewportStatus) {
    println(mapViewportState.mapViewportStatus)
    if (trackingSuccess && mapViewportState.mapViewportStatus == ViewportStatus.Idle) {
        trackingSuccess = false
        setIsTrackingLocation(false)
    }
}*/


/*// Create Bitmap From Vector
fun resizeBitmapFromVector(context: Context, drawableId: Int, radius: Float): Bitmap {
    // Convert dp size to pixels (e.g., radius is the accuracy in meters, we assume 1 meter = 1 dp for example)
    val radiusDp = radius // Adjust this conversion as needed
    val scale = context.resources.displayMetrics.density
    val radiusPx = (radiusDp * scale).toInt()

    // Get the drawable and set its size
    val drawable = context.resources.getDrawable(drawableId, context.theme) as GradientDrawable
    drawable.setSize(
        Integer.max(radiusPx * 2, 1),
        Integer.max(radiusPx * 2, 1)
    ) // Drawable size for radius

    // Create a bitmap from drawable
    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}*/


/*// Sync locationComponentPlugin settings with locationComponentSettings
mapView.location.let { locationComponentPlugin ->
    locationComponentPlugin.updateSettings {
        locationComponentSettings = this
            .setEnabled(true)
            .setPulsingEnabled(true)
            .build()
    }
    locationComponentPlugin.addOnIndicatorPositionChangedListener { location ->
        setCurrentLocation(location)
    }
}*/


/*// Location Manager Location Updates
// onLocationResult
locationResult.lastLocation?.let { location ->
    val locationPoint = Point.fromLngLat(location.longitude, location.latitude)
    locationCallback(locationPoint)
}
// Call to Receive Location Manager Location Updates
val locationManager = FusedLocationManager(
    mapContext,
    setCurrentLocation = setCurrentLocation,
    timeInterval = 1000,
    minimalDistance = 1f
)
locationManager.startLocationTracking()
*/

/*// Decoupled MapContent Call
fun MapContent(
    mapUiState: MapUiState,
    setCurrentLocation: (Point) -> Unit,
    setIsTrackingLocation: (Boolean) -> Unit,
    setIs3DView: (Boolean) -> Unit,
    setUploadPointAnnotation: (Point?) -> Unit,
    setIsUploadPinVisible: (Boolean) -> Unit,
    hasLocationPermission: Boolean,
    showUploadSheet: () -> Unit,
)*/


/*// Alternate Bitmap Rescaler
fun @receiver:DrawableRes Int.toResizedBitmap(context: Context, widthDp: Float): Bitmap {
    return try {
        val resources = context.resources
        val originalBitmap = BitmapFactory.decodeResource(resources, this)
        val aspectRatio = originalBitmap.height.toFloat() / originalBitmap.width.toFloat()
        val widthPx = (widthDp * resources.displayMetrics.density).toInt()
        val heightPx = (widthPx * aspectRatio).toInt()

        Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, true)
    } catch (e: Exception) {
        println(e)
        val drawable = ContextCompat.getDrawable(context, R.drawable.map_pin)
        return drawable!!.toBitmap()
    }
}*/
