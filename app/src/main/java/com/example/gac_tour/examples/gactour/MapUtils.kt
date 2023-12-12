package com.example.gac_tour.examples.gactour

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.style.expressions.dsl.generated.interpolate
import com.mapbox.maps.extension.style.layers.generated.HeatmapLayer
import com.mapbox.maps.extension.style.layers.generated.heatmapLayer
import com.mapbox.maps.plugin.delegates.MapProjectionDelegate
import kotlinx.coroutines.tasks.await
import java.lang.Integer.max
import java.util.*
import kotlin.math.cos
import kotlin.math.pow

object MapUtils {
    private const val TAG = "FirebaseUpload"

    // Function to handle different media types based on the type flag
    suspend fun uploadMedia(
        firestore: FirebaseFirestore,
        storageReference: FirebaseStorage,
        uris: List<Uri>,
        typeFlag: String,
        annotatedPoint: Point
    ) {

        for (uri in uris) {
            val path = when (typeFlag) {
                "photos" -> "images/${UUID.randomUUID()}.jpg"
                "videos" -> "videos/${UUID.randomUUID()}.mp4"
                "audios" -> "audios/${UUID.randomUUID()}.mp3"
                else -> throw IllegalArgumentException("Invalid media type")
            }

            val mediaRef = storageReference.reference.child(path)

            try {
                Log.d(TAG, "Uploading")

                // Upload file to Firebase Storage and wait for it to complete
                mediaRef.putFile(uri).await()

                // Get download URL from Firebase Storage and wait for it to complete
                val downloadUrl = mediaRef.downloadUrl.await().toString()
                Log.d(TAG, "Uploaded to Storage: $downloadUrl")

                val geohash = GeoFireUtils.getGeoHashForLocation(
                    GeoLocation(
                        annotatedPoint.latitude(),
                        annotatedPoint.longitude()
                    )
                )

                // Create a new document with the media details
                val mediaDocument = firestore.collection("media_uploads").document()

                // Prepare the data to be saved in Firestore
                val mediaData = hashMapOf(
                    "url" to downloadUrl,
                    "type" to typeFlag,
                    "geohash" to geohash,
                    "latitude" to annotatedPoint.latitude(),
                    "longitude" to annotatedPoint.longitude()
                )

                // Save the document in Firestore
                Log.d(TAG, "Saving to Firestore")
                // Save the document in Firestore
                mediaDocument.set(mediaData).await()

                Log.d(TAG, "Saved to Firestore: $downloadUrl")

            } catch (e: Exception) {
                Log.e(TAG, "Error during media upload", e)

                // Handle the error, for example, logging or showing a user-friendly message
            }
        }
    }

    fun createHeatmapLayer(): HeatmapLayer {
        return heatmapLayer(HEATMAP_LAYER_ID, HEATMAP_SOURCE_ID) {
            maxZoom(9.0)
            heatmapColor(
                interpolate {
                    linear()
                    heatmapDensity()
                    stop {
                        literal(0)
                        rgba(33.0, 102.0, 172.0, 0.0)
                    }
                    stop {
                        literal(0.2)
                        rgb(103.0, 169.0, 207.0)
                    }
                    stop {
                        literal(0.4)
                        rgb(209.0, 229.0, 240.0)
                    }
                    stop {
                        literal(0.6)
                        rgb(253.0, 219.0, 199.0)
                    }
                    stop {
                        literal(0.8)
                        rgb(239.0, 138.0, 98.0)
                    }
                    stop {
                        literal(1)
                        rgb(178.0, 24.0, 43.0)
                    }
                }
            )
            heatmapWeight(
                interpolate {
                    linear()
                    // Since you may not have "mag" property, use a constant value
                    zoom()
                    stop {
                        literal(0)
                        literal(1) // Give a minimum weight of 1 to ensure visibility
                    }
                    stop {
                        literal(9)
                        literal(3) // Increase the weight at closer zoom levels for visibility
                    }
                }
            )
            heatmapIntensity(
                interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0)
                        literal(1) // Standard intensity
                    }
                    stop {
                        literal(9)
                        literal(3) // Increased intensity at closer zoom levels
                    }
                }
            )
            heatmapRadius(
                interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0)
                        literal(15) // A larger radius to make each point spread out more
                    }
                    stop {
                        literal(9)
                        literal(50) // An even larger radius at closer zoom levels
                    }
                }
            )
            heatmapOpacity(
                interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(7)
                        literal(0.75) // Higher opacity for visibility
                    }
                    stop {
                        literal(9)
                        literal(0.75) // Maintain the same opacity across zoom levels
                    }
                }
            )
        }
    }

    fun resizeBitmapFromVector(context: Context, drawableId: Int, radius: Float): Bitmap {
        // Convert dp size to pixels (e.g., radius is the accuracy in meters, we assume 1 meter = 1 dp for example)
        val radiusDp = radius // Adjust this conversion as needed
        val scale = context.resources.displayMetrics.density
        val radiusPx = (radiusDp * scale).toInt()

        // Get the drawable and set its size
        val drawable = context.resources.getDrawable(drawableId, context.theme) as GradientDrawable
        drawable.setSize(max(radiusPx * 2, 1), max(radiusPx * 2, 1)) // Drawable size for radius

        // Create a bitmap from drawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun resizedBitmap(context: Context, @DrawableRes id: Int, widthDp: Int): Bitmap {
        val resources = context.resources
        val originalBitmap = BitmapFactory.decodeResource(resources, id)
        val aspectRatio = originalBitmap.height.toDouble() / originalBitmap.width.toDouble()
        val heightPx = (widthDp * resources.displayMetrics.density * aspectRatio).toInt()
        val widthPx = (widthDp * resources.displayMetrics.density).toInt()

        return Bitmap.createScaledBitmap(originalBitmap, widthPx, heightPx, false)
    }

    fun computePulsingRadiusPixels(latitude: Double, zoomLevel: Double): Float {
        // Calculate scale from zoom level
        val scale = 2.0.pow(zoomLevel)

        // Calculate meters per pixel at the given latitude
        val metersPerPixel =
            (EARTH_EQUATOR_CIRCUMFERENCE * cos(Math.toRadians(latitude))) / (TILE_SIZE * scale)

        // Convert the pulsing radius from meters to pixels
        return (PULSING_RADIUS_METRES / metersPerPixel * CORRECTION_FACTOR).toFloat()
    }

    /**
     * Calculate the radius in pixels from real-world meters.
     *
     * @param projection MapProjectionDelegate from MapboxMap.
     * @param realWorldRadiusMeters The radius in meters.
     * @param latitude The latitude at which you want to calculate the radius.
     * @return The radius in pixels.
     */
    fun calculatePixelRadius(
        projection: MapProjectionDelegate,
        latitude: Double
    ): Float {
        // Calculate the number of pixels per meter at the given latitude.
        val pixelsPerMeter = projection.getMetersPerPixelAtLatitude(latitude)
        // Convert real-world meters to pixels.
        return (PULSING_RADIUS_METRES / pixelsPerMeter).toFloat()
    }

    const val EARTH_EQUATOR_CIRCUMFERENCE = 40075017.0 // in meters
    const val PULSING_RADIUS_METRES = 55f
    const val TILE_SIZE: Int = 256 // The standard tile size in pixels
    const val CORRECTION_FACTOR: Double = 2.0 // Adjust this as necessary to match the expected size
    const val HEATMAP_LAYER_ID = "heatmap-layer"
    const val HEATMAP_SOURCE_ID = "heatmap-source"

}