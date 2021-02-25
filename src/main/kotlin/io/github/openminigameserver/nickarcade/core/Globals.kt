package io.github.openminigameserver.nickarcade.core

import cloud.commandframework.annotations.AnnotationParser
import io.github.openminigameserver.hypixelapi.HypixelService
import io.github.openminigameserver.hypixelapi.utis.HypixelPlayerInfoHelper
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.plugin.helper.commands.NickArcadeCommandHelper
import io.github.openminigameserver.nickarcade.plugin.helper.commands.NickArcadeCommandManager
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.logging.Logger

val database: CoroutineDatabase by IoC

val logger: Logger by IoC

val hypixelService: HypixelService by IoC

val hypixelPlayerInfoHelper: HypixelPlayerInfoHelper by IoC

val commandHelper: NickArcadeCommandHelper by IoC

val commandManager: NickArcadeCommandManager<ArcadeSender> by IoC

val commandAnnotationParser: AnnotationParser<ArcadeSender> by IoC