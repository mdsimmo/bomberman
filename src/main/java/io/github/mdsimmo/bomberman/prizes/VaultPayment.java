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
	private Economy economy;
	
	private VaultPayment( double amount ) {
		if ( amount < 0 )
			throw new IllegalArgumentException( "Cannot have a economy payment of less than 0" );
		this.amount = amount;
	}
	
	private boolean hasEconomy() {
		if ( economy != null )
			return true;
		if ( !Bukkit.getPluginManager().isPluginEnabled( "Vault" ) )
			return false;
		RegisteredServiceProvider<Economy> provider = Bukkit.getServer().getServicesManager().getRegistration( Economy.class );
		if ( provider == null ) {
			return false;
		}
		economy = provider.getProvider();
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
				return "economy";
			if ( args.get( 0 ).equalsIgnoreCase( "amount" ) )
				return Double.toString( amount );
		}
		if ( hasEconomy() )
			return economy.format( amount );
		else
			return "$" + amount;
	}

	@Override
	public void giveTo( Player player ) {
		if ( !hasEconomy() )
			return;
		economy.depositPlayer( player, amount );
	}

	@Override
	public boolean takeFrom( Player player ) {
		if ( !hasEconomy() )
			return true;
		EconomyResponse response = economy.withdrawPlayer( player, amount );
		return response.transactionSuccess();
	}

	
	
}
