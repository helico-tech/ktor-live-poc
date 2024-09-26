package nl.helicotech.ktor.live.lib.component

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.reflect.KProperty

abstract class LiveComponent(
    dataset : Map<String, String> = mutableMapOf(),
    handlers : Map<String, EventHandler<*>> = mutableMapOf(),
    val serializersModule: SerializersModule = Json.serializersModule
) {
    val dataset = dataset.toMutableMap()
    val handlers = handlers.toMutableMap()

    fun interface Factory<T : LiveComponent> {
        val name: String get() {
            return requireNotNull(this::class.qualifiedName) { "Please override ${this.javaClass.name} with a known name"}
        }

        fun create(): T

        fun hydrate(dataset: Map<String, String>): T {
            val component = create()
            component.hydrate(dataset)
            return component
        }
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

    class DataSetDelegate<T : Any?>(
        private val map: MutableMap<String, String>,
        private val default: T,
        private val serializer: KSerializer<T>
    ) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return (map[property.name]?.let {
                Json.decodeFromString(serializer, it)
            } ?: default)
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            map[property.name] = Json.encodeToString(serializer, value)
        }
    }

    class HandlerDelegate<T : Any?>(
        private val handlers: MutableMap<String, EventHandler<*>>,
        private val initialHandler: (payload: T) -> Unit,
        private val serializer: KSerializer<T>
    ) {

        @Suppress("UNCHECKED_CAST")
        operator fun getValue(thisRef: Any?, property: KProperty<*>): EventHandler<T> {
            return handlers.getOrPut(property.name) {
                EventHandler(property.name, initialHandler, serializer)
            } as EventHandler<T>
        }

        @Suppress("UNCHECKED_CAST")
        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: (T) -> Unit) {
            handlers[property.name] = EventHandler(property.name, value, serializer)
        }
    }

    abstract fun render(tag: LIVECOMPONENTTAG)

    inline fun <reified T : Any?> state(default: T) = DataSetDelegate(dataset, default, serializer = serializersModule.serializer())

    inline fun <reified T : Any?> event(noinline handle: (payload: T) -> Unit) = HandlerDelegate(handlers, handle, serializer = serializersModule.serializer())

    fun hydrate(dataset: Map<String, String>) {
        this.dataset.clear()
        this.dataset.putAll(dataset)
    }
}

