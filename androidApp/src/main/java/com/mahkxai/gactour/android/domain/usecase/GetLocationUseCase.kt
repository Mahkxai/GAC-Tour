package com.mahkxai.gactour.android.domain.usecase

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import com.mahkxai.gactour.android.data.location.LocationService
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocationUseCase @Inject constructor(
    private val locationService: LocationService
) {
    @RequiresApi(Build.VERSION_CODES.S)
    operator fun invoke(): Flow<Point?> = locationService.requestLocationUpdates()
}