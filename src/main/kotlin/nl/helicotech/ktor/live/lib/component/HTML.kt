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
    ) {}
}

fun FlowContent.liveComponent(
    endpoint: String,
    name: String,
    component: LiveComponent
) = LIVECOMPONENTTAG(endpoint, name, component, consumer).visit {
    component.render(this)
}

fun FlowContent.liveComponent(
    endpoint: String,
    factory: LiveComponent.Factory<*>,
) = liveComponent(endpoint, factory.name, factory.create())

fun <T : LiveComponent> FlowContent.liveComponent(
    endpoint: String,
    factory: LiveComponent.Factory<T>,
    builder: T.() -> Unit
) = liveComponent(endpoint, factory.name, factory.create().also(builder))

fun <T> FlowContent.action(type: String, handler: LiveComponent.EventHandler<T>, payload: T) {
    attributes["action-$type"] = handler.name
    attributes["action-$type-payload"] = Json.encodeToString(handler.serializer, payload)
}

fun FlowContent.action(type: String, handler: LiveComponent.EventHandler<Unit>) {
    attributes["action-$type"] = handler.name
}

fun <T, C : TagConsumer<T>> C.liveComponent(
    endpoint: String,
    name: String,
    component: LiveComponent
) = LIVECOMPONENTTAG(endpoint, name, component, this).visitAndFinalize(this) {
    component.render(this)
}

fun <T, C : TagConsumer<T>, K : LiveComponent> C.liveComponent(
    endpoint: String,
    factory: LiveComponent.Factory<K>,
    builder: K.() -> Unit
) = liveComponent(endpoint, factory.name, factory.create().also(builder))