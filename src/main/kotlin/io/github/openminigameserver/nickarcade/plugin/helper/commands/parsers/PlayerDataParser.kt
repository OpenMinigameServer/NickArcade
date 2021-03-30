package io.github.openminigameserver.nickarcade.plugin.helper.commands.parsers

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import io.github.openminigameserver.hypixelapi.models.HypixelPlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayerData
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.PlayerOverrides
import io.github.openminigameserver.nickarcade.core.div
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataManager
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Bukkit.getPlayer
import org.litote.kmongo.eq
import org.litote.kmongo.include
import org.litote.kmongo.or
import java.util.*

class PlayerDataParser<C> : ArgumentParser<C, ArcadePlayer> {
    var isCachedWhenLoaded = true

    override fun parse(commandContext: CommandContext<C>, queue: Queue<String>): ArgumentParseResult<ArcadePlayer> {
        return runBlocking {
            val argument: String? = queue.peek()
            if (argument != null) {
                queue.remove()

                //Try a name
                var data = getPlayer(argument)?.getArcadeSender()
/*
                if (data != null && data.displayOverrides.displayProfile != null && data.displayOverrides.displayProfile?.displayName == argument) {
                    //Do not expose nicked players
                    data = null
                }
*/

                //Try a UUID
                if (data == null) {
                    data = kotlin.runCatching { getPlayer(UUID.fromString(argument)) }.getOrNull()
                        ?.getArcadeSender()
                }

                //Try finding from the displayName of a PlayerData
                if (data == null) {
                    val displayNameOverride = ArcadePlayerData::overrides / PlayerOverrides::nameOverride
                    val displayName = ArcadePlayerData::rawHypixelData / HypixelPlayer::displayName
                    val foundPlayer = PlayerDataManager.playerDataCollection.findOne(
                        or(
                            displayNameOverride eq argument,
                            displayName eq argument
                        )
                    )
                    if (foundPlayer != null) {
                        data = ArcadePlayer(foundPlayer)
                    }

                }
                if (data != null) {
                    if (PlayerDataManager.isPlayerDataLoaded(data.uuid)) {
                        //Use the player data we already have loaded
                        data = PlayerDataManager.getPlayerData(data.uuid, data.actualDisplayName)
                    } else if (isCachedWhenLoaded) {
                        //Store in memory first, then return.
                        PlayerDataManager.storeInMemory(data)
                    }

                    return@runBlocking ArgumentParseResult.success(data)
                }
                return@runBlocking ArgumentParseResult.failure(
                    Exception(
                        argument,

                        )
                )
            }
            return@runBlocking ArgumentParseResult.failure<ArcadePlayer>(
                Exception(
                    "Unable to find player named '$argument'"
                )
            )
        }
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): MutableList<String> {
        return runBlocking {
            val displayName = ArcadePlayerData::rawHypixelData / HypixelPlayer::displayName
            val displayNameOverride = ArcadePlayerData::overrides / PlayerOverrides::nameOverride
            val allElements =
                PlayerDataManager.playerDataCollection.find()
                    .projection(include(ArcadePlayer::uuid, displayName, displayNameOverride))
                    .toList()

            return@runBlocking (allElements.mapNotNull {
                it.hypixelData?.displayName
            } + getOnlinePlayers().map { it.getArcadeSender() }.map { it.actualDisplayName }).distinct()
                .toMutableList()
        }
    }

}