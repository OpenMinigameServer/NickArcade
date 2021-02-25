package io.github.openminigameserver.nickarcade.plugin.helper.commands

import cloud.commandframework.permission.CommandPermission
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import java.util.*

class HypixelPackageRankPermission(val rank: HypixelPackageRank) : CommandPermission {
    override fun getPermissions(): Collection<CommandPermission> {
        return Collections.singletonList(this)
    }
}