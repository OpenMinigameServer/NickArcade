package io.github.openminigameserver.nickarcade.core.ui

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.checkerframework.checker.nullness.qual.NonNull

fun chestGui(rows: Int, title: ComponentLike, code: ChestGui.() -> Unit): ChestGui {
    return ChestGui(rows, LegacyComponentSerializer.legacySection().serialize(title.asComponent())).also(code)
}

fun ItemStack.itemMeta(code: ItemMeta.() -> Unit): ItemStack {
    return itemMeta<ItemMeta>(code)
}

@JvmName("itemMetaGeneric")
fun <T : ItemMeta> ItemStack.itemMeta(code: T.() -> Unit): ItemStack {
    return this.apply {
        itemMeta = itemMeta.apply { code(this as T) }
    }
}

fun Component.disableItalic(): @NonNull Component {
    return this.decoration(TextDecoration.ITALIC, false)
}