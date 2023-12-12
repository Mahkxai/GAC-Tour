package com.example.gactour.ui.presentation.viewModels;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import com.example.gactour.utils.MapStyles;
import com.mapbox.geojson.Point;
import com.mapbox.maps.plugin.viewport.ViewportStatus;
import com.mapbox.maps.plugin.viewport.state.FollowPuckViewportState;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

@dagger.hilt.android.lifecycle.HiltViewModel
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u001f\u001a\u00020 J\b\u0010!\u001a\u00020 H\u0014J\u0010\u0010\"\u001a\u00020 2\b\u0010#\u001a\u0004\u0018\u00010\u0005J\u000e\u0010$\u001a\u00020 2\u0006\u0010\u0013\u001a\u00020\u0007J\u000e\u0010%\u001a\u00020 2\u0006\u0010\u0014\u001a\u00020\u0007J\u000e\u0010&\u001a\u00020 2\u0006\u0010\'\u001a\u00020\nJ\u000e\u0010(\u001a\u00020 2\u0006\u0010\u0017\u001a\u00020\fR\u0016\u0010\u0003\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u000f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00050\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\n0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\f0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000e0\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012\u00a8\u0006)"}, d2 = {"Lcom/example/gactour/ui/presentation/viewModels/MapViewModel;", "Landroidx/lifecycle/ViewModel;", "()V", "_annotatedPoint", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/mapbox/geojson/Point;", "_isAnimating", "", "_isTrackingLocation", "_mapStyle", "", "_pulsingRadius", "", "_zoomLevel", "", "annotatedPoint", "Lkotlinx/coroutines/flow/StateFlow;", "getAnnotatedPoint", "()Lkotlinx/coroutines/flow/StateFlow;", "isAnimating", "isTrackingLocation", "mapStyle", "getMapStyle", "pulsingRadius", "getPulsingRadius", "viewModelJob", "Lkotlinx/coroutines/CompletableJob;", "viewModelScope", "Lkotlinx/coroutines/CoroutineScope;", "zoomLevel", "getZoomLevel", "cleanup", "", "onCleared", "setAnnotatedPoint", "point", "setIsAnimating", "setIsTrackingLocation", "setMapStyle", "style", "setPulsingRadius", "app_debug"})
public final class MapViewModel extends androidx.lifecycle.ViewModel {
    private final kotlinx.coroutines.CompletableJob viewModelJob = null;
    private final kotlinx.coroutines.CoroutineScope viewModelScope = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isTrackingLocation = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTrackingLocation = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<com.mapbox.geojson.Point> _annotatedPoint = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.mapbox.geojson.Point> annotatedPoint = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Double> _zoomLevel = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Double> zoomLevel = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isAnimating = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isAnimating = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _mapStyle = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> mapStyle = null;
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Float> _pulsingRadius = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Float> pulsingRadius = null;
    
    @javax.inject.Inject
    public MapViewModel() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTrackingLocation() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.mapbox.geojson.Point> getAnnotatedPoint() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Double> getZoomLevel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isAnimating() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getMapStyle() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Float> getPulsingRadius() {
        return null;
    }
    
    public final void setIsTrackingLocation(boolean isTrackingLocation) {
    }
    
    public final void setAnnotatedPoint(@org.jetbrains.annotations.Nullable
    com.mapbox.geojson.Point point) {
    }
    
    public final void setIsAnimating(boolean isAnimating) {
    }
    
    public final void setMapStyle(@org.jetbrains.annotations.NotNull
    java.lang.String style) {
    }
    
    public final void setPulsingRadius(float pulsingRadius) {
    }
    
    public final void cleanup() {
    }
    
    @java.lang.Override
    protected void onCleared() {
    }
}