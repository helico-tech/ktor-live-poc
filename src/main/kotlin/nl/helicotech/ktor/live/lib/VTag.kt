package nl.helicotech.ktor.live.lib

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import kotlinx.html.stream.appendHTML
import kotlinx.html.visit
import java.security.MessageDigest

class VTag(
    val index: Int,
    val inner: Tag,
    val parent: VTag?,
    val children: MutableList<VTag> = mutableListOf(),
    var content: String? = null
) : Tag by inner {

    data class Fingerprint(
        val tagName: String,
        val attributes: String,
        val children: String
    ) {
        companion object {
            @OptIn(ExperimentalStdlibApi::class)
            fun fromVTag(vtag: VTag): Fingerprint {
                val hasher = MessageDigest.getInstance("SHA-256")

                vtag.attributes.entries
                    .sortedBy { it.key }
                    .forEach { (key, value) ->
                        hasher.update(key.toByteArray())
                        hasher.update(value.toByteArray())
                    }

                val attributes = hasher.digest().toHexString()

                hasher.reset()

                vtag.children.forEach {
                    hasher.update(it.fingerprint.toString().toByteArray())
                }

                val children = hasher.digest().toHexString()

                return Fingerprint(
                    vtag.tagName,
                    attributes,
                    children
                )
            }
        }
    }

    private var _fingerprint: Fingerprint? = null

    val path by lazy {
        generateSequence(this) { it.parent }.toList().reversed()
    }

    val indices by lazy { path.map { it.index } }

    val fingerprint: Fingerprint
        get() {
            _fingerprint = _fingerprint ?: Fingerprint.fromVTag(this)
            return _fingerprint!!
        }

    fun invalidate() {
        _fingerprint = null
    }

    private var _consumer: TagConsumer<*>? = null

    override var consumer: TagConsumer<*>
        get() = _consumer ?: inner.consumer
        set(value) {
            _consumer = value
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
        this.consumer = consumer
        this.visit {
            content?.let {
                consumer.onTagContent(it)
            }
            children.forEach { it.visit(consumer) }
        }
    }
}