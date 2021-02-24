package io.github.openminigameserver.nickarcade.core.io.database

import io.github.openminigameserver.nickarcade.core.IoC
import io.github.openminigameserver.nickarcade.core.io.config.impl.MongoDbConfiguration
import io.github.openminigameserver.nickarcade.core.io.database.helpers.MongoDbConnectionHelper

class DatabaseService(config: MongoDbConfiguration) {
    val databaseClient = config.run {
        MongoDbConnectionHelper.buildClient(host, port, username, database, password)
    }
    val database = databaseClient.getDatabase(config.database)

    init {
        IoC += database
        IoC += databaseClient
    }
}