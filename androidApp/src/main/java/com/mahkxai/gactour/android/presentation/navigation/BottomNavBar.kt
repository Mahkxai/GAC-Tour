package com.mahkxai.gactour.android.presentation.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.presentation.destinations.DirectionDestination

@Composable
fun GACTourBottomBar(
    destinations: List<NavigationBarDestination>,
    onNavigateToDestination: (NavigationBarDestination) -> Unit,
    currentDestination: DirectionDestination,
) {
    GACTourNavigationBar {
        destinations.forEach { destination ->
            GACTourNavigationItem(
                selected = (destination.direction == currentDestination),
                onClick = { onNavigateToDestination(destination) },
                unselectedIcon = {
                    Icon(
                        // imageVector = ImageVector.vectorResource(id = destination.unselectedIcon),
                        imageVector = destination.unselectedIcon,
                        contentDescription = stringResource(id = destination.titleTextId),
                    )
                },
                selectedIcon = {
                    Icon(
                        // imageVector = ImageVector.vectorResource(id = destination.selectedIcon),
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(id = destination.titleTextId),
                    )
                },
                label = { Text(stringResource(destination.iconTextId)) },
            )
        }
    }
}

@Composable
fun GACTourNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = GACTourNavDefaults.navigationContentColor(),
        tonalElevation = 50.dp,
        content = content,
    )
}

@Composable
fun RowScope.GACTourNavigationItem(
    selected: Boolean,
    onClick: () -> Unit,
    unselectedIcon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = unselectedIcon,
    enabled: Boolean = true,
    label: @Composable (() -> Unit)? = null,
    alwaysShowLabel: Boolean = true,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else unselectedIcon,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = GACTourNavDefaults.navigationSelectedItemColor(),
            unselectedIconColor = GACTourNavDefaults.navigationContentColor(),
            selectedTextColor = GACTourNavDefaults.navigationSelectedItemColor(),
            unselectedTextColor = GACTourNavDefaults.navigationContentColor(),
            indicatorColor = GACTourNavDefaults.navigationIndicatorColor(),
        ),
    )
}

object GACTourNavDefaults {
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}


