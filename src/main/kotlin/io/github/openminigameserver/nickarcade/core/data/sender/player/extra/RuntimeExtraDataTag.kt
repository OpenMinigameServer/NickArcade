package io.github.openminigameserver.nickarcade.core.data.sender.player.extra

interface RuntimeExtraDataTag<T> {
    val tagName: String

    companion object {
        fun <T> of(name: String): RuntimeExtraDataTag<T> {
            return object : RuntimeExtraDataTag<T> {
                override val tagName: String
                    get() = name
            }
        }
    }
}