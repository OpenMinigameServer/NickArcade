package io.github.openminigameserver.nickarcade.plugin

import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.AsyncCoroutineDispatcher
import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.BukkitCoroutineDispatcher
import io.github.openminigameserver.nickarcade.plugin.helper.coroutines.CoroutineSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import org.bukkit.event.Event
import org.bukkit.event.EventException
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
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
    ignoreCancelled: Boolean = false, forceAsync: Boolean = false,
    noinline code: suspend T.(CoroutineScope) -> Unit
) {
    pluginInstance.server.pluginManager.registerEvent(
        T::class.java as Class<out Event>, object : Listener {}, eventPriority,
        { _, event ->
            if (!T::class.java.isInstance(event)) return@registerEvent
            try {
                val isAsync = forceAsync || event.isAsynchronous
                if (isAsync) {
                    launchAsync { code(event as T, this) }
                } else {
                    launch { code(event as T, this) }
                }
            } catch (var4: InvocationTargetException) {
                throw EventException(var4.cause)
            } catch (var5: Throwable) {
                throw EventException(var5)
            }
        },
        pluginInstance, ignoreCancelled
    )
}
