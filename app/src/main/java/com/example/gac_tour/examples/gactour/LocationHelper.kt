package com.example.gac_tour.examples.gactour

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.mapbox.geojson.Point

class LocationHelper(private val context: Context) {
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    init {
        createLocationRequest()
    }

    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .build()
    }

    fun createLocationCallback(updateLocation: (Point) -> Unit) {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val currentLocation = Point.fromLngLat(location.longitude, location.latitude)
                    updateLocation(currentLocation)
                }
            }
        }
    }

    fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
