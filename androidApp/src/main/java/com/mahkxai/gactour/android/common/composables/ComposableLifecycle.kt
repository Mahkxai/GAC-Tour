package com.mahkxai.gactour.android.common.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A composable that observes lifecycle events.
 *
 * @param lifeCycleOwner The LifecycleOwner to observe. Defaults to LocalLifecycleOwner.current.
 * @param onComposableDisposed Action to be performed when the composable is disposed.
 * @param onEvent Action to be performed on lifecycle events.
 */
@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onComposableDisposed: () -> Unit,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)

        onDispose {
            onComposableDisposed()
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}