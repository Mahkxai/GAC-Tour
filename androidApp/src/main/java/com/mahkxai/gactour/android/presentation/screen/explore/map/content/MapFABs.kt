package com.mahkxai.gactour.android.presentation.screen.explore.map.content

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.presentation.screen.explore.map.MapUiState

@Composable
fun MapFABs(
    modifier: Modifier = Modifier,
    isTrackingLocation: Boolean,
    isTrackingBearing: Boolean,
    is3DView: Boolean,
    setIsTrackingLocation: (Boolean) -> Unit,
    setIsTrackingBearing: (Boolean) -> Unit,
    setIs3DView: (Boolean) -> Unit,
    hasLocationPermission: Boolean,
    onDisabledLocationFABClick: () -> Unit,
    fabContainerOffset: Dp = 0.dp
) {
    Column(
        modifier = modifier
            .offset(y = fabContainerOffset)
    ) {
        Toggle3DFAB(
            is3DView = is3DView,
            on3DViewChange = setIs3DView,
        )
        LocationFAB(
            isTrackingLocation = isTrackingLocation,
            isTrackingBearing = isTrackingBearing,
            setIsTrackingLocation = setIsTrackingLocation,
            setIsTrackingBearing = setIsTrackingBearing,
            hasLocationPermission = hasLocationPermission,
            onDisabledLocationFABClick = onDisabledLocationFABClick
        )
    }
}

@Composable
fun Toggle3DFAB(
    is3DView: Boolean,
    on3DViewChange: (Boolean) -> Unit
) {
    FloatingActionButton(
        modifier = Modifier
            .padding(8.dp, 12.dp)
            .size(56.dp)
            .border(
                width = 1.dp,
                color = Color.Black,
                shape = CircleShape
            ),
        onClick = { on3DViewChange(!is3DView) },
        shape = CircleShape,
        containerColor = Color.White,
        content = {
            val dimension = if (is3DView) "3" else "2"
            val highlightColor = if (is3DView) Color.Red else Color.Blue
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(color = highlightColor)) { append(dimension) }
                    append("D")
                },
                fontSize = 20.sp
            )
        },
    )
}

@Composable
fun LocationFAB(
    isTrackingLocation: Boolean,
    isTrackingBearing: Boolean,
    setIsTrackingLocation: (Boolean) -> Unit,
    setIsTrackingBearing: (Boolean) -> Unit,
    hasLocationPermission: Boolean,
    onDisabledLocationFABClick: () -> Unit,
) {
    val trackingEnabledIcon = painterResource(id = R.drawable.map_tracking_enabled)
    val trackingDisabledIcon = painterResource(id = R.drawable.map_tracking_disabled)
    val locationDisabledIcon = painterResource(id = R.drawable.map_location_disabled)
    val bearingEnableIcon = painterResource(id = R.drawable.map_bearing_enabled)


    FloatingActionButton(
        modifier = Modifier
            .padding(8.dp, 12.dp)
            .size(56.dp)
            .border(
                width = 1.dp,
                color = when {
                    !hasLocationPermission -> Color.Red
                    isTrackingLocation -> Color(0xFF286DA8)
                    else -> Color(0xFF000000)
                },
                shape = CircleShape
            ),
        onClick = {
            if (hasLocationPermission) {
                if (isTrackingLocation) {
                    setIsTrackingBearing(!isTrackingBearing)
                } else {
                    setIsTrackingLocation(true)
                }
            } else {
                onDisabledLocationFABClick()
            }
        },
        shape = CircleShape,
        containerColor = Color.White,
        contentColor = when {
            !hasLocationPermission -> Color.Red
            isTrackingLocation -> Color(0xFF286DA8)
            else -> Color(0xFF000000)
        },
        content = {
            Icon(
                painter =
                when {
                    !hasLocationPermission -> locationDisabledIcon
                    isTrackingBearing -> bearingEnableIcon
                    isTrackingLocation -> trackingEnabledIcon
                    else -> trackingDisabledIcon
                },
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
