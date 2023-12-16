package com.mahkxai.gactour.android.data.firebase

import com.firebase.geofire.GeoLocation
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import kotlinx.coroutines.flow.Flow

interface FirestoreService {
    fun fetchMediaNearLocation(center: GeoLocation, radiusInM: Double):
            Flow<Map<GACTourMediaType, List<GACTourMediaItem>>>

    suspend fun uploadMediaDocument(mediaType: GACTourMediaType, mediaItem: GACTourMediaItem):
            Flow<Unit>
}