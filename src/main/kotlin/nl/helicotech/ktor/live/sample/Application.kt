package nl.helicotech.ktor.live.sample

import androidx.compose.runtime.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.html.button
import kotlinx.html.h1
import nl.helicotech.ktor.live.lib.live
import nl.helicotech.ktor.live.lib.view
import java.time.Duration

fun main()  {
    embeddedServer(Netty, port = 8080, module = Application::module).start(wait = true)
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

        live("/") { events ->
            var count by remember { mutableStateOf(0) }

            LaunchedEffect(Unit) {
                events.collect { event ->
                    when (event) {
                        "add" -> {
                            count += 1
                        }
                        "substract" -> {
                            count += -1
                        }
                    }
                }
            }

            view {
                h1 {
                    +"Counter: $count"
                }

                button {
                    attributes["data-action"] = "add"
                    +"Add"
                }

                button {
                    attributes["data-action"] = "substract"
                    +"Substract"
                }
            }
        }
    }
}