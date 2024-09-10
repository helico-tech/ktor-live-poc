package nl.helicotech.ktor.live.lib

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.html.TagConsumer

typealias Update<State, Event> = (State, Event) -> State
typealias Render<State> = TagConsumer<*>.(State) -> Unit

fun <State, Event> Routing.live(
    path: String,
    initialState: State,
    update: Update<State, Event>,
    render: Render<State>,
) {

    val state = MutableStateFlow(initialState)

    val tree = MutableStateFlow(buildVTag { render(state.value) })

    get(path) {
        call.respondText(tree.value.outerHTML(), contentType = ContentType.Text.Html)
    }
}