package nl.helicotech.ktor.live.lib.component

import kotlinx.html.*

class LIVECOMPONENTTAG(
    initialAttributes: Map<String, String> = emptyMap(),
    consumer: TagConsumer<*>
) : HTMLTag(
    tagName = "live-component",
    consumer = consumer,
    initialAttributes = initialAttributes,
    inlineTag = false,
    emptyTag = false
), HtmlBlockTag

fun FlowContent.liveComponent(
    name: String,
    component: LiveComponent
) = LIVECOMPONENTTAG(initialAttributes = emptyMap(), consumer = consumer).visit {

    attributes["data-component"] = name

    component.dataset.forEach { (key, value) ->
        attributes["data-state-${key}"] = value
    }
    with(component) {
        render()
    }
}

fun FlowContent.liveComponent(
    factory: LiveComponent.Factory<*>,
    attributes: Map<String, String> = emptyMap()
) = liveComponent(factory.name, factory.create(attributes))

fun FlowContent.liveComponent(
    factory: LiveComponent.Factory<*>,
    vararg attributes: Pair<String, Any> = emptyArray()
) = liveComponent(factory, attributes.toMap().mapValues { it.value.toString() })

fun <T : LiveComponent> FlowContent.liveComponent(
    factory: LiveComponent.Factory<T>,
    builder: T.() -> Unit
) = liveComponent(factory.name, factory.create().also(builder))