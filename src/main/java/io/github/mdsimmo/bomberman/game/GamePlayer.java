package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.game.gamestate.GameStartingState;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamePlayer implements Formattable, Listener {

	private static JavaPlugin plugin = Bomberman.instance;

	final Player player;
	private final Game game;
	private final Stats stats;

	private int immunity = 0;
	private final Map<Location, BlockData> cageBlocks;

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
		this.stats = new Stats();

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
		player.setGameMode(GameMode.SURVIVAL);
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
		cageBlocks = surroundCage();
	}

	public void gameStarted() {
		removeCage();
	}

	/**
	 * Removes the player from the game
	 */
	void removeFromGame() {
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(spawnMaxHealth);
		player.setHealthScale( spawnHealthScale );
		player.setHealth( spawnHealth );
		player.teleport( spawn );
		player.setGameMode( spawnGameMode );
		player.getInventory().setContents( spawnInventory );
		player.setFoodLevel( spawnHunger );

		removeCage();
		removePotionEffects();
		HandlerList.unregisterAll(this);
	}

	private Map<Location, BlockData> surroundCage() {
		HashMap<Location, BlockData> cage = new HashMap<>();
		Location playerLoc = player.getLocation();
		for ( int i = -1; i <= 1; i++ ) {
			for ( int j = -1; j <= 2; j++ ) {
				for ( int k = -1; k <= 1; k++ ) {
					if ( ( j == 0 || j == 1 ) && i == 0 && k == 0 )
						continue;
					Location blockLoc = playerLoc.clone().add(i, j, k);
					Block b = blockLoc.getBlock();
					if ( b.getType().isSolid() )
						continue;
					cage.put(blockLoc, b.getBlockData());
					b.setType( Material.WHITE_STAINED_GLASS);
				}
			}
		}
		return cage;
	}

	public void damage(GamePlayer attacker) {
		boolean dead = false;
		if ( immunity > 0 )
			return;

		if ( player.getHealth() > 1 )
			player.damage( 1 );
		else
			dead = true;
		new Immunity(this).start(20);

		++attacker.stats.hitsGiven;
		++stats.hitsTaken;

		if ( !dead ) {
			Message message;
			if ( attacker == this ) {
				message = game.getMessage(Text.HIT_SUICIDE, player);
			} else {
				message = game.getMessage(Text.HIT_BY, player );
				message.put("attacker", attacker).put("defender", this);
				Chat.sendMessage(message);

				message = game.getMessage(Text.HIT_OPPONENT, player);
			}
			message.put("attacker", attacker).put("defender", this);
			Chat.sendMessage(message);
		} else {
			stats.deaths++;
			attacker.stats.kills++;
			if (attacker == this) {
				Message message = game.getMessage(Text.KILL_SUICIDE, player);
				message.put("attacker", attacker).put("defender", this);
				Chat.sendMessage(message);
				stats.suicides++;
			} else {
				Message message = game.getMessage(Text.KILLED_BY, player);
				message.put("attacker", attacker).put("defender", this);
				Chat.sendMessage(message);

				message = game.getMessage(Text.KILL_OPPONENT, attacker.player);
				message.put("attacker", attacker).put("defender", this);
				Chat.sendMessage(message);
			}
		}

		if (dead)
			removeFromGame();
	}


	private void removeCage() {
		cageBlocks.forEach((loc, data) -> loc.getBlock().setBlockData(data));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPlaceBlock( BlockPlaceEvent e ) {
		if ( e.isCancelled() || e.getPlayer() != player )
			return;
		Block b = e.getBlock();
		// create a bomb when placing tnt
		if (b.getType() == game.getSettings().bombMaterial) {
			new Bomb(game, this, b);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerBreakBlock(BlockBreakEvent e) {
		if ( e.isCancelled() || e.getPlayer() != player )
			return;
		e.setCancelled( true );
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		if (e.isCancelled() || player != e.getPlayer())
			return;
		// waiting for game to start
		if (game.state instanceof GameStartingState)
			e.setCancelled( true );
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
		if (e.isCancelled())
			return;
		// Player cannot be burnt during game play
		if (e.getEntity() == player) {
			player.setFireTicks( 0 );
			e.setCancelled(true);
		}
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
