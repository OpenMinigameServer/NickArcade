package io.github.openminigameserver.nickarcade.core

import kotlin.reflect.KClass

object IoC {
    val objects = mutableMapOf<KClass<*>, Any>()

    inline operator fun <reified T : Any> invoke(): T {
        return objects[T::class] as? T ?: throw Exception("Unable to get value of type ${T::class.java.name}")
    }

    inline operator fun <reified T: Any> plusAssign(value: T) {
        objects[T::class] = value
    }
}