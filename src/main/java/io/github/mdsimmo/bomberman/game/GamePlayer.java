package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.PlayerHurtEvent;
import io.github.mdsimmo.bomberman.events.PlayerKilledEvent;
import io.github.mdsimmo.bomberman.events.PlayerLeaveGameEvent;
import io.github.mdsimmo.bomberman.events.PlayerOnBombEvent;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.List;

public class GamePlayer implements Formattable, Listener {

	private static JavaPlugin plugin = Bomberman.instance;

	public final Player player;
	private final Game game;

	private int immunity = 0;

	private final ItemStack[] spawnInventory;
	private final Location spawn;
	private final int spawnHunger;
	private final GameMode spawnGameMode;
	private final double spawnHealth;
	private final double spawnMaxHealth;
	private final double spawnHealthScale;

	/**
	 * Starts the given in the given game at the given starting point.
	 *
	 * This constructor will move the player into the start location, build walls around the player and remember
	 * all the details about the player
	 * @param player the player to move
	 * @param game the game to join
	 * @param start where to put the player
	 */
	public GamePlayer(Player player, Game game, Location start) {
		this.player = player;
		this.game = game;

		// remember the player stats
		spawnHealth = player.getHealth();
		spawnGameMode = player.getGameMode();
		spawnHealthScale = player.getHealthScale();
		spawnMaxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
		spawn = player.getLocation();
		spawnHunger = player.getFoodLevel();
		spawnInventory = player.getInventory().getContents();

		player.getServer().getPluginManager().registerEvents(this, plugin);

		// Initialise the player for the game
		player.teleport(start);
		player.setGameMode(GameMode.ADVENTURE);
		player.setHealth(game.getSettings().lives);
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(game.getSettings().lives);
		player.setHealthScale(game.getSettings().lives * 2);
		player.setExhaustion(0);
		player.setFoodLevel(100000); // just a big number
		player.getInventory().clear();
		for (ItemStack stack : game.getSettings().initialitems) {
			ItemStack s = stack.clone();
			player.getInventory().addItem(s);
		}
		removePotionEffects();
	}

	/**
	 * Removes the player from the game and removes any hooks to this player. Treats the player like they disconnected
	 * from the server.
	 */
	public void removeFromGame() {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(spawnMaxHealth);
		player.setHealthScale(spawnHealthScale);
		player.setHealth(spawnHealth);
		player.teleport(spawn);
		player.setGameMode(spawnGameMode);
		player.getInventory().setContents(spawnInventory);
		player.setFoodLevel(spawnHunger);


		removePotionEffects();
		HandlerList.unregisterAll(this);

		Bukkit.getPluginManager().callEvent(new PlayerLeaveGameEvent(game, player));
	}


	public void damageFrom(Player attacker) {
		Bukkit.getPluginManager().callEvent(new PlayerHurtEvent(game, player, attacker));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerWalkedIntoBomb(PlayerOnBombEvent e) {
		if (e.getPlayer() != this.player)
			return;

		Bukkit.getPluginManager().callEvent(new PlayerHurtEvent(game, player, e.getCause()));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPlayerDamageWithImmunity(PlayerHurtEvent e) {
		if (e.getPlayer() != player)
			return;

		if (immunity > 0 )
			e.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPlayerDamaged(PlayerHurtEvent e) {
		if (e.getPlayer() != player)
			return;

		if (player.getHealth() > 1)
			player.damage(1); // TODO player.damage(int) call the damage event?
		else {
			PlayerKilledEvent killedEvent = new PlayerKilledEvent(game, player, e.getAttacker());
			Bukkit.getPluginManager().callEvent(killedEvent);
			removeFromGame();
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onPlayerPlaceBlock( BlockPlaceEvent e ) {
		if (e.getPlayer() != player)
			return;
		Block b = e.getBlock();
		// create a bomb when placing tnt
		if (b.getType() == game.getSettings().bombMaterial) {
			new Bomb(game, this, b);
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDrinkPotion(final PlayerItemConsumeEvent e ) {
		if ( e.isCancelled() || e.getPlayer() != player )
			return;

		// make potion effects the correct duration
		if ( e.getItem().getItemMeta() instanceof PotionMeta) {
			PotionMeta potion = (PotionMeta)e.getItem().getItemMeta();
			PotionData data = potion.getBasePotionData();

			PotionEffectType effects = data.getType().getEffectType();
			if (effects != null) {
				// Set the potion to do nothing
				potion.setBasePotionData(new PotionData(PotionType.WATER));

				// Add the effect that we want
				potion.addCustomEffect(
						new PotionEffect( effects,
								20* game.getSettings().potionDuration * (data.isExtended() ? 2 : 1),
								data.isUpgraded() ? 1 : 2),
						false );
				// don't need to change custom effects since they are manually changeable
				player.addPotionEffects(potion.getCustomEffects());
			}

			// delete the glass bottle left over (we don't know if it was consumed from right/left hand)
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask(plugin, () -> {
						if (player.getInventory().getItemInMainHand().getType() == Material.GLASS_BOTTLE)
							player.getInventory().setItemInMainHand(null);
						if (player.getInventory().getItemInOffHand().getType() == Material.GLASS_BOTTLE)
							player.getInventory().setItemInOffHand(null);
					});
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (e.getPlayer() == player) {
			removeFromGame();
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerRegen(EntityRegainHealthEvent e) {
		if (e.getEntity() == player) {
			if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC)
				if (game.isSuddenDeath()) {
					Message message = Text.NO_REGEN.getMessage(player);
					message.put("game", game);
					Chat.sendMessage(message);
				} else {
					player.setHealth(Math.min(player.getHealth() + 1, player.getMaxHealth()));
				}
			e.setCancelled( true );
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDamaged(EntityDamageEvent e) {
		if (e.isCancelled() || e.getEntity() != player)
			return;
		// Player cannot be burnt during game play
		player.setFireTicks(0);
		e.setCancelled(true);
	}

	public int bombStrength() {
		int strength = 0;
		for (ItemStack stack : player.getInventory().getContents()) {
			if (stack != null && stack.getType() == game.getSettings().powerMaterial) {
				strength += stack.getAmount();
			}
		}
		return Math.max( strength, 1 );
	}

	public int bombAmount() {
		int strength = 0;
		for ( ItemStack stack : player.getInventory().getContents() ) {
			if (stack != null && stack.getType() == game.getSettings().bombMaterial) {
				strength += stack.getAmount();
			}
		}
		return Math.max( strength, 1 );
	}

	private void removePotionEffects() {
		Server server = player.getServer();
		if (plugin.isEnabled())
			server.getScheduler()
					.scheduleSyncDelayedTask(plugin, () -> {
						player.setFireTicks(0);
						for (PotionEffect effect : player.getActivePotionEffects()) {
							player.removePotionEffect(effect.getType());
						}
					});
	}

	@Override
	public String format(Message message, List<String> args) {
		if ( args.size() == 0 )
			return player.getName();
		if ( args.size() != 1 )
			throw new RuntimeException( "Players can have at most one argument" );
		switch ( args.get( 0 ) ) {
			case "name":
				return player.getName();
			case "lives":
				return Integer.toString((int)player.getHealth() );
			case "power":
				return Integer.toString(bombStrength());
			case "bombs":
					return Integer.toString(bombAmount());
			default:
				return null;
		}
	}

	private static class Immunity {

		private final GamePlayer player;

		Immunity(GamePlayer player) {
			this.player = player;
		}

		public void start(int duration) {
			++player.immunity;
			player.player.getServer().getScheduler().scheduleSyncDelayedTask( plugin, () -> {
					// remove the fire
					if ( --player.immunity == 0 )
						player.player.setFireTicks( 0 );
				}, duration);
		}
	}
}
