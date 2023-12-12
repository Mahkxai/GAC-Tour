package com.example.gac_tour.examples.utils

import com.mapbox.maps.Style

data class MapStyle(val name: String, val url: String)

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


    // This list is useful if you want to iterate over all styles
    val values = listOf(
        MINIMO, PERSONAL, MAPBOX_STREET, OUTDOORS, LIGHT,
        DARK, TRAFFIC_DAY, TRAFFIC_NIGHT, SATELLITE, SATELLITE_STREETS
    )
}
