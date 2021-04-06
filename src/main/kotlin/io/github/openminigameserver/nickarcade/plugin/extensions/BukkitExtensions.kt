package io.github.openminigameserver.nickarcade.plugin.extensions

import io.github.openminigameserver.nickarcade.plugin.NickArcadePlugin
import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.AsyncCoroutineDispatcher
import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.BukkitCoroutineDispatcher
import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.CoroutineSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.bukkit.World
import org.bukkit.event.*
import org.bukkit.plugin.EventExecutor
import java.lang.reflect.InvocationTargetException

val pluginInstance: NickArcadePlugin
    get() = NickArcadePlugin.INSTANCE

fun launch(block: suspend CoroutineScope.() -> Unit) {
    CoroutineSession.launch(BukkitCoroutineDispatcher, block)
}

fun launchAsync(block: suspend CoroutineScope.() -> Unit) {
    CoroutineSession.launch(AsyncCoroutineDispatcher, block)
}

suspend inline fun <T> async(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(AsyncCoroutineDispatcher, block)

suspend inline fun <T> sync(noinline block: suspend CoroutineScope.() -> T): T =
    withContext(BukkitCoroutineDispatcher, block)

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Event> event(
    eventPriority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false, forceAsync: Boolean = false, forceBlocking: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
) {
    pluginInstance.server.pluginManager.registerEvent(
        T::class.java as Class<out Event>, object : Listener {}, eventPriority,
        computeEventExecutor(forceAsync, forceBlocking, code),
        pluginInstance, ignoreCancelled
    )
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T> worldBoundEvent(
    world: World,
    eventPriority: EventPriority = EventPriority.NORMAL,
    ignoreCancelled: Boolean = false, forceAsync: Boolean = false, forceBlocking: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
)
        where T : Event,
              T : WorldProvider {
    pluginInstance.server.pluginManager.registerEvent(
        T::class.java as Class<out Event>, object : Listener {}, eventPriority,
        computeEventExecutor(forceAsync, forceBlocking, code),
        pluginInstance, ignoreCancelled,
        world
    )
}

inline fun <reified T> computeEventExecutor(
    forceAsync: Boolean,
    forceBlocking: Boolean,
    crossinline code: suspend T.(CoroutineScope) -> Unit
) where T : Event = EventExecutor { _, event: Event ->
    if (!T::class.java.isInstance(event)) return@EventExecutor
    if (event !is T) return@EventExecutor
    try {
        val isAsync = forceAsync || event.isAsynchronous
        when {
            forceBlocking -> {
                runBlocking { code(event, this) }
            }
            isAsync -> {
                launchAsync { code(event, this) }
            }
            else -> {
                launch { code(event, this) }
            }
        }
    } catch (var4: InvocationTargetException) {
        throw EventException(var4.cause)
    } catch (var5: Throwable) {
        throw EventException(var5)
    }
}

