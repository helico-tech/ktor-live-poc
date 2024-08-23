package nl.helicotech.ktor.live.lib

import androidx.compose.runtime.Composable
import app.cash.molecule.RecompositionMode
import app.cash.molecule.moleculeFlow
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.html.*
import kotlinx.html.stream.appendHTML

fun view(
    head: HEAD.() -> Unit = {},
    block: BODY.() -> Unit
): String {
    return buildString {
        appendHTML().html {
            head {
                script(src = "https://unpkg.com/idiomorph@0.3.0") {}
                script(src = "/assets/live.js") {
                    attributes["defer"] = "true"
                }
                head()
            }
            body {
                block()
            }
        }
    }
}

fun Routing.live(
    endpoint: String,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    body: @Composable (events: Flow<String>) -> String
) {
    var state : StateFlow<String>? = null

    val events = MutableSharedFlow<String>(onBufferOverflow = BufferOverflow.DROP_OLDEST, extraBufferCapacity = 1)

    val flow = moleculeFlow(mode = RecompositionMode.Immediate, body = { body(events) })

    get(endpoint) {
        if (state == null) {
            state = flow.stateIn(coroutineScope)
        }

        val result = state!!.value
        call.respondText(result, ContentType.Text.Html)
    }

    webSocket("$endpoint/ws") {
        application.log.info("WebSocket connection established")

        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    events.emit(frame.readText())
                }
            }
        }

        state?.collect { html ->
            send(html)
        }
    }
}