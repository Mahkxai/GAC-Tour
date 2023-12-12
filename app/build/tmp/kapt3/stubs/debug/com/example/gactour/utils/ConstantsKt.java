package com.example.gactour.utils;

import com.example.gactour.models.*;
import com.mapbox.maps.Style;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u00004\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0007\n\u0002\b\u001a\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006\"\u0011\u0010\u0007\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0006\"\u000e\u0010\t\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u001d\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\f0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0011\u0010\u000f\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u000e\u0010\u0012\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0013\u001a\u00020\u0014X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0015\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0017\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0018\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0019\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u001a\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u001b\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u001c\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u001d\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u001e\u001a\u00020\u001fX\u0086T\u00a2\u0006\u0002\n\u0000\"\u0011\u0010 \u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0006\"\u0011\u0010\"\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0006\"\u000e\u0010$\u001a\u00020\u0016X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010%\u001a\u00020\u0014X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010&\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\'\u001a\u00020\u0001X\u0086T\u00a2\u0006\u0002\n\u0000\"\u0011\u0010(\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0011\"\u0011\u0010*\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\u0011\"\u0011\u0010,\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010.\"\u0011\u0010/\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010.\"\u0011\u00101\u001a\u00020\u0001\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010.\"\u0011\u00103\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010\u0011\"\u0011\u00105\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\u0011\"\u0011\u00107\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\u0011\u00a8\u00069"}, d2 = {"BEARING_DEFAULT", "", "BEARING_ORIENTED", "BOTTOM_LEFT_BOUND", "Lcom/mapbox/geojson/Point;", "getBOTTOM_LEFT_BOUND", "()Lcom/mapbox/geojson/Point;", "BOTTOM_RIGHT_BOUND", "getBOTTOM_RIGHT_BOUND", "BUILDING_HEIGHT", "CAMPUS_BOUNDARY", "Lkotlin/Pair;", "Lcom/example/gactour/models/Coordinate;", "getCAMPUS_BOUNDARY", "()Lkotlin/Pair;", "DEFAULT_POS", "getDEFAULT_POS", "()Lcom/example/gactour/models/Coordinate;", "EARTH_EQUATOR_CIRCUMFERENCE", "FLY_DURATION", "", "LOCATION_SERVICES", "", "MAIN_ACTIVITY", "MAIN_APPLICATION", "MAP_BOX_MAP", "MAP_SCREEN", "MIN_ZOOM", "PITCH_3D", "PITCH_DEFAULT", "PULSING_RADIUS_METRES", "", "TOP_LEFT_BOUND", "getTOP_LEFT_BOUND", "TOP_RIGHT_BOUND", "getTOP_RIGHT_BOUND", "VIEW_MODEL", "ZOOM_DEBOUNCE_TIME", "ZOOM_DEFAULT", "ZOOM_FOCUSED", "bottomLeft", "getBottomLeft", "bottomRight", "getBottomRight", "distanceToMidpoint", "getDistanceToMidpoint", "()D", "midLat", "getMidLat", "midLon", "getMidLon", "midPoint", "getMidPoint", "topLeft", "getTopLeft", "topRight", "getTopRight", "app_debug"})
public final class ConstantsKt {
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String MAP_BOX_MAP = "MapBoxMap";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String MAP_SCREEN = "MapScreen";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String LOCATION_SERVICES = "LocationServices";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String MAIN_APPLICATION = "MainApplication";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String MAIN_ACTIVITY = "MainActivity";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String VIEW_MODEL = "ViewModel";
    public static final double BUILDING_HEIGHT = 20.0;
    public static final double ZOOM_DEFAULT = 14.0;
    public static final double ZOOM_FOCUSED = 16.5;
    public static final double BEARING_DEFAULT = 0.0;
    public static final double BEARING_ORIENTED = -58.211;
    public static final double PITCH_DEFAULT = 0.0;
    public static final double PITCH_3D = 45.0;
    public static final float PULSING_RADIUS_METRES = 100.0F;
    public static final double EARTH_EQUATOR_CIRCUMFERENCE = 4.0075017E7;
    public static final long ZOOM_DEBOUNCE_TIME = 500L;
    public static final long FLY_DURATION = 700L;
    public static final double MIN_ZOOM = 15.0;
    @org.jetbrains.annotations.NotNull
    private static final com.mapbox.geojson.Point TOP_LEFT_BOUND = null;
    @org.jetbrains.annotations.NotNull
    private static final com.mapbox.geojson.Point BOTTOM_RIGHT_BOUND = null;
    @org.jetbrains.annotations.NotNull
    private static final com.mapbox.geojson.Point TOP_RIGHT_BOUND = null;
    @org.jetbrains.annotations.NotNull
    private static final com.mapbox.geojson.Point BOTTOM_LEFT_BOUND = null;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate topRight = null;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate bottomLeft = null;
    private static final double midLat = 0.0;
    private static final double midLon = 0.0;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate midPoint = null;
    private static final double distanceToMidpoint = 0.0;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate topLeft = null;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate bottomRight = null;
    @org.jetbrains.annotations.NotNull
    private static final kotlin.Pair<com.example.gactour.models.Coordinate, com.example.gactour.models.Coordinate> CAMPUS_BOUNDARY = null;
    @org.jetbrains.annotations.NotNull
    private static final com.example.gactour.models.Coordinate DEFAULT_POS = null;
    
    @org.jetbrains.annotations.NotNull
    public static final com.mapbox.geojson.Point getTOP_LEFT_BOUND() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.mapbox.geojson.Point getBOTTOM_RIGHT_BOUND() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.mapbox.geojson.Point getTOP_RIGHT_BOUND() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.mapbox.geojson.Point getBOTTOM_LEFT_BOUND() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getTopRight() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getBottomLeft() {
        return null;
    }
    
    public static final double getMidLat() {
        return 0.0;
    }
    
    public static final double getMidLon() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getMidPoint() {
        return null;
    }
    
    public static final double getDistanceToMidpoint() {
        return 0.0;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getTopLeft() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getBottomRight() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final kotlin.Pair<com.example.gactour.models.Coordinate, com.example.gactour.models.Coordinate> getCAMPUS_BOUNDARY() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.models.Coordinate getDEFAULT_POS() {
        return null;
    }
}