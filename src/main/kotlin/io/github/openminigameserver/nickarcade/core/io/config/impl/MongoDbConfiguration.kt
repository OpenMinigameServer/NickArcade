package io.github.openminigameserver.nickarcade.core.io.config.impl

import com.mongodb.ServerAddress
import org.spongepowered.configurate.objectmapping.ConfigSerializable

@ConfigSerializable
data class MongoDbConfiguration(
    val host: String = "localhost",
    val port: Int = ServerAddress.defaultPort(),
    val database: String = "NickArcade",
    val username: String = "",
    val password: String = ""
)