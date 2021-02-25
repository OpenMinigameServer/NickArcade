package io.github.openminigameserver.nickarcade.core.data.sender.player.extra

import com.fasterxml.jackson.annotation.JsonTypeInfo

data class ExtraDataValue(
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    val value: Any
)
