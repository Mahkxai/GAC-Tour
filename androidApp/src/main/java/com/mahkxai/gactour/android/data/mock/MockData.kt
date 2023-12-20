package com.mahkxai.gactour.android.data.mock

import com.mahkxai.gactour.android.domain.model.GACTourMediaItem
import com.mahkxai.gactour.android.domain.model.GACTourMediaType
import com.mapbox.geojson.Point
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    fun mediaItems(currentLocation: Point?): Map<GACTourMediaType, List<GACTourMediaItem>> {
        val randomImageUrls = imageUrls.shuffleAndResizeList()
        val randomVideoUrls = videoUrls.shuffleAndResizeList()

        val imageList = randomImageUrls.map { url ->
            val randomPoint = generateRandomPoint(currentLocation, 50.0)
            GACTourMediaItem(
                url = url,
                latitude = randomPoint.latitude(),
                longitude = randomPoint.longitude()
            )
        }

        val videoList = randomVideoUrls.mapIndexed { index, url ->
            val thumbnailUrl = thumbnailUrls[index]
            val randomPoint = generateRandomPoint(currentLocation, 50.0)

            GACTourMediaItem(
                url = url,
                thumbnailUrl = thumbnailUrl,
                latitude = randomPoint.latitude(),
                longitude = randomPoint.longitude()
            )
        }

        return mapOf(
            GACTourMediaType.IMAGE to imageList,
            GACTourMediaType.VIDEO to videoList
        )
    }

    // take in current location and generate a random point within a certain radius in meters
    private fun generateRandomPoint(currentLocation: Point?, radiusInM: Double): Point {
        val lng = currentLocation?.longitude()
        val lat = currentLocation?.latitude()

        val r = radiusInM / 111300f

        val u = Math.random()
        val v = Math.random()
        val w = r * sqrt(u)
        val t = 2 * Math.PI * v
        val x = w * cos(t)
        val y = w * sin(t)

        val newLng = x + lng!!
        val newLat = y + lat!!

        return Point.fromLngLat(newLng, newLat)
    }

    // shuffle and resize list to a random size
    private fun List<String>.shuffleAndResizeList() =
        this.shuffled().take((Math.random() * this.size).toInt())
}

