package com.mahkxai.gactour.android.presentation.permission

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.mahkxai.gactour.android.R

data class PermissionTexts(
    val title: String,
    val rationale: String,
    val navigateToSettings: String
)

enum class PermissionKey(
    val label: String,
    private val titleResId: Int,
    private val rationaleResId: Int,
    private val settingsResId: Int,
) {
    FINE_LOCATION(
        Manifest.permission.ACCESS_FINE_LOCATION,
        R.string.location_permission_title,
        R.string.location_permission_rationale,
        R.string.location_permission_settings
    ),
    COARSE_LOCATION(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        R.string.location_permission_title,
        R.string.location_permission_rationale,
        R.string.location_permission_settings,
    ),
    CAMERA(
        Manifest.permission.CAMERA,
        R.string.camera_permission_title,
        R.string.camera_permission_rationale,
        R.string.camera_permission_settings,
    ),
    MICROPHONE(
        Manifest.permission.RECORD_AUDIO,
        R.string.microphone_permission_title,
        R.string.microphone_permission_rationale,
        R.string.microphone_permission_settings,
    ),
    READ_STORAGE(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    ),

    @RequiresApi(Build.VERSION_CODES.R)
    MANAGE_STORAGE(
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NOTIFICATIONS(
        Manifest.permission.POST_NOTIFICATIONS,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    IMAGES(
        Manifest.permission.READ_MEDIA_IMAGES,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    ),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    VIDEO(
        Manifest.permission.READ_MEDIA_VIDEO,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    ),
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    AUDIO(
        Manifest.permission.READ_MEDIA_AUDIO,
        R.string.storage_permission_title,
        R.string.storage_permission_rationale,
        R.string.storage_permission_settings,
    );

    fun getTitle(context: Context) = context.getString(titleResId)

    fun getRationaleMessage(context: Context) = context.getString(rationaleResId)

    fun getSettingsRedirectMessage(context: Context) = context.getString(settingsResId)

    fun getPermissionMessages(context: Context) = PermissionTexts(
        context.getString(titleResId),
        context.getString(rationaleResId),
        context.getString(settingsResId)
    )

    companion object {
        fun getAllowText(context: Context) = context.getString(R.string.permission_allow)

        fun getSettingsPromptText(context: Context) =
            context.getString(R.string.permission_redirect)

        fun getGrantText(context: Context) = context.getString(R.string.permission_grant)

        fun getOpenSettingsText(context: Context) = context.getString(R.string.permission_settings)

        fun getCancelText(context: Context) = context.getString(R.string.permission_cancel)

        fun findByLabel(label: String): PermissionKey {
            return values().firstOrNull { it.label == label }
                ?: throw IllegalArgumentException("No PermissionKey found with label: $label")
        }
    }
}

fun List<PermissionKey>.toStringList(): List<String> {
    return this.map { permissionKey -> permissionKey.label }
}
