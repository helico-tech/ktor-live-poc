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

        assertEquals("<html><head><title>Hello</title></head><body><h1 class=\"foo bar\">World</h1></body></html>", outer)
        assertEquals("<head><title>Hello</title></head><body><h1 class=\"foo bar\">World</h1></body>", inner)
    }
}