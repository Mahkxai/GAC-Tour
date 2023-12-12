package com.mahkxai.gactour.android.presentation.permission

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor() : ViewModel() {
    private var startPermissionRequest = 0L
    private var endPermissionRequest = 0L
    var state = mutableStateOf(PermissionStatusState())
        private set

    fun onPermissionRequestClick() {
        startPermissionRequest = System.currentTimeMillis()
    }

    fun onSettingsLaunched() {
        state.value = state.value.copy(isPermanentlyDenied = false)
    }

    fun onLauncherResult() {
        endPermissionRequest = System.currentTimeMillis()
        val isPermanentlyDenied = (endPermissionRequest - startPermissionRequest < 300)
        state.value = state.value.copy(isPermanentlyDenied = isPermanentlyDenied)
    }

}

data class PermissionStatusState(
    val isPermanentlyDenied: Boolean = false,
)