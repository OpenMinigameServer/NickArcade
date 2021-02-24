package io.github.openminigameserver.nickarcade.core.io.database.helpers

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.openminigameserver.nickarcade.core.io.database.helpers.instant.InstantDeserializer
import io.github.openminigameserver.nickarcade.core.io.database.helpers.instant.InstantSerializer
import kotlinx.datetime.Instant

object ArcadeModule : SimpleModule("NickArcade") {
    init {
        addSerializer(Instant::class.java, InstantSerializer)
        addDeserializer(Instant::class.java, InstantDeserializer)
    }
}