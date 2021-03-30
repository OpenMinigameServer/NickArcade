package io.github.openminigameserver.nickarcade.core.data.sender.player.extra

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration

data class TimedObject<T>(
    val value: T,
    val storedTime: Instant = Clock.System.now()
)

interface RuntimeExtraDataTag<T> {
    val tagName: String
    val expirationDuration: Duration

    companion object {
        fun <T> of(name: String): RuntimeExtraDataTag<T> = of(name, Duration.ZERO)

        fun <T> of(name: String, expirationDuration: Duration): RuntimeExtraDataTag<T> {
            return object : RuntimeExtraDataTag<T> {
                override val tagName: String
                    get() = name
                override val expirationDuration: Duration
                    get() = expirationDuration
            }
        }
    }
}