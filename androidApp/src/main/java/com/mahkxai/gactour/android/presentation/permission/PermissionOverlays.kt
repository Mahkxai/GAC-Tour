package com.mahkxai.gactour.android.presentation.permission

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mahkxai.gactour.android.common.ext.overlay
import com.mahkxai.gactour.android.presentation.theme.RichGold

@Composable
fun PermissionDefaultOverlay(
    permissionKey: PermissionKey,
    onRequestPermission: () -> Unit,
) {
    val context = LocalContext.current
    // val permissionTexts = remember(permissionKey) { permissionKey.getPermissionMessages(context) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Blurred background layer
        Box(modifier = Modifier.overlay())

        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = permissionKey.getTitle(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Text(
                text = permissionKey.getRationaleMessage(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
            TextButton(
                onClick = { onRequestPermission() }
            ) {
                Text(
                    text = PermissionKey.getAllowText(context),
                    textAlign = TextAlign.Center,
                    color = RichGold
                )
            }
        }
    }
}

@Composable
fun OpenSettingsDialog(
    onInteracted: () -> Unit,
    onGoToAppSettingsClick: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        text = { Text(PermissionKey.getSettingsPromptText(context)) },
        confirmButton = {
            TextButton(
                onClick = {
                    onGoToAppSettingsClick()
                    onInteracted()
                }
            ) { Text(PermissionKey.getOpenSettingsText(context)) }
        },
        dismissButton = {
            TextButton(onClick = { onInteracted() }) {
                Text(text = PermissionKey.getCancelText(context))
            }
        },
        onDismissRequest = { onInteracted() },
    )
}


// For future use cases when more comprehensive permission state are available

@Composable
fun PermissionRationaleOverlay(
    permissionKey: PermissionKey,
    onRequestPermission: () -> Unit,
) {
    val context = LocalContext.current
    val permissionTexts = remember(permissionKey) {
        permissionKey.getPermissionMessages(context)
    }
    // Blurred background layer
    Box(modifier = Modifier.overlay())
    // Permission Rationale
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = permissionKey.getTitle(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Text(
                text = permissionKey.getRationaleMessage(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
            TextButton(
                onClick = { onRequestPermission() }
            ) {
                Text(
                    text = PermissionKey.getGrantText(context),
                    textAlign = TextAlign.Center,
                    color = RichGold
                )
            }
        }
    }
}

@Composable
fun PermissionSettingsOverlay(
    permissionKey: PermissionKey,
    onGoToAppSettingsClick: () -> Unit,
) {
    val context = LocalContext.current
    val permissionTexts = remember(permissionKey) {
        permissionKey.getPermissionMessages(context)
    }
    // Blurred background layer
    Box(modifier = Modifier.overlay())
    // Permission Settings
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = permissionKey.getTitle(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
            )
            Text(
                text = permissionKey.getSettingsRedirectMessage(context),
                textAlign = TextAlign.Center,
                color = Color.White,
                fontSize = MaterialTheme.typography.bodyMedium.fontSize,
            )
            TextButton(
                onClick = { onGoToAppSettingsClick() }
            ) {
                Text(
                    text = PermissionKey.getOpenSettingsText(context),
                    textAlign = TextAlign.Center,
                    color = RichGold
                )
            }
        }
    }
}