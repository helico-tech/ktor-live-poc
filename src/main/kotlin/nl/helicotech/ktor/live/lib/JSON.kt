package nl.helicotech.ktor.live.lib

import kotlinx.serialization.json.Json

val JSONSerializer = Json {
    prettyPrint = false
    explicitNulls = true
}