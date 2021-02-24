package io.github.openminigameserver.nickarcade.core.data.sender.player.extra

interface ExtraDataTag<T> {
    val tagName: String

    companion object {
        fun <T> of(name: String): ExtraDataTag<T> {
            return object : ExtraDataTag<T> {
                override val tagName: String
                    get() = name
            }
        }
    }
}