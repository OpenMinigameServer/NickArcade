package io.github.openminigameserver.nickarcade.core.data.sender.player

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.MessageType
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.inventory.Book
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainComponentSerializer
import org.geysermc.cumulus.SimpleForm
import org.geysermc.floodgate.api.FloodgateApi

class FloodgateAudience(val player: ArcadePlayer) : Audience by player.player!! {
    override fun openBook(book: Book) {
        val builder = SimpleForm.builder().apply {
            title(PlainComponentSerializer.plain().serialize(book.title()))
            val page = book.pages().first()

            val (content, actions) = page.children().partition { it.clickEvent() == null }
            val actionsMap = mapOf(*(actions.map {
                LegacyComponentSerializer.legacySection().serialize(it.replaceText { textReplace ->
                    textReplace.matchLiteral("➤ ").replacement("")
                }) to it
            }).toTypedArray())

            var contentResult = Component.empty()
            content.forEach {
                contentResult = contentResult.append(it)
            }

            val text = LegacyComponentSerializer.legacySection().serialize(contentResult)
            content(text)

            actionsMap.forEach { (text, _) ->
                button(text)
            }
            this.responseHandler { form, resultStr ->
                val result = form.parseResponse(resultStr)
                if (result.isCorrect) {
                    val component = actionsMap[result.clickedButton?.text]
                    val clickEvent = component?.clickEvent() ?: return@responseHandler

                    if (clickEvent.action() == ClickEvent.Action.RUN_COMMAND) {
                        player.player?.chat(clickEvent.value())
                    }
                }
            }
        }
        FloodgateApi.getInstance().sendForm(player.uuid, builder)
    }

    override fun sendMessage(source: Identity, message: Component, type: MessageType) {
        player.player?.sendMessage(source, message.replaceText {
            it.matchLiteral("  ")
            it.replacement { c ->
                val build = c.build()
                return@replacement text("--", build.color())
            }
        }, type)
    }
}