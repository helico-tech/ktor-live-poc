package nl.helicotech.ktor.live.sample

import kotlinx.html.*
import nl.helicotech.ktor.live.lib.component.LIVECOMPONENTTAG
import nl.helicotech.ktor.live.lib.component.LiveComponent
import nl.helicotech.ktor.live.lib.component.action

class Counter(
    initialAttributes: Map<String, Any> = emptyMap(),
) : LiveComponent(initialAttributes) {

    var count by data(0)

    val increment = event("increment") { amount: Int -> count += amount }

    val decrement = event("decrement") { amount: Int -> count -= amount }

    override fun LIVECOMPONENTTAG.render() {
        div {
            button {
                action("click", increment, 1)
                +"Increment"
            }
            button {
                action("click", decrement, 1)
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