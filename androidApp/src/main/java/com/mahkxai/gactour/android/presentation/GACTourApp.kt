package com.mahkxai.gactour.android.presentation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.mahkxai.gactour.android.common.ext.findActivity
import com.mahkxai.gactour.android.presentation.navigation.GACTourBottomBar
import com.mahkxai.gactour.android.presentation.navigation.GACTourDestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Destination
@Composable
fun GACTourApp() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        val context = LocalContext.current
        val activity = context.findActivity()
        val appState: GACTourAppState = rememberGACTourAppState()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Black,
            contentColor = MaterialTheme.colorScheme.onBackground,
            bottomBar = {
                if (appState.shoudShowBottomBar) {
                    GACTourBottomBar(
                        destinations = appState.navBarDestinations,
                        onNavigateToDestination = appState::navigateToScreenDestination,
                        currentDestination = appState.currentDestination
                    )
                }
            },
            // contentWindowInsets = WindowInsets.
        ) {
            GACTourDestinationsNavHost(
                appState = appState,
                activity = activity,
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
            )
        }
    }
}