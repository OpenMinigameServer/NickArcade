package io.github.openminigameserver.nickarcade.plugin.extensions

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import cloud.commandframework.annotations.Hidden
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import org.bukkit.entity.Player
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.*


fun Component.clickEvent(handler: suspend Player.() -> Unit): @NonNull Component {
    val id = ComponentExtensions.generateClickEvent(handler)
    return this.clickEvent(ClickEvent.runCommand("/nickarcade internal $id"))
}

internal object ComponentExtensions {
    private val clickEvents = mutableMapOf<UUID, suspend (Player) -> Unit>()

    fun generateClickEvent(handler: suspend (Player) -> Unit): UUID {
        val id = UUID.randomUUID()
        clickEvents[id] = handler
        return id
    }

    @Hidden
    @CommandMethod("nickarcade internal <id>")
    fun clickEventExecutor(player: ArcadePlayer, @Argument("id") id: UUID) {
        val event = clickEvents[id] ?: return
        clickEvents.remove(id)
        command(player) {
            player.player?.let { event(it) }
        }
    }

}