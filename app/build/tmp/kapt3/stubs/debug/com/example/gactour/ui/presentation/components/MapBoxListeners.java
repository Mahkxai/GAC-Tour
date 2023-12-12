package com.example.gactour.ui.presentation.components;

import android.util.Log;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener;
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\u0002\u0010\u0007J\u0010\u0010\u0014\u001a\u00020\u00062\b\b\u0002\u0010\u0015\u001a\u00020\u0016J\u0010\u0010\u0017\u001a\u00020\u00062\b\b\u0002\u0010\u0015\u001a\u00020\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0010\u001a\u00020\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u0018"}, d2 = {"Lcom/example/gactour/ui/presentation/components/MapBoxListeners;", "", "mapView", "Lcom/mapbox/maps/MapView;", "afterCameraTrackingDismissedAction", "Lkotlin/Function0;", "", "(Lcom/mapbox/maps/MapView;Lkotlin/jvm/functions/Function0;)V", "onIndicatorBearingChangedListener", "Lcom/mapbox/maps/plugin/locationcomponent/OnIndicatorBearingChangedListener;", "getOnIndicatorBearingChangedListener", "()Lcom/mapbox/maps/plugin/locationcomponent/OnIndicatorBearingChangedListener;", "onIndicatorPositionChangedListener", "Lcom/mapbox/maps/plugin/locationcomponent/OnIndicatorPositionChangedListener;", "getOnIndicatorPositionChangedListener", "()Lcom/mapbox/maps/plugin/locationcomponent/OnIndicatorPositionChangedListener;", "onMoveListener", "Lcom/mapbox/maps/plugin/gestures/OnMoveListener;", "getOnMoveListener", "()Lcom/mapbox/maps/plugin/gestures/OnMoveListener;", "onCameraTrackingDismissed", "trackBearing", "", "onCameraTrackingStarted", "app_debug"})
public final class MapBoxListeners {
    private final com.mapbox.maps.MapView mapView = null;
    private final kotlin.jvm.functions.Function0<kotlin.Unit> afterCameraTrackingDismissedAction = null;
    @org.jetbrains.annotations.NotNull
    private final com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener onIndicatorPositionChangedListener = null;
    @org.jetbrains.annotations.NotNull
    private final com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener onIndicatorBearingChangedListener = null;
    @org.jetbrains.annotations.NotNull
    private final com.mapbox.maps.plugin.gestures.OnMoveListener onMoveListener = null;
    
    public MapBoxListeners(@org.jetbrains.annotations.NotNull
    com.mapbox.maps.MapView mapView, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> afterCameraTrackingDismissedAction) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener getOnIndicatorPositionChangedListener() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener getOnIndicatorBearingChangedListener() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.mapbox.maps.plugin.gestures.OnMoveListener getOnMoveListener() {
        return null;
    }
    
    public final void onCameraTrackingStarted(boolean trackBearing) {
    }
    
    public final void onCameraTrackingDismissed(boolean trackBearing) {
    }
}