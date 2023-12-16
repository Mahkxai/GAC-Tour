package com.mahkxai.gactour.android.domain.usecase

import com.mahkxai.gactour.android.data.firebase.FirestoreService
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mahkxai.gactour.android.data.mock.MockData
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
        // val geoLocation = GeoLocation(location.latitude(), location.longitude())
        // return firestoreService.fetchMediaNearLocation(geoLocation, radiusInM)

        // TODO: Remove this mock data
        return flowOf(MockData.mediaItems(location))
    }
}
