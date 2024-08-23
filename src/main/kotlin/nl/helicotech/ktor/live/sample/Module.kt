package nl.helicotech.ktor.live.sample

import androidx.compose.runtime.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.html.button
import kotlinx.html.h1
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import nl.helicotech.ktor.live.lib.action
import nl.helicotech.ktor.live.lib.live
import nl.helicotech.ktor.live.lib.onEvent
import nl.helicotech.ktor.live.lib.view
import java.time.Duration

@Serializable
sealed interface Event {

    @Serializable
    @SerialName("add")
    data class Add(val amount: Int) : Event

    @Serializable
    @SerialName("subtract")
    data class Subtract(val amount: Int) : Event
}

fun Application.module() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {

        staticResources("/assets", "assets")

        live<Event>("/") {
            var count by remember { mutableStateOf(0) }

            onEvent { event ->
                when (event) {
                    is Event.Add -> count += event.amount
                    is Event.Subtract -> count -= event.amount
                }
            }

            view {
                h1 { +"Counter: $count"}

                button {
                    action(Event.Subtract(5))
                    +"Subtract 5"
                }

                button {
                    action(Event.Subtract(1))
                    + "Subtract 1"
                }

                button {
                    action(Event.Add(1))
                    +"Add 1"
                }

                button {
                    action(Event.Add(5))
                    +"Add 5"
                }
            }
        }
    }
}