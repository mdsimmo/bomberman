package io.github.mdsimmo.bomberman.events;

public abstract class BmIntent extends BmEvent {

    private boolean handled;

    public void setHandled() {
        this.handled = true;
    }

    public boolean isHandled() {
        return handled;
    }

    public void verifyHandled() {
        if (!handled)
            throw new RuntimeException("Event not handled: " + this);
    }

}
