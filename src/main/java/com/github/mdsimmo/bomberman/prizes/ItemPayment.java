package com.github.mdsimmo.bomberman.prizes;

import com.sun.xml.internal.ws.api.message.saaj.SaajStaxWriter;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * An ItemPayment is a payment that represents a bunch of ItemStacks. An ItemPayment
 * may have no ItemStacks in which case, it will behave the exact same as an
 * EmptyPayment. Any player in creative mode will always be able to pay the
 * payment regardless of what is in his inventory.
 */
public final class ItemPayment implements Payment {

    /**
     * Creates a payment from a collection of items. The passed collection is
     * cloned before being used
     * @param items the items to create the collection from
     * @return the created payment
     * @throws NullPointerException if any ItemStack is null
     */
    public static ItemPayment of( ItemStack ... items ) {
        return new ItemPayment( items );
    }

    private final ItemStack[] stacks;

    private ItemPayment( ItemStack[] stacks ) {
        validate( stacks );
        this.stacks = compress( copy( stacks ) );
    }

    @Override
    public boolean ownedBy( Player player ) {
        if ( player.getGameMode() == GameMode.CREATIVE )
            return true;
        for ( ItemStack stack : stacks )
            if ( !player.getInventory().containsAtLeast( new ItemStack( stack ), stack.getAmount() ))
                return false;
        return true;
    }

    @Override
    public void giveTo( Player player ) {
        HashMap<Integer, ItemStack> notPayed = player.getInventory().addItem( copy( stacks ) );
        if ( notPayed.isEmpty() )
            return;
        for ( ItemStack stack : notPayed.values() ) {
            Location l = player.getLocation();
            l.getWorld().dropItem( l, stack );
        }
    }

    @Override
    public boolean takeFrom( Player player ) {
        if ( !ownedBy( player ) )
            return false;
        player.getInventory().removeItem( copy( stacks ) );
        return true;
    }

    /**
     * Gets a copy of the items that this payment is representing
     * @return this payments items
     */
    public ItemStack[] getItems() {
        return copy( stacks );
    }

    /**
     * Creates a deep clone of {@link #stacks}. Cloning all the items is needed as adding/removing stacks
     * to/from players can alter the stacks amount if the item stacks don't fit completely
     * @return the cloned items stacks
     */
    private static ItemStack[] copy( ItemStack[] stacks ) {
        ItemStack[] copy = new ItemStack[stacks.length];
        for ( int i = 0; i < stacks.length; i++ )
            copy[i] = new ItemStack( stacks[i] );
        return copy;
    }

    private static void validate( ItemStack[] stacks ) {
        if ( stacks == null )
            throw new NullPointerException( "'stacks' cannot be null" );

        // validate and deep copy the passed array.
        for ( ItemStack itemStack : stacks ) {
            if ( itemStack == null )
                throw new NullPointerException( "Found a null ItemStack in a Payment. Passed array was: " + Arrays.toString( stacks ) );
        }
    }

    private ItemStack[] compress( ItemStack[] stacks ) {

        int compressed = 0;

        // combine and nullify similar items
        for ( int i = 0; i < stacks.length-1; i++ ) {
            ItemStack stackA = stacks[i];
            if ( stackA == null )
                continue;
            for ( int j = i+1; j < stacks.length; j++ ) {
                ItemStack stackB = stacks[j];
                if ( stackB == null )
                    continue;
                if ( stackA.isSimilar( stackB ) ) {
                    stackA.setAmount( stackA.getAmount() + stackB.getAmount() );
                    stacks[j] = null;
                    compressed++;
                }
            }
        }

        if ( compressed == 0 )
            // did nothing - return immediately
            return stacks;

        ItemStack[] result = new ItemStack[stacks.length - compressed];
        int index = 0;
        for ( ItemStack stack : stacks ) {
            if ( stack == null )
                continue;
            result[index++] = stack;
        }

        if ( index != result.length )
            throw new RuntimeException( "Write index should always be eaqual to result length!" );
        return result;
    }
}
