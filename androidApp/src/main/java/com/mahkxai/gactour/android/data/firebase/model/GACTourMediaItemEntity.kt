package com.mahkxai.gactour.android.data.firebase.model


class GACTourMediaItemEntity(
    val id: String = "",
    val uploaderId: String = "",
    val title: String? = null,
    val description: String? = null,
    val url: String = "",
    val thumbnailUrl: String? = null,
    val timestamp: Long = 0L,
    val date: String = "",
    val timeCT: String = "",
    val geohash: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)