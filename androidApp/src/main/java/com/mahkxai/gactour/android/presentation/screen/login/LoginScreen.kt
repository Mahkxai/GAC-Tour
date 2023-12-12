package com.mahkxai.gactour.android.presentation.screen.login

import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.presentation.NavGraphs
import com.mahkxai.gactour.android.presentation.destinations.MapScreenDestination
import com.mahkxai.gactour.android.presentation.navigation.AuthNavGraph
import com.mahkxai.gactour.android.presentation.startAppDestination
import com.mahkxai.gactour.android.presentation.theme.RichGold
import com.mapbox.maps.MapboxExperimental
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.popUpTo

@OptIn(ExperimentalMaterial3Api::class, MapboxExperimental::class)
@AuthNavGraph(start = true)
@Destination
@Composable
fun LoginScreen(
    navigator: DestinationsNavigator
) {
    LoginScreenContent {
        navigator.navigate(MapScreenDestination) {
            popUpTo(NavGraphs.root.startAppDestination) { inclusive = true }
        }
    }

}

@Composable
fun LoginScreenContent(onLoginClick: () -> Unit) {
    val context = LocalContext.current

    val imageLoader = ImageLoader
        .Builder(context)
        .components {
            if (Build.VERSION.SDK_INT >= 28) add(ImageDecoderDecoder.Factory())
            else add(GifDecoder.Factory())
        }
        .build()


    Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray)) {
        Image(
            modifier = Modifier.fillMaxSize().background(RichGold),
            painter = rememberAsyncImagePainter(
                imageLoader = imageLoader,
                model = ImageRequest
                    .Builder(context)
                    .data(data = R.drawable.gac_intro)
                    .build()
            ),
            contentScale = ContentScale.Crop,
            contentDescription = "Default Image",
        )
        Button(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            onClick = onLoginClick,
        ) {
            Text(text = "Login")
        }
    }
}
