package com.mahkxai.gactour.android.common.util

import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.R
import com.mapbox.geojson.Point

object CameraConstants {
    const val ZOOM_DEFAULT = 0.0
    const val ZOOM_FOCUSED = 17.0
    const val BEARING_DEFAULT = 0.0
    const val BEARING_GAC = -58.0
    const val PITCH_2D = 0.0
    const val PITCH_3D = 45.0
}

object MapConstants {
    val GAC_LOCATION: Point = Point.fromLngLat(-93.972280,44.322933)
    const val FETCH_RING_RADIUS = 55f
    enum class MapStyle(val styleName: String, val drawableResId: Int) {
        DAWN("dawn", R.drawable.mapbox_lightpreset_dawn),
        DAY("day", R.drawable.mapbox_lightpreset_day),
        DUSK("dusk", R.drawable.mapbox_lightpreset_dusk),
        NIGHT("night", R.drawable.mapbox_lightpreset_night);

        companion object {
            fun toList(): List<Pair<String, Int>> {
                return values().map { it.styleName to it.drawableResId }
            }
        }
    }
    val PEEK_HEIGHT = 48.dp
}

object LogTags {
    const val FFMPEG = "FFMPEG"
    const val ExoPlayer = "ExoPlayer"
}