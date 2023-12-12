package com.example.gactour.ui.presentation.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import com.example.gactour.ui.theme.NoRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gactour.R
import com.example.gactour.location.hasLocationPermission
import com.example.gactour.ui.presentation.viewModels.MapViewModel

@Composable
fun LocationFAB(
    modifier: Modifier = Modifier,
//    hasLocationPermission: Boolean,
//    isTrackingLocation: Boolean,
//    setLocationTracking: (Boolean) -> Unit
) {
    val mapViewModel: MapViewModel = hiltViewModel()
    val isTrackingLocation by mapViewModel.isTrackingLocation.collectAsState()

    val trackingEnabledIcon = painterResource(id = R.drawable.map_user_enabled)
    val trackingDisabledIcon = painterResource(id = R.drawable.map_user_disabled)

    val hasLocationPermission = LocalContext.current.hasLocationPermission()

    CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
        FloatingActionButton(
            modifier = modifier
                .padding(16.dp)
                .size(56.dp)
                .border(
                    width = 1.dp,
                    color = when {
                        hasLocationPermission && isTrackingLocation -> Color(0xFF286DA8)
                        hasLocationPermission -> Color(0xFF000000)
                        else -> Color(0x77000000)
                    },
                    shape = CircleShape
                ),
            onClick = { mapViewModel.setIsTrackingLocation(!isTrackingLocation) },
            shape = CircleShape,
//            backgroundColor = if (hasLocationPermission) Color.White else Color.LightGray,
            contentColor = when {
                hasLocationPermission && isTrackingLocation -> Color(0xFF286DA8)
                hasLocationPermission -> Color(0xFF000000)
                else -> Color.Gray
            },
            content = {
                Icon(
                    painter = if (isTrackingLocation) trackingEnabledIcon else trackingDisabledIcon,
                    contentDescription = "Current Location"
                )
            },
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = if (hasLocationPermission) 6.dp else 0.dp,
                pressedElevation = if (hasLocationPermission) 12.dp else 0.dp,
                hoveredElevation = if (hasLocationPermission) 8.dp else 0.dp,
                focusedElevation = if (hasLocationPermission) 16.dp else 0.dp,
            )
        )
    }
}