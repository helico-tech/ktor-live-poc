package nl.helicotech.ktor.live.lib

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.html.BODY
import kotlinx.html.HEAD
import kotlinx.html.TagConsumer
import kotlinx.html.*

typealias Update<State, Event> = (State, Event) -> State

inline fun <State, reified Event> Routing.live(
    path: String,
    initialState: State,
    block: LiveBuilder<State, Event>.() -> Unit,
) {
    val ctx = LiveBuilderImpl<State, Event>(path, initialState).apply(block).build()

    get(path) {
        call.respondText(ctx.tree.value.outerHTML(), ContentType.Text.Html)
    }

    webSocket("$path/ws") {
        application.log.info("WebSocket connection established")

        launch {
            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
                    val event = JSONSerializer.decodeFromString<Event>(text)
                    ctx.send(event)
                }
            }
        }

        ctx.diffs.collect { diffs ->
            send(diffs.toJSON())
        }
    }
}

interface LiveContext<State, Event> {

    val path: String
    val state: StateFlow<State>
    val tree: StateFlow<VTag>
    val diffs: SharedFlow<List<Diff>>

    fun send(event: Event)
}

class LiveContextImpl<State, Event>(
    override val path: String,
    val initialState: State,
    val update: Update<State, Event>,
    val head: HEAD.(state: State) -> Unit,
    val body: BODY.(state: State) -> Unit,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
): LiveContext<State, Event> {

    override val state = MutableStateFlow(initialState)

    override val tree = state.map { state ->
        buildVTag {
            render(this, state)
        }
    }.stateIn(scope, SharingStarted.Eagerly, buildVTag { render(this, initialState) })

    override val diffs = flow {
        tree.reduce { old, new ->
            emit(diff(old, new))
            new
        }
    }.shareIn(scope, SharingStarted.Eagerly, replay = 0)

    override fun send(event: Event) {
        state.update { update(it, event) }
    }

    private fun render(tagConsumer: TagConsumer<*>, state: State): Unit = with(tagConsumer) {
        html {

            head {
                script(src = "/assets/live.js") {
                    attributes["defer"] = "true"
                }

                this.head(state)
            }

            body {
                this.body(state)
            }
        }
    }
}

interface LiveBuilder<State, Event> {
    fun onEvent(update: Update<State, Event>)
    fun head(block: HEAD.(state: State) -> Unit)
    fun body(block: BODY.(state: State) -> Unit)
    fun build(): LiveContext<State, Event>
}

class LiveBuilderImpl<State, Event>(
    val path: String,
    val initialState: State
): LiveBuilder<State, Event> {

    private var update: Update<State, Event> = { state, event -> state }

    private var head: HEAD.(state: State) -> Unit = {}

    private var body: BODY.(state: State) -> Unit = {}

    override fun onEvent(update: Update<State, Event>) {
        this.update = update
    }

    override fun head(block: HEAD.(state: State) -> Unit) {
        this.head = block
    }

    override fun body(block: BODY.(state: State) -> Unit) {
        this.body = block
    }

    override fun build(): LiveContext<State, Event> {
        return LiveContextImpl(
            path = path,
            initialState = initialState,
            update = update,
            head = head,
            body = body,
        )
    }
}