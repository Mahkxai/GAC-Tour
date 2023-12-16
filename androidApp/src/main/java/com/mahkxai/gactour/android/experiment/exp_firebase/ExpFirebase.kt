package com.mahkxai.gactour.android.experiment.exp_firebase


/*// FirestoreService and Impl
fun fetchMediaUpdatesNearLocation(center: GeoLocation, radiusInM: Double):
            Flow<List<GACTourMediaItem>>

override fun fetchMediaUpdatesNearLocation(
    center: GeoLocation,
    radiusInM: Double
): Flow<List<GACTourMediaItem>> = callbackFlow {

    val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)
    val tasks = bounds.map { bound ->
        firestore.collection("media_uploads")
            .orderBy("geohash")
            .startAt(bound.startHash)
            .endAt(bound.endHash)
            .get().asDeferred()
    }
    val results = tasks.awaitAll()
    val mediaItems = results.flatMap { querySnapshot ->
        querySnapshot.documents.mapNotNull { doc ->
            val location = GeoLocation(
                doc.getDouble("latitude") ?: return@mapNotNull null,
                doc.getDouble("longitude") ?: return@mapNotNull null
            )
            if (GeoFireUtils.getDistanceBetween(location, center) <= radiusInM) {
                doc.toObject<GACTourMediaItem>()
                // doc.toObject<FirestoreMediaItem>()?.toGACTourMediaItem()
            } else null
        }
    }
    trySend(mediaItems)
    awaitClose {}
}*/


/*
data class FirestoreMediaItem(
    val id: String = "",
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val type: String = "photos",
    val url: String = "",
    val thumbnailUrl: String? = null,
    val uploaderId: String = "anonymous",
    val timestamp: Long = 0L,
    val title: String? = null,
    val description: String? = null
) {
    fun toGACTourMediaItem(): GACTourMediaItem {
        return GACTourMediaItem(
            id = id,
            geohash = geohash,
            latitude = latitude,
            longitude = longitude,
            // type = GACTourMediaType.getTypeFromString(type),
            url = url,
            thumbnailUrl = thumbnailUrl,
            uploaderId = uploaderId,
            timestamp = timestamp,
            title = title,
            description = description
        )
    }
}*/
