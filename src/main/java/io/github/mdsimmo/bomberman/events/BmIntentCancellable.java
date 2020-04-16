package io.github.mdsimmo.bomberman.events;

import org.bukkit.event.Cancellable;

public abstract class BmIntentCancellable extends BmIntent implements Cancellable {

    private boolean cancelled;

    @Override
    public void verifyHandled() {
        if (!isHandled() && !isCancelled())
            throw new RuntimeException("Event not handled: " + this);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
