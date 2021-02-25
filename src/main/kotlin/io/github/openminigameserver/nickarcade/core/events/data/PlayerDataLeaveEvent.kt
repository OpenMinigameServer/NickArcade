package io.github.openminigameserver.nickarcade.core.events.data

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer

class PlayerDataLeaveEvent(player: ArcadePlayer, val isProfileReload: Boolean = false) : PlayerDataEvent(player)