package io.github.openminigameserver.nickarcade.core.manager

import io.github.openminigameserver.nickarcade.core.data.providers.PlayerDataProvider

object PlayerDataProviderManager {
    private val registeredProviders = mutableListOf<PlayerDataProvider>()

    fun registerProvider(provider: PlayerDataProvider) {
        registeredProviders.add(0, provider)
    }

    fun getPlayerDataProvider(): PlayerDataProvider? {
        return registeredProviders.firstOrNull()
    }
}