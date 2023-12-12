package com.mahkxai.gactour.android.presentation.permission

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.mahkxai.gactour.android.common.ext.findActivity
import com.mahkxai.gactour.android.common.ext.goToApplicationSettings


@OptIn(ExperimentalPermissionsApi::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun PermissionHandler(
    permissionKey: PermissionKey,
    isFeatureOverlay: Boolean = false,
    result: (Boolean) -> Unit = {},
) {
    val permissionViewModel: PermissionViewModel = viewModel()
    val permissionVMState by permissionViewModel.state
    val permissionState = rememberPermissionState(permission = permissionKey.label) {
        permissionViewModel.onLauncherResult()
    }
    val context = LocalContext.current
    val activity = context.findActivity()
    var shouldNavigateToSettings by remember { mutableStateOf(false) }

    LaunchedEffect(permissionState.status.isGranted) {
        result(permissionState.status.isGranted)
    }

    if (!permissionState.status.isGranted) {

        // Listens to vm state and alerts UI if user should navigate to settings to allow permission
        LaunchedEffect(permissionVMState.isPermanentlyDenied) {
            if (permissionVMState.isPermanentlyDenied) {
                shouldNavigateToSettings = true
                permissionViewModel.onSettingsLaunched()
            }
        }

        if (shouldNavigateToSettings) {
            if (isFeatureOverlay) {
                OpenSettingsDialog(
                    onInteracted = { shouldNavigateToSettings = false },
                    onGoToAppSettingsClick = { activity.goToApplicationSettings() }
                )
            } else {
                PermissionSettingsDialog(permissionKey = permissionKey) {
                    activity.goToApplicationSettings()
                }
            }
        } else {
            if (isFeatureOverlay) {
                PermissionDefaultOverlay(permissionKey = permissionKey) {
                    permissionViewModel.onPermissionRequestClick()
                    permissionState.launchPermissionRequest()
                }
            } else if (permissionState.status.shouldShowRationale) {
                PermissionRationaleDialog(permissionKey = permissionKey) {
                    permissionViewModel.onPermissionRequestClick()
                    permissionState.launchPermissionRequest()
                }
            } else {
                LaunchedEffect(permissionState) {
                    permissionViewModel.onPermissionRequestClick()
                    permissionState.launchPermissionRequest()
                }
            }
        }
    }

    /*// Simpler but expensive integration with MultiplePermissionsHandler
    val permissionKeys = listOf(PermissionKey)
    MultiplePermissionsHandler(
        permissionKeys = permissionKeys,
        isRequired = isRequired,
    ) {  }*/
}


// @RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MultiplePermissionsHandler(
    permissionKeys: List<PermissionKey>,
    isFeatureOverlay: Boolean = false,
    result: (Boolean) -> Unit = {},
) {
    val permissionViewModel: PermissionViewModel = viewModel()
    val permissionVMState by permissionViewModel.state
    val permissionState = rememberMultiplePermissionsState(permissionKeys.toStringList()) {
        permissionViewModel.onLauncherResult()
    }
    val context = LocalContext.current
    val activity = context.findActivity()
    var shouldNavigateToSettings by remember { mutableStateOf(false) }
    var isPermissionRequested by remember { mutableStateOf(false) }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        result(permissionState.allPermissionsGranted)
    }

    if (!permissionState.allPermissionsGranted) {
        // Listens to vm state and alerts UI if user should navigate to settings to allow permission
        LaunchedEffect(permissionVMState.isPermanentlyDenied) {
            if (permissionVMState.isPermanentlyDenied) {
                shouldNavigateToSettings = true
                permissionViewModel.onSettingsLaunched()
            }
        }

       /* if (shouldNavigateToSettings) {
            if (isFeatureOverlay) {
                OpenSettingsDialog(
                    onInteracted = {
                        shouldNavigateToSettings = false
                        isPermissionRequested = false
                    },
                    onGoToAppSettingsClick = { activity.goToApplicationSettings() }
                )
            } else {
                PermissionSettingsDialog(permissionKey = permissionKeys.first()) {
                    activity.goToApplicationSettings()
                }
            }
        } else {*/
            if (isFeatureOverlay) {
                if (!permissionState.shouldShowRationale) {
                    LaunchedEffect(permissionState) {
                        permissionViewModel.onPermissionRequestClick()
                        permissionState.launchMultiplePermissionRequest()
                    }
                }

                if (isPermissionRequested && shouldNavigateToSettings) {
                    OpenSettingsDialog(
                        onInteracted = {
                            shouldNavigateToSettings = false
                            isPermissionRequested = false
                        },
                        onGoToAppSettingsClick = { activity.goToApplicationSettings() }
                    )
                }

                PermissionDefaultOverlay(permissionKey = permissionKeys.first()) {
                    isPermissionRequested = true
                    permissionState.launchMultiplePermissionRequest()
                    permissionViewModel.onPermissionRequestClick()
                }
            } else if(shouldNavigateToSettings) {
                PermissionSettingsDialog(permissionKey = permissionKeys.first()) {
                    activity.goToApplicationSettings()
                }
            } else if (permissionState.shouldShowRationale) {
                PermissionRationaleDialog(
                    permissionKey = permissionKeys.first()
                ) {
                    permissionViewModel.onPermissionRequestClick()
                    permissionState.launchMultiplePermissionRequest()
                }
            } else {
                LaunchedEffect(permissionState) {
                    permissionViewModel.onPermissionRequestClick()
                    permissionState.launchMultiplePermissionRequest()
                }
            }

    }
}