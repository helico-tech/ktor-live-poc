package nl.helicotech.ktor.live.lib

import kotlinx.html.h1
import kotlinx.html.h2
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DifferTests {

    @Test
    fun same() {
        val diffs = diff(
            old = buildVTag { h1 { + "foo" } },
            new = buildVTag { h1 { + "bar "} }
        )
        assertEquals(0, diffs.size)
    }

    @Test
    fun differentTag() {
        val diffs = diff(
            old = buildVTag { h1 { + "foo" } },
            new = buildVTag { h2 { + "foo "} }
        )

        assertEquals(1, diffs.size)
        assertIs<Differ.Diff.Replace>(diffs[0])
    }
}