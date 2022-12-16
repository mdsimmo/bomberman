package io.github.mdsimmo.bomberman.messaging

interface Formattable {
    /**
     * Formats this custom object into "simple" text
     * @param args a list of modifiers
     * @param context additional variables in scope
     */
    fun format(args: List<Message>, context: Context): Message
}

interface FormattableNoArgs : Formattable {

    /**
     * Formats this custom object into "simple" text
     * @param context additional variables in scope
     */
    fun format(context: Context): Message

    override fun format(args: List<Message>, context: Context): Message {
        return format(context)
    }
}