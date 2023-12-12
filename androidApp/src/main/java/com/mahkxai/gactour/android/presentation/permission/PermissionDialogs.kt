package com.mahkxai.gactour.android.presentation.permission

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionRationaleDialog(
    permissionKey: PermissionKey,
    onRequestPermission: () -> Unit
) {
    val context = LocalContext.current
    var showWarningDialog by remember { mutableStateOf(true) }
    // val permissionTexts = permissionKey.getPermissionMessages(context)

    if (showWarningDialog) {
        AlertDialog(
            modifier = Modifier,
            title = { Text(permissionKey.getTitle(context)) },
            text = { Text(permissionKey.getRationaleMessage(context)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRequestPermission()
                        showWarningDialog = false
                    }
                ) { Text(text = PermissionKey.getGrantText(context)) }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text(text = PermissionKey.getCancelText(context))
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}

@Composable
fun PermissionSettingsDialog(
    permissionKey: PermissionKey,
    onGoToAppSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    var showWarningDialog by remember { mutableStateOf(true) }
    // val permissionTexts = permissionKey.getPermissionMessages(context)

    if (showWarningDialog) {
        AlertDialog(
            modifier = Modifier,
            title = { Text(permissionKey.getTitle(context)) },
            text = { Text(permissionKey.getSettingsRedirectMessage(context)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onGoToAppSettingsClick()
                        showWarningDialog = false
                    }
                ) { Text(text = PermissionKey.getOpenSettingsText(context)) }
            },
            dismissButton = {
                TextButton(onClick = { showWarningDialog = false }) {
                    Text(text = PermissionKey.getCancelText(context))
                }
            },
            onDismissRequest = { showWarningDialog = false }
        )
    }
}