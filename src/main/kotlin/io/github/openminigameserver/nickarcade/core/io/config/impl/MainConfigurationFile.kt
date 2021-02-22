package io.github.openminigameserver.nickarcade.core.io.config.impl

import org.spongepowered.configurate.objectmapping.ConfigSerializable
import org.spongepowered.configurate.objectmapping.meta.Comment
import java.util.*

@ConfigSerializable
data class MainConfigurationFile(
    @Comment("The API key used to fetch data off of the Hypixel Network")
    var hypixelKey: UUID = UUID(0, 0),

    @Comment("The configuration for the SQL configuration")
    var mongoDbConfiguration: MongoDbConfiguration = MongoDbConfiguration()
)