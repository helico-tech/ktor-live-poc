package nl.helicotech.ktor.live.sample

import kotlinx.html.*
import nl.helicotech.ktor.live.lib.action
import nl.helicotech.ktor.live.lib.component.LIVECOMPONENTTAG
import nl.helicotech.ktor.live.lib.component.LiveComponent

class Counter(
    initialAttributes: Map<String, Any> = emptyMap(),
) : LiveComponent(initialAttributes) {

    var count by data(0)

    override fun LIVECOMPONENTTAG.render() {
        div {
            button {
                +"Increment"
            }
            button {
                +"Decrement"
            }
            div {
                +"Count: $count"
            }
        }
    }

    companion object : Factory<Counter> {
        override val name = "counter"
        override fun create(attributes: Map<String, String>) = Counter(attributes)
    }
}