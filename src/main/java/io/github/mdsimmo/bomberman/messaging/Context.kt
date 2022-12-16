package io.github.mdsimmo.bomberman.messaging

import org.bukkit.command.CommandSender
import org.bukkit.inventory.ItemStack

/**
 * An immutable set of things referenced in the current scope
 */
class Context {
    private val objects: Map<String, Formattable>
    private val functions: Set<(String) -> String?>
    val elevatedPermissions: Boolean

    constructor(elevated: Boolean) {
        objects = mapOf()
        functions = emptySet()
        this.elevatedPermissions = elevated
    }

    private constructor(objects: Map<String, Formattable>, functions: Set<(String) -> String?>, elevated: Boolean) {
        this.objects = objects
        this.functions = functions
        this.elevatedPermissions = elevated
    }

    operator fun get(key: String): Formattable? = objects[key]

    fun getFunction(key: String): String? {
        return functions.firstNotNullOfOrNull { it(key) }
    }

    fun addFunctions(functions: (String) -> String?): Context {
        return Context(objects, this.functions+functions, elevatedPermissions)
    }

    fun newScope(): Context {
        return Context(elevatedPermissions)
    }

    fun plus(key: String, thing: Formattable): Context {
        return Context(objects.plus(Pair(key, thing)), functions, elevatedPermissions)
    }

    fun plus(context: Context): Context {
        return Context(objects.plus(context.objects), functions + context.functions, elevatedPermissions or context.elevatedPermissions)
    }

    fun plus(key: String, value: String) = plus(key, StringWrapper(value))

    fun plus(key: String, value: Int) = plus(key, StringWrapper(value.toString()))

    fun plus(key: String, value: Collection<Formattable>) = plus(key, CollectionWrapper(value))

    fun plus(key: String, stack: ItemStack) = plus(key, ItemWrapper(stack))

    fun plus(key: String, sender: CommandSender) = plus(key, SenderWrapper(sender))

}