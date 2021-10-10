package io.github.mdsimmo.bomberman.messaging

import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack
import javax.annotation.CheckReturnValue


/**
 * A Contexted is a string that can have Formattables attached to it and then expanded into a message
 */
@CheckReturnValue
interface Contexted {

    fun with(key: String, thing: Formattable): Contexted

    fun format(): Message

    fun with(key: String, value: String): Contexted {
        return with(key, StringWrapper(value))
    }

    fun with(key: String, value: Int): Contexted {
        return with(key, StringWrapper(value.toString()))
    }

    fun with(key: String, value: Collection<Formattable>): Contexted {
        return with(key, CollectionWrapper(value))
    }

    fun with(key: String, stack: ItemStack): Contexted {
        return with(key, ItemWrapper(stack))
    }

    fun with(key: String, sender: CommandSender): Contexted {
        return with(key, SenderWrapper(sender))
    }

    fun sendTo(sender: CommandSender) {
        format().sendTo(sender)
    }
}