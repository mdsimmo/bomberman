package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

public class Explosion implements Listener {

	public static class BlockPlan {
		public final Block block;
		public final BlockState prior, ignited, destroyed;

		public BlockPlan(Block block, BlockState prior, BlockState ignited, BlockState destroyed) {
			this.block = block;
			this.prior = prior;
			this.ignited = ignited;
			this.destroyed = destroyed;
		}
	}

	private static final Plugin plugin = Bomberman.instance;

	public static boolean spawnExplosion(Game game, Location center, Player cause, int strength) {

		// Find where the explosion should expand to
		Set<Block> firePlanned = planFire(center, game, strength);
		Set<BlockPlan> plannedTypes = firePlanned.stream()
				.map(b -> {
					BlockState prior = b.getState();
					BlockState ignited = b.getState();
					ignited.setType(Material.FIRE);
					BlockState converted = b.getState();
					converted.setType(Material.AIR);
					return new BlockPlan(b, prior, ignited, converted);
				})
				.collect(Collectors.toSet());

		// Let others know
		BmExplosionEvent event = new BmExplosionEvent(game, cause, plannedTypes);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled())
			return false;

		// Save the current state of all exploding blocks (for drops generation latter)
		var igniting = event.getIgniting();
		igniting.forEach(b -> b.ignited.update(true));
		Objects.requireNonNull(center.getWorld())
				.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 1,
				(float)Math.random() + 0.5f );

		// Add an explosion obj to handle cleanup/watching for kills
		Explosion explosion = new Explosion(game, igniting, cause);
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
		if (game.getSettings().getDestructable().contains(b.getType()))
			blocks.add(b);
		return true;
	}

	private final Game game;
	private final Player cause;
	private final Set<BlockPlan> blocks;
	private final int taskId;

	private Explosion(Game game, Set<BlockPlan> blocks, Player cause) {
		this.game = game;
		this.cause = cause;
		this.blocks = blocks;

		this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			// Replace fire with air.
			// Note: Check if fire first because a player may have placed a block onto the space.
			// We cannot just use state.update(false) because the state was captured taken BEFORE the block was
			// converted into fire, so that would always do nothing
			blocks.forEach(b -> {
				if (b.ignited.getType() == b.block.getType()) {
					b.destroyed.update(true);
				}
			});

			// Give player back their TNT
			// Check for tag in case they have left the game already
			if (cause.getScoreboardTags().contains("bm_player"))
				cause.getInventory().addItem(new ItemStack(game.getSettings().getBombItem(), 1));

			// Drop loot
			var dropsPlaned = planDrops();
			var lootEvent = new BmDropLootEvent(game, cause, blocks, dropsPlaned);
			Bukkit.getPluginManager().callEvent(lootEvent);
			if (!lootEvent.isCancelled()) {
				lootEvent.getDrops().forEach((location, items) -> items.forEach(item -> {
					if (item.getAmount() > 0) {
						Objects.requireNonNull(location.getWorld())
								.dropItem(location.clone().add(0.5, 0.5, 0.5), item);
					}
				}));
			}

			// Delete ths obj from memory
			HandlerList.unregisterAll(this);
		}, 20);

	}

	private Map<Location, Set<ItemStack>> planDrops() {
		Map<Material, Map<ItemStack, Number>> loot = game.getSettings().getBlockLoot();
		return blocks.stream()
				.map(b -> new AbstractMap.SimpleEntry<>(
						b.block.getLocation(),
						lootSelect(loot.getOrDefault(b.prior.getType(), Collections.emptyMap()))))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	static <T> Set<T> lootSelect(Map<? extends T, ? extends Number> loot) {
		double sum = loot.values().stream()
				.map(Number::doubleValue)
				.reduce(0.0, Double::sum);
		for (var entry : loot.entrySet()) {
			var item = entry.getKey();
			var weight = entry.getValue().doubleValue();
			if (sum * Math.random() <= weight) {
				return new HashSet<>(Set.of(item));
			}
			sum -= weight;
		}

		if (sum == 0)
			return new HashSet<>();
		else
			throw new RuntimeException("Explosion.drop didn't select (should never happen)");
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerMove(BmPlayerMovedEvent e) {
		if (e.getGame() != game)
			return;
		if (isTouching(e.getPlayer(), blocks.stream().map(b -> b.block).collect(Collectors.toSet()))) {
			BmPlayerHitIntent.hit(e.getPlayer(), cause);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onAnotherExplosion(BmExplosionEvent e) {
		// Don't double remove blocks
		blocks.removeIf(thisBlock -> e.getIgniting().stream()
				.anyMatch(eventBlock -> eventBlock.block.equals(thisBlock.block)));
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onRunStopped(BmRunStoppedIntent e) {
		if (e.getGame() != game)
			return;
		Bukkit.getScheduler().cancelTask(taskId);
	}
}