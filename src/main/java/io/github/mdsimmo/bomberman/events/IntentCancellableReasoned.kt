package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.messaging.Message
import org.bukkit.event.Cancellable

interface IntentCancellableReasoned : Cancellable, Intent {

    @Deprecated(message = "Use cancelFor")
    override fun setCancelled(cancel: Boolean)

    fun cancelFor(reason: Message)

    fun cancelledReason(): Message?
}