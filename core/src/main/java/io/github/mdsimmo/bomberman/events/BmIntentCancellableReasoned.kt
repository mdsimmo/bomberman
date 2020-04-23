package io.github.mdsimmo.bomberman.events

import io.github.mdsimmo.bomberman.messaging.Message

class BmIntentCancellableReasoned : IntentCancellable by BmIntentCancellable(), IntentCancellableReasoned {

    private var reason: Message? = null

    override fun cancelFor(reason: Message) {
        this.reason = reason
        isCancelled = true
    }

    override fun cancelledReason(): Message? {
        return reason
    }
}