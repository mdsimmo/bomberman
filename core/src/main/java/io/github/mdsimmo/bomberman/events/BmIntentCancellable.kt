package io.github.mdsimmo.bomberman.events

class BmIntentCancellable : IntentCancellable {
    private var cancelled = false
    private var handled = false

    override fun verifyHandled() {
        if (!handled && !isCancelled) throw RuntimeException("Event not handled: $this")
    }

    override fun isCancelled(): Boolean {
        return cancelled
    }

    override fun setCancelled(cancel: Boolean) {
        cancelled = cancel
    }

    override fun isHandled(): Boolean = handled

    override fun setHandled() {
        handled = true
    }
}