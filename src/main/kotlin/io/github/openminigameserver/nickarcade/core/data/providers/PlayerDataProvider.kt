package io.github.openminigameserver.nickarcade.core.data.providers

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.PlayerOverrides

interface PlayerDataProvider {
    fun provideOverrides(player: ArcadePlayer): PlayerOverrides?

    fun provideDisplayName(player: ArcadePlayer): String?
}