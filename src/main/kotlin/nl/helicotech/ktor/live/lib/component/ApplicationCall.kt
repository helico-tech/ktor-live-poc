package nl.helicotech.ktor.live.lib.component

import io.ktor.server.application.*
import kotlinx.html.stream.appendHTML

fun ApplicationCall.respondLiveComponent(endpoint: String, name: String, component: LiveComponent) {
    val text = buildString {
        appendHTML().live(endpoint, name, component)
    }
}