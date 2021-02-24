package io.github.openminigameserver.nickarcade.plugin.helper.coroutines

import io.github.openminigameserver.nickarcade.core.IoC
import io.github.openminigameserver.nickarcade.plugin.NickArcadePlugin
import kotlinx.coroutines.CoroutineDispatcher
import org.bukkit.Bukkit
import kotlin.coroutines.CoroutineContext

object BukkitCoroutineDispatcher : CoroutineDispatcher() {
    /**
     * Handles dispatching the coroutine on the correct thread.
     */
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        Bukkit.getScheduler().runTask(IoC<NickArcadePlugin>(), block)
    }
}