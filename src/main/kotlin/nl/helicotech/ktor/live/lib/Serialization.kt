package nl.helicotech.ktor.live.lib

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.ClassDiscriminatorMode
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
val serializer = Json {
    prettyPrint = false
    classDiscriminator = "type"
    classDiscriminatorMode = ClassDiscriminatorMode.ALL_JSON_OBJECTS
}