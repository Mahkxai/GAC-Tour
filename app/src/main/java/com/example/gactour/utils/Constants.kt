package com.example.gactour.utils

import com.example.gactour.models.*
import com.mapbox.maps.Style

/* Log Tags */
const val MAP_BOX_MAP = "MapBoxMap"
const val MAP_SCREEN = "MapScreen"
const val LOCATION_SERVICES = "LocationServices"
const val MAIN_APPLICATION = "MainApplication"
const val MAIN_ACTIVITY = "MainActivity"
const val VIEW_MODEL = "ViewModel"

/* Map Properties */
const val BUILDING_HEIGHT = 20.0
const val ZOOM_DEFAULT = 14.0
const val ZOOM_FOCUSED = 16.5
const val BEARING_DEFAULT = 0.0
const val BEARING_ORIENTED = -58.211
const val PITCH_DEFAULT = 0.0
const val PITCH_3D = 45.0
const val PULSING_RADIUS_METRES = 100f  // 100 metres
const val EARTH_EQUATOR_CIRCUMFERENCE = 40075017.0
const val ZOOM_DEBOUNCE_TIME = 500L  // 500 milliseconds
const val FLY_DURATION = 700L  // 500 milliseconds
const val MIN_ZOOM = 15.0

// GAC Bounds Manual
val TOP_LEFT_BOUND = Coordinate(44.320149, -93.987607).toPoint()
val BOTTOM_RIGHT_BOUND = Coordinate(44.326362, -93.964514).toPoint()
val TOP_RIGHT_BOUND = Coordinate(44.331555, -93.979749).toPoint()
val BOTTOM_LEFT_BOUND = Coordinate(44.315231, -93.974031).toPoint()

// GAC Bounds Calculated
val topRight = Coordinate(44.332655, -93.979409)
val bottomLeft = Coordinate(44.315606, -93.973200)
val midLat = (topRight.latitude + bottomLeft.latitude) / 2
val midLon = (topRight.longitude + bottomLeft.longitude) / 2
val midPoint = Coordinate(midLat, midLon)
val distanceToMidpoint = haversineDistance(topRight, midPoint)
val topLeft =
    destinationPoint(bottomLeft, Math.toRadians(90 + BEARING_ORIENTED), distanceToMidpoint)
val bottomRight =
    destinationPoint(topRight, Math.toRadians(-90 + BEARING_ORIENTED), distanceToMidpoint)

// Strict North Campus Bounds
val CAMPUS_BOUNDARY = Pair(
    Coordinate(BOTTOM_LEFT_BOUND.latitude(), TOP_LEFT_BOUND.longitude()), // southwest
    Coordinate(TOP_RIGHT_BOUND.latitude(), BOTTOM_RIGHT_BOUND.longitude())  // northeast
)

object MapStyles {
    val MINIMO = MapStyle("Minimo", "mapbox://styles/hardikshr/clo6nj5w800kf01pde8r370x7")
    val PERSONAL = MapStyle("Personal", "mapbox://styles/hardikshr/clo57xzbx00hd01p678qg0226")
    val MAPBOX_STREET = MapStyle("MapBox Street", "mapbox://styles/mapbox/streets-v12")
    val OUTDOORS = MapStyle("Outdoors", "mapbox://styles/mapbox/outdoors-v12")
    val LIGHT = MapStyle("Light", Style.LIGHT)
    val DARK = MapStyle("Dark", "mapbox://styles/mapbox/dark-v11")
    val TRAFFIC_DAY = MapStyle("Traffic Day", "mapbox://styles/mapbox/navigation-day-v1")
    val TRAFFIC_NIGHT = MapStyle("Traffic Night", "mapbox://styles/mapbox/navigation-night-v1")
    val SATELLITE = MapStyle("Satellite", "mapbox://styles/mapbox/satellite-v9")
    val SATELLITE_STREETS =
        MapStyle("Satellite Streets", "mapbox://styles/mapbox/satellite-streets-v12")

    /* Previous Versions
    val SatelliteStreets = MapStyle("Satellite Streets", Style.SATELLITE_STREETS) val MapBoxStreet = MapStyle("MapBox Street", Style.MAPBOX_STREETS)
    val Outdoors = MapStyle("Outdoors", Style.OUTDOORS)
    val LightMode = MapStyle("Light Mode", Style.LIGHT)
    val DarkMode = MapStyle("Dark Mode", Style.DARK)
    val TrafficDay = MapStyle("Traffic Day", Style.TRAFFIC_DAY)
    val TrafficNight = MapStyle("Traffic Night", Style.TRAFFIC_NIGHT)
    val Satellite = MapStyle("Satellite", Style.SATELLITE)
    val SatelliteStreets = MapStyle("Satellite Streets", Style.SATELLITE_STREETS)
    */

    // This list is useful if you want to iterate over all styles
    val values = listOf(
        MINIMO, PERSONAL, MAPBOX_STREET, OUTDOORS, LIGHT,
        DARK, TRAFFIC_DAY, TRAFFIC_NIGHT, SATELLITE, SATELLITE_STREETS
    )
}

val DEFAULT_POS = Coordinate(44.324493, -93.969890)

