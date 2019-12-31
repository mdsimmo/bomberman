package io.github.mdsimmo.bomberman.commands.game.set;

import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.commands.Cmd;
import io.github.mdsimmo.bomberman.commands.GameCommand;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Phrase;
import io.github.mdsimmo.bomberman.messaging.Text;
import io.github.mdsimmo.bomberman.prizes.EmptyPayment;
import io.github.mdsimmo.bomberman.prizes.ItemPayment;
import io.github.mdsimmo.bomberman.prizes.Payment;
import io.github.mdsimmo.bomberman.prizes.VaultPayment;
import io.github.mdsimmo.bomberman.prizes.XpPayment;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Fare extends GameCommand {

	public Fare( Cmd parent ) {
		super( parent );
	}

	@Override
	public Phrase nameShort() {
		return Text.FARE_NAME;
	}

	@Override
	public List<String> shortOptions( CommandSender sender, List<String> args ) {
		if ( args.size() == 1 ) {
			List<String> options = new ArrayList<>();
			options.add( Text.FARE_NONE.getMessage( sender ).toString() );
			options.add( Text.FARE_XP.getMessage( sender ).toString() );
			options.add( Text.FARE_IN_HAND.getMessage( sender ).toString() );
			options.add( Text.FARE_VAULT.getMessage( sender ).toString() );
			for ( Material m : Material.values() )
				options.add( m.toString().toLowerCase() );
			return options;
		} else
			return null;
	}

	@Override
	public boolean runShort( CommandSender sender, List<String> args, Game game ) {
		if ( args.size() < 1 || args.size() > 2 )
			return false;

		final Payment payment;
		
		// try for an empty payment
		String none = Text.FARE_NONE.getMessage( sender ).toString();
		if ( args.get( 0 ).equalsIgnoreCase( none ) ) {
			payment = EmptyPayment.getEmptyPayment();
		} else if ( args.get( 0 ).equalsIgnoreCase( Text.FARE_IN_HAND.getMessage( sender ).toString() ) ) {
			if ( sender instanceof Player ) {
				Player player = (Player)sender;
				ItemStack stack = player.getItemInHand();
				if ( stack == null || stack.getAmount() <= 0 ) {
					payment = EmptyPayment.getEmptyPayment();
				} else {
					payment = ItemPayment.of( stack );
				}
			} else {
				Chat.sendMessage( getMessage( Text.MUST_BE_PLAYER, sender ) );
				return true;
			}
		} else {
			// try for an xp payment
			String xp = Text.FARE_XP.getMessage( sender ).toString();
			if ( args.size() == 2 && args.get( 0 ).equalsIgnoreCase( xp ) ) {
				String amountString = args.get( 1 );
				int amount;
				try {
					amount = Integer.parseInt( amountString );
				} catch ( NumberFormatException e ) {
					Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender )
							.put( "number", amountString ) );
					return true;
				}
				if ( amount <= 0 ) {
					Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender )
							.put( "number", amountString ) );
					return true;
				}
				payment = XpPayment.of( amount );
				
			} else if ( args.size() == 2 && args.get( 0 ).equalsIgnoreCase( Text.FARE_VAULT.getMessage( sender ).toString() ) ) {
				try {
					double amount = Double.parseDouble( args.get( 1 ));
					payment = VaultPayment.of( amount );
				} catch ( Exception e ) {
					Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender )
							.put( "number", args.get( 1 ) ) );
					return true;
				}				
			} else if ( args.size() == 2 ) {
				Material m = Material.getMaterial( args.get( 0 ).toUpperCase() );
				if ( m == null )
					return false;
				try {
					int amount = Integer.parseInt( args.get( 1 ) );
					ItemStack stack = new ItemStack( m, amount );
					payment = ItemPayment.of( stack );
				} catch ( Exception e ) {
					Chat.sendMessage( getMessage( Text.INVALID_NUMBER, sender )
							.put( "number", args.get( 1 ) ) );
					return true;
				}
			} else {
				return false;
			}
		}
		
		// set the fare
		game.setFare( payment );
		Chat.sendMessage( getMessage( Text.FARE_SET, sender ).put( "game", game ).put( "fare", payment ) );
		return true;
	}

	@Override
	public Permission permission() {
		return Permission.GAME_DICTATE;
	}

	@Override
	public Phrase extraShort() {
		return Text.FARE_EXTRA;
	}

	@Override
	public Phrase exampleShort() {
		return Text.FARE_EXAMPLE;
	}

	@Override
	public Phrase descriptionShort() {
		return Text.FARE_DESCRIPTION;
	}

	@Override
	public Phrase usageShort() {
		return Text.FARE_USAGE;
	}

}
