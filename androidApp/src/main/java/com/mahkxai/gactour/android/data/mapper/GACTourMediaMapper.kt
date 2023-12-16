package com.mahkxai.gactour.android.data.mapper

import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItemEntity
import com.mahkxai.gactour.android.domain.model.GACTourMediaItem

fun GACTourMediaItemEntity.toGACTourMediaItem() = GACTourMediaItem(
    id = id,
    uploaderId = uploaderId,
    title = title,
    description = description,
    url = url,
    thumbnailUrl = thumbnailUrl,
    timestamp = timestamp,
    date = date,
    timeCT = timeCT,
    geohash = geohash,
    latitude = latitude,
    longitude = longitude
)

fun GACTourMediaItem.toGACTourMediaItemEntity() = GACTourMediaItemEntity(
    id = id,
    uploaderId = uploaderId,
    title = title,
    description = description,
    url = url,
    thumbnailUrl = thumbnailUrl,
    timestamp = timestamp,
    date = date,
    timeCT = timeCT,
    geohash = geohash,
    latitude = latitude,
    longitude = longitude
)