package com.example.gactour

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import com.example.gactour.utils.*
//import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.TileStoreUsageMode
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Global initializations
        Log.d(MAIN_APPLICATION, "App initialized")

        // Initialize Mapbox with the default settings using ResourceOptionsManager
        /*ResourceOptionsManager
            .getDefault(this, getString(R.string.mapbox_access_token))
            .update {
                tileStoreUsageMode(TileStoreUsageMode.READ_ONLY)
            }*/

        // Initialize Notification Handlers
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }
}