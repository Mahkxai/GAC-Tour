package com.mahkxai.gactour.android.experiment.exp_permission

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mahkxai.gactour.android.presentation.permission.PermissionKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExpPermissionViewModel(
    private val permissionKeys: List<PermissionKey>,
    shouldAskPermission: Boolean
) : ViewModel() {
    private var startPermissionRequest = 0L
    private var endPermissionRequest = 0L
    private val _state = MutableStateFlow(
        MultiplePermissionsState(
            permissionKeys = permissionKeys,
            shouldAskPermission = shouldAskPermission,
            // status = PermissionStatus.
        )
    )
    val state: StateFlow<MultiplePermissionsState> = _state.asStateFlow()

    fun onPermissionRequested() {
        _state.update { currentState ->
            currentState.copy(
                shouldAskPermission = true,
                shouldShowRationale = false,
                shouldNavigateToSettings = false
            )
        }
    }

    fun onGrantPermissionClicked() {
        startPermissionRequest = System.currentTimeMillis()
        _state.update { it.copy(shouldAskPermission = true) }
    }

    fun onResult(result: Map<String, Boolean>) {
        endPermissionRequest = System.currentTimeMillis()

        _state.update { it.copy(shouldAskPermission = false) }

        // Determine which permissions were not granted
        val deniedPermissionKeys = permissionKeys.filter { result[it.label] == false }
        val allPermissionsGranted = deniedPermissionKeys.isEmpty()

        _state.update {
            if (allPermissionsGranted) {
                it.copy(
                    shouldShowRationale = false,
                    allPermissionsGranted = true
                )
            } else {
                // RequestMultiplePermissionsLauncher terminates immediately on double denial
                val shouldNavigateToSettings = (endPermissionRequest - startPermissionRequest < 200)
                it.copy(
                    permissionKeys = deniedPermissionKeys,
                    shouldShowRationale = deniedPermissionKeys.isNotEmpty(),
                    shouldNavigateToSettings = shouldNavigateToSettings
                )
            }

        }


        // if (allPermissionsGranted) {
        //     _state.update {
        //         it.copy(
        //             shouldShowRationale = false,
        //             allPermissionsGranted = true
        //         )
        //     }
        // } else {
        //     _state.update { it ->
        //         it.copy(
        //             permissionKeys = notGrantedPermissionKeys,
        //             shouldShowRationale = notGrantedPermissionKeys.isNotEmpty(),
        //         )
        //     }
        //
        //     if (endPermissionRequest - startPermissionRequest < 200) {
        //         _state.update { it.copy(shouldNavigateToSettings = true) }
        //     }
        // }
    }

    companion object {
        fun provideFactory(
            permissionKeys: List<PermissionKey>,
            askPermission: Boolean
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpPermissionViewModel(permissionKeys, askPermission) as T
            }
        }
    }
}

data class MultiplePermissionsState(
    val permissionKeys: List<PermissionKey>,
    val allPermissionsGranted: Boolean = false,
    val shouldAskPermission: Boolean,
    val shouldShowRationale: Boolean = false,
    val shouldNavigateToSettings: Boolean = false,
    // val status: PermissionStatus
)


class ExpSampleViewModel(
    private val permissionKey: PermissionKey,
    initialState: ExpPermissionState
) : ViewModel() {
    private val _permissionState = MutableStateFlow(initialState)
    val permissionState: StateFlow<ExpPermissionState> = _permissionState.asStateFlow()

    fun updatePermissionState(newState: ExpPermissionState) {
        _permissionState.value = newState
    }

    companion object {
        fun provideFactory(
            permissionKey: PermissionKey,
            initialState: ExpPermissionState
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExpSampleViewModel(permissionKey, initialState) as T
            }
        }
    }
}

enum class ExpPermissionState(val label: String) {
    InitialRequest("InitialRequest"),
    Rationale("Rationale"),
    PermanentlyDenied("PermanentlyDenied"),
    Granted("Granted"),
    Undefined("Undefined");
}