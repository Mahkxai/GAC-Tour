package com.example.gactour.location

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.mapbox.geojson.Point
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LocationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val listeners = mutableListOf<(Point) -> Unit>()

    private val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val lat = intent?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val long = intent?.getDoubleExtra("longitude", 0.0) ?: 0.0
            val point = Point.fromLngLat(long, lat)
            notifyListeners(point)
        }
    }

    init {
        val filter = IntentFilter().apply {
            addAction("LOCATION_UPDATE")
        }
        context.registerReceiver(locationReceiver, filter)
    }

    fun addLocationListener(listener: (Point) -> Unit) {
        listeners.add(listener)
    }

    fun removeLocationListener(listener: (Point) -> Unit) {
        listeners.remove(listener)
    }

    private fun notifyListeners(point: Point) {
        listeners.forEach { it(point) }
    }

    fun startLocationUpdates() {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        context.startService(intent)
    }

    fun stopLocationUpdates() {
        val intent = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
        }
        context.stopService(intent)
    }

    fun cleanup() {
        context.unregisterReceiver(locationReceiver)
    }
}
