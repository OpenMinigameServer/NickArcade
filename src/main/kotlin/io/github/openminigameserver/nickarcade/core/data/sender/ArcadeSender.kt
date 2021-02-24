package io.github.openminigameserver.nickarcade.core.data.sender

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import java.util.*

abstract class ArcadeSender(@JsonProperty("_id") val uuid: UUID) {

    @get:JsonIgnore
    open val audience: Audience
        get() = commandSender

    @get:JsonIgnore
    abstract val displayName: String

    @get:JsonIgnore
    abstract val commandSender: CommandSender

    abstract fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean = false): Boolean

    @JsonIgnore
    abstract fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String
}