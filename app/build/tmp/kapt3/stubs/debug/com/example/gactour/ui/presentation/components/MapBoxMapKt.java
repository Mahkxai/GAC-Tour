package com.example.gactour.ui.presentation.components;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.util.Log;
import androidx.annotation.DrawableRes;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Modifier;
import androidx.core.content.ContextCompat;
import com.example.gactour.R;
import com.example.gactour.ui.presentation.viewModels.LocationViewModel;
import com.example.gactour.ui.presentation.viewModels.MapViewModel;
import com.example.gactour.utils.*;
import com.mapbox.maps.*;
import com.mapbox.maps.CameraBoundsOptions;
import com.mapbox.maps.CameraOptions;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.maps.extension.style.expressions.generated.Expression;
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility;
import com.mapbox.maps.extension.style.sources.generated.VectorSource;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import kotlinx.coroutines.*;
import java.io.IOException;
import java.util.*;
import com.mapbox.maps.extension.style.layers.properties.generated.ProjectionName;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;

@kotlin.Metadata(mv = {1, 8, 0}, k = 2, d1 = {"\u0000\\\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a(\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0007\u001a,\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000f\u001a\u0016\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000b\u001a4\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0015\u001a\u00020\u000b2\u0006\u0010\u0016\u001a\u00020\u00172\u0014\u0010\u0018\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001a\u0012\u0004\u0012\u00020\u00010\u0019\u001a\u0016\u0010\u001b\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000b2\u0006\u0010\u0013\u001a\u00020\u000b\u001a\u001a\u0010\u001c\u001a\u00020\u001d2\b\b\u0001\u0010\u001e\u001a\u00020\u001f2\u0006\u0010 \u001a\u00020\u001fH\u0007\u001a\u000e\u0010!\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020#\u00a8\u0006$"}, d2 = {"MapBoxMap", "", "modifier", "Landroidx/compose/ui/Modifier;", "boundsOptions", "Lcom/mapbox/maps/CameraBoundsOptions;", "showSheet", "Lkotlin/Function0;", "buildCameraOptions", "Lcom/mapbox/maps/CameraOptions;", "zoom", "", "pitch", "bearing", "center", "Lcom/mapbox/geojson/Point;", "computePulsingRadiusPixels", "", "latitude", "zoomLevel", "getPlaceName", "longitude", "context", "Landroid/content/Context;", "callback", "Lkotlin/Function1;", "", "metersPerPixel", "resizedBitmap", "Landroid/graphics/Bitmap;", "id", "", "widthDp", "updateMapStyle", "style", "Lcom/mapbox/maps/Style;", "app_debug"})
public final class MapBoxMapKt {
    
    @androidx.compose.runtime.Composable
    @android.annotation.SuppressLint(value = {"UseCompatLoadingForDrawables"})
    public static final void MapBoxMap(@org.jetbrains.annotations.NotNull
    androidx.compose.ui.Modifier modifier, @org.jetbrains.annotations.NotNull
    com.mapbox.maps.CameraBoundsOptions boundsOptions, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> showSheet) {
    }
    
    public static final double metersPerPixel(double latitude, double zoomLevel) {
        return 0.0;
    }
    
    public static final float computePulsingRadiusPixels(double latitude, double zoomLevel) {
        return 0.0F;
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    public static final void getPlaceName(double latitude, double longitude, @org.jetbrains.annotations.NotNull
    android.content.Context context, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> callback) {
    }
    
    @org.jetbrains.annotations.NotNull
    @androidx.compose.runtime.Composable
    public static final android.graphics.Bitmap resizedBitmap(@androidx.annotation.DrawableRes
    int id, int widthDp) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static final com.mapbox.maps.CameraOptions buildCameraOptions(double zoom, double pitch, double bearing, @org.jetbrains.annotations.NotNull
    com.mapbox.geojson.Point center) {
        return null;
    }
    
    public static final void updateMapStyle(@org.jetbrains.annotations.NotNull
    com.mapbox.maps.Style style) {
    }
}