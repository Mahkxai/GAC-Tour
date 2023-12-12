package com.example.gactour.location;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.mapbox.geojson.Point;
import dagger.hilt.android.qualifiers.ApplicationContext;
import javax.inject.Inject;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001a\u0010\f\u001a\u00020\t2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007J\u0006\u0010\u000e\u001a\u00020\tJ\u0010\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\bH\u0002J\u001a\u0010\u0011\u001a\u00020\t2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007J\u0006\u0010\u0012\u001a\u00020\tJ\u0006\u0010\u0013\u001a\u00020\tR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\u0005\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/example/gactour/location/LocationManager;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "listeners", "", "Lkotlin/Function1;", "Lcom/mapbox/geojson/Point;", "", "locationReceiver", "Landroid/content/BroadcastReceiver;", "addLocationListener", "listener", "cleanup", "notifyListeners", "point", "removeLocationListener", "startLocationUpdates", "stopLocationUpdates", "app_debug"})
public final class LocationManager {
    private final android.content.Context context = null;
    private final java.util.List<kotlin.jvm.functions.Function1<com.mapbox.geojson.Point, kotlin.Unit>> listeners = null;
    private final android.content.BroadcastReceiver locationReceiver = null;
    
    @javax.inject.Inject
    public LocationManager(@org.jetbrains.annotations.NotNull
    @dagger.hilt.android.qualifiers.ApplicationContext
    android.content.Context context) {
        super();
    }
    
    public final void addLocationListener(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.mapbox.geojson.Point, kotlin.Unit> listener) {
    }
    
    public final void removeLocationListener(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super com.mapbox.geojson.Point, kotlin.Unit> listener) {
    }
    
    private final void notifyListeners(com.mapbox.geojson.Point point) {
    }
    
    public final void startLocationUpdates() {
    }
    
    public final void stopLocationUpdates() {
    }
    
    public final void cleanup() {
    }
}