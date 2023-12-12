package com.example.gactour.ui.presentation.viewModels;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.gactour.location.LocationManager;
import com.example.gactour.models.Coordinate;
import com.example.gactour.utils.*;
import com.mapbox.geojson.Point;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@dagger.hilt.android.lifecycle.HiltViewModel
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0012\u001a\u00020\u0013J\u0006\u0010\u0014\u001a\u00020\u0013J\b\u0010\u0015\u001a\u00020\u0013H\u0014J\u0010\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0007H\u0002J\u0010\u0010\u0018\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\u0007H\u0002R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\rR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/example/gactour/ui/presentation/viewModels/LocationViewModel;", "Landroidx/lifecycle/ViewModel;", "locationManager", "Lcom/example/gactour/location/LocationManager;", "(Lcom/example/gactour/location/LocationManager;)V", "_location", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/mapbox/geojson/Point;", "_toastEvent", "", "location", "Lkotlinx/coroutines/flow/StateFlow;", "getLocation", "()Lkotlinx/coroutines/flow/StateFlow;", "toastEvent", "getToastEvent", "userPreviouslyInBounds", "", "cleanup", "", "clearToastEvent", "onCleared", "updateLocation", "point", "updateLocationBasedOnBoundary", "newLocation", "app_debug"})
public final class LocationViewModel extends androidx.lifecycle.ViewModel {
    private final com.example.gactour.location.LocationManager locationManager = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.mapbox.geojson.Point> _location = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.mapbox.geojson.Point> location = null;
    private boolean userPreviouslyInBounds = true;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _toastEvent = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> toastEvent = null;
    
    @javax.inject.Inject
    public LocationViewModel(@org.jetbrains.annotations.NotNull
    com.example.gactour.location.LocationManager locationManager) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.mapbox.geojson.Point> getLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getToastEvent() {
        return null;
    }
    
    private final void updateLocation(com.mapbox.geojson.Point point) {
    }
    
    private final com.mapbox.geojson.Point updateLocationBasedOnBoundary(com.mapbox.geojson.Point newLocation) {
        return null;
    }
    
    public final void clearToastEvent() {
    }
    
    @java.lang.Override
    protected void onCleared() {
    }
    
    public final void cleanup() {
    }
}