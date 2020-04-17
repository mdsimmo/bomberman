package io.github.mdsimmo.bomberman.messaging

interface Formattable {
    fun format(args: List<Message>): Message
}