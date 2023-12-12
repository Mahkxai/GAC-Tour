package com.example.gactour.location;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.example.gactour.R;
import com.example.gactour.utils.*;
import com.google.android.gms.location.LocationServices;
import kotlinx.coroutines.Dispatchers;

@kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0007\u0018\u0000 \u00152\u00020\u0001:\u0001\u0015B\u0005\u00a2\u0006\u0002\u0010\u0002J\u0014\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0016J\b\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\fH\u0016J\"\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u0012\u001a\u00020\u000fH\u0016J\b\u0010\u0013\u001a\u00020\fH\u0002J\b\u0010\u0014\u001a\u00020\fH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lcom/example/gactour/location/LocationService;", "Landroid/app/Service;", "()V", "locationClient", "Lcom/example/gactour/location/LocationClient;", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "onBind", "Landroid/os/IBinder;", "p0", "Landroid/content/Intent;", "onCreate", "", "onDestroy", "onStartCommand", "", "intent", "flags", "startId", "start", "stop", "Companion", "app_debug"})
public final class LocationService extends android.app.Service {
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    private com.example.gactour.location.LocationClient locationClient;
    @org.jetbrains.annotations.NotNull
    public static final com.example.gactour.location.LocationService.Companion Companion = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String ACTION_START = "ACTION_START";
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String ACTION_STOP = "ACTION_STOP";
    
    public LocationService() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable
    @java.lang.Override
    public android.os.IBinder onBind(@org.jetbrains.annotations.Nullable
    android.content.Intent p0) {
        return null;
    }
    
    @java.lang.Override
    public void onCreate() {
    }
    
    @java.lang.Override
    public int onStartCommand(@org.jetbrains.annotations.Nullable
    android.content.Intent intent, int flags, int startId) {
        return 0;
    }
    
    private final void start() {
    }
    
    @kotlin.Suppress(names = {"DEPRECATION"})
    private final void stop() {
    }
    
    @java.lang.Override
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 8, 0}, k = 1, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lcom/example/gactour/location/LocationService$Companion;", "", "()V", "ACTION_START", "", "ACTION_STOP", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}