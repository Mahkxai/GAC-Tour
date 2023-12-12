package com.example.gactour.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.Priority;
import kotlinx.coroutines.flow.Flow;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\u0006\u0010\n\u001a\u00020\u000bH\u0017R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/example/gactour/location/DefaultLocationClient;", "Lcom/example/gactour/location/LocationClient;", "context", "Landroid/content/Context;", "client", "Lcom/google/android/gms/location/FusedLocationProviderClient;", "(Landroid/content/Context;Lcom/google/android/gms/location/FusedLocationProviderClient;)V", "getLocationUpdates", "Lkotlinx/coroutines/flow/Flow;", "Landroid/location/Location;", "interval", "", "app_debug"})
public final class DefaultLocationClient implements com.example.gactour.location.LocationClient {
    private final android.content.Context context = null;
    private final com.google.android.gms.location.FusedLocationProviderClient client = null;
    
    public DefaultLocationClient(@org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    com.google.android.gms.location.FusedLocationProviderClient client) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    @java.lang.Override
    public kotlinx.coroutines.flow.Flow<android.location.Location> getLocationUpdates(long interval) {
        return null;
    }
}