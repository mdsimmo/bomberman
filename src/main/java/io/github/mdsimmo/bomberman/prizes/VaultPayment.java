package io.github.mdsimmo.bomberman.prizes;

import io.github.mdsimmo.bomberman.messaging.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultPayment implements Payment {
	
	public static VaultPayment of( double amount ) {
		return new VaultPayment( amount );
	}

	private final double amount;
	private Economy ecconomy;
	
	private VaultPayment( double amount ) {
		if ( amount < 0 )
			throw new IllegalArgumentException( "Cannot have a ecconomy payment of less than 0" );
		this.amount = amount;
	}
	
	private boolean hasEcconomy() {
		if ( ecconomy != null )
			return true;
		if ( !Bukkit.getPluginManager().isPluginEnabled( "Vault" ) )
			return false;
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration( Economy.class );
		if ( provider == null ) {
			return false;
		}
		ecconomy = provider.getProvider();
		return true;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put( "amount", amount );
		return map;
	}
	
	public static VaultPayment deserialize( Map<String, Object> map ) {
        double amount = (double)map.get( "amount" );
        return new VaultPayment( amount );
    }

	@Override
	public String format( Message message, List<String> args ) {
		if ( args.size() > 0 ) {
			if ( args.get( 0 ).equalsIgnoreCase( "ptype" ) )
				return "ecconomy";
			if ( args.get( 0 ).equalsIgnoreCase( "amount" ) )
				return Double.toString( amount );
		}
		if ( hasEcconomy() )
			return ecconomy.format( amount );
		else
			return "$" + amount;
	}

	@Override
	public void giveTo( Player player ) {
		if ( !hasEcconomy() )
			return;
		ecconomy.depositPlayer( player, amount );
	}

	@Override
	public boolean takeFrom( Player player ) {
		if ( !hasEcconomy() )
			return true;
		EconomyResponse response = ecconomy.withdrawPlayer( player, amount );
		return response.transactionSuccess();
	}

	
	
}
