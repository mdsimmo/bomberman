package io.github.mdsimmo.bomberman.prizes;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.messaging.Message;

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
     *
     * @param items the items to create the collection from
     * @return the created payment
     * @throws NullPointerException if any ItemStack is null
     */
    public static ItemPayment of( ItemStack item ) {
        return new ItemPayment( item );
    }

    private final ItemStack stack;

    private ItemPayment( ItemStack stack ) {
        validate( stack );
        this.stack = copy( stack );
    }

    @Override
    public boolean ownedBy( Player player ) {
        if ( player.getGameMode() == GameMode.CREATIVE )
            return true;
        if ( !player.getInventory().containsAtLeast( new ItemStack( stack ), stack.getAmount() ) )
            return false;
        return true;
    }

    @Override
    public void giveTo( Player player ) {
        HashMap<Integer, ItemStack> notPayed = player.getInventory().addItem( copy( stack ) );
        if ( notPayed.isEmpty() )
            return;
        for ( ItemStack stack : notPayed.values() ) {
            Location l = player.getLocation();
            l.getWorld().dropItem( l, stack );
        }
        updatePlayerInventory( player );
    }

    @Override
    public boolean takeFrom( Player player ) {
        if ( !ownedBy( player ) )
            return false;
        if ( player.getGameMode() == GameMode.CREATIVE )
        	return true;
        player.getInventory().removeItem( copy( stack ) );
        updatePlayerInventory( player );
        return true;
    }
    
	private void updatePlayerInventory( final Player player ) {
    	// makes sure the inventory is correct
		if ( Bomberman.instance.isEnabled() ) {
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask( Bomberman.instance, new Runnable() {
						@SuppressWarnings( "deprecation" )
						@Override
						public void run() {
							player.updateInventory();
						}
					} );
		}
    }

    /**
     * Gets a copy of the items that this payment is representing
     *
     * @return this payments items
     */
    public ItemStack getItem() {
        return copy( stack );
    }

    /**
     * Creates a deep clone of {@link #stacks}. Cloning all the items is needed as adding/removing stacks
     * to/from players can alter the stacks amount if the item stacks don't fit completely
     *
     * @return the cloned items stacks
     */
    private static ItemStack copy( ItemStack stack ) {
        return new ItemStack( stack );
    }

    private static void validate( ItemStack stack ) {
        if ( stack == null )
            throw new NullPointerException( "'stack' cannot be null" );
        if ( stack.getAmount() <= 0 )
        	throw new IllegalArgumentException( "Item cannot have negative quantity" );
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "item", stack );
        return map;
    }

    public static ItemPayment deserialize( Map<String, Object> map ) {
        ItemStack stack = (ItemStack)map.get( "item" );
        return ItemPayment.of( stack );
    }

	@Override
	public String format( Message message, List<String> args ) {
		if ( args.size() > 0 && args.get( 0 ).equalsIgnoreCase( "ptype" ) )
			return "item";
		return new ItemWrapper( copy( stack ) ).format( message, args );
	}
}