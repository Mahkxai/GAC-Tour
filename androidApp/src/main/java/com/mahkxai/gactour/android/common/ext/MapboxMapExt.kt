package com.mahkxai.gactour.android.common.ext

import com.firebase.geofire.GeoLocation
import com.mahkxai.gactour.android.common.util.MapConstants
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.delegates.MapProjectionDelegate

fun MapProjectionDelegate.calculatePixelRadius(latitude: Double): Float {
    val meterPerPixel = this.getMetersPerPixelAtLatitude(latitude)
    return (MapConstants.FETCH_RING_RADIUS / meterPerPixel).toFloat()
}

fun Point.toGeoLocation() = GeoLocation(this.latitude(), this.longitude())