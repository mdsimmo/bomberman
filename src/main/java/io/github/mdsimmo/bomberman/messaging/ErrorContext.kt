package io.github.mdsimmo.bomberman.messaging

import javax.annotation.CheckReturnValue

@CheckReturnValue

class ErrorContext(private val text: String) : Contexted {

    override fun with(key: String, thing: Formattable): Contexted {
        // not needed
        return this
    }

    override fun format(): Message {
        return Message.error(text)
    }
}