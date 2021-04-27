package io.github.openminigameserver.nickarcade.plugin.events

import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataJoinEvent
import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataLeaveEvent
import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataReloadEvent
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataManager
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.core.ticks
import io.github.openminigameserver.nickarcade.core.ui.ItemActionHelper
import io.github.openminigameserver.nickarcade.plugin.extensions.async
import io.github.openminigameserver.nickarcade.plugin.extensions.event
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.player.*

object PlayerEvents {

    private fun PlayerLoginEvent.sendPlayerDataActionBar() {
        launch {
            val audience = player
            while (!PlayerDataManager.isPlayerDataLoaded(player.uniqueId)) {
                audience.sendActionBar(
                    text(
                        "Fetching player data from Hypixel, please wait!",
                        NamedTextColor.RED,
                        TextDecoration.BOLD
                    )
                )
                delay(5.ticks)
            }
            audience.sendActionBar(
                text(
                    "Player data fetched from Hypixel! Have a nice stay.",
                    NamedTextColor.GREEN
                )
            )
        }
    }

    private val validNamePattern = Regex("^[a-zA-Z0-9_]{3,16}\$")
    fun registerHandlers() {
        registerJoinEvent()
        registerPreLoginEvent()
        registerLoginEvent()
        registerQuitEvent()
        registerItemActionEvent()
    }

    private fun registerItemActionEvent() {
        event<PlayerDropItemEvent>(forceBlocking = true) {
            if (ItemActionHelper.getItemAction(itemDrop.itemStack) != null) {
                isCancelled = true
            }
        }
        event<PlayerInteractEvent>(forceBlocking = true) {
            if (this.hasItem() && this.item != null && this.item?.hasItemMeta() == true) {
                isCancelled = true
                launchAsync {
                    ItemActionHelper.executeAction(this@event.item!!.itemMeta, player.getArcadeSender())
                }
            }
        }
    }

    private fun registerJoinEvent() {
        event<PlayerJoinEvent>(forceBlocking = true) {
            joinMessage(null)
        }
    }

    private fun registerLoginEvent() {
        event<PlayerLoginEvent> {
            launch { sendPlayerDataActionBar() }

            val playerData = async {
                player.getArcadeSender()
            }

            PlayerDataJoinEvent(playerData).callEvent()
            PlayerDataReloadEvent(playerData).callEvent()
        }
    }

    private fun registerQuitEvent() {
        event<PlayerQuitEvent> {
            PlayerDataLeaveEvent(player.getArcadeSender()).callEvent()
            PlayerDataManager.saveAndRemovePlayerData(player.uniqueId)
        }
    }

    private fun registerPreLoginEvent() {
        event<AsyncPlayerPreLoginEvent>(forceBlocking = true) {

            val isValidName = validNamePattern.matchEntire(name) != null

            if (!isValidName) {
                this.kickMessage(text("You are using an invalid Minecraft name and thus you got denied access."))
                this.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_BANNED
            } else if (PlayerDataManager.isPlayerDataLoaded(uniqueId)) {
                PlayerDataManager.saveAndRemovePlayerData(uniqueId)
                kickMessage(
                    text(
                        "Please wait while we save your previous session.",
                        NamedTextColor.YELLOW
                    )
                )
                this.loginResult = AsyncPlayerPreLoginEvent.Result.KICK_OTHER
            }

        }
    }
}