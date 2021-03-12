package io.github.openminigameserver.nickarcade.core.data.sender.player

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.hypixelapi.models.HypixelPlayer
import io.github.openminigameserver.hypixelapi.utis.MinecraftChatColor
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataValue
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.RuntimeExtraDataTag
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataProviderManager
import net.kyori.adventure.audience.Audience
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.HoverEventSource
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor.getLastColors
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.time.Duration

class ArcadePlayer(val data: ArcadePlayerData) : ArcadeSender(data.uuid) {

    suspend fun coolDown(name: String, cooldownDuration: Duration, code: (suspend () -> Unit)? = null): Boolean {
        val lastUse = data.cooldowns[name]
        val finishTime = lastUse?.plus(cooldownDuration.inMilliseconds)
        return if (finishTime == null || System.currentTimeMillis() > finishTime) {
            data.cooldowns[name] = System.currentTimeMillis()
            code?.invoke()
            true
        } else false
    }

    override val audience: Audience
        get() = commandSender


    val player: Player?
        get() = Bukkit.getPlayer(uuid)

    //region Extra data

    override val extraData: MutableMap<String, ExtraDataValue>
        get() = data.extraData

    private val runtimeExtraData = mutableMapOf<String, Any?>()
    operator fun <T> contains(dataTag: RuntimeExtraDataTag<T>): Boolean {
        return get(dataTag) != null
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(dataTag: RuntimeExtraDataTag<T>): T? {
        return runtimeExtraData[dataTag.tagName] as? T?
    }


    operator fun <T> set(dataTag: RuntimeExtraDataTag<T>, value: T?) {
        if (value == null) {
            runtimeExtraData.remove(dataTag.tagName)
            return
        }
        runtimeExtraData[dataTag.tagName] = value
    }
    //endregion


    val isOnline: Boolean
        get() = player != null


    val effectivePrefix: String
        get() = computeEffectivePrefix() ?: ""

    fun computeEffectivePrefix(actualData: Boolean = false): String? = with(data) {
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

    private val playerDataProvider get() = PlayerDataProviderManager.getPlayerDataProvider()

    private fun effectivePlayerOverrides() = playerDataProvider?.provideOverrides(this) ?: data.overrides

    override val displayName: String
        get() {
            return playerDataProvider?.provideDisplayName(this) ?: actualDisplayName
        }

    override val commandSender: CommandSender
        get() = player!!


    val actualDisplayName: String
        get() = with(data) { overrides.nameOverride ?: hypixelData?.displayName ?: "" }


    val effectiveRank: HypixelPackageRank
        get() = data.overrides.rankOverride ?: data.hypixelData?.effectiveRank ?: HypixelPackageRank.NONE


    val effectiveDisplayRank: HypixelPackageRank
        get() = effectivePlayerOverrides().rankOverride ?: effectiveRank


    val networkLevel: Long
        get() = effectivePlayerOverrides().networkLevel ?: data.hypixelData?.networkLevel ?: 1

    val actualNetworkLevel: Long
        get() = data.overrides.networkLevel ?: data.hypixelData?.networkLevel ?: 1

    fun getChatName(): String = getChatName(false)

    fun getChatName(actualData: Boolean): String = getChatName(actualData, false)

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

    //region Rank
    override fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean): Boolean {
        return actualData && hasAtLeastRank(rank) || hasAtLeastDisplayRank(rank)
    }

    private fun hasAtLeastRank(rank: HypixelPackageRank): Boolean {
        return effectiveRank.ordinal >= rank.ordinal
    }

    fun hasAtLeastDisplayRank(rank: HypixelPackageRank): Boolean {
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
    //endregion


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

    fun getOrCreateParty(): Party {
        if (getCurrentParty() == null) {
            return PartyManager.createParty(this)
        }
        return getCurrentParty() as Party
    }


    fun getCurrentGame(): Game? {
        return MiniGameManager.getCurrentGame(this)
    }


    fun getCurrentParty(showPrompt: Boolean = false): Party? {
        return PartyManager.getParty(this).also {
            if (it == null && showPrompt) {
                audience.sendMessage(separator {
                    append(text("You are not currently in a party.", NamedTextColor.RED))
                })
            }
        }
    }


    fun setCurrentParty(party: Party?) {
        return PartyManager.setPlayerParty(this, party)
    }

*/
