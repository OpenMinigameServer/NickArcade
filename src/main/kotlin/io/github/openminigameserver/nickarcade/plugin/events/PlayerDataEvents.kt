package io.github.openminigameserver.nickarcade.plugin.events

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.hypixelapi.utis.MinecraftChatColor.*
import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataJoinEvent
import io.github.openminigameserver.nickarcade.plugin.extensions.event
import net.kyori.adventure.text.Component.text
import org.bukkit.Bukkit

object PlayerDataEvents {
    fun registerHandlers() {
        event<PlayerDataJoinEvent>(forceBlocking = true) {
            showLobbyMessage()
        }
    }

    private fun PlayerDataJoinEvent.showLobbyMessage() {
        val superStarColors = listOf(BLUE, RED, GREEN)
        val joinPrefix =
            if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${superStarColors.joinToString("") { "$it>" }} " else ""
        val joinSuffix = if (player.hasAtLeastDisplayRank(HypixelPackageRank.SUPERSTAR)) " ${
            superStarColors.reversed().joinToString("") { "$it<" }
        } " else ""
        if (player.hasAtLeastDisplayRank(HypixelPackageRank.MVP_PLUS)) {
            Bukkit.getServer().sendMessage(
                text("$joinPrefix${player.getChatName()}§6 joined the lobby!$joinSuffix")
                    .hoverEvent(player.computeHoverEventComponent())
            )
        }
    }
}