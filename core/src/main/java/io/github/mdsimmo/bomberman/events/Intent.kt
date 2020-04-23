package io.github.mdsimmo.bomberman.events

interface Intent {

    fun isHandled(): Boolean

    fun setHandled()

    fun verifyHandled()
}