package io.github.mdsimmo.bomberman.messaging;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import io.github.mdsimmo.bomberman.PlayerRep;
import net.objecthunter.exp4j.ExpressionBuilder;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public interface Formattable {
	
	public String format( Message message, List<String> args );
	
	public static class ItemWrapper implements Formattable { 
		
		private ItemStack item;
		
		public ItemWrapper( ItemStack item ) {
			this.item = item;
		}

		@Override
		public String format( Message message, List<String> args ){
			if ( args.size() == 0 )
				return format( message, Arrays.asList( "amount" ) ) 
						+ " " 
						+ format( message, Arrays.asList( "type" ) );
			switch ( args.get( 0 ) ) {
			case "amount":
				return Integer.toString( item.getAmount() );
			case "type":
				return item.getType().toString().replace( '_', ' ' ).toLowerCase();
			}
			return null;
		}
	}
	
	public static class SenderWrapper implements Formattable {

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
		public String format( Message message, List<String> args ){
			if ( rep != null )
				return rep.format( message, args );
			return sender.getName();
		}
		
	}
	
	public static class Equation implements Formattable {
		
		@Override
		public String format( Message message, List<String> args ) {
			if ( args.size() != 1 )
				throw new RuntimeException("Equation must have exactly one argument: " + message.toString());
			try {
				double answer = new ExpressionBuilder( args.get( 0 ) ).build().evaluate();
				return BigDecimal.valueOf( answer ).stripTrailingZeros().toPlainString();
			} catch ( Exception e ) {
				throw new RuntimeException( "Expression has invalid numerical imputs: " + args.get( 0 ), e );
			}
		}
	}
	
}
