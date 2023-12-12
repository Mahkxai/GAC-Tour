package com.mahkxai.gactour.android.common.ext

import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.mahkxai.gactour.android.presentation.permission.PermissionKey

fun Context.findActivity(): ComponentActivity = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> throw IllegalStateException("Context does not have an associated activity.")
}

fun Context.goToApplicationSettings() {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    )
    startActivity(intent)
}
fun Context.isPermissionGranted(permissionKey: PermissionKey): Boolean {
    return ContextCompat.checkSelfPermission(
        this, permissionKey.label
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasPickMediaPermission(): Boolean {
    return when {
        // If Android Version is Greater than Android Pie!
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> true

        else -> isPermissionGranted(permissionKey = PermissionKey.READ_STORAGE)
    }
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun Context.hasOnlyCoarseLocationPermission(): Boolean {
    val isFineLocationGranted = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val isCoarseLocationGranted = ContextCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return !isFineLocationGranted && isCoarseLocationGranted
}
