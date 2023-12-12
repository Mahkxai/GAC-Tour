package com.mahkxai.gactour.android.experiment.exp_permission

import com.mahkxai.gactour.android.presentation.permission.PermissionDefaultOverlay
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import com.mahkxai.gactour.android.presentation.permission.toStringList
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mahkxai.gactour.android.common.ext.findActivity
import com.mahkxai.gactour.android.common.ext.goToApplicationSettings

@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun ExpPermissionHandler(
    permissionKey: PermissionKey,
    askPermission: Boolean,
    result: (Boolean) -> Unit,
) {
    val permissionKeys = listOf(permissionKey)
    ExpMultiplePermissionsHandler(
        permissionKeys = permissionKeys,
        askPermission = askPermission,
        // onPermissionChange = { result(it) }
    ) { multiplePermissionState ->
        result(multiplePermissionState.allPermissionsGranted)
    }
}

/*
*  Pass permissions keys in order of importance for appropriate rationales
* */
@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ExpMultiplePermissionsHandler(
    permissionKeys: List<PermissionKey>,
    askPermission: Boolean,
    // onPermissionChange: (Boolean) -> Unit = {},
    result: (MultiplePermissionsState) -> Unit = {}
) {
    val context = LocalContext.current
    val activity = context.findActivity()
    val permissionViewModel: ExpPermissionViewModel = viewModel(
        factory = ExpPermissionViewModel.provideFactory(
            permissionKeys = permissionKeys,
            askPermission = askPermission
        )
    )
    val permissionState = permissionViewModel.state.collectAsStateWithLifecycle().value
    val permissions = permissionState.permissionKeys.toStringList().toTypedArray()
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            // println(it)
            result(permissionState)
            permissionViewModel.onResult(it)
        }
    )
    val state = rememberPermissionState(permission = permissionKeys.first().label)

    LaunchedEffect(permissionState) {
        permissionKeys.forEach {
            handlePermissionState(context, permissionKeys.first(), state)
        }
    }

    LaunchedEffect(permissionState.shouldAskPermission) {
        if (permissionState.shouldAskPermission) {
            permissionLauncher.launch(permissions)
        }
    }

    LaunchedEffect(permissionState.shouldNavigateToSettings) {
        if (permissionState.shouldNavigateToSettings) {
            activity.goToApplicationSettings()
            permissionViewModel.onPermissionRequested()
        }
    }

    AnimatedVisibility(
        visible = permissionState.shouldShowRationale,
        enter = fadeIn(initialAlpha = 0.5f),
        exit = fadeOut()
    ) {
        PermissionDefaultOverlay(
            permissionKey = permissionState.permissionKeys.first(),
            onRequestPermission = permissionViewModel::onGrantPermissionClicked
        )
    }
}

/*
    Permission requested
        if not in sys pref:
            set sys pref to InitialRequest
        else if in sys pref:
            if InitiallyRequested and state.isGranted:
                granted without denying
                set sys pref to Granted
            else if InitiallyRequested and !state.isGranted:
                hasn't interacted with permission dialog yet
                don't change sys pref
            else if InitiallyRequested and state.shouldShowRationale:
                denied for the first time
                set sys pref to Rationale

            else if Rationale and is state.isGranted:
                granted on rationale message
                set sys pref to Granted
            else if Rationale and !state.isGranted:
                denied permanently
                set sys pref to PermanentlyDenied
            else if Rationale and state.shouldShowRationale
                hasn't interacted with rationale dialog yet
                don't change sys pref

            else if PermanentlyDenied and state.isGranted:
                granted manually through settings
                set sys pref to Rationale
            else if PermanentlyDenied and !state.isGranted:
                <confused>
            else if PermanentlyDenied and !state.shouldShowRationale:
                <confused>

            else if Granted and state.isGranted:
                no changes in the system
                don't change sys pref
            else if Granted and !state.isGranted:
                denied manually by user
                set sys pref to PermanentlyDenied
            else if Granted and state.shouldShowRationale:
                <confused>
    */