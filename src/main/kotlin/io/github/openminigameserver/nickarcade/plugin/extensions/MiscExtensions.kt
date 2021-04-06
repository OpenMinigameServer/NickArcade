package io.github.openminigameserver.nickarcade.plugin.extensions

import cloud.commandframework.Command
import cloud.commandframework.kotlin.MutableCommandBuilder
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.plugin.helper.commands.HypixelPackageRankPermission

fun <C> Command.Builder<C>.permission(rank: HypixelPackageRank): Command.Builder<C> {
    return this.permission(HypixelPackageRankPermission(rank))
}

fun <C : Any> MutableCommandBuilder<C>.permission(rank: HypixelPackageRank): MutableCommandBuilder<C> {
    return this.permission(HypixelPackageRankPermission(rank))
}