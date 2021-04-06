package io.github.openminigameserver.nickarcade.plugin.helper.commands

import cloud.commandframework.CommandTree
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator
import cloud.commandframework.execution.CommandExecutionCoordinator
import cloud.commandframework.paper.PaperCommandManager
import cloud.commandframework.permission.CommandPermission
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import io.github.openminigameserver.nickarcade.plugin.extensions.pluginInstance
import io.github.openminigameserver.nickarcade.plugin.helper.commands.parsers.PlayerDataParser
import io.leangen.geantyref.TypeToken
import kotlinx.coroutines.runBlocking
import org.bukkit.command.CommandSender
import org.checkerframework.checker.nullness.qual.NonNull
import java.util.function.Function

open class NickArcadeCommandManager<C : ArcadeSender>(
    commandExecutionCoordinator: @NonNull Function<@NonNull CommandTree<C>, @NonNull CommandExecutionCoordinator<C>> = AsynchronousCommandExecutionCoordinator.newBuilder<C>()
        .withAsynchronousParsing().withExecutor { launch { it.run() } }.build(),
    commandSenderMapper: Function<CommandSender, C> = Function {
        runBlocking { it.getArcadeSender() as C }
    },
    backwardsCommandSenderMapper: Function<C, CommandSender> = Function { it.commandSender }
) : PaperCommandManager<C>(
    pluginInstance,
    commandExecutionCoordinator,
    commandSenderMapper,
    backwardsCommandSenderMapper
) {
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