package nl.helicotech.ktor.live.lib

import kotlinx.html.HTMLTag
import kotlinx.serialization.encodeToString

inline fun <reified T : Any> HTMLTag.action(event: T) {
    attributes["data-action"] = JSONSerializer.encodeToString(event)
}