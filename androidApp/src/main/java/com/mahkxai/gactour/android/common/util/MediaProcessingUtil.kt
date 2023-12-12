package com.mahkxai.gactour.android.common.util

import android.content.Context
import android.net.Uri
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import com.mapbox.maps.logE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class MediaProcessingUtil @Inject constructor(
    @ApplicationContext private val context: Context
) {
    suspend fun compressVideo(videoUri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val sourceFile = createTempFileFromUri(videoUri)
            val outputFile =
                File.createTempFile("compressed_", ".mp4", context.cacheDir)


            // val command =
            //     "-y -i ${sourceFile.absolutePath} -crf 24 ${outputFile.absolutePath}"

            // FFMPEG command to sync audio with compressed video
            val command =
                "-y -i ${sourceFile.absolutePath} -c:v libx264 -crf 24 -c:a aac -map 0:v -map 0:a ${outputFile.absolutePath}"


            val session = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(session.returnCode)) {
                println("Compression Success with Size: ${outputFile.length()}")
                outputFile
            } else {
                logE("FFMPEG", "Video Compression Failed: ${session.failStackTrace}")
                println("Compression Failed: ${session.failStackTrace}")
                null  // Return null on failure
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Video Compression Exception: ${e.message}")
            null
        }
    }

    suspend fun extractThumbnail(videoUri: Uri): File? = withContext(Dispatchers.IO) {
        try {
            val sourceFile = createTempFileFromUri(videoUri)
            val outputFile =
                File.createTempFile("thumbnail_", ".jpg", context.cacheDir)

            val command =
                "-y -i ${sourceFile.absolutePath} -frames:v 1 ${outputFile.absolutePath}"

            val session = FFmpegKit.execute(command)
            if (ReturnCode.isSuccess(session.returnCode)) {
                println("Thumbnail Created with Size: ${outputFile.length()}")
                outputFile
            } else {
                logE("FFMPEG", "Thumbnail Failed: ${session.failStackTrace}")
                println("Thumbnail Extraction Failed.")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Thumbnail Extraction Exception: ${e.message}")
            null
        }
    }

    private suspend fun createTempFileFromUri(uri: Uri): File {
        return withContext(Dispatchers.IO) {
            // Create a temporary file
            val tempFile =
                File.createTempFile("source_", ".tmp", context.cacheDir)

            context.contentResolver.openInputStream(uri).use { inputStream ->
                FileOutputStream(tempFile).use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
            tempFile
        }
    }

}