package io.github.mdsimmo.bomberman.prizes;

import org.bukkit.entity.Player;

import io.github.mdsimmo.bomberman.messaging.Message;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * An empty payment is a payment worth nothing. All players can pay the payment
 * and giving it to a player does nothing.
 */
public final class EmptyPayment implements Payment {

    /**
     * Gets an empty payment.
     * @return an empty payment
     */
    public static EmptyPayment getEmptyPayment() {
        return instance;
    }

    static final EmptyPayment instance = new EmptyPayment();

    private EmptyPayment() {
    }

    @Override
    public void giveTo(Player player) {

    }

    @Override
    public boolean takeFrom(Player player) {
        return true;
    }

    @Override
    public Map<String, Object> serialize() {
        return Collections.emptyMap();
    }

    public static EmptyPayment deserialize( Map<String, Object> map ) {
        return EmptyPayment.getEmptyPayment();
    }

	@Override
	public String format( Message message, List<String> args ) {
		return "nothing";
	}

}