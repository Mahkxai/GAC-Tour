package com.example.gactour;

import android.os.Bundle;
import androidx.activity.ComponentActivity;
import androidx.compose.foundation.layout.*;
import androidx.compose.ui.Modifier;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import androidx.compose.material.*;
import androidx.compose.material3.ExperimentalMaterial3Api;
import com.example.gactour.location.LocationService;
import com.example.gactour.ui.presentation.viewModels.LocationViewModel;
import com.example.gactour.ui.presentation.viewModels.MapViewModel;
import com.mapbox.maps.plugin.Plugin;
import dagger.hilt.android.AndroidEntryPoint;
import kotlinx.coroutines.FlowPreview;

@kotlinx.coroutines.FlowPreview
@androidx.compose.material3.ExperimentalMaterial3Api
@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0014J\b\u0010\u0012\u001a\u00020\u000fH\u0014J\b\u0010\u0013\u001a\u00020\u000fH\u0002J\b\u0010\u0014\u001a\u00020\u000fH\u0002R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006R\u001b\u0010\t\u001a\u00020\n8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\b\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0015"}, d2 = {"Lcom/example/gactour/MainActivity;", "Landroidx/activity/ComponentActivity;", "()V", "locationViewModel", "Lcom/example/gactour/ui/presentation/viewModels/LocationViewModel;", "getLocationViewModel", "()Lcom/example/gactour/ui/presentation/viewModels/LocationViewModel;", "locationViewModel$delegate", "Lkotlin/Lazy;", "mapViewModel", "Lcom/example/gactour/ui/presentation/viewModels/MapViewModel;", "getMapViewModel", "()Lcom/example/gactour/ui/presentation/viewModels/MapViewModel;", "mapViewModel$delegate", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onDestroy", "requestLocationPermissions", "startLocationService", "app_debug"})
@dagger.hilt.android.AndroidEntryPoint
public final class MainActivity extends androidx.activity.ComponentActivity {
    private final kotlin.Lazy locationViewModel$delegate = null;
    private final kotlin.Lazy mapViewModel$delegate = null;
    
    public MainActivity() {
        super();
    }
    
    private final com.example.gactour.ui.presentation.viewModels.LocationViewModel getLocationViewModel() {
        return null;
    }
    
    private final com.example.gactour.ui.presentation.viewModels.MapViewModel getMapViewModel() {
        return null;
    }
    
    @java.lang.Override
    protected void onCreate(@org.jetbrains.annotations.Nullable
    android.os.Bundle savedInstanceState) {
    }
    
    @java.lang.Override
    protected void onDestroy() {
    }
    
    private final void requestLocationPermissions() {
    }
    
    private final void startLocationService() {
    }
}