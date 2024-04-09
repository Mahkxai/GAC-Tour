package com.mahkxai.gactour.android.domain.usecase

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.net.Uri
import androidx.core.net.toUri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.firebase.geofire.GeoFireUtils
import com.mahkxai.gactour.android.data.firebase.FirebaseStorageService
import com.mahkxai.gactour.android.data.firebase.FirestoreService
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.common.ext.toGeoLocation
import com.mahkxai.gactour.android.domain.model.GACTourUploadItem
import com.mahkxai.gactour.android.common.util.MediaProcessingUtil
import com.mapbox.maps.logE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class UploadMediaUseCase @Inject constructor(
    private val firebaseStorageService: FirebaseStorageService,
    private val firestoreService: FirestoreService,
    private val mediaProcessingUtil: MediaProcessingUtil
) {

    suspend fun execute(
        mediaType: GACTourMediaType,
        uploadItem: GACTourUploadItem,
        onProgress: (Double) -> Unit = {}
    ) {
        var mediaUri = uploadItem.mediaUri
        var compressedVideoFile: File? = null
        var fbThumbnailUrl: String? = null

        if (mediaType == GACTourMediaType.VIDEO) {
            val thumbnailFile = mediaProcessingUtil.extractThumbnail(mediaUri)

            thumbnailFile?.let { file ->
                val thumbnailUri = Uri.fromFile(file)
                println("Uploading Thumbnail")
                fbThumbnailUrl = firebaseStorageService.uploadMedia(thumbnailUri)
                file.delete()
                println("Deleted Thumbnail")
            }

            compressedVideoFile = mediaProcessingUtil.compressVideo(mediaUri)
            if (compressedVideoFile != null) mediaUri = compressedVideoFile.toUri()
        }

        val fbMediaUrl = firebaseStorageService.uploadMedia(mediaUri, mediaType) { progress ->
            onProgress(progress)
        }
        compressedVideoFile?.delete()

        val mediaItemDetails = prepareUploadDocument(uploadItem, fbMediaUrl, fbThumbnailUrl)
        firestoreService.uploadMediaDocument(mediaType, mediaItemDetails).collect()
    }

    private fun prepareUploadDocument(
        uploadItem: GACTourUploadItem,
        fbMediaUrl: String,
        fbThumbnailUrl: String?
    ): GACTourMediaItem {
        val geoLocation = uploadItem.mediaLocation.toGeoLocation()
        val geohash = GeoFireUtils.getGeoHashForLocation(geoLocation)

        // Generate the Unix timestamp at the moment of upload
        val uploadTimestamp = System.currentTimeMillis()

        // Format current date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        // Format current time in Central Time (CT)
        val timeFormatCT = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("America/Chicago")
        }
        val currentTimeCT = timeFormatCT.format(Date())

        return GACTourMediaItem(
            title = uploadItem.title,
            description = uploadItem.description,
            url = fbMediaUrl,
            thumbnailUrl = fbThumbnailUrl,
            timestamp = uploadTimestamp,
            date = currentDate,
            timeCT = currentTimeCT,
            geohash = geohash,
            latitude = uploadItem.mediaLocation.latitude(),
            longitude = uploadItem.mediaLocation.longitude()
        )
    }

}
