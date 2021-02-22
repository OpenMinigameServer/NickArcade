package io.github.openminigameserver.nickarcade.plugin

import io.github.openminigameserver.nickarcade.core.IoC
import io.github.openminigameserver.nickarcade.core.io.config.ArcadeConfigurationFile
import io.github.openminigameserver.nickarcade.core.io.config.impl.MainConfigurationFile
import io.github.openminigameserver.nickarcade.core.io.database.DatabaseService
import io.github.openminigameserver.nickarcade.core.io.database.helpers.MongoDbConnectionHelper
import org.bukkit.plugin.java.JavaPlugin
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase

class NickArcadePlugin : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: NickArcadePlugin
    }

    private lateinit var databaseService: DatabaseService
    private lateinit var mainConfiguration: MainConfigurationFile

    override fun onEnable() {
        INSTANCE = this
        IoC += this

        initMainConfig()
        connectToDatabase()
    }

    private fun connectToDatabase() {
        System.setProperty(
            "org.litote.mongo.test.mapping.service",
            "org.litote.kmongo.jackson.JacksonClassMappingTypeService"
        )
        databaseService = DatabaseService(mainConfiguration.mongoDbConfiguration)
        IoC += databaseService
    }

    private fun initMainConfig() {
        mainConfiguration = ArcadeConfigurationFile("config.yml").load()
        IoC += mainConfiguration
    }
}