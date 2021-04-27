package io.github.openminigameserver.nickarcade.core.ui

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import io.github.openminigameserver.nickarcade.plugin.extensions.pluginInstance
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object ItemActionHelper {
    private val actionKey = NamespacedKey(pluginInstance, "item_action")
    private val actions = mutableMapOf<String, suspend (ArcadePlayer) -> Unit>()

    fun getItemAction(itemStack: ItemStack): String? {
        return itemStack.takeIf { it.hasItemMeta() }?.itemMeta?.persistentDataContainer?.get(actionKey, PersistentDataType.STRING)
    }

    fun registerAction(key: NamespacedKey, handler: suspend (ArcadePlayer) -> Unit): ItemMeta.() -> ItemMeta {
        actions[key.toString()] = handler

        return {
            apply {
                this.persistentDataContainer.set(actionKey, PersistentDataType.STRING, key.toString())
            }
        }
    }

    fun executeAction(key: NamespacedKey, player: ArcadePlayer) {
        actions[key.toString()]?.let { launch { it.invoke(player) } }
    }

    fun executeAction(itemMeta: ItemMeta, player: ArcadePlayer) {
        itemMeta.persistentDataContainer.get(actionKey, PersistentDataType.STRING)?.let {
            actions[it]?.let { launch { it.invoke(player) } }
        }
    }
}