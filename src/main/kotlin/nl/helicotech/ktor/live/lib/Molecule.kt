package nl.helicotech.ktor.live.lib

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable fun <T> LiveContext<T>.onEvent(handler: (T) -> Unit) {
    LaunchedEffect(events) {
        events.collect { event ->
            handler(event)
        }
    }
}