package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.BmExplosionEvent;
import io.github.mdsimmo.bomberman.events.BmPlayerMovedEvent;
import io.github.mdsimmo.bomberman.events.BmPlayerHitIntent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Explosion implements Listener {

	private static final Plugin plugin = Bomberman.instance;

	public static boolean spawnExplosion(Game game, Location center, Player cause, int strength) {

		// Find where the explosion should expand to
		Set<Block> fire = planFire(center, game, strength);

		// Let others know
		BmExplosionEvent event = new BmExplosionEvent(game, cause, fire);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return false;

		// Make the boom
		Set<Block> exploding = new HashSet<>(event.getIgnited());
		for (Block b : fire) {
			b.setType(Material.FIRE);
		}
		Objects.requireNonNull(center.getWorld())
				.playSound( center, Sound.ENTITY_GENERIC_EXPLODE, 1,
				(float)Math.random() + 0.5f );

		// Add an explosion obj to handle cleanup/watching for kills
		Explosion explosion = new Explosion(game, exploding, cause);
		Bukkit.getPluginManager().registerEvents(explosion, plugin);
		return true;
	}

	public static boolean isTouching(Player player, Set<Block> blocks) {
		return blocks.stream().anyMatch(b -> {
			double margin = 0.295; // magical value that seems to be how far fire burns players
			Location l = player.getLocation();
			Location min = b.getLocation().add( 0, -1, 0 );
			Location max = b.getLocation().add( 1, 2, 1 );
			return 	   l.getX() >= min.getX() - margin
					&& l.getX() <= max.getX() + margin
					&& l.getY() >= min.getY() - margin
					&& l.getY() <= max.getY() + margin
					&& l.getZ() >= min.getZ() - margin
					&& l.getZ() <= max.getZ() + margin;
		});
	}

	/**
	 * creates fire in the '+' pattern
	 */
	private static Set<Block> planFire(Location center, Game game, int strength) {
		Set<Block> blocks = new HashSet<>();
		// arms
		blocks.addAll(planFire(center, game, strength, 0, 1));
		blocks.addAll(planFire(center, game, strength, 0, -1));
		blocks.addAll(planFire(center, game, strength, 1, 0));
		blocks.addAll(planFire(center, game, strength, -1, 0));

		// center column
		for (int i = -1; i <= 1; i++) {
			planFire(center, game, 0, i, 0, blocks);
		}
		return blocks;
	}

	/**
	 * creates a line of fire in the given x, z direction;
	 *
	 * @param xstep the unit to step in the x direction
	 * @param zstep the unit to step in the z direction
	 */
	private static Set<Block> planFire(Location center, Game game, int strength, int xstep, int zstep) {
		Set<Block> blocks = new HashSet<>();
		for (int i = 1; i <= strength; i++) {
			planFire(center, game, i * xstep, 1, i * zstep, blocks);
			planFire(center, game, i * xstep, -1, i * zstep, blocks);
			if (planFire(center, game, i * xstep, 0, i * zstep, blocks))
				return blocks;
		}
		return blocks;
	}

	/**
	 * creates fire at the given location if it can.
	 *
	 * @return true if the fire-ball should stop
	 */
	private static boolean planFire(Location center, Game game, int x, int y, int z, Set<Block> blocks) {
		Location l = center.clone().add(z, y, x);
		Block b = l.getBlock();

		// Pass through air/weak things
		if (b.isPassable()) {
			blocks.add(b);
			return false;
		}

		// If hit dirt, blow up one block and then stop
		if (game.getSettings().destructable.contains(b.getType()))
			blocks.add(b);
		return true;
	}

	private final Game game;
	private final Player cause;
	private final Set<Block> blocks;

	private Explosion(Game game, Set<Block> blocks, Player cause) {
		this.game = game;
		this.cause = cause;
		this.blocks = blocks;

		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			// Replace fire with air.
			// Check if fire first because a player may have placed a block onto the space
			blocks.forEach(b -> {
				if (b.getType() == Material.FIRE)
					b.setType(Material.AIR);
			});

			// Give player back their TNT
			// Check for tag in case they have left the game already
			if (cause.getScoreboardTags().contains("bm_player"))
				cause.getInventory().addItem(new ItemStack(game.getSettings().bombItem, 1));

			// Delete ths obj from memory
			HandlerList.unregisterAll(this);
		}, 20);

	}

	public ItemStack drop(Material original) {
		/*if (Math.random() < dropChance && schema.isDropping(type)) {
			var sum = 0
			for (stack in drops)
				sum += stack.getAmount()
			var rand = Math.random() * sum
			for (stack in drops) {
				rand -= stack.getAmount().toDouble()
				if (rand <= 0) {
					val drop = stack.clone()
					drop.setAmount(1)
					l.world!!.dropItem(l, drop)
					return
				}
			}
		}*/
		// TODO loot tables
		return new ItemStack(Material.AIR, 0);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(BmPlayerMovedEvent e) {
		if (e.getGame() != game)
			return;
		if (isTouching(e.getPlayer(), blocks)) {
			BmPlayerHitIntent.hit(e.getPlayer(), cause);
		}
		// FIXME players only burnt on movement (should be every tick)
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAnotherExplosion(BmExplosionEvent e) {
		// Don't double remove blocks
		blocks.removeAll(e.getIgnited());
	}

}