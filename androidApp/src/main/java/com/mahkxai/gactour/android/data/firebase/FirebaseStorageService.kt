package com.mahkxai.gactour.android.data.firebase

import android.net.Uri
import com.mahkxai.gactour.android.domain.model.GACTourMediaType

interface FirebaseStorageService {
    suspend fun uploadMedia(
        fileUri: Uri,
        mediaType: GACTourMediaType? = null,
        onProgress: (Double) -> Unit = {},
    ): String
}