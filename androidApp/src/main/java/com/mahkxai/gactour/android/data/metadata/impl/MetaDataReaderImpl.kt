package com.mahkxai.gactour.android.data.metadata.impl

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.mahkxai.gactour.android.data.metadata.MetaDataReader
import com.mahkxai.gactour.android.data.metadata.model.MetaData

class MetaDataReaderImpl(
    private val app: Application
) : MetaDataReader {
    override fun getMetaDataFromUri(contentUri: Uri): MetaData? {
        if (contentUri.scheme != "content") {
            return null
        }
        val fileName = app.contentResolver
            .query(
                contentUri,
                arrayOf(MediaStore.Video.VideoColumns.DISPLAY_NAME),
                null,
                null,
                null,
            )?.use { cursor ->
                val index = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(index)
            }
        return fileName?.let { fullFileName ->
            MetaData(
                fileName = Uri.parse(fullFileName).lastPathSegment ?: return null
            )
        }
    }
}