package nl.helicotech.ktor.live.lib.component

import kotlinx.html.*
import kotlinx.serialization.json.Json

class LIVECOMPONENTTAG(
    initialAttributes: Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) : HTMLTag(
    tagName = "live-component",
    consumer = consumer,
    initialAttributes = initialAttributes,
    inlineTag = false,
    emptyTag = false
), HtmlBlockTag {
    constructor(endpoint: String, name: String, component: LiveComponent,  consumer: TagConsumer<*>) : this(
        initialAttributes = mapOf(
            "endpoint" to endpoint,
            "component" to name,
            *component.dataset.map { (key, value) -> "state-${key}" to value }.toTypedArray()
        ),
        consumer = consumer
    )
}

fun FlowContent.live(
    endpoint: String,
    name: String,
    component: LiveComponent
) = LIVECOMPONENTTAG(endpoint, name, component, consumer).visit {
    component.render(this)
}

fun FlowContent.live(
    endpoint: String,
    factory: LiveComponent.Factory<*>,
) = live(endpoint, factory.name, factory.create())

fun <T : LiveComponent> FlowContent.live(
    endpoint: String,
    factory: LiveComponent.Factory<T>,
    builder: T.() -> Unit
) = live(endpoint, factory.name, factory.create().also(builder))

fun <T> FlowContent.action(type: String, handler: LiveComponent.EventHandler<T>, payload: T) {
    attributes["action-$type"] = handler.name
    attributes["action-$type-payload"] = Json.encodeToString(handler.serializer, payload)
}

fun FlowContent.action(type: String, handler: LiveComponent.EventHandler<Unit>) {
    attributes["action-$type"] = handler.name
}

fun <T, C : TagConsumer<T>> C.live(
    endpoint: String,
    name: String,
    component: LiveComponent
) = LIVECOMPONENTTAG(endpoint, name, component, this).visitAndFinalize(this) {
    component.render(this)
}

fun <T, C : TagConsumer<T>, K : LiveComponent> C.live(
    endpoint: String,
    factory: LiveComponent.Factory<K>,
    builder: K.() -> Unit
) = live(endpoint, factory.name, factory.create().also(builder))