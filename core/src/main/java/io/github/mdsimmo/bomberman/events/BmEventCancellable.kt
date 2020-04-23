package io.github.mdsimmo.bomberman.events

import org.bukkit.event.Cancellable

/**
 * Simple implementation of Cancellable interface
 */
class BmCancellable : Cancellable {

    private var isCancelled = false

    override fun isCancelled(): Boolean {
        return isCancelled
    }

    override fun setCancelled(cancel: Boolean) {
        isCancelled = cancel
    }

}