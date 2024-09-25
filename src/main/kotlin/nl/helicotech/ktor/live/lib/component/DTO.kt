package nl.helicotech.ktor.live.lib.component

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LiveComponentRequest(
    @SerialName("component") val componentName: String,
    val action: String,
    val payload: String?,
    val state: Map<String, String>
)