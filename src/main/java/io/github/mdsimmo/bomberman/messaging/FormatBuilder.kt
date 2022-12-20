package io.github.mdsimmo.bomberman.messaging

open class PartialWrapper(val call: () -> Formattable) : Formattable {

    private val built: Formattable by lazy {
        call()
    }

    override fun applyModifier(arg: Message): Formattable {
        return built.applyModifier(arg)
    }

    override fun format(context: Context): Message {
        return built.format(context)
    }
}

class PartialDefault(val text: Message, val function: (Message) -> Formattable) : Formattable {

    constructor(text: String, function: (Message) -> Formattable)
            : this(Message.of(text), function)

    override fun applyModifier(arg: Message): Formattable {
        return function(arg)
    }

    override fun format(context: Context): Message {
        return function(text).format(context)
    }
}

class PartialRequired(val function: (Message) -> Formattable) : Formattable {
    override fun applyModifier(arg: Message): Formattable {
        return function(arg)
    }

    override fun format(context: Context): Message {
        throw IllegalArgumentException("Extra argument required")
    }
}

class PartialContext(val function: (Context) -> Formattable) : Formattable {

    override fun applyModifier(arg: Message): Formattable {
        return ExtraArgsHolder(function, arg)
    }

    override fun format(context: Context): Message {
        return function(context).format(context)
    }
}

internal class ExtraArgsHolder(
    private val initial: (Context) -> Formattable,
    vararg args: Message
) : Formattable {

    private val extraArgs = args.toList()

    override fun applyModifier(arg: Message): Formattable {
        return ExtraArgsHolder(initial, *(extraArgs + arg).toTypedArray())
    }

    override fun format(context: Context): Message {
        val initialFormattable = initial(context)
        return extraArgs.fold(initialFormattable) { obj, arg ->
            obj.applyModifier(arg)
        }.format(context)
    }
}