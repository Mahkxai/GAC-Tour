package com.mahkxai.gactour.android.common.util


import android.content.Context
import android.content.SharedPreferences
import com.mahkxai.gactour.android.experiment.exp_permission.ExpPermissionState
import com.mahkxai.gactour.android.presentation.permission.PermissionKey

object SharedPreferencesUtil {
    private const val PREFERENCES_FILE_KEY = "com.mahkxai.gactour.permissions_preferences"

    private fun Context.getSharedPreferences(): SharedPreferences {
        return getSharedPreferences(PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)
    }

    fun setPermissionState(context: Context, permissionKey: PermissionKey, state: ExpPermissionState) {
        val sharedPreferences = context.getSharedPreferences()
        with(sharedPreferences.edit()) {
            putString(permissionKey.label, state.label)
            apply()
        }
    }

    fun getPermissionState(context: Context, permissionKey: PermissionKey): ExpPermissionState {
        val sharedPreferences = context.getSharedPreferences()
        val stateLabel = sharedPreferences.getString(
            permissionKey.label,
            ExpPermissionState.Undefined.label
        )
        return ExpPermissionState.valueOf(stateLabel ?: ExpPermissionState.Undefined.name)
    }
}