package com.mahkxai.gactour.android.domain.usecase

import com.firebase.geofire.GeoLocation
import com.mahkxai.gactour.android.data.firebase.FirestoreService
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType
import com.mahkxai.gactour.android.mock.MockData
import com.mapbox.geojson.Point
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class FetchNearbyMediaUseCase @Inject constructor(
    private val firestoreService: FirestoreService
) {
    // fun execute(location: Point, radiusInM: Double): Flow<List<GACTourMediaItem>> {
    //     return firestoreService.fetchMediaUpdatesNearLocation(geoLocation, radiusInM)
    // }

    fun execute(
        location: Point,
        radiusInM: Double
    ): Flow<Map<GACTourMediaType, List<GACTourMediaItem>>> {
        /*val geoLocation = GeoLocation(location.latitude(), location.longitude())
        return firestoreService.fetchMediaNearLocation(geoLocation, radiusInM)*/

        // Mock data
        return flowOf(MockData.mediaItems(location))
    }
}
