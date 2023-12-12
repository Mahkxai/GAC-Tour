package com.mahkxai.gactour.android.experiment.exp_location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.locationcomponent.LocationConsumer
import com.mapbox.maps.plugin.locationcomponent.LocationProvider

@SuppressLint("MissingPermission")
class AlternateFusedLocationManager(
    context: Context,
    private val locationCallback: (Location) -> Unit,
    private var timeInterval: Long,
    private var minimalDistance: Float
) : LocationCallback() {

    private var request: LocationRequest
    private var locationClient: FusedLocationProviderClient

    init {
        locationClient = LocationServices.getFusedLocationProviderClient(context)
        request = createRequest()
    }

    private fun createRequest(): LocationRequest =
        LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, timeInterval).apply {
            setMinUpdateIntervalMillis(timeInterval)
            setMinUpdateDistanceMeters(minimalDistance)
            setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
            setWaitForAccurateLocation(true)
        }.build()

    fun changeRequest(timeInterval: Long, minimalDistance: Float) {
        this.timeInterval = timeInterval
        this.minimalDistance = minimalDistance
        createRequest()
        stopLocationTracking()
        startLocationTracking()
    }

    fun startLocationTracking() =
        locationClient.requestLocationUpdates(request, this, Looper.getMainLooper())

    fun stopLocationTracking() {
        locationClient.flushLocations()
        locationClient.removeLocationUpdates(this)
    }

    override fun onLocationResult(locationResult: LocationResult) {
        locationResult.lastLocation?.let { location ->
            locationCallback(location)
        }
    }

    override fun onLocationAvailability(availability: LocationAvailability) {
        // TODO: react on the availability change
    }

}

class FusedLocationProvider(
    context: Context,
    private val setCurrentLocation: (Point) -> Unit,
    timeInterval: Long,
    minimalDistance: Float
) : LocationProvider {
    private var locationConsumers = mutableListOf<LocationConsumer>()
    private val locationManager =
        AlternateFusedLocationManager(
            context = context,
            locationCallback = ::onNewLocation,
            timeInterval,
            minimalDistance
        )

    init {
        locationManager.startLocationTracking()
    }

    private fun onNewLocation(location: Location) {
        val locationPoint = Point.fromLngLat(location.longitude, location.latitude)
        val locationBearing = location.bearing.toDouble()
        setCurrentLocation(locationPoint)
        locationConsumers.forEach {
            it.onLocationUpdated(locationPoint)
            it.onBearingUpdated(locationBearing)
        }
    }

    override fun registerLocationConsumer(locationConsumer: LocationConsumer) {
        locationConsumers.add(locationConsumer)
    }

    override fun unRegisterLocationConsumer(locationConsumer: LocationConsumer) {
        locationConsumers.remove(locationConsumer)
    }

    fun stopLocationUpdates() {
        locationManager.stopLocationTracking()
    }
}
