package io.github.mdsimmo.bomberman.messaging;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import io.github.mdsimmo.bomberman.game.GamePlayer;
import net.objecthunter.exp4j.ExpressionBuilder;
import net.objecthunter.exp4j.function.Function;

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
				return Integer.toString( item == null ? 0 : item.getAmount() );
			case "type":
				return item == null ? "none" : item.getType().toString().replace( '_', ' ' ).toLowerCase();
			}
			return null;
		}
	}
	
	public static class SenderWrapper implements Formattable {

		private final CommandSender sender;
		private final GamePlayer rep;
		
		public SenderWrapper( CommandSender sender ) {
			this.sender = sender;
			if ( sender instanceof Player )
				this.rep = GamePlayer.getPlayerRep( (Player)sender );
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
				double answer = new ExpressionBuilder( args.get( 0 ) ).function( SignFunction.instance ).build().evaluate();
				return BigDecimal.valueOf( answer ).stripTrailingZeros().toPlainString();
			} catch ( Exception e ) {
				throw new RuntimeException( "Expression has invalid numerical imputs: " + args.get( 0 ), e );
			}
		}
	}
	
	static class SignFunction extends Function {

		public static SignFunction instance = new SignFunction();
		
		public SignFunction() {
			super( "sign", 1 );
		}
		
		@Override
		public double apply( double... args ) {
			if ( args.length != 1 )
				throw new IllegalArgumentException( "Sign function can only have one argument. (" + args.length + " args given)" );
			double val = args[0];
			if ( val > 0 )
				return 1;
			else if ( val < 0 )
				return -1;
			else if ( val == 0 )
				return 0;
			else
				return Double.NaN;
		}
	}
	
}
