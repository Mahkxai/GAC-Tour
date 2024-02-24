package com.mahkxai.gactour.android.presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.mahkxai.gactour.android.presentation.theme.GACTourTheme
import com.mapbox.maps.MapboxExperimental
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
@MapboxExperimental
@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            // Checks while the splash screen is loading
        }
        setContent {
            GACTourTheme {
                GACTourApp()
            }
        }
    }
}