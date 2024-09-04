package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlinx.html.org.w3c.dom.events.Event

class VTagConsumer : TagConsumer<VTag> {

    private var result: VTag? = null

    private val deque = ArrayDeque<VTag>()

    private val UnsafeImpl = object : Unsafe {
        override operator fun String.unaryPlus() {
            val node = deque.firstOrNull()
            node?.content = this
            node?.invalidate()
        }
    }

    override fun onTagStart(tag: Tag) {
        val parent = deque.firstOrNull()

        val index = parent?.children?.size ?: 0
        val wrapper = VTag(index, tag, parent)

        parent?.children?.add(wrapper)
        parent?.invalidate()
        deque.addFirst(wrapper)
    }

    override fun onTagEnd(tag: Tag) {
        result = deque.removeFirst()
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        if (deque.isEmpty()) return
        val node = deque.first()
        node.invalidate()

        if (value == null) {
            node.inner.attributes.remove(attribute)
            return
        }
        node.inner.attributes.put(attribute, value)
    }

    override fun onTagComment(content: CharSequence) {
        // ignore
    }

    override fun onTagContent(content: CharSequence) {
        val node = deque.firstOrNull()
        node?.content = content.toString()
        node?.invalidate()
    }

    override fun onTagContentEntity(entity: Entities) {
        // ignore
    }

    override fun onTagContentUnsafe(block: Unsafe.() -> Unit) {
        block(UnsafeImpl)
    }

    override fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit) {
        // ignore
    }

    override fun finalize(): VTag {
        return result ?: error("No result")
    }
}

fun buildVTag(block: VTagConsumer.() -> Unit): VTag {
    val consumer = VTagConsumer()
    consumer.block()
    return consumer.finalize()
}