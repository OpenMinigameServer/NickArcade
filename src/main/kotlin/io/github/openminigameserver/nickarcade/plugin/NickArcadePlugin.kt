package io.github.openminigameserver.nickarcade.plugin

import io.github.openminigameserver.hypixelapi.HypixelApi
import io.github.openminigameserver.hypixelapi.utis.HypixelPlayerInfoHelper
import io.github.openminigameserver.nickarcade.core.IoC
import io.github.openminigameserver.nickarcade.core.commandAnnotationParser
import io.github.openminigameserver.nickarcade.core.io.config.ArcadeConfigurationFile
import io.github.openminigameserver.nickarcade.core.io.config.impl.MainConfigurationFile
import io.github.openminigameserver.nickarcade.core.io.database.DatabaseService
import io.github.openminigameserver.nickarcade.plugin.events.PlayerEvents
import io.github.openminigameserver.nickarcade.plugin.extensions.ComponentExtensions
import io.github.openminigameserver.nickarcade.plugin.helper.commands.NickArcadeCommandHelper
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class NickArcadePlugin : JavaPlugin() {
    companion object {
        lateinit var INSTANCE: NickArcadePlugin
    }

    private lateinit var databaseService: DatabaseService
    private lateinit var mainConfiguration: MainConfigurationFile

    override fun onEnable() {
        INSTANCE = this

        prepareIoCValues()
        initMainConfig()
        connectToDatabase()
        initHypixelServices()

        PlayerEvents.registerHandlers()

        IoC += NickArcadeCommandHelper().apply { init() }

        commandAnnotationParser.parse(ComponentExtensions)
    }

    private fun prepareIoCValues() {
        IoC += this
        IoC += this.logger
    }


    private fun initHypixelServices() {
        if (mainConfiguration.hypixelKey != UUID(0, 0)) {
            val service = HypixelApi.getService(mainConfiguration.hypixelKey)
            IoC += service
            IoC += HypixelPlayerInfoHelper(service)
            return
        }
        throw Exception("Unable to initialize plugin! Reason: Unable to initialize Hypixel services")
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