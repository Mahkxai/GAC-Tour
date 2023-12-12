package com.mahkxai.gactour.android.experiment.exp_permission

import android.content.Context
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.mahkxai.gactour.android.common.util.SharedPreferencesUtil
import com.mahkxai.gactour.android.presentation.permission.PermissionKey

@OptIn(ExperimentalPermissionsApi::class)
fun handlePermissionState(
    context: Context,
    permissionKey: PermissionKey,
    state: PermissionState
) {
    val currentState = SharedPreferencesUtil.getPermissionState(context, permissionKey)
    when (currentState) {
        ExpPermissionState.InitialRequest -> {
            if (state.status.isGranted) {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.Granted
                )
            } else if (state.status.shouldShowRationale) {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.Rationale
                )
            }
        }

        ExpPermissionState.Rationale -> {
            if (state.status.shouldShowRationale) return
            if (state.status.isGranted) {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.Granted
                )
            } else {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.PermanentlyDenied
                )
            }
        }

        ExpPermissionState.PermanentlyDenied -> {
            if (state.status.isGranted || state.status.shouldShowRationale) {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.Rationale
                )
            }
        }

        ExpPermissionState.Granted -> {
            if (state.status.isGranted) return
            if (state.status.shouldShowRationale) {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.Rationale
                )
            } else {
                SharedPreferencesUtil.setPermissionState(
                    context,
                    permissionKey,
                    ExpPermissionState.PermanentlyDenied
                )
            }
        }

        ExpPermissionState.Undefined -> {
            SharedPreferencesUtil.setPermissionState(
                context,
                permissionKey,
                ExpPermissionState.InitialRequest
            )
        }
    }
    val newState = SharedPreferencesUtil.getPermissionState(context, permissionKey)
}
