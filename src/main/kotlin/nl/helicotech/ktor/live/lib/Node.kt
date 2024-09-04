package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import java.security.MessageDigest

class Node(
    val tag: Tag,
    val children: MutableList<Node> = mutableListOf(),
    var content: String? = null
) {
    data class Fingerprint(
        val tagName: String,
        val attributes: String,
        val children: String
    ) {
        companion object {
            @OptIn(ExperimentalStdlibApi::class)
            fun fromVNode(node: Node): Fingerprint {
                val hasher = MessageDigest.getInstance("SHA-256")

                node.tag.attributes.entries
                    .sortedBy { it.key }
                    .forEach { (key, value) ->
                        hasher.update(key.toByteArray())
                        hasher.update(value.toByteArray())
                    }

                val attributes = hasher.digest().toHexString()

                hasher.reset()

                node.children.forEach {
                    hasher.update(it.fingerprint.toString().toByteArray())
                }

                val children = hasher.digest().toHexString()

                return Fingerprint(
                    node.tag.tagName,
                    attributes,
                    children
                )
            }
        }
    }

    private var _fingerprint: Fingerprint? = null

    val fingerprint: Fingerprint
        get() {
            _fingerprint = _fingerprint ?: Fingerprint.fromVNode(this)
            return _fingerprint!!
        }

    fun invalidate() {
        _fingerprint = null
    }

    fun <O> visitAndFinalize(consumer: TagConsumer<O>): O {
        visit(consumer)
        return consumer.finalize()
    }

    fun outerHTML(prettyPrint: Boolean = false, xhtmlCompatible: Boolean = false): String = buildString {
        visitAndFinalize(appendHTML(prettyPrint, xhtmlCompatible))
    }

    fun innerHTML(prettyPrint: Boolean = false, xhtmlCompatible: Boolean = false): String = buildString {
        children.forEach { it.visit(appendHTML(prettyPrint, xhtmlCompatible)) }
    }

    private fun <O> visit(consumer: TagConsumer<O>) {

        val newTag = HTMLTag(
            tagName = tag.tagName,
            consumer = consumer,
            initialAttributes = tag.attributes,
            namespace = tag.namespace,
            inlineTag = tag.inlineTag,
            emptyTag = tag.emptyTag
        )

        newTag.visit {
            content?.let { +it }
            children.forEach { it.visit(consumer) }
        }
    }
}