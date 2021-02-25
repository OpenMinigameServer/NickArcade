package io.github.openminigameserver.nickarcade.plugin.extensions

import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.ExtraDataValue
import kotlin.reflect.KProperty

operator fun <T> ArcadeSender.getValue(owner: Any, property: KProperty<*>): T? {
    return this.extraData[property.name]?.value as? T
}

operator fun <T> ArcadeSender.setValue(owner: Any, property: KProperty<*>, value: T?) {
    if (value == null) {
        this.extraData.remove(property.name)
    } else {
        this.extraData[property.name] = ExtraDataValue(value)
    }
}


fun <T> ArcadeSender.getExtraDataValue(property: KProperty<*>): T? {
    return this.getValue(this, property)
}

fun <T> ArcadeSender.setExtraDataValue(property: KProperty<*>, value: T?) {
    this.setValue(this, property, value)
}
