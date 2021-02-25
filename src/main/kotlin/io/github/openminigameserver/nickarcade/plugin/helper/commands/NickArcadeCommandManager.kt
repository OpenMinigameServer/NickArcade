package io.github.openminigameserver.nickarcade.plugin.helper.commands

import cloud.commandframework.CommandTree
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import cloud.commandframework.permission.CommandPermission
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.plugin.extensions.pluginInstance
import io.github.openminigameserver.nickarcade.plugin.helper.commands.parsers.PlayerDataParser
import io.leangen.geantyref.TypeToken
import org.bukkit.command.CommandSender
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.function.Function

class NickArcadeCommandManager<C : ArcadeSender>(
    commandExecutionCoordinator: @NonNull Function<@NonNull CommandTree<C>, @NonNull CommandExecutionCoordinator<C>>,
    commandSenderMapper: Function<CommandSender, C>, backwardsCommandSenderMapper: Function<C, CommandSender>
) : PaperCommandManager<C>(pluginInstance, commandExecutionCoordinator, commandSenderMapper, backwardsCommandSenderMapper) {
    init {
        registerPlayerDataParser()
    }

    private fun registerPlayerDataParser() {
        parserRegistry.registerParserSupplier(TypeToken.get(ArcadePlayer::class.java)) { PlayerDataParser() }
    }

    override fun hasPermission(sender: C, permission: CommandPermission): Boolean {
        if (permission is HypixelPackageRankPermission) {
            return sender.hasAtLeastRank(permission.rank, true)
        }
        return super.hasPermission(sender, permission)
    }
}