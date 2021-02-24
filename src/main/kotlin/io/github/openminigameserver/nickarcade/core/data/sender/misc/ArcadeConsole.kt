package io.github.openminigameserver.nickarcade.core.data.sender.misc

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.util.*

object ArcadeConsole : ArcadeSender(UUID(0, 0)) {
    override val displayName: String
        get() = "Server Console"

    override val commandSender: CommandSender
        get() = Bukkit.getConsoleSender()

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return true
    }

    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String {
        return displayName
    }
}