package io.github.mdsimmo.bomberman.messaging

import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import javax.annotation.CheckReturnValue

@CheckReturnValue

class SimpleContext(private val text: String, private val elevatedPermission: Boolean) : Contexted {

    private val things = mutableMapOf<String, Formattable>()

    override fun with(key: String, thing: Formattable): Contexted {
        things[key] = thing
        return this
    }

    override fun format(): Message {
        return Expander.expand(text, things, elevatedPermission)
    }
}