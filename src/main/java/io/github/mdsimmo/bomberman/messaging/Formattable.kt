package io.github.mdsimmo.bomberman.messaging

interface Formattable {

    /**
     * Applies a modifier to this object
     * @param arg the value of the modifier
     */
    fun applyModifier(arg: Message): Formattable

    fun applyModifier(arg: String) : Formattable = applyModifier(Message.of(arg))

    /**
     * Formats this custom object into "simple" text
     * @param context additional variables in scope
     */
    fun format(context: Context): Message
}