package io.github.openminigameserver.nickarcade.core.data.sender

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataValue
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import java.util.*

abstract class ArcadeSender(@JsonProperty("_id") val uuid: UUID) {

    open val audience: Audience
        get() = commandSender

    abstract val extraData: MutableMap<String, ExtraDataValue>

    abstract val displayName: String

    abstract val commandSender: CommandSender

    abstract fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean = false): Boolean

    abstract fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String
}