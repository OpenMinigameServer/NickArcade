package io.github.openminigameserver.nickarcade.core.misc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor

class RightClickSuffixComponent(private val text: ComponentLike) : ComponentLike {
    override fun asComponent(): Component {
        return text.asComponent().append(text(" ")).append(text("(Right-Click)", NamedTextColor.GRAY))
    }


}