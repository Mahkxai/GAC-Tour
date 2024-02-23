package com.mahkxai.gactour.android.presentation.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import com.mahkxai.gactour.android.presentation.NavGraphs
import com.mahkxai.gactour.android.presentation.GACTourAppState
import com.mahkxai.gactour.android.R
import com.mahkxai.gactour.android.presentation.MainViewModel
import com.mahkxai.gactour.android.common.icon.NavBarIcons
import com.mahkxai.gactour.android.presentation.destinations.CameraScreenDestination
import com.mahkxai.gactour.android.presentation.destinations.DirectionDestination
import com.mahkxai.gactour.android.presentation.destinations.ExploreScreenDestination
import com.mahkxai.gactour.android.presentation.destinations.LoginScreenDestination
import com.mahkxai.gactour.android.presentation.destinations.ProfileScreenDestination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.DestinationsNavHost


@Composable
fun GACTourDestinationsNavHost(
    modifier: Modifier = Modifier,
    appState: GACTourAppState,
    activity: ComponentActivity,
) {
    val startRoute = NavigationBarDestination.EXPLORE.direction
    val navController = appState.navController

    DestinationsNavHost(
        navController = navController,
        navGraph = NavGraphs.bottomBar,
        modifier = modifier,
        dependenciesContainerBuilder = {
            dependency(ExploreScreenDestination) { appState::showBottomBar }
            dependency(hiltViewModel<MainViewModel>(activity))
        },
    )
}


@RootNavGraph(start = true)
@NavGraph
annotation class AuthNavGraph(val start: Boolean = false)

@RootNavGraph
@NavGraph
annotation class BottomBarNavGraph(val start: Boolean = false)

enum class NavigationBarDestination(
    val direction: DirectionDestination,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    // @DrawableRes val selectedIcon: Int,
    // @DrawableRes val unselectedIcon: Int,
    val iconTextId: Int,
    val titleTextId: Int,
) {
    EXPLORE(
        direction = ExploreScreenDestination,
        selectedIcon = NavBarIcons.Map,
        unselectedIcon = NavBarIcons.MapBorder,
        iconTextId = R.string.navbar_map_icon_text,
        titleTextId = R.string.navbar_map_title_text,
    ),
    CAMERA(
        direction = CameraScreenDestination,
        selectedIcon = NavBarIcons.Camera,
        unselectedIcon = NavBarIcons.CameraBorder,
        iconTextId = R.string.navbar_camera_icon_text,
        titleTextId = R.string.navbar_camera_title_text,
    ),
    PROFILE(
        direction = ProfileScreenDestination,
        selectedIcon = NavBarIcons.Profile,
        unselectedIcon = NavBarIcons.ProfileBorder,
        iconTextId = R.string.navbar_profile_icon_text,
        titleTextId = R.string.navbar_profile_title_text,
    ),
    /*STREAM(
        direction = StreamScreenDestination,
        selectedIcon = NavBarIcons.Stream,
        unselectedIcon = NavBarIcons.StreamBorder,
        iconTextId = R.string.navbar_stream_icon_text,
        titleTextId = R.string.navbar_stream_title_text,
    ),*/
}

enum class AuthDestination(
    val direction: DirectionDestination,
) {
    LOGIN(
        direction = LoginScreenDestination,
    ),
}

