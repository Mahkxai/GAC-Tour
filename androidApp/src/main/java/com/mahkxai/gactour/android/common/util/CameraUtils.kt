package com.mahkxai.gactour.android.common.util

import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object CameraUtils {

    fun Bitmap.toJpegUri(context: Context): Uri {
        // Create a temporary JPEG file
        val tempFile = File.createTempFile("temp", ".jpg", context.cacheDir)

        // Compress the bitmap data into the FileOutputStream as JPEG
        FileOutputStream(tempFile).use { outputStream ->
            this.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        return Uri.fromFile(tempFile)
    }

    fun createImageFile(context: Context): File {
        // Create an image file name
        val filenameFormat = "yyyy-MM-dd-HH-mm-ss-SSS"
        val timeStamp = SimpleDateFormat(filenameFormat, Locale.US).format(System.currentTimeMillis())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", //prefix
            ".jpg", //suffix
            storageDir //directory
        )
    }

    fun getImageUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "application_authority",
            file
        )
    }
}