package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlinx.serialization.encodeToString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DifferTests {

    @Test
    fun same() {
        val diffs = diff(
            old = buildVTag { h1 { + "foo" } },
            new = buildVTag { h1 { + "foo"} }
        )
        assertEquals(0, diffs.size)
    }

    @Test
    fun content() {
        val diffs = diff(
            old = buildVTag { h1 { + "foo" } },
            new = buildVTag { h1 { + "bar "} }
        )
        assertEquals(1, diffs.size)
    }

    @Test
    fun differentTag() {
        val diffs = diff(
            old = buildVTag { h1 { + "foo" } },
            new = buildVTag { h2 { + "foo "} }
        )

        assertEquals(1, diffs.size)
        assertIs<Diff.Replace>(diffs[0])
    }

    @Test
    fun attributes() {
        val diffs = diff(
            old = buildVTag { h1 { attributes["id"] = "foo"; attributes["removed"] = "true"; attributes["same"] = "same" } },
            new = buildVTag { h1 { attributes["id"] = "bar"; attributes["added"] = "true"; attributes["same"] = "same"} }
        )

        assertEquals(3, diffs.size)
        assertEquals(1, diffs.filterIsInstance<Diff.RemoveAttribute>().size)
        assertEquals(2, diffs.filterIsInstance<Diff.SetAttribute>().size)
    }

    @Test
    fun childrenSame() {
        val diffs = diff(
            old = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            },
            new = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            }
        )

        assertEquals(0, diffs.size)
    }

    @Test
    fun childrenDifferent() {
        val diffs = diff(
            old = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            },
            new = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "baz" }
                }
            }
        )

        assertEquals(1, diffs.size)
    }

    @Test
    fun childrenDifferentSize() {
        val diffs = diff(
            old = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            },
            new = buildVTag {
                ul {
                    li { + "foo" }
                }
            }
        )
        assertEquals(1, diffs.size)
    }

    @Test
    fun childrenDifferentSize2() {
        val diffs = diff(
            old = buildVTag {
                ul {
                    li { + "foo" }
                }
            },
            new = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            }
        )
        assertEquals(1, diffs.size)
    }

    @Test
    fun childrenDifferentContents() {
        val diffs = diff(
            old = buildVTag {
                ul {
                    li { + "foo" }
                    li { + "bar" }
                }
            },
            new = buildVTag {
                ul {
                    li {
                        p {
                            + "foo"
                        }
                    }
                    li { + "bar" }
                }
            }
        )
        assertEquals(2, diffs.size)
    }
}