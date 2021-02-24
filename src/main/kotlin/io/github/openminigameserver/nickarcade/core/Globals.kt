package io.github.openminigameserver.nickarcade.core

import io.github.openminigameserver.hypixelapi.HypixelService
import io.github.openminigameserver.hypixelapi.utis.HypixelPlayerInfoHelper
import org.litote.kmongo.coroutine.CoroutineDatabase
import java.util.logging.Logger

val database: CoroutineDatabase by IoC

val logger: Logger by IoC

val hypixelService: HypixelService by IoC

val hypixelPlayerInfoHelper: HypixelPlayerInfoHelper by IoC