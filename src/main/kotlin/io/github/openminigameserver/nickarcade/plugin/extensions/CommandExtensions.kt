package io.github.openminigameserver.nickarcade.plugin.extensions

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player

fun command(
    sender: ArcadeSender,
    requiredRank: HypixelPackageRank = HypixelPackageRank.NONE,
    block: suspend CoroutineScope.() -> Unit
) {
    runBlocking {
        val isPlayer = sender is Player
        val rank = requiredRank.name.toLowerCase()
        val requiresPermission = requiredRank != HypixelPackageRank.NONE
        val hasPermission = !isPlayer || sender.hasAtLeastRank(requiredRank, true)
        if (requiresPermission && !hasPermission) {
            sender.commandSender.sendMessage(Component.text("You must be $rank or higher to use this command!", NamedTextColor.RED))
            return@runBlocking
        }
        launch(block)
    }
}