package nl.helicotech.ktor.live.sample

import kotlinx.html.*
import nl.helicotech.ktor.live.lib.component.LIVECOMPONENTTAG
import nl.helicotech.ktor.live.lib.component.LiveComponent
import nl.helicotech.ktor.live.lib.component.action
import nl.helicotech.ktor.live.lib.component.live

val MyCounter = MyCounter(0)

fun MyCounter(initialValue: Int = 0) = live("Counter") {

    var count by state<Int>(initialValue)

    val increment by event<Int> { amount -> count += amount }
    val decrement by event<Int> { amount -> count -= amount }
    val reset by event<Unit> { count = 0 }

    return@live {
        button {
            action("click", increment, 1)
            +"Increment"
        }
        button {
            action("click", decrement, 1)
            +"Decrement"
        }

        button {
            action("click", reset)
            if (count  == 0) {
                disabled = true
            }
            +"Reset"
        }

        div {
            +"Count: $count"
        }
    }
}

class Counter: LiveComponent() {

    var count by state<Int>(0)

    val increment by event<Int> { amount -> count += amount }

    val decrement by event<Int> { amount -> count -= amount }

    val reset by event<Unit> { count = 0 }

    override fun render(tag: LIVECOMPONENTTAG) = tag.run {
        button {
            action("click", increment, 1)
            +"Increment"
        }
        button {
            action("click", decrement, 1)
            +"Decrement"
        }

        button {
            action("click", reset)
            if (count  == 0) {
                disabled = true
            }
            +"Reset"
        }

        div {
            +"Count: $count"
        }
    }

    companion object : Factory<Counter> {

        override fun create(): Counter = Counter()
    }
}