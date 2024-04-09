package com.mahkxai.gactour.android.presentation.screen.explore.stream

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.domain.usecase.FetchNearbyMediaUseCase
import com.mahkxai.gactour.android.domain.usecase.UploadMediaUseCase
import com.mahkxai.gactour.android.common.ext.toGeoLocation
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mapbox.geojson.Point
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamViewModel @Inject constructor(
    private val fetchNearbyMediaUseCase: FetchNearbyMediaUseCase,
    private val uploadMediaUseCase: UploadMediaUseCase
) : ViewModel() {

    private val _streamState = MutableStateFlow(
        StreamState(
            mediaItems = emptyMap(),
            mediaCategoryIndices = GACTourMediaType.values().associateWith { 0 }
        )
    )
    val streamState = _streamState.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()


    fun fetchNearbyMedia(center: Point, radiusInMetres: Double) {
        viewModelScope.launch {
            fetchNearbyMediaUseCase.execute(center, radiusInMetres).collect { mediaItems ->
                _streamState.value = _streamState.value.copy(mediaItems = mediaItems)
            }
        }
    }

    fun getAllMedias(): List<GACTourMediaItem> {
        return _streamState.value.mediaItems.flatMap { it.value }
    }

    private fun getMediaItemsByCategory(mediaType: GACTourMediaType): List<GACTourMediaItem> {
        return _streamState.value.mediaItems[mediaType] ?: emptyList()
    }

    fun setMediaCategory(newCategory: GACTourMediaType) {
        val filteredMediaItems = getMediaItemsByCategory(newCategory)

        _streamState.value = _streamState.value.copy(
            activeMediaCategory = newCategory,
            activeCategoryMediaItems = filteredMediaItems
        )
    }

    fun setSelectedMediaIndex(category: GACTourMediaType, newIndex: Int) {
        val updatedIndices = _streamState.value.mediaCategoryIndices.toMutableMap().apply {
            this[category] = newIndex
        }
        _streamState.value = _streamState.value.copy(mediaCategoryIndices = updatedIndices)
    }

    private fun resetUploadState() {
        _uploadState.value = UploadState.Idle
    }

    fun uploadMedia(
        mediaType: GACTourMediaType,
        itemsToUpload: List<GACTourUploadItem>,
    ) {
        viewModelScope.launch {
            try {
                coroutineScope {
                    itemsToUpload.forEach { item ->
                        updateUploadProgress(item.mediaUri, 0.0)
                        launch(Dispatchers.IO) { // async uploads
                            uploadMediaUseCase.execute(mediaType, item) { progress ->
                                updateUploadProgress(item.mediaUri, progress)
                            }
                        }
                    }
                }

                val uploadedUris = itemsToUpload.map { it.mediaUri }
                _uploadState.value = UploadState.Success(uploadedUris)

                delay(5000)
                resetUploadState()
            } catch (e: Exception) {
                _uploadState.value = UploadState.Failed
            }
        }
    }

    private fun updateUploadProgress(uri: Uri, progress: Double) {
        val currentProgress = _uploadState.value.let { state ->
            if (state is UploadState.Uploading) state.uploadProgress.toMutableMap()
            else mutableMapOf()
        }
        currentProgress[uri] = progress
        _uploadState.value = UploadState.Uploading(currentProgress)
    }

}

data class StreamState(
    val mediaItems: Map<GACTourMediaType, List<GACTourMediaItem>>,
    val activeMediaCategory: GACTourMediaType = GACTourMediaType.IMAGE,
    val activeCategoryMediaItems: List<GACTourMediaItem> = emptyList(),
    val mediaCategoryIndices: Map<GACTourMediaType, Int>
)

sealed class UploadState {
    data object Idle : UploadState()
    data class Uploading(val uploadProgress: Map<Uri, Double>) : UploadState()
    data class Success(val uploadedUris: List<Uri>) : UploadState()
    data object Failed : UploadState()
}