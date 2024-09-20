package nl.helicotech.ktor.live.sample

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helicotech.ktor.live.lib.component.liveComponent


fun Application.module() {

    routing {
        staticResources("/assets", "assets")

        get("/") {
            call.respondHtml {
                head {
                    title("Ktor Live Component Sample")
                    script(src = "/assets/live-component.js") {
                        attributes["defer"] = "true"
                    }
                }
                body {
                    liveComponent(Counter) {
                        count = 5
                    }
                }
            }
        }
    }
}