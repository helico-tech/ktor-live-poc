package nl.helicotech.ktor.live.lib.component

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty

abstract class LiveComponent(
    initialDataset: Map<String, Any> = emptyMap()
) {
    val handlers = mutableMapOf<String, EventHandler<*>>()
    val dataset = initialDataset.mapValues { it.value.toString() }.toMutableMap()

    abstract fun LIVECOMPONENTTAG.render()

    open protected val serializersModule = Json.serializersModule

    protected inline fun <reified T : Any> data(default: T) = MapDelegate(dataset, default, serializersModule.serializer())

    protected inline fun event(name: String, noinline handler: () -> Unit): EventHandler<Unit> {
        return EventHandler(name, { handler() }, serializersModule.serializer<Unit>()).also {
            handlers[name] = it
        }
    }

    protected inline fun <reified T : Any> event(name: String, noinline handler: (T) -> Unit): EventHandler<T> {
        return EventHandler(name, handler, serializersModule.serializer()).also {
            handlers[name] = it
        }
    }


    interface Factory<T : LiveComponent> {
        val name: String
        fun create(attributes: Map<String, String> = emptyMap()): T
    }

    data class EventHandler<T>(
        val name: String,
        val handle: (payload: T) -> Unit,
        val serializer: KSerializer<T>,
    ) {
        fun handle(payload: String) {
            handle(Json.decodeFromString(serializer, payload))
        }
    }
}

class MapDelegate<T : Any>(
    private val map: MutableMap<String, String>,
    private val default: T,
    private val serializer: KSerializer<T>,
) {

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return (map[property.name]?.let {
            Json.decodeFromString(serializer, it)
        } ?: default) as T
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        map[property.name] = Json.encodeToString(serializer, value)
    }
}