package nl.helicotech.ktor.live.lib

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString

internal fun VTag.pathAsList() = path.drop(1).map { it.index }

fun Diff.toJSON(): String = JSONSerializer.encodeToString(this)
fun List<Diff>.toJSON(): String = JSONSerializer.encodeToString(this)

@Serializable
sealed interface Diff {

    @Serializable
    @SerialName("insert")
    data class Insert(
        val path: List<Int>,
        val tagName: String,
        val outerHTML: String
    ) : Diff {
        constructor(vTag: VTag) : this(vTag.pathAsList(), vTag.tagName, vTag.outerHTML())
    }

    @Serializable
    @SerialName("remove")
    data class Remove(
        val path: List<Int>
    ) : Diff {
        constructor(vTag: VTag) : this(vTag.pathAsList())
    }

    @Serializable
    @SerialName("replace")
    data class Replace(
        val path: List<Int>,
        val outerHTML: String
    ) : Diff {
        constructor(old: VTag, new: VTag) : this(old.pathAsList(), new.outerHTML())
    }

    @Serializable
    @SerialName("set-attribute")
    data class SetAttribute(
        val path: List<Int>,
        val attr: String,
        val value: String
    ): Diff {
        constructor(vTag: VTag, attr: String, value: String) : this(vTag.pathAsList(), attr, value)
    }

    @Serializable
    @SerialName("remove-attribute")
    data class RemoveAttribute(
        val path: List<Int>,
        val attr: String
    ): Diff {
        constructor(vTag: VTag, attr: String) : this(vTag.pathAsList(), attr)
    }

    @Serializable
    @SerialName("set-content")
    data class SetContent(
        val path: List<Int>,
        val content: String? = null
    ): Diff {
        constructor(vTag: VTag, content: String?) : this(vTag.pathAsList(), content)
    }
}

