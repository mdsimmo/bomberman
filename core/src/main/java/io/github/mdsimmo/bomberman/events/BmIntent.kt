package io.github.mdsimmo.bomberman.events

class BmIntent : Intent {

    private var isHandled = false

    override fun isHandled(): Boolean = isHandled

    override fun setHandled() {
        isHandled = true
    }

    override fun verifyHandled() {
        if (!isHandled) throw RuntimeException("Event not handled: $this")
    }
}