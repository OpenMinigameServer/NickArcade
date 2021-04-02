package io.github.openminigameserver.nickarcade.core.events.data

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.event.Event
import org.bukkit.event.HandlerList
import org.bukkit.event.WorldProvider

abstract class PlayerDataEvent(val player: ArcadePlayer) : Event(), WorldProvider {

    override fun getWorld(): World {
        return player.player?.world ?: Bukkit.getWorlds().first()
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}