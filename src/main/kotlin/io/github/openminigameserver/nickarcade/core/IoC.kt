package io.github.openminigameserver.nickarcade.core

import kotlin.reflect.KClass
import kotlin.reflect.KProperty

object IoC {
    val objects = mutableMapOf<KClass<*>, Any>()
    val namedObjectsMap = mutableMapOf<String, Any>()

    inline operator fun <reified T : Any> invoke(name: String? = null): T {
        return name.let { namedObjectsMap[name] as? T } ?: objects[T::class] as? T ?: throw Exception("Unable to get value of type ${T::class.java.name}")
    }

    inline operator fun <reified T: Any> plusAssign(value: Pair<String, T>) {
        namedObjectsMap[value.first] = value.second
    }

    inline operator fun <reified T: Any> plusAssign(value: T) {
        objects[T::class] = value
    }

    inline operator fun <reified T : Any> getValue(owner: Any, property: KProperty<*>): T {
        return invoke(property.name)
    }

    inline operator fun <reified T : Any> getValue(owner: Nothing?, property: KProperty<*>): T {
        return invoke(property.name)
    }
}