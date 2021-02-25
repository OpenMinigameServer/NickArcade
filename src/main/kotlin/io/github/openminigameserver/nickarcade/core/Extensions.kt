package io.github.openminigameserver.nickarcade.core

import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toCollection
import net.kyori.adventure.audience.Audience
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.litote.kmongo.property.KPropertyPath
import kotlin.reflect.KProperty1
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds


/**
 * Creates an audience based on a viewer predicate.
 *
 * @param predicate a predicate
 * @return an audience
 * @since 4.0.0
 */
suspend fun Audience.filterSuspend(predicate: suspend (CommandSender) -> Boolean): Audience {
    val list = Bukkit.getOnlinePlayers() + Bukkit.getConsoleSender()
    return Audience.audience(list.asFlow().filter(predicate).toCollection(mutableListOf()))
}

/** Returns a [Duration] equal to this [Int] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Int.ticks
    get() = (this * 50).milliseconds

/** Returns a [Duration] equal to this [Long] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Long.ticks
    get() = (this * 50L).milliseconds

/** Returns a [Duration] equal to this [Double] number of ticks. */
@SinceKotlin("1.3")
@ExperimentalTime
val Double.ticks
    get() = (this * 50.0).milliseconds

operator fun <T0, T1, T2, T3> KProperty1<T0, T1?>.div(p2: KProperty1<T2, T3?>): KProperty1<T0, T3?> =
    @Suppress("INVISIBLE_MEMBER")
    (KPropertyPath(this, p2))