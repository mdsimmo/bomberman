package io.github.mdsimmo.bomberman.messaging

interface Formattable {
    /**
     * Formats this custom object into "simple" text
     * @param args a list of modifiers
     * @param elevated if {#exec|...} is allowed (TODO remove me)
     */
    fun format(args: List<Message>, elevated: Boolean): Message
}