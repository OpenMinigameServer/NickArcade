package io.github.openminigameserver.nickarcade.core.events.data

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

abstract class PlayerDataEvent(val player: ArcadePlayer) : Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        @JvmStatic
        val handlerList = HandlerList()
    }
}