package com.github.mdsimmo.bomberman.prizes;

import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The combined payment is a grouping of multiple payments.
 */
public final class CombinedPayment implements Payment {

    /**
     * Bundles all the given payments up into a single payment.
     * @param payments the payments to bundle up
     * @return a single combined payment
     * @throws NullPointerException if any payment is null
     */
    public static CombinedPayment of( Payment ... payments ) {
        return new CombinedPayment( payments );
    }

    private final Payment[] payments;

    private CombinedPayment( Payment ... payments ) {
        validate( payments );
        this.payments = copy( payments );
    }

    @Override
    public boolean ownedBy( Player player ) {
        for ( Payment payment : payments ) {
            if ( !payment.ownedBy( player ) )
                return false;
        }
        return true;
    }

    @Override
    public void giveTo( Player player ) {
        for ( Payment payment : payments )
            payment.giveTo( player );
    }

    @Override
    public boolean takeFrom( Player player ) {
        for ( int i = 0; i < payments.length; i++ ) {
            if ( !payments[i].takeFrom( player ) ) {
                // return the taken payments to the player
                for ( int j = 0; j < i; j++) {
                    payments[j].giveTo( player );
                }
                return false;
            }
        }
        return true;
    }

    /**
     * Gets a copy of the payments that this CombinedPayment is made up of.
     * @return the combined payments
     */
    public Payment[] getPayments() {
        return copy( payments );
    }

    private static Payment[] copy( Payment[] payments ) {
        Payment[] copy = new Payment[payments.length];
        System.arraycopy( payments, 0, copy, 0, payments.length );
        return copy;
    }

    private static void validate( Payment[] payments ) {
        if ( payments == null )
            throw new NullPointerException( "payments cannot be null" );
        for ( Payment payment : payments ) {
            if ( payment == null )
                throw new NullPointerException( "no payment in payments may be null. Passed array was: " + Arrays.toString( payments ) );
        }
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "payments", copy( payments ) );
        return map;
    }

    public static CombinedPayment deserialize( Map<String, Object> map ) {
        @SuppressWarnings( "unchecked" )
        List<Payment> payments = (List<Payment>)map.get( "payments" );
        return CombinedPayment.of( payments.toArray( new Payment[payments.size()] ) );
    }
}
