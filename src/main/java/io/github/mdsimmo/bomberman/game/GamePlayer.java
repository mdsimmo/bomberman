package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.*;
import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

public class GamePlayer implements Formattable, Listener {

    private static JavaPlugin plugin = Bomberman.instance;

    public static void spawnGamePlayer(@Nonnull Player player, @Nonnull  Game game, @Nonnull Location start) {

        GamePlayer gamePlayer = new GamePlayer(player, game);

        player.getServer().getPluginManager().registerEvents(gamePlayer, plugin);

        // Initialise the player for the game
        player.teleport(start.clone().add(0.5, 0.5, 0.5));
        player.setGameMode(GameMode.SURVIVAL);
        player.setHealth(game.getSettings().getLives());
        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(game.getSettings().getLives());
        // if setHealthScale is not delayed, it can sometimes cause the client side to think they died?!?!?
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () ->player.setHealthScale(game.getSettings().getLives() * 2));
        player.setExhaustion(0);
        player.setFoodLevel(100000); // just a big number
        player.getInventory().clear();
        for (ItemStack stack : game.getSettings().getInitialItems()) {
            ItemStack s = stack.clone();
            player.getInventory().addItem(s);
        }

        gamePlayer.removePotionEffects();
        player.addScoreboardTag("bm_player");
    }

    private final Player player;
    private final Game game;

    private boolean immunity = false;

    private final ItemStack[] spawnInventory;
    private final Location spawn;
    private final int spawnHunger;
    private final GameMode spawnGameMode;
    private final double spawnHealth;
    private final double spawnMaxHealth;
    private final double spawnHealthScale;

    private GamePlayer(Player player, Game game) {
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
    }

    /**
     * Removes the player from the game and removes any hooks to this player. Treats the player like they disconnected
     * from the server.
     */
    private void resetStuffAndUnregister() {
        reset();
        HandlerList.unregisterAll(this);
    }

    private void reset() {
        // remove items in the direct vicinity (prevents player dropping items at spawn)
        player.getWorld().getNearbyEntities(player.getLocation(), 1, 2, 1).stream()
                .filter(it -> it instanceof ItemStack)
                .forEach(Entity::remove);

        player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(spawnMaxHealth);
        player.setHealthScale(spawnHealthScale);
        player.setHealth(spawnHealth);
        player.teleport(spawn);
        player.setGameMode(spawnGameMode);
        player.getInventory().setContents(spawnInventory);
        player.setFoodLevel(spawnHunger);

        player.removeScoreboardTag("bm_player");

        removePotionEffects();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerLeaveGameEvent(BmPlayerLeaveGameIntent e) {
        if (e.getPlayer() != player)
            return;
        if (player.isDead()) {
            // Give player their stuff back when they respawn
            // Attempting to do this when player is dead causes very strange bugs
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onPlayerRespawn(PlayerRespawnEvent e) {
                    e.setRespawnLocation(spawn);
                    reset();
                    HandlerList.unregisterAll(this);
                }
            }, plugin);
            HandlerList.unregisterAll(this);
        } else {
            resetStuffAndUnregister();
        }
        e.setHandled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onGameTerminated(BmGameTerminatedIntent e) {
        if (e.getGame() != this.game)
            return;
        resetStuffAndUnregister();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerMoved(PlayerMoveEvent e) {
        if (e.getPlayer() != player)
            return;
        BmPlayerMovedEvent bmEvent = new BmPlayerMovedEvent(game, player, e.getFrom(), e.getTo());
        Bukkit.getPluginManager().callEvent(bmEvent);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onExplosion(BmExplosionEvent e) {
        // TODO duplicate code: both GamePlayer and Explosion do touching checks
        if (Explosion.isTouching(player, e.getIgniting().stream().map(b->b.block).collect(Collectors.toSet()))) {
            BmPlayerHitIntent.hit(player, e.getCause());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerHit(BmPlayerHitIntent e) {
        if (e.getPlayer() != this.player)
            return;

        BmPlayerHurtIntent.run(game, player, e.getCause());
        e.setHandled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerHurtWithImmunity(BmPlayerHurtIntent e) {
        if (e.getPlayer() != player)
            return;

        if (immunity)
            e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDamaged(BmPlayerHurtIntent e) {
        if (e.getPlayer() != player)
            return;

        if (player.getHealth() > 1) {
            player.damage(1);
            immunity = true;
            player.setFireTicks(22);
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                immunity = false;
                player.setFireTicks(0);
                // Call the player move event to recheck damage required
                Bukkit.getPluginManager().callEvent(new BmPlayerMovedEvent(game, player, player.getLocation(), player.getLocation()));
            }, 22); // 22 is slightly longer than 20 ticks a bomb is active for
        } else {
            BmPlayerKilledIntent.kill(game, player, e.getAttacker());
        }
        e.setHandled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerKilledInGame(BmPlayerKilledIntent e) {
        if (e.getPlayer() != player)
            return;
        player.setHealth(0);
        BmPlayerLeaveGameIntent.leave(player);
        e.setHandled();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerPlaceBlock(BlockPlaceEvent e) {
        if (e.getPlayer() != player)
            return;
        Block b = e.getBlock();
        // create a bomb when placing tnt
        if (b.getType() == game.getSettings().getBombItem()) {
            if (!Bomb.spawnBomb(game, player, b)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogout(PlayerQuitEvent e) {
        if (e.getPlayer() == player) {
            BmPlayerLeaveGameIntent.leave(player);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerRegen(EntityRegainHealthEvent e) {
        if (e.getEntity() == player) {
            if (e.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC) {
                e.setAmount(1);
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDamaged(EntityDamageEvent e) {
        if (e.getEntity() != player)
            return;
        // Allow custom damage events (ie. from plugin)
        if (e.getCause() == EntityDamageEvent.DamageCause.CUSTOM)
            return;
        // Player cannot be burnt or hurt during game play
        e.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerJoinGame(BmPlayerJoinGameIntent e) {
        // Cannot join two games at once
        if (e.getPlayer() == player) {
            e.cancelFor(Text.JOIN_ALREADY_JOINED
                    .with("game", e.getGame())
                    .with("player", player)
                    .format());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onGameStopped(BmRunStoppedIntent e) {
        BmPlayerLeaveGameIntent.leave(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPlayerBreakBlockWithWrongTool(PlayerInteractEvent e) {
        // Disable player from breaking a block
        if (e.getPlayer() != player)
            return;
        if (e.getAction() != Action.LEFT_CLICK_BLOCK || !e.hasBlock()) {
            // Only care about block breaking events
            return;
        }

        // TODO only let player break block if they have used the correct tool
        // Maybe use CanDestroy tag? - That requires NBT though...

        // Cannot break things with hand
        e.setCancelled(true);
        e.setUseInteractedBlock(Event.Result.DENY);
        e.setUseItemInHand(Event.Result.DENY);
        // apply mining fatigue so player doesn't see block breaking
        e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20, 1));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerWon(BmPlayerWonEvent e) {
        if (e.getPlayer() != player)
            return;
        Text.PLAYER_WON.with("player", player).sendTo(player);
        // Let player walk around like a boss
        immunity = true;
    }

    public static int bombStrength(Game game, Player player) {
        int strength = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == game.getSettings().getPowerItem()) {
                strength += stack.getAmount();
            }
        }
        return Math.max(strength, 1);
    }

    private int bombStrength() {
        return bombStrength(game, player);
    }

    private int bombAmount() {
        int strength = 0;
        for (ItemStack stack : player.getInventory().getContents()) {
            if (stack != null && stack.getType() == game.getSettings().getBombItem()) {
                strength += stack.getAmount();
            }
        }
        return Math.max(strength, 1);
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
    public Message format(@Nonnull List<Message> args) {
        if (args.size() == 0)
            return Message.of(player.getName());
        if (args.size() != 1)
            throw new RuntimeException("Players can have at most one argument");
        switch (args.get(0).toString()) {
            case "name":
                return Message.of(player.getName());
            case "lives":
                return Message.of((int) player.getHealth());
            case "power":
                return Message.of(bombStrength());
            case "bombs":
                return Message.of(bombAmount());
            default:
                return null;
        }
    }
}
