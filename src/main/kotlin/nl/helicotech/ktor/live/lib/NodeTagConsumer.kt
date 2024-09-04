package nl.helicotech.ktor.live.lib

import kotlinx.html.*
import kotlinx.html.org.w3c.dom.events.Event

class NodeTagConsumer : TagConsumer<Node> {

    private var result: Node? = null

    private val deque = ArrayDeque<Node>()

    private val UnsafeImpl = object : Unsafe {
        override operator fun String.unaryPlus() {
            val node = deque.firstOrNull()
            node?.content = this
            node?.invalidate()
        }
    }

    override fun onTagStart(tag: Tag) {
        val parent = deque.firstOrNull()
        val node = Node(tag)

        parent?.children?.add(node)
        parent?.invalidate()
        deque.addFirst(node)
    }

    override fun onTagEnd(tag: Tag) {
        result = deque.removeFirst()
    }

    override fun onTagAttributeChange(tag: Tag, attribute: String, value: String?) {
        if (deque.isEmpty()) return
        val node = deque.first()
        node.invalidate()

        if (value == null) {
            node.tag.attributes.remove(attribute)
            return
        }
        node.tag.attributes.put(attribute, value)
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

    override fun finalize(): Node {
        return result ?: error("No result")
    }
}

fun buildVNode(block: NodeTagConsumer.() -> Unit): Node {
    val consumer = NodeTagConsumer()
    consumer.block()
    return consumer.finalize()
}