package com.mahkxai.gactour.android.data.location

import android.location.Location
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow

interface LocationService {
    fun requestLocationUpdates(): Flow<Point?>
    fun requestCurrentLocation(): Flow<Point?>
}
