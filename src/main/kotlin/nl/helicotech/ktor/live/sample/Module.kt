package nl.helicotech.ktor.live.sample

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import io.ktor.server.routing.head
import io.ktor.server.websocket.*
import kotlinx.html.*
import nl.helicotech.ktor.live.lib.live
import java.time.Duration


fun Application.module() {

    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {

        staticResources("/assets", "assets")

        live<Int, Int>(
            path = "/",
            initialState = 0,
            update = { state, event -> state + event },
        ) { state ->
            html {
                head {
                    title { +"Live Sample" }
                }
                body {
                    h1 {
                        +"Counter: $state"
                    }
                    button {
                        +"Increment"
                    }
                    button {
                        +"Decrement"
                    }
                }
            }
        }
    }
}