package com.mahkxai.gactour.android.presentation.screen.camera

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.presentation.theme.RichGold
import com.mapbox.geojson.Point
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

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
                    currentLocation?.let { location ->
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
                GACTourMediaType.VIDEO -> { /* TODO: Upload video */
                }

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageUploadPreview(
    mediaUri: Uri
) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    var caption by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AsyncImage(
            model = mediaUri,
            modifier = Modifier
                .fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )

        DraggableCaption(
            caption = caption,
            offsetY = offsetY,
            captionMaxChars = 80,
            captionMaxLines = 5,
            onTextDragged = { offsetY += it },
        ) { caption = it }
    }
}

@Composable
fun BoxScope.DraggableCaption(
    caption: String,
    offsetY: Float,
    captionMaxChars: Int,
    captionMaxLines: Int,
    onTextDragged: (Float) -> Unit,
    onTextChange: (String) -> Unit
) {
    BasicTextField(
        value = caption,
        onValueChange = {
            if (it.length <= captionMaxChars && it.count { it == '\n' } <= captionMaxLines) {
                onTextChange(it)
            }
        },
        modifier = Modifier
            .offset { IntOffset(0, offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    onTextDragged(dragAmount.y)
                }
            }
            .background(Color.Black.copy(alpha = 0.8f))
            .fillMaxWidth()
            .align(Alignment.Center)
            .padding(4.dp),
        textStyle = TextStyle(
            fontSize = 20.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        ),
        decorationBox = { innerTextField ->
            if (caption.isEmpty()) {
                Text(
                    text = "Enter caption...",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
            innerTextField()
        },
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
            progress = progress.toFloat() / 100,
        )
    }
}