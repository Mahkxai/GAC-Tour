package com.mahkxai.gactour.android.data.location.impl

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.mahkxai.gactour.android.data.location.LocationService
import com.mahkxai.gactour.android.common.ext.hasLocationPermission
import com.mapbox.geojson.Point
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class LocationServiceImpl @Inject constructor(
    private val context: Context,
    private val locationClient: FusedLocationProviderClient
): /*LocationProvider,*/ LocationService {

    /*// LocationProviderImpl
    private val locationConsumers = mutableListOf<LocationConsumer>()

    override fun registerLocationConsumer(locationConsumer: LocationConsumer) {
        locationConsumers.add(locationConsumer)
    }

    override fun unRegisterLocationConsumer(locationConsumer: LocationConsumer) {
        locationConsumers.remove(locationConsumer)
    }*/

    // LocationService Impl
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    override fun requestLocationUpdates(): Flow<Point?> = callbackFlow {
        if (!context.hasLocationPermission()) {
            trySend(null)
            return@callbackFlow
        }

        val request = LocationRequest.Builder(1000)
            .setIntervalMillis(1000)
            .setMinUpdateDistanceMeters(5f)
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.lastOrNull()?.let { location ->
                    val currentLocation = Point.fromLngLat(location.longitude, location.latitude)
                    val currentBearing = location.bearing.toDouble()

                    /*locationConsumers.forEach { consumer ->
                        consumer.onLocationUpdated(MapConstants.GAC_LOCATION)
                        consumer.onBearingUpdated(-90.0)
                    }*/

                    trySend(currentLocation)
                }
            }
        }

        locationClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )

        awaitClose {
            locationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun requestCurrentLocation(): Flow<Point> {
        TODO("Handle Last Known Location Request")
    }

}