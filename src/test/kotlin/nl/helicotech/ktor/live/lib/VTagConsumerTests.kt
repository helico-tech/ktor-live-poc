package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlin.test.Test
import kotlin.test.assertEquals

class VTagConsumerTests {

    @Test
    fun equalTrees() {
        val vtag = buildVTag {
            html {
                head {
                    title { +"Hello" }
                }
                body {
                    h1 { +"World" }
                }
            }
        }

        val vtag2 = buildVTag {
            html {
                head {
                    title { +"Hello" }
                }
                body {
                    h1 { +"World" }
                }
            }
        }

        assertEquals(vtag.fingerprint, vtag2.fingerprint)
    }

    @Test
    fun visitAndFinalize() {
        val node = buildVTag {
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