package io.github.openminigameserver.nickarcade.core.data.sender.misc

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataValue
import io.github.openminigameserver.nickarcade.core.manager.consoleData
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import java.util.*

object ArcadeWatcherSender : ArcadeSender(UUID.randomUUID()) {
    var messageSender: ((Component) -> Unit)? = null

    fun sendMessage(message: Component) {
        messageSender?.invoke(message)
    }

    override val audience: Audience = Audience.empty()
    override val extraData: MutableMap<String, ExtraDataValue> = mutableMapOf()
    override val displayName: String = "NickArcade"

    override val commandSender: CommandSender
        get() = consoleData.commandSender

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean = true

    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String {
        return "${ChatColor.RED}$displayName Watcher"
    }
}