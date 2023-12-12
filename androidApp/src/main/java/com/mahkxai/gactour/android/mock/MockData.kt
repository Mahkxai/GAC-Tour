package com.mahkxai.gactour.android.mock

import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaItem
import com.mahkxai.gactour.android.data.firebase.model.GACTourMediaType

object MockData {
    val imageUrls: List<String> = listOf(
        "https://source.unsplash.com/random/484x800",
        "https://source.unsplash.com/random/483x800",
        "https://source.unsplash.com/random/720x1280",
        "https://source.unsplash.com/random/481x800",
        "https://source.unsplash.com/random/480x800",
        "https://source.unsplash.com/random/489x800",
        "https://source.unsplash.com/random/720x1600",
        "https://source.unsplash.com/random/477x800",
        "https://source.unsplash.com/random/725x1600",
        "https://source.unsplash.com/random/475x800"
    )

    val videoUrls: List<String> = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/SubaruOutbackOnStreetAndDirt.mp4"
    )

    val thumbnailUrls: List<String> = listOf(
        "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
        "https://orange.blender.org/wp-content/themes/orange/images/media.jpg",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerBlazes.jpg",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerEscapes.jpg",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerFun.jpg",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerJoyrides.jpg",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/ForBiggerMeltdowns.jpg",
        "https://durian.blender.org/wp-content/uploads/2009/05/title_template_03.jpg",
        "https://mango.blender.org/wp-content/uploads/2012/06/title_00007-300x300.png",
        "https://storage.googleapis.com/gtv-videos-bucket/sample/images/SubaruOutbackOnStreetAndDirt.jpg"
    )

}

fun MockData.toMediaItems(): Map<GACTourMediaType, List<GACTourMediaItem>> {
    val beckLong = -93.972967
    val beckLat = 44.323707

    val imageList = this.imageUrls.map { url ->
        GACTourMediaItem(url = url, latitude = beckLat, longitude = beckLong)
    }

    val videoList = this.videoUrls.mapIndexed { index, url ->
        val thumbnailUrl = this.thumbnailUrls[index]
        GACTourMediaItem(
            url = url, thumbnailUrl = thumbnailUrl, latitude = beckLat, longitude = beckLong
        )
    }

    return mapOf(
        GACTourMediaType.IMAGE to imageList,
        GACTourMediaType.VIDEO to videoList
    )

}