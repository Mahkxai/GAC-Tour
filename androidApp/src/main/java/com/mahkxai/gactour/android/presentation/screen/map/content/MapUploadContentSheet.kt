package com.mahkxai.gactour.android.presentation.screen.map.content

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.presentation.permission.MultiplePermissionsHandler
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import com.mahkxai.gactour.android.presentation.screen.stream.UploadState
import com.mapbox.geojson.Point

@ExperimentalMaterial3Api
@Composable
fun MapUploadContentSheet(
    setIsUploadPinVisible: (Boolean) -> Unit,
    hideUploadSheet: () -> Unit,
    uploadState: UploadState,
    uploadMedia: (GACTourMediaType, List<GACTourUploadItem>) -> Unit,
    contentPoint: Point?,
    // setUploadPointAnnotation: (Point?) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(0.5f),
        sheetState = sheetState,
        scrimColor = Color.Transparent,
        onDismissRequest = {
            hideUploadSheet()
            setIsUploadPinVisible(false)
        },
    ) {

        when (uploadState) {
            UploadState.Idle -> UploadSheetContent(contentPoint, uploadMedia)

            is UploadState.Uploading -> {
                uploadState.uploadProgress.forEach { (uri, progress) ->
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = "Uploading $uri",
                            modifier = Modifier.weight(1f),
                        )
                        CircularProgressIndicator(progress.toFloat())
                    }
                }
            }

            is UploadState.Success -> Text(text = "Successfully Uploaded:\n${uploadState.uploadedUris}")

            UploadState.Failed -> println("Upload Failed")
        }

    }
}

@Composable
fun ColumnScope.UploadSheetContent(
    contentPoint: Point?,
    uploadMedia: (GACTourMediaType, List<GACTourUploadItem>) -> Unit,
) {
    var mediaPermissionGranted by remember { mutableStateOf(false) }
    val permissionKeys =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(PermissionKey.IMAGES, PermissionKey.VIDEO)
        } else {
            listOf(PermissionKey.READ_STORAGE)
        }

    UploadTitle()

    Column(Modifier.weight(1f)) {
        MultiplePermissionsHandler(
            permissionKeys = permissionKeys,
            isFeatureOverlay = true
        ) { mediaPermissionGranted = it }

        if (mediaPermissionGranted) {
            Row {
                PhotoPicker { uris ->
                    if (uris.isNotEmpty()) {
                        val itemsToUpload = uris.map { uri -> GACTourUploadItem(contentPoint!!, uri) }
                        uploadMedia(GACTourMediaType.IMAGE, itemsToUpload)
                    }
                }
                MediaDivider()
                VideoPicker { uris ->
                    if (uris.isNotEmpty()) {
                        val itemsToUpload = uris.map { uri -> GACTourUploadItem(contentPoint!!, uri) }
                        uploadMedia(GACTourMediaType.VIDEO, itemsToUpload)
                    }
                }
            }
        }
    }

    NarrativeInputContainer {
        TextStoryField()
        AudioRecorder()
    }
}


@Composable
fun UploadTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Share Content",
            fontSize = MaterialTheme.typography.headlineSmall.fontSize,
        )
    }
}

@Composable
fun RowScope.PhotoPicker(onUpload: (List<Uri>) -> Unit) {
    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> onUpload(uris) }
    )

    VisualMediaInputContainer(
        onMediaTypeSelected = {
            multiplePhotoPickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    ) {
        Icon(
            imageVector = Icons.Default.Image,
            contentDescription = "Pick Image",
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
        )
        Text(text = "Photos")
    }
}

@Composable
fun RowScope.VideoPicker(onUpload: (List<Uri>) -> Unit) {
    val multipleVideoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> onUpload(uris) }
    )

    VisualMediaInputContainer(
        onMediaTypeSelected = {
            multipleVideoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
            )
        }
    ) {
        Icon(
            imageVector = Icons.Default.VideoLibrary,
            contentDescription = "Pick Video",
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
        )
        Text(text = "Videos")
    }
}

@Composable
fun RowScope.TextStoryField() {
    var textStory by remember { mutableStateOf("") }

    OutlinedTextField(
        modifier = Modifier.weight(1f),
        value = textStory,
        onValueChange = { textStory = it },
        placeholder = { Text("Share a story") }
    )
}

@Composable
fun AudioRecorder() {
    IconButton(onClick = { /*TODO*/ }) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Record Audio"
        )
    }
}

@Composable
fun MediaDivider() {
    Divider(
        modifier = Modifier
            .fillMaxHeight()
            .padding(vertical = 16.dp)
            .width(1.dp),
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
    )
}

@Composable
fun RowScope.VisualMediaInputContainer(
    onMediaTypeSelected: () -> Unit,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { onMediaTypeSelected() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        content = content
    )
}

@Composable
fun NarrativeInputContainer(
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        content = content
    )
}



