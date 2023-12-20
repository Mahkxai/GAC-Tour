package com.mahkxai.gactour.android.data.local

import android.net.Uri
import com.mahkxai.gactour.android.data.local.model.MetaData

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}
