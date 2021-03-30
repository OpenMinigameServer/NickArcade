package io.github.openminigameserver.nickarcade.core.data.sender

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataValue
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.RuntimeExtraDataTag
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.TimedObject
import kotlinx.datetime.Clock
import net.kyori.adventure.audience.Audience
import org.bukkit.command.CommandSender
import java.util.*
import kotlin.time.Duration

abstract class ArcadeSender(@JsonProperty("_id") val uuid: UUID) {

    open val audience: Audience
        get() = commandSender

    abstract val extraData: MutableMap<String, ExtraDataValue>

    abstract val displayName: String

    abstract val commandSender: CommandSender

    abstract fun hasAtLeastRank(rank: HypixelPackageRank, actualData: Boolean = false): Boolean

    abstract fun getChatName(actualData: Boolean, colourPrefixOnly: Boolean): String

    //region Extra data

    private val runtimeExtraData = mutableMapOf<String, Any?>()
    operator fun <T> contains(dataTag: RuntimeExtraDataTag<T>): Boolean {
        return get(dataTag) != null
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T> get(dataTag: RuntimeExtraDataTag<T>): T? {
        val result = runtimeExtraData[dataTag.tagName]
        if (result is TimedObject<*>) {
            return if (Clock.System.now() <= (result.storedTime + dataTag.expirationDuration)) {
                result.value as? T?
            } else {
                runtimeExtraData.remove(dataTag.tagName)
                null
            }
        }
        return result as? T?
    }


    operator fun <T> set(dataTag: RuntimeExtraDataTag<T>, value: T?) {
        if (value == null) {
            runtimeExtraData.remove(dataTag.tagName)
            return
        }
        if (dataTag.expirationDuration > Duration.ZERO) {
            runtimeExtraData[dataTag.tagName] = TimedObject(value)
        } else {
            runtimeExtraData[dataTag.tagName] = value
        }
    }
    //endregion

}