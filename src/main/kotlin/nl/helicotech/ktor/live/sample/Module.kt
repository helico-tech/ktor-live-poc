package nl.helicotech.ktor.live.sample

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.html.*
import nl.helicotech.ktor.live.lib.buildVTag
import nl.helicotech.ktor.live.lib.component.LiveComponentRequest
import nl.helicotech.ktor.live.lib.component.live
import nl.helicotech.ktor.live.lib.diff


fun Application.module() {

    routing {
        staticResources("/assets", "assets")

        install(ContentNegotiation) {
            json()
        }

        val registry = listOf(
            MyCounter,
            Counter
        ).associateBy { it.name }

        post("/live") {
            val request = call.receive<LiveComponentRequest>()

            val factory = registry[request.componentName] ?: return@post call.respond(HttpStatusCode.NotFound)

            val component = factory.hydrate(dataset = request.state)

            val currentTree = buildVTag {
                live("/live", request.componentName, component)
            }

            val handler = component.handlers[request.action] ?: return@post call.respond(HttpStatusCode.BadRequest)

            handler.handle(request.payload ?: "{}")

            val newTree = buildVTag {
                live("/live", request.componentName, component)
            }

            val changes = diff(currentTree, newTree)

            call.respond(HttpStatusCode.OK, changes)
        }

        get("/") {
            call.respondHtml {
                head {
                    title("Ktor Live Component Sample")
                    script(src = "/assets/live-component.js") {
                        attributes["defer"] = "true"
                    }
                }
                body {
                    live("/live", MyCounter)
                }
            }
        }
    }
}