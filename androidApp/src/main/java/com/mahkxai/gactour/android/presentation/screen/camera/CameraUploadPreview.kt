package com.mahkxai.gactour.android.presentation.screen.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.presentation.theme.RichGold
import com.mapbox.geojson.Point
import kotlinx.coroutines.delay

@Composable
fun CameraUploadPreview(
    mediaType: GACTourMediaType,
    mediaUri: Uri,
    currentLocation: Point?,
    onBackPressed: () -> Unit,
    onUpload: (GACTourMediaType, GACTourUploadItem, (Double) -> Unit) -> Unit
) {
    // Override back button behavior
    var uploadProgress by remember { mutableDoubleStateOf(0.0) }
    var isUploading by remember { mutableStateOf(false) }

    BackHandler(
        onBack = {
            onBackPressed()
            uploadProgress = 0.0
        }
    )

    Scaffold(
        floatingActionButton = {
            if (!isUploading) {
                UploadButton {
                    currentLocation?.let {  location ->
                        onUpload(
                            GACTourMediaType.IMAGE,
                            GACTourUploadItem(
                                mediaLocation = location,
                                mediaUri = mediaUri,
                            )
                        ) { progress ->
                            isUploading = true
                            uploadProgress = progress
                        }
                    }
                    println("Upload button clicked at location $currentLocation")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (mediaType) {
                GACTourMediaType.IMAGE -> ImageUploadPreview(mediaUri = mediaUri)
                GACTourMediaType.VIDEO -> { /* TODO: Upload video */ }
                else -> throw IllegalArgumentException("Unsupported media type: $mediaType")
            }

            if (!isUploading) {
                BackButton {
                    onBackPressed()
                    uploadProgress = 0.0
                }
            }

            if (isUploading) {
                UploadOverlay(uploadProgress) { onBackPressed() }
            }
        }
    }
}


@Composable
fun ImageUploadPreview(
    mediaUri: Uri
) {
    AsyncImage(
        model = mediaUri,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun ImageUploadPreview(
    media: Bitmap
) {
    val painterState = rememberAsyncImagePainter(media)
    Image(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        painter = painterState,
        contentDescription = null,
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun UploadButton(
    onClick: () -> Unit,
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = RichGold,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Upload",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun BackButton(
    onBackPressed: () -> Unit,
) {
    IconButton(
        onClick = onBackPressed,
        modifier = Modifier.padding(16.dp)

    ) {
        Icon(
            Icons.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
        )
    }
}

@Composable
fun UploadOverlay(
    progress: Double,
    onUploadComplete: () -> Unit
) {
    if (progress >= 100.0) {
        LaunchedEffect(Unit) {
            delay(3000) // delay for 3 seconds
            onUploadComplete()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (progress < 100.0) "Uploading..." else "Uploaded!",
        )

        CircularProgressIndicator(
            progress = progress.toFloat()/100,
        )
    }
}