package io.github.openminigameserver.nickarcade.core.data.sender.player

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.treeToValue
import io.github.openminigameserver.hypixelapi.models.HypixelPlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.PlayerOverrides
import kotlinx.datetime.Instant
import java.util.*

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "_id")
data class ArcadePlayerData(
    @JsonProperty("_id") val uuid: UUID,
    @JsonIgnore
    var hypixelData: HypixelPlayer?,
    val overrides: PlayerOverrides = PlayerOverrides(),
    var rawHypixelData: JsonNode? = hypixelData?.rawJsonNode,
    val cooldowns: MutableMap<String, Long> = mutableMapOf(),
    var lastProfileUpdate: Instant = Instant.DISTANT_PAST
) {

    init {
        updateHypixelData()
    }

    fun updateHypixelData(shouldUpdateFromJson: Boolean = true) {
        if (shouldUpdateFromJson && rawHypixelData != null) {
            hypixelData = io.github.openminigameserver.hypixelapi.HypixelApi.objectMapper.treeToValue<HypixelPlayer>(
                rawHypixelData!!
            )
        } else if (hypixelData != null) {
            rawHypixelData = io.github.openminigameserver.hypixelapi.HypixelApi.objectMapper.valueToTree(hypixelData)
        }
    }
}
