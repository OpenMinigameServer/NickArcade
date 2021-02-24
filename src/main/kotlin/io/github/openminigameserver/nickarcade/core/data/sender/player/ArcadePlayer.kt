package io.github.openminigameserver.nickarcade.core.data.sender.player

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.openminigameserver.hypixelapi.HypixelApi
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.hypixelapi.models.HypixelPlayer
import io.github.openminigameserver.hypixelapi.utis.MinecraftChatColor
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataTag
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor.getLastColors
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class ArcadePlayer(val data: ArcadePlayerData) : ArcadeSender(data.uuid) {

    override val audience: Audience
        get() = commandSender

    @get:JsonIgnore
    val player: Player?
        get() = Bukkit.getPlayer(uuid)

    //region Extra data

    private val extraData = mutableMapOf<String, Any?>()
    operator fun <T> contains(dataTag: ExtraDataTag<T>): Boolean {
        return get(dataTag) != null
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(dataTag: ExtraDataTag<T>): T? {
        return extraData[dataTag.tagName] as? T?
    }

    @JsonIgnore
    operator fun <T> set(dataTag: ExtraDataTag<T>, value: T) {
        if (value == null) {
            extraData.remove(dataTag.tagName)
            return
        }
        extraData[dataTag.tagName] = value
    }
    //endregion

    @get:JsonIgnore
    val isOnline: Boolean
        get() = player != null

    @get:JsonIgnore
    val effectivePrefix: String
        get() = computeEffectivePrefix() ?: ""

    private fun computeEffectivePrefix(actualData: Boolean = false): String? = with(data) {
        val playerOverrides = if (actualData) overrides else effectivePlayerOverrides()
        return playerOverrides.prefixOverride?.let { if (!it.endsWith(' ')) "$it " else it }
            ?: if (hypixelData != null)
                hypixelData?.let { hypixelPlayer: HypixelPlayer ->
                    if (playerOverrides.monthlyRankColorOverride != null || playerOverrides.rankPlusColorOverride != null) {
                        return@let (if (actualData) effectiveRank else effectiveDisplayRank).computePrefixForPlayer(
                            playerOverrides.rankPlusColorOverride ?: MinecraftChatColor.RED,
                            playerOverrides.monthlyRankColorOverride ?: MinecraftChatColor.GOLD
                        )
                    } else {
                        return@let playerOverrides.rankOverride?.computePrefixForPlayer(
                            hypixelPlayer
                        ) ?: hypixelData?.effectivePrefix
                    }
                } else null
    }

    private fun effectivePlayerOverrides() = with(data) { /*displayOverrides.overrides ?:*/ overrides }

    @get:JsonIgnore
    override val displayName: String
        get() = with(data) { /*displayOverrides.displayProfile?.name ?:*/ actualDisplayName }

    override val commandSender: CommandSender
        get() = player!!

    @get:JsonIgnore
    val actualDisplayName: String
        get() = with(data) { overrides.nameOverride ?: hypixelData?.displayName ?: "" }

    @get:JsonIgnore
    val effectiveRank: HypixelPackageRank
        get() = data.overrides.rankOverride ?: data.hypixelData?.effectiveRank ?: HypixelPackageRank.NONE

    @get:JsonIgnore
    val effectiveDisplayRank: HypixelPackageRank
        get() = effectivePlayerOverrides().rankOverride ?: effectiveRank

    @get:JsonIgnore
    val networkLevel: Long
        get() = effectivePlayerOverrides().networkLevel ?: data.hypixelData?.networkLevel ?: 1

    @JsonIgnore
    fun getChatName(): String = getChatName(false)

    @JsonIgnore
    fun getChatName(actualData: Boolean): String = getChatName(actualData, false)

    @JsonIgnore
    override fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String {
        var name = displayName
        var prefix = effectivePrefix
        if (actualData) {
            name = actualDisplayName
            prefix = computeEffectivePrefix(true) ?: effectivePrefix
        }

        if (colourPrefixOnly) {
            prefix = getLastColors(prefix)
        }

        return "$prefix$name"
    }

    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return actualData && hasAtLeastRank(rank) || hasAtLeastDisplayRank(rank)
    }

    private fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }

    private fun hasAtLeastDisplayRank(rank: HypixelPackageRank): Boolean {
        return effectiveDisplayRank.ordinal >= rank.ordinal
    }

    fun computeHoverEventComponent(actualData: Boolean = false): HoverEventSource<*> {
        return text {
            it.run {
                append(text(getChatName(actualData, false)))
                append(newline())
                append(text("Hypixel Level: ", NamedTextColor.GRAY))
                append(
                    text(
                        if (actualData) data.hypixelData?.networkLevel ?: 1 else networkLevel,
                        NamedTextColor.GOLD
                    )
                )
            }
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArcadePlayer

        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        return uuid.hashCode()
    }
}
/*
    TODO: Player games / party data
    @JsonIgnore
    fun getOrCreateParty(): Party {
        if (getCurrentParty() == null) {
            return PartyManager.createParty(this)
        }
        return getCurrentParty() as Party
    }

    @JsonIgnore
    fun getCurrentGame(): Game? {
        return MiniGameManager.getCurrentGame(this)
    }

    @JsonIgnore
    fun getCurrentParty(showPrompt: Boolean = false): Party? {
        return PartyManager.getParty(this).also {
            if (it == null && showPrompt) {
                audience.sendMessage(separator {
                    append(text("You are not currently in a party.", NamedTextColor.RED))
                })
            }
        }
    }

    @JsonIgnore
    fun setCurrentParty(party: Party?) {
        return PartyManager.setPlayerParty(this, party)
    }

*/
