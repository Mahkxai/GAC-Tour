package com.mahkxai.gactour.android.presentation.screen.map.content

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.presentation.theme.RichGold
import com.mahkxai.gactour.android.common.util.MapConstants

@Composable
fun MapStyleControls(
    modifier: Modifier = Modifier,
    currentMapStyle: String,
    setMapStyle: (String) -> Unit
) {
    val mapStyles = MapConstants.MapStyle.toList()
    var activeStyle by remember { mutableStateOf(currentMapStyle) }
    var activeButton by remember { mutableIntStateOf(0) }

    Card(
        modifier = modifier.padding(16.dp),
        shape = CircleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        )
    ) {
        Row {
            mapStyles.forEach { (styleName, drawableResId) ->
                val isCurrentIcon = (currentMapStyle == styleName)
                MapStyleIcon(
                    styleName = styleName,
                    drawableResId = drawableResId,
                    isCurrentIcon = isCurrentIcon,
                    setMapStyle = setMapStyle
                )
            }
        }
    }
}

@Composable
fun MapStyleIcon(
    styleName: String,
    drawableResId: Int,
    isCurrentIcon: Boolean,
    setMapStyle: (String) -> Unit,
) {
    IconButton(
        onClick = { setMapStyle(styleName) },
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = if (isCurrentIcon) RichGold else Color.White,
            contentColor = if (isCurrentIcon) Color.White else RichGold,
        ),
    ) {
        Icon(
            modifier = Modifier
                .padding(4.dp)
                .size(56.dp),
            painter = painterResource(id = drawableResId),
            contentDescription = styleName
        )
    }
}
