package nl.helicotech.ktor.live.lib.component

fun live(
    name: String,
    builder: LiveComponent.() -> LIVECOMPONENTTAG.() -> Unit
): LiveComponent.Factory<LiveComponent> {
    return object : LiveComponent.Factory<LiveComponent> {
        override val name: String = name
        override fun create(): LiveComponent {
            var renderFn: (LIVECOMPONENTTAG) -> Unit = {}

            val component = object : LiveComponent() {
                override fun render(tag: LIVECOMPONENTTAG) = renderFn(tag)
            }

            renderFn = builder(component)

            return component
        }
    }
}