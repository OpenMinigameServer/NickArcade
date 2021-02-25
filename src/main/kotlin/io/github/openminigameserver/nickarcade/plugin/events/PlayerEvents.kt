package io.github.openminigameserver.nickarcade.plugin.events

import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataJoinEvent
import io.github.openminigameserver.nickarcade.core.events.data.PlayerDataLeaveEvent
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataManager
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.core.ticks
import io.github.openminigameserver.nickarcade.plugin.extensions.async
import io.github.openminigameserver.nickarcade.plugin.extensions.event
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.event.player.AsyncPlayerPreLoginEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent

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
    /* TODO: Invite
                if (!InviteManager.hasPlayerReceivedInvite(playerUuid)) {
                    player.kick(MinestomComponentSerializer.get().serialize(Component.text {
                        it.append(
                            Component.text("You are not allowed to join this server!", NamedTextColor.RED)
                                .append(Component.newline())
                        )
                        it.append(Component.newline())
                        it.append(
                            Component.text("Reason: ", NamedTextColor.GRAY).append(
                                Component.text(
                                    "You have not received an invite to play on this server.",
                                    NamedTextColor.WHITE
                                )
                            ).append(Component.newline())
                        )
                    }))
                    return@event
                }
    */

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