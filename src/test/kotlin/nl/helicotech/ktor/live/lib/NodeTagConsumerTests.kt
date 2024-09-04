package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlin.test.Test
import kotlin.test.assertEquals

class NodeTagConsumerTests {

    @Test
    fun equalTrees() {
        val node = buildVNode {
            html {
                head {
                    title { +"Hello" }
                }
                body {
                    h1 { +"World" }
                }
            }
        }

        val node2 = buildVNode {
            html {
                head {
                    title { +"Hello" }
                }
                body {
                    h1 { +"World" }
                }
            }
        }

        assertEquals(node.fingerprint, node2.fingerprint)
    }

    @Test
    fun visitAndFinalize() {
        val node = buildVNode {
            html {
                head {
                    title { +"Hello" }
                }
                body {
                    h1(classes = "foo bar") { +"World" }
                }
            }
        }

        val outer = node.outerHTML()
        val inner = node.innerHTML()
    }
}