package com.mahkxai.gactour.android.data.firebase.impl

import android.icu.text.SimpleDateFormat
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.StorageReference
import com.mahkxai.gactour.android.data.firebase.FirebaseStorageService
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class FirebaseStorageServiceImpl @Inject constructor(
    private val storageReference: StorageReference
) : FirebaseStorageService {

    override suspend fun uploadMedia(
        fileUri: Uri,
        mediaType: GACTourMediaType?,
        onProgress: (Double) -> Unit
    ): String {
        val userId = getCurrentUserId()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "${userId}_${timeStamp}_${fileUri.lastPathSegment}"

        val collectionName = mediaType?.collectionName() ?: "thumbnails"
        val filePath = "${collectionName}/$fileName"
        val fileRef = storageReference.child(filePath)
        val uploadTask = fileRef.putFile(fileUri)

        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            onProgress(progress)
        }

        return uploadTask.await().storage.downloadUrl.await().toString()
    }

    private fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: "anonymous"
    }
}