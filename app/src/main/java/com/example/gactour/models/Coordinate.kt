package com.example.gactour.models

import com.mapbox.geojson.Point
import kotlin.math.*

data class Coordinate(val latitude: Double, val longitude: Double)

fun Coordinate.toPoint(): Point {
    return Point.fromLngLat(this.longitude, this.latitude)
}

fun haversineDistance(coord1: Coordinate, coord2: Coordinate): Double {
    val R = 6371e3
    val lat1Rad = Math.toRadians(coord1.latitude)
    val lat2Rad = Math.toRadians(coord2.latitude)
    val deltaLat = lat2Rad - lat1Rad
    val deltaLon = Math.toRadians(coord2.longitude - coord1.longitude)

    val a = sin(deltaLat / 2).pow(2.0) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2).pow(2.0)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return R * c
}

fun destinationPoint(start: Coordinate, brng: Double, dist: Double): Coordinate {
    val R = 6371.0
    val lat1 = Math.toRadians(start.latitude)
    val lon1 = Math.toRadians(start.longitude)

    val lat2 = asin(sin(lat1) * cos(dist / R) + cos(lat1) * sin(dist / R) * cos(brng))
    val lon2 = lon1 + atan2(sin(brng) * sin(dist / R) * cos(lat1), cos(dist / R) - sin(lat1) * sin(lat2))

    return Coordinate(Math.toDegrees(lat2), Math.toDegrees(lon2))
}


