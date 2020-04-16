package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.BmExplosionEvent;
import io.github.mdsimmo.bomberman.events.BmPlayerPlacedBombEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public final class Bomb implements Listener {

    private static final Plugin plugin = Bomberman.instance;

    public static boolean spawnBomb(Game game, Player player, Block b) {
        BmPlayerPlacedBombEvent tntPlaceEvent = new BmPlayerPlacedBombEvent(game, player, b);
        Bukkit.getPluginManager().callEvent(tntPlaceEvent);
        if (tntPlaceEvent.isCancelled())
            return false;

        Bomb bomb = new Bomb(game, player, b, tntPlaceEvent.getStrength());
        Bukkit.getPluginManager().registerEvents(bomb, plugin);

        return true;
    }

    private final Game game;
    private final Player player;
    private final Block block;
    private final int strength;
    private int taskId;
    private boolean ran = false;

    private Bomb(Game game, Player player, Block block, int strength) {
        this.game = game;
        this.player = player;
        this.block = block;
        this.strength = strength;
        this.taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::explode, 40);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onExplosion(BmExplosionEvent e) {
        if (!ran && e.getIgnited().contains(block)) {
            // explode one tick latter
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(Bomberman.instance, this::explode);
        }
    }

    private void explode() {
        // The ran flag prevents the tnt from exploding itself twice
        if (ran)
            return;
        ran = true;
        if (Explosion.spawnExplosion(game, block.getLocation(), player, strength)) {
            HandlerList.unregisterAll(this);
        }
        // TODO what should happen to unexploded bomb?
    }

}