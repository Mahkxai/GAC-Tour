package com.mahkxai.gactour.android.presentation.screen.camera

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahkxai.gactour.android.common.ext.disableInteraction
import com.mahkxai.gactour.android.presentation.navigation.BottomBarNavGraph
import com.mahkxai.gactour.android.presentation.permission.MultiplePermissionsHandler
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import com.ramcosta.composedestinations.annotation.Destination
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.presentation.MainViewModel
import com.mahkxai.gactour.android.presentation.theme.RichGold
import com.mapbox.geojson.Point
import java.io.File

//@NavigationBarNavGraph(start = true)
@RequiresApi(Build.VERSION_CODES.S)
@BottomBarNavGraph
@Destination
@Composable
fun CameraScreen(
    mainViewModel: MainViewModel,
    cameraViewModel: CameraViewModel = hiltViewModel(),
) {
    val locationState by mainViewModel.locationState.collectAsStateWithLifecycle()
    var allCameraPermissionsGranted by remember { mutableStateOf(false) }
    var allLocationPermissionsGranted by remember { mutableStateOf(false) }
    var currentLocation by remember { mutableStateOf<Point?>(null) }

    val bitmaps by cameraViewModel.bitmaps.collectAsState()

    LaunchedEffect(allLocationPermissionsGranted) {
        if (allLocationPermissionsGranted) mainViewModel.startLocationUpdate()
    }

    LaunchedEffect(locationState.location) {
        locationState.apply {
            location?.let { currentLocation = it }
        }
    }

    CameraScreenContent(
        cameraViewModel = cameraViewModel,
        bitmaps = bitmaps,
        currentLocation = currentLocation,
        modifier = Modifier
            .disableInteraction(
                !(allLocationPermissionsGranted && allCameraPermissionsGranted)
            ),
        // onUpload = cameraViewModel::uploadMedia,
    )

    MultiplePermissionsHandler(
        permissionKeys = listOf(
            PermissionKey.CAMERA,
            PermissionKey.MICROPHONE,
        ),
        isFeatureOverlay = true,
    ) { allCameraPermissionsGranted = it }

    if (allCameraPermissionsGranted) {
        MultiplePermissionsHandler(
            permissionKeys = listOf(
                PermissionKey.FINE_LOCATION,
                PermissionKey.COARSE_LOCATION,
            ),
            isFeatureOverlay = true
        ) { allLocationPermissionsGranted = it }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(
    cameraViewModel: CameraViewModel,
    bitmaps: List<Bitmap>,
    currentLocation: Point?,
    modifier: Modifier = Modifier,
    // onUpload: (GACTourMediaType, GACTourUploadItem, (Double) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState()
    var recording by remember { mutableStateOf<Recording?>(null) }
    val exoPlayer = remember { ExoPlayer.Builder(context).build() }

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE
            )
        }
    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var shouldShowUploadPreview by remember { mutableStateOf(false) }
    var videoUri by remember { mutableStateOf<Uri?>(null) }

    Scaffold(
        floatingActionButton = {
            if (!shouldShowUploadPreview) {
                MediaCaptureButton(
                    onPhotoTaken = {
                        takePhoto(
                            context = context,
                            controller = controller,
                            onPhotoTaken = { uri ->
                                imageUri = uri
                                shouldShowUploadPreview = true
                            },
                        )
                    },
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            CameraPreview(
                controller = controller,
                modifier = Modifier
                    .fillMaxSize()
            )

            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
                modifier = Modifier
                    .offset(16.dp, 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Cameraswitch,
                    contentDescription = "Switch camera"
                )
            }

            /*// Video Capture
            IconButton(
                onClick = {
                    recordVideo(context, recording, controller, exoPlayer, {recording = it}) {
                        videoUri = it
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = "Record video"
                )
            }*/

            if (shouldShowUploadPreview) {
                println("ImageBitmap: $imageBitmap")
                imageUri?.let { uri ->
                    CameraUploadPreview(
                        mediaType = GACTourMediaType.IMAGE,
                        mediaUri = uri,
                        currentLocation = currentLocation,
                        onBackPressed = { shouldShowUploadPreview = false },
                        onUpload = cameraViewModel::uploadMedia,
                    )
                }
            }
        }
    }
}

@Composable
fun MediaCaptureButton(
    onPhotoTaken: () -> Unit,
) {
    FloatingActionButton(
        onClick = onPhotoTaken,
        containerColor = RichGold,
        shape = CircleShape,
    ) {
        Icon(
            imageVector = Icons.Default.PhotoCamera,
            contentDescription = "Take photo",
        )
    }
}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Uri) -> Unit,
    // onPhotoTaken: (Bitmap) -> Unit
) {
    val tempFile = File.createTempFile("temp", ".jpg", context.cacheDir)

    controller.takePicture(
        // Uri version
        ImageCapture.OutputFileOptions.Builder(tempFile).build(),
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val imageUri = Uri.fromFile(tempFile)
                onPhotoTaken(imageUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }

        /*// Bitmap version
        ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(),
                    0,
                    0,
                    image.width,
                    image.height,
                    matrix,
                    true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
                Log.e("Camera", "Couldn't take photo: ", exception)
            }
        }*/
    )
}

@SuppressLint("MissingPermission")
private fun recordVideo(
    context: Context,
    recording: Recording?,
    controller: LifecycleCameraController,
    player: ExoPlayer,
    setRecording: (Recording?) -> Unit,
    onVideoRecorded: (Uri) -> Unit,
) {
    if (recording != null) {
        recording.stop()
        setRecording(null)
        return
    }

    val outputFile = File(context.filesDir, "my-recording.mp4")

    val newRecording = controller.startRecording(
        FileOutputOptions.Builder(outputFile).build(),
        AudioConfig.create(true),
        ContextCompat.getMainExecutor(context)
    ) { event ->
        when (event) {
            is VideoRecordEvent.Finalize -> {
                if (event.hasError()) {
                    recording?.close()
                    setRecording(null)

                    Toast.makeText(context, "Video capture failed", Toast.LENGTH_LONG).show()
                } else {
                    onVideoRecorded(Uri.fromFile(outputFile))
                    Toast.makeText(context, "Video capture succeeded", Toast.LENGTH_LONG).show()
                    // Set recording to null after successful completion
                    setRecording(null)
                }
            }
        }
    }

    // Update the recording state
    setRecording(newRecording)
}