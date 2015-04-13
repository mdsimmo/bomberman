package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public interface Formattable {
	
	public Object format( Message message, String value );
	
	static class ItemWrapper implements Formattable { 
		
		private ItemStack item;
		
		public ItemWrapper( ItemStack item ) {
			this.item = item;
		}

		@Override
		public Object format( Message message, String value ){
			if ( value == null )
				return format( message, "amount" ) + " " + format( message, "type" );
			switch ( value ) {
			case "amount":
				return item.getAmount();
			case "type":
				return item.getType().toString().replace( '_', ' ' ).toLowerCase();
			}
			return null;
		}
	}
	
	static class SenderWrapper implements Formattable {

		private final CommandSender sender;
		private final PlayerRep rep;
		
		public SenderWrapper( CommandSender sender ) {
			this.sender = sender;
			if ( sender instanceof Player )
				this.rep = PlayerRep.getPlayerRep( (Player)sender );
			else
				this.rep = null;
		}
		
		@Override
		public Object format( Message message, String value ){
			if ( rep != null )
				return rep.format( message, value );
			return sender.getName();
		}
		
	}
	
}
