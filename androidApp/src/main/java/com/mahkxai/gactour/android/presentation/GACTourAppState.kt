package com.mahkxai.gactour.android.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.mahkxai.gactour.android.presentation.destinations.DirectionDestination
import com.mahkxai.gactour.android.presentation.navigation.NavigationBarDestination
import com.mapbox.maps.MapboxExperimental
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.navigation.navigate
import com.ramcosta.composedestinations.spec.NavHostEngine

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun rememberGACTourAppState(
    navEngine: NavHostEngine = rememberAnimatedNavHostEngine(),
    navController: NavHostController = navEngine.rememberNavController(),
//    userNewsResourceRepository: UserNewsResourceRepository,
): GACTourAppState {
    return remember(navController) {
        GACTourAppState(navController)
    }
}

@OptIn(
    ExperimentalMaterial3Api::class,
    MapboxExperimental::class,
)
@Stable
class GACTourAppState(
    val navController: NavHostController,
//    userNewsResourceRepository: UserNewsResourceRepository,
) {
    var shoudShowBottomBar by mutableStateOf(true)
        private set

    val navBarDestinations: List<NavigationBarDestination> =
        NavigationBarDestination.values().asList()

    val currentDestination
        @Composable get() = (navController.appCurrentDestinationAsState().value
            ?: NavGraphs.root.startAppDestination) as DirectionDestination

    fun navigateToScreenDestination(navBarDestination: NavigationBarDestination) {
        navController.navigate(navBarDestination.direction) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun showBottomBar(flag: Boolean) {
        shoudShowBottomBar = flag
    }
}
