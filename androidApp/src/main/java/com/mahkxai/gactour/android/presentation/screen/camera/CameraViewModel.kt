package com.mahkxai.gactour.android.presentation.screen.camera

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.domain.usecase.UploadMediaUseCase
import com.mahkxai.gactour.android.presentation.screen.explore.stream.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val uploadMediaUseCase: UploadMediaUseCase
) : ViewModel() {
    private val _bitmaps = MutableStateFlow<List<Bitmap>>(emptyList())
    val bitmaps = _bitmaps.asStateFlow()

    fun onTakePhoto(bitmap: Bitmap) {
        _bitmaps.value += bitmap
    }

    fun uploadMedia(
        mediaType: GACTourMediaType,
        mediaItem: GACTourUploadItem,
        uploadProgress: (Double) -> Unit
    ) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    launch(Dispatchers.IO) { // async uploads
                        uploadMediaUseCase.execute(mediaType, mediaItem) { progress ->
                            uploadProgress(progress)
                        }
                    }
                }
            } catch (e: Exception) {
                println("Upload failed: $e")
            }
        }
    }
}