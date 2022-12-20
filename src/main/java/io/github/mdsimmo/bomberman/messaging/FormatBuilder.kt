package io.github.mdsimmo.bomberman.messaging

class DefaultArg(val text: Message, val function: (Message) -> Formattable) : Formattable {

    constructor(text: String, function: (Message) -> Formattable)
            : this(Message.of(text), function)

    override fun applyModifier(arg: Message): Formattable {
        return function(arg)
    }

    override fun format(context: Context): Message {
        return function(text).format(context)
    }
}

class RequiredArg(val function: (Message) -> Formattable) : Formattable {
    override fun applyModifier(arg: Message): Formattable {
        return function(arg)
    }

    override fun format(context: Context): Message {
        throw IllegalArgumentException("Extra argument required")
    }
}

class ContextArg(val function: (Context) -> Formattable) : Formattable {

    override fun applyModifier(arg: Message): Formattable {
        // Store all extra arguments until formatted with context
        return ExtraArgsHolder({ context, args ->
            // Apply the Context
            val initial = function(context)

            // Pass all stored arguments to the result
            args.fold(initial) { obj, arg ->
                obj.applyModifier(arg)
            }
        }, arg)
    }

    override fun format(context: Context): Message {
        return function(context).format(context)
    }
}

class AdditionalArgs(private val function: (List<Message>) -> Formattable) : Formattable {
    override fun applyModifier(arg: Message): Formattable {
        // Pass all additional arguments to the caller
        return ExtraArgsHolder({ _, args -> function(args) }, arg)
    }

    override fun format(context: Context): Message {
        return function(emptyList()).format(context)
    }
}

internal class ExtraArgsHolder(
    private val callback: (Context, List<Message>) -> Formattable,
    vararg args: Message
) : Formattable {

    private val extraArgs = args.toList()

    override fun applyModifier(arg: Message): Formattable {
        return ExtraArgsHolder(callback, *(extraArgs + arg).toTypedArray())
    }

    override fun format(context: Context): Message {
        return callback(context, extraArgs).format(context)
    }
}