package io.github.openminigameserver.nickarcade.plugin.extensions

import cloud.commandframework.Command
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank

fun <C> Command.Builder<C>.permission(rank: HypixelPackageRank): Command.Builder<C> {
    return this.permission(rank.name.toLowerCase())
}