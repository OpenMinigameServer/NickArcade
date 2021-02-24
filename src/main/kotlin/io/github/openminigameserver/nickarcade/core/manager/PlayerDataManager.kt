package io.github.nickacpt.nickarcade.data.player

import com.mongodb.client.model.UpdateOptions
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.hypixelapi.models.HypixelPlayer
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.misc.ArcadeConsole
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayerData
import io.github.openminigameserver.nickarcade.core.database
import io.github.openminigameserver.nickarcade.core.hypixelPlayerInfoHelper
import io.github.openminigameserver.nickarcade.core.logger
import kotlinx.datetime.Clock
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.hours
import kotlin.time.measureTimedValue

object PlayerDataManager {
    private val loadedPlayerMap = ConcurrentHashMap<UUID, ArcadePlayer>()

    fun isPlayerDataLoaded(id: UUID): Boolean {
        return loadedPlayerMap.containsKey(id)
    }

    private suspend fun createPlayerDataFromHypixel(id: UUID, name: String): ArcadePlayer {
        return ArcadePlayer(ArcadePlayerData(id, fetchHypixelPlayerData(id, name))).also {
            if (it.effectiveRank >= HypixelPackageRank.HELPER) {
                it.data.overrides.rankOverride = HypixelPackageRank.MVP_PLUS
            }
        }
    }

    private suspend fun fetchHypixelPlayerData(id: UUID, name: String): HypixelPlayer {
        val tryGetPlayerById = hypixelPlayerInfoHelper.tryGetPlayerById(id)

        return if (tryGetPlayerById != null) {
            logger.info("Fetched Hypixel Player Data for $name [$id] successfully.")
            tryGetPlayerById
        } else {
            logger.info("Unable to fetch Hypixel Player Data for $name [$id].")
            HypixelPlayer(
                name.toLowerCase(),
                name,
                networkExp = 0.0
            )
        }
    }

    suspend fun getPlayerData(uniqueId: UUID, name: String): ArcadePlayer {
        return if (loadedPlayerMap[uniqueId] != null) {
            loadedPlayerMap[uniqueId]!!
        } else {
            logger.info("Unable to find cached player data for $name [$uniqueId]. Fetching from MongoDb or Hypixel.")
            val playerData = loadPlayerDataFromMongoDb(uniqueId) ?: createPlayerDataFromHypixel(uniqueId, name)
            playerData.also {
                loadedPlayerMap[uniqueId] = it
            }
        }
    }

    private val refreshTime = 24.hours
    private suspend fun loadPlayerDataFromMongoDb(uniqueId: UUID) = playerDataCollection.findOneById(uniqueId)?.also {
        if ((Clock.System.now() - it.lastProfileUpdate) >= refreshTime) {
            val user = "${it.hypixelData?.displayName ?: "Unknown name"} [${it.uuid}]"
            println("Updating user $user due to profile being too old.")
            val (value, duration) = measureTimedValue {
                fetchHypixelPlayerData(it.uuid, it.hypixelData?.displayName!!)
            }
            it.hypixelData = value
            it.updateHypixelData(false)
            it.lastProfileUpdate = Clock.System.now()
            savePlayerData(it)
            println("Updated user $user successfully (Took ${duration}).")
        }
    }?.let { ArcadePlayer(it) }

    val playerDataCollection by lazy {
        database.getCollection<ArcadePlayerData>("players")
    }

    suspend fun saveAndRemovePlayerData(uuid: UUID) {
        loadedPlayerMap[uuid]?.also {
            savePlayerData(it.data)
            loadedPlayerMap.remove(uuid)
        }
    }

    fun removePlayerData(uuid: UUID) {
        val id = uuid
        loadedPlayerMap[id]?.also {
            loadedPlayerMap.remove(id)
        }
    }

    suspend fun savePlayerData(it: ArcadeSender) {
        //Don't save Console Player Data
        if (it == consoleData || it !is ArcadePlayer) return


        logger.info("Saving player data for ${it.displayName} [${it.uuid}]")
        savePlayerData(it.data)
    }

    private suspend fun savePlayerData(data: ArcadePlayerData) {
        playerDataCollection.updateOneById(data.uuid, data, UpdateOptions().upsert(true))
    }

    fun storeInMemory(data: ArcadePlayer) {
        loadedPlayerMap[data.uuid] = data
    }
}

val consoleData = ArcadeConsole

suspend fun Player.getArcadeSender(): ArcadePlayer = (this as CommandSender).getArcadeSender() as ArcadePlayer

suspend fun CommandSender.getArcadeSender(): ArcadeSender {
    if (this !is Player) {
        return consoleData
    }
    return PlayerDataManager.getPlayerData(uniqueId, name)
}
