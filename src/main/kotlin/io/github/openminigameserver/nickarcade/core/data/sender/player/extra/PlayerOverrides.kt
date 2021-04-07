package io.github.openminigameserver.nickarcade.core.data.sender.player.extra

import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.hypixelapi.utis.MinecraftChatColor

data class PlayerOverrides(
    var rankOverride: HypixelPackageRank? = null,
    var prefixOverride: String? = null,
    var monthlyRankColorOverride: MinecraftChatColor? = null,
    var rankPlusColorOverride: MinecraftChatColor? = null,
    var networkLevel: Long? = null,
    var isLegacyPlayer: Boolean? = null,
    var nameOverride: String? = null
)