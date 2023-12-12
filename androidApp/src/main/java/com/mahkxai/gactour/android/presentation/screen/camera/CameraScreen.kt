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
import androidx.compose.ui.Alignment
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
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.Recording
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.ContextCompat
import androidx.media3.exoplayer.ExoPlayer
import com.mahkxai.gactour.android.presentation.screen.stream.content.VideosContainer
import kotlinx.coroutines.launch
import java.io.File

//@NavigationBarNavGraph(start = true)
@RequiresApi(Build.VERSION_CODES.R)
@BottomBarNavGraph
@Destination
@Composable
fun CameraScreen(
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    var allCameraPermissionsGranted by remember { mutableStateOf(false) }
    var allLocationPermissionsGranted by remember { mutableStateOf(false) }

    val bitmaps by cameraViewModel.bitmaps.collectAsState()

    CameraScreenContent(
        cameraViewModel = cameraViewModel,
        bitmaps = bitmaps,
        modifier = Modifier.disableInteraction(!allCameraPermissionsGranted),
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
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreenContent(cameraViewModel: CameraViewModel, bitmaps: List<Bitmap>, modifier: Modifier = Modifier) {
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

    var videoUri by remember { mutableStateOf<Uri?>(null) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 0.dp,
        sheetContent = {
            PhotoBottomSheetContent(bitmaps = bitmaps, modifier = Modifier.fillMaxWidth())
        }
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

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                IconButton(
                    onClick = {
                        scope.launch {
                            scaffoldState.bottomSheetState.expand()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Photo,
                        contentDescription = "Open gallery"
                    )
                }
                IconButton(
                    onClick = {
                        takePhoto(
                            context = context,
                            controller = controller,
                            onPhotoTaken = cameraViewModel::onTakePhoto
                        )
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Take photo"
                    )
                }
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
                }
            }
        }
    }


    /*Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        IconButton(
            modifier = Modifier.padding(16.dp),
            onClick = { println("Snap Snap!") }
        ) {
            Image(imageVector = Icons.Outlined.Info, contentDescription = "Capture")
        }
    }*/

}

private fun takePhoto(
    context: Context,
    controller: LifecycleCameraController,
    onPhotoTaken: (Bitmap) -> Unit
) {
    controller.takePicture(
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
        }
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

// class MainActivity : ComponentActivity() {
//
//     private var recording: Recording? = null
//
//     override fun onCreate(savedInstanceState: Bundle?) {
//         super.onCreate(savedInstanceState)
//         if (!hasRequiredPermissions()) {
//             ActivityCompat.requestPermissions(
//                 this, CAMERAX_PERMISSIONS, 0
//             )
//         }
//         setContent {
//             CameraXGuideTheme {
//                 val scope = rememberCoroutineScope()
//                 val scaffoldState = rememberBottomSheetScaffoldState()
//                 val controller = remember {
//                     LifecycleCameraController(applicationContext).apply {
//                         setEnabledUseCases(
//                             CameraController.IMAGE_CAPTURE or
//                                     CameraController.VIDEO_CAPTURE
//                         )
//                     }
//                 }
//                 val viewModel = viewModel<MainViewModel>()
//                 val bitmaps by viewModel.bitmaps.collectAsState()
//
//                 BottomSheetScaffold(
//                     scaffoldState = scaffoldState,
//                     sheetPeekHeight = 0.dp,
//                     sheetContent = {
//                         PhotoBottomSheetContent(
//                             bitmaps = bitmaps,
//                             modifier = Modifier
//                                 .fillMaxWidth()
//                         )
//                     }
//                 ) { padding ->
//                     Box(
//                         modifier = Modifier
//                             .fillMaxSize()
//                             .padding(padding)
//                     ) {
//                         CameraPreview(
//                             controller = controller,
//                             modifier = Modifier
//                                 .fillMaxSize()
//                         )
//
//                         IconButton(
//                             onClick = {
//                                 controller.cameraSelector =
//                                     if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
//                                         CameraSelector.DEFAULT_FRONT_CAMERA
//                                     } else CameraSelector.DEFAULT_BACK_CAMERA
//                             },
//                             modifier = Modifier
//                                 .offset(16.dp, 16.dp)
//                         ) {
//                             Icon(
//                                 imageVector = Icons.Default.Cameraswitch,
//                                 contentDescription = "Switch camera"
//                             )
//                         }
//
//                         Row(
//                             modifier = Modifier
//                                 .fillMaxWidth()
//                                 .align(Alignment.BottomCenter)
//                                 .padding(16.dp),
//                             horizontalArrangement = Arrangement.SpaceAround
//                         ) {
//                             IconButton(
//                                 onClick = {
//                                     scope.launch {
//                                         scaffoldState.bottomSheetState.expand()
//                                     }
//                                 }
//                             ) {
//                                 Icon(
//                                     imageVector = Icons.Default.Photo,
//                                     contentDescription = "Open gallery"
//                                 )
//                             }
//                             IconButton(
//                                 onClick = {
//                                     takePhoto(
//                                         controller = controller,
//                                         onPhotoTaken = viewModel::onTakePhoto
//                                     )
//                                 }
//                             ) {
//                                 Icon(
//                                     imageVector = Icons.Default.PhotoCamera,
//                                     contentDescription = "Take photo"
//                                 )
//                             }
//                             IconButton(
//                                 onClick = {
//                                     recordVideo(controller)
//                                 }
//                             ) {
//                                 Icon(
//                                     imageVector = Icons.Default.Videocam,
//                                     contentDescription = "Record video"
//                                 )
//                             }
//                         }
//                     }
//                 }
//             }
//         }
//     }
//
//     private fun takePhoto(
//         controller: LifecycleCameraController,
//         onPhotoTaken: (Bitmap) -> Unit
//     ) {
//         if (!hasRequiredPermissions()) {
//             return
//         }
//
//         controller.takePicture(
//             ContextCompat.getMainExecutor(applicationContext),
//             object : OnImageCapturedCallback() {
//                 override fun onCaptureSuccess(image: ImageProxy) {
//                     super.onCaptureSuccess(image)
//
//                     val matrix = Matrix().apply {
//                         postRotate(image.imageInfo.rotationDegrees.toFloat())
//                     }
//                     val rotatedBitmap = Bitmap.createBitmap(
//                         image.toBitmap(),
//                         0,
//                         0,
//                         image.width,
//                         image.height,
//                         matrix,
//                         true
//                     )
//
//                     onPhotoTaken(rotatedBitmap)
//                 }
//
//                 override fun onError(exception: ImageCaptureException) {
//                     super.onError(exception)
//                     Log.e("Camera", "Couldn't take photo: ", exception)
//                 }
//             }
//         )
//     }
//
//     @SuppressLint("MissingPermission")
//     private fun recordVideo(controller: LifecycleCameraController) {
//         if (recording != null) {
//             recording?.stop()
//             recording = null
//             return
//         }
//
//         if (!hasRequiredPermissions()) {
//             return
//         }
//
//         val outputFile = File(filesDir, "my-recording.mp4")
//         recording = controller.startRecording(
//             FileOutputOptions.Builder(outputFile).build(),
//             AudioConfig.create(true),
//             ContextCompat.getMainExecutor(applicationContext),
//         ) { event ->
//             when (event) {
//                 is VideoRecordEvent.Finalize -> {
//                     if (event.hasError()) {
//                         recording?.close()
//                         recording = null
//
//                         Toast.makeText(
//                             applicationContext,
//                             "Video capture failed",
//                             Toast.LENGTH_LONG
//                         ).show()
//                     } else {
//                         Toast.makeText(
//                             applicationContext,
//                             "Video capture succeeded",
//                             Toast.LENGTH_LONG
//                         ).show()
//                     }
//                 }
//             }
//         }
//     }
//
//     private fun hasRequiredPermissions(): Boolean {
//         return CAMERAX_PERMISSIONS.all {
//             ContextCompat.checkSelfPermission(
//                 applicationContext,
//                 it
//             ) == PackageManager.PERMISSION_GRANTED
//         }
//     }
//
//     companion object {
//         private val CAMERAX_PERMISSIONS = arrayOf(
//             Manifest.permission.CAMERA,
//             Manifest.permission.RECORD_AUDIO,
//         )
//     }
// }
