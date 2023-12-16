package com.mahkxai.gactour.android.data.firebase.impl

import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.mahkxai.gactour.android.data.firebase.FirestoreService
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreServiceImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FirestoreService {
    override fun fetchMediaNearLocation(
        center: GeoLocation,
        radiusInM: Double
    ): Flow<Map<GACTourMediaType, List<GACTourMediaItem>>> = callbackFlow {
        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radiusInM)

        coroutineScope {
            val mediaItemsMap = GACTourMediaType.values().map { mediaType ->
                async(Dispatchers.IO) {
                    try {
                        val collectionName = mediaType.collectionName()
                        val tasks = bounds.map { bound ->
                            firestore.collection(collectionName)
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
                                } else null
                            }
                        }
                        mediaType to mediaItems
                    } catch (e: FirebaseFirestoreException) {
                        // Handle Firestore exception, return empty list for this media type
                        mediaType to emptyList()
                    }
                }
            }.awaitAll().toMap()
            trySend(mediaItemsMap)
        }
        awaitClose {}
    }

    override suspend fun uploadMediaDocument(
        mediaType: GACTourMediaType,
        mediaItem: GACTourMediaItem
    ) = flow {
        val document = firestore.collection(mediaType.collectionName()).document()
        val updatedFirestoreMediaItem = mediaItem.copy(id = document.id)
        document.set(updatedFirestoreMediaItem).await()
        emit(Unit)
    }

}
