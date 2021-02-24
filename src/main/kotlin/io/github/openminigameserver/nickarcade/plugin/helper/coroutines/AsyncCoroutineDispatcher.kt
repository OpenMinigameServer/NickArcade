package io.github.openminigameserver.nickarcade.plugin.helper.coroutines

import io.github.openminigameserver.nickarcade.core.IoC
import io.github.openminigameserver.nickarcade.plugin.NickArcadePlugin
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import kotlin.coroutines.CoroutineContext

object AsyncCoroutineDispatcher : CoroutineDispatcher() {
    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(IoC<NickArcadePlugin>(), block)
    }
}

