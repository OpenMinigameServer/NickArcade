package io.github.openminigameserver.nickarcade.plugin.helper.coroutines

import io.github.openminigameserver.nickarcade.core.logger
import kotlinx.coroutines.*
import java.util.logging.Level
import kotlin.coroutines.CoroutineContext

object CoroutineSession {

    private val scope: CoroutineScope by lazy {
        CoroutineScope(BukkitCoroutineDispatcher)
    }

    fun launch(dispatcher: CoroutineContext, f: suspend CoroutineScope.() -> Unit): Job {

        if (dispatcher == Dispatchers.Unconfined) {
            // If the dispatcher is unconfined. Always schedule immediately.
            return launchInternal(dispatcher, CoroutineStart.UNDISPATCHED, f)
        }

        return launchInternal(dispatcher, CoroutineStart.DEFAULT, f)
    }

    /**
     * Executes the launch
     */
    private fun launchInternal(
        dispatcher: CoroutineContext,
        coroutineStart: CoroutineStart,
        f: suspend CoroutineScope.() -> Unit
    ): Job {
        // Launch a new coroutine on the current thread thread on the plugin scope.
        return scope.launch(dispatcher, coroutineStart) {
            try {
                // The user may or may not launch multiple sub suspension operations. If
                // one of those fails, only this scope should fail instead of the plugin scope.
                coroutineScope {
                    f.invoke(this)
                }
            } catch (e: CancellationException) {
                logger.info("Coroutine has been cancelled.")
            } catch (e: Exception) {
                logger.log(
                    Level.SEVERE,
                    "This is not an error of NickArcade's coroutines! See sub exception for details.",
                    e
                )
            }
        }
    }
}