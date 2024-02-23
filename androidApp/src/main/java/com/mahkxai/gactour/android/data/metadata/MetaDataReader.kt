package com.mahkxai.gactour.android.data.metadata

import android.net.Uri
import com.mahkxai.gactour.android.data.metadata.model.MetaData

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}
