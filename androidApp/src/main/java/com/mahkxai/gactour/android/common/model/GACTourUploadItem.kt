package com.mahkxai.gactour.android.common.model

import android.net.Uri
import com.mapbox.geojson.Point

data class GACTourUploadItem(
    val mediaLocation: Point,
    val mediaUri: Uri,
    val title: String? = null,
    val description: String? = null,
)
