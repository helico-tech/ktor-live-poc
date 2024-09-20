package nl.helicotech.ktor.live.lib.component

import kotlin.reflect.KProperty

abstract class LiveComponent(
    initialDataset: Map<String, Any> = emptyMap()
) {
    val dataset = initialDataset.mapValues { it.value.toString() }.toMutableMap()

    constructor(vararg data : Pair<String, Any>) : this(data.toMap().mapValues { it.value.toString() }.toMutableMap())

    abstract fun LIVECOMPONENTTAG.render()

    protected fun <T> data(default: T, transformer: StringTransformer<T>) = MapDelegate(dataset, default, transformer)

    protected fun data(default: Int) = data(default, Transformers.Int)
    protected fun data(default: String) = data(default, Transformers.String)
    protected fun data(default: Boolean) = data(default, Transformers.Boolean)
    protected fun data(default: Double) = data(default, Transformers.Double)
    protected fun data(default: Float) = data(default, Transformers.Float)

    interface Factory<T : LiveComponent> {
        val name: String
        fun create(attributes: Map<String, String> = emptyMap()): T
    }
}

class MapDelegate<T>(
    private val map: MutableMap<String, String>,
    private val default: T,
    private val transformer: StringTransformer<T>
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = map[property.name]?.let { transformer.fromString(it) } ?: default

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        map[property.name] = transformer.asString(value)
    }
}

interface StringTransformer<T> {
    fun fromString(value: String): T
    fun asString(value: T): String
}

object Transformers {
    val Int = object : StringTransformer<Int> {
        override fun fromString(value: String): Int = value.toInt()
        override fun asString(value: Int): String = value.toString()
    }
    val String = object : StringTransformer<String> {
        override fun fromString(value: String): String = value
        override fun asString(value: String): String = value
    }
    val Boolean = object : StringTransformer<Boolean> {
        override fun fromString(value: String): Boolean = value.toBoolean()
        override fun asString(value: Boolean): String = value.toString()
    }
    val Double = object : StringTransformer<Double> {
        override fun fromString(value: String): Double = value.toDouble()
        override fun asString(value: Double): String = value.toString()
    }
    val Float = object : StringTransformer<Float> {
        override fun fromString(value: String): Float = value.toFloat()
        override fun asString(value: Float): String = value.toString()
    }
}