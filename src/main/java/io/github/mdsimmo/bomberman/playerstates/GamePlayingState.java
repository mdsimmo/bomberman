package io.github.mdsimmo.bomberman.playerstates;

import io.github.mdsimmo.bomberman.BlockRep;
import io.github.mdsimmo.bomberman.Bomb;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.Game.State;
import io.github.mdsimmo.bomberman.Game.Stats;
import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.messaging.Chat;
import io.github.mdsimmo.bomberman.messaging.Message;
import io.github.mdsimmo.bomberman.messaging.Text;

import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class GamePlayingState extends PlayerState implements Listener {

	private final Game game;
	private final DyeColor team;
	private ItemStack[] spawnInventory;
	private Location spawn;
	private int spawnHunger;
	private GameMode spawnGameMode;
	private int immunity = 0;
	private boolean isPlaying = false;
	private BlockRep[][][] cageBlocks = new BlockRep[3][4][3];
	private double spawnHealth;
	private double spawnMaxHealth;
	private double spawnHealthScale;

	public GamePlayingState( PlayerRep rep, DyeColor team ) {
		super( rep );
		this.game = rep.getActiveGame();
		this.team = team;
	}

	@Override
	public boolean onEnable() {
		if ( game == null ) {
			Message message = Text.SPECIFY_GAME.getMessage( player );
			Chat.sendMessage( message );
			return false;
		}
		spawn = player.getLocation();
		Vector gameSpawn = game.findSpareSpawn( team );
		if ( gameSpawn == null ) {
			Message message = Text.GAME_FULL.getMessage( player );
			message.put( "game", game );
			Chat.sendMessage( message );
			return false;
		}
		if ( game.getFare() != null ) {
			if ( player.getInventory().containsAtLeast( game.getFare(),
					game.getFare().getAmount() )
					|| player.getGameMode() == GameMode.CREATIVE )
				player.getInventory().removeItem( game.getFare() );
			else {
				Message message = Text.TOO_POOR.getMessage( player ).put(
						"game", game );
				Chat.sendMessage( message );
				return false;
			}
		}
		for ( PlayerRep rep : game.observers ) {
			Message message = Text.PLAYER_JOINED.getMessage( rep.getPlayer() );
			message.put( "game", game ).put( "player", player );
			Chat.sendMessage( message );
		}
		rep.getPlayer().teleport( game.box.corner().add( gameSpawn ) );
		surroundCage();
		spawnGameMode = player.getGameMode();
		spawnHealth = player.getHealth();
		spawnHealthScale = player.getHealthScale();
		spawnMaxHealth = player.getMaxHealth();
		player.setGameMode( GameMode.SURVIVAL );
		player.setHealth( game.getLives() );
		player.setMaxHealth( game.getLives() );
		player.setHealthScale( game.getLives() * 2 );
		player.setExhaustion( 0 );
		spawnHunger = player.getFoodLevel();
		player.setFoodLevel( 10000 ); // just a big number
		rep.removeEffects();
		spawnInventory = player.getInventory().getContents();
		game.initialise( rep );
		game.addPlayer( rep );
		isPlaying = true;
		plugin.getServer().getPluginManager().registerEvents( this, plugin );
		return true;
	}

	public void gameStarted() {
		removeCage();
	}

	@SuppressWarnings( "deprecation" )
	private void surroundCage() {
		Location loc = rep.getPlayer().getLocation();
		Location temp = loc.clone();
		for ( int i = -1; i <= 1; i++ ) {
			for ( int j = 0; j < 4; j++ ) {
				for ( int k = -1; k <= 1; k++ ) {
					if ( ( j == 1 || j == 2 ) && j == 0 && k == 0 )
						continue;
					temp.setX( loc.getX() + i );
					temp.setY( loc.getY() + j );
					temp.setZ( loc.getZ() + k );
					Block b = temp.getBlock();
					if ( b.getType().isSolid() )
						continue;
					cageBlocks[i + 1][j][k + 1] = BlockRep.createBlock( b );
					b.setType( Material.STAINED_GLASS );
					b.setData( DyeColor.WHITE.getData() );
				}
			}
		}
	}

	private void removeCage() {
		Location loc = rep.getPlayer().getLocation();
		Location temp = loc.clone();
		for ( int i = -1; i <= 1; i++ ) {
			for ( int j = 0; j < 4; j++ ) {
				for ( int k = -1; k <= 1; k++ ) {
					BlockRep bRep = cageBlocks[i + 1][j][k + 1];
					if ( bRep == null )
						continue;
					temp.setX( loc.getX() + i );
					temp.setY( loc.getY() + j );
					temp.setZ( loc.getZ() + k );
					bRep.setBlock( temp.getBlock() );
				}
			}
		}
	}

	@Override
	public boolean onDisable() {
		if ( isPlaying )
			return false;
		System.out.println( "Removed event listener from " + player.getName() );
		HandlerList.unregisterAll( this );
		return true;
	}

	public Game getGame() {
		return game;
	}

	/**
	 * Kills the player and notifies the joined game
	 * 
	 * @return true if killed successfully
	 */
	public void kill() {
		System.out.println( "player: " + player.getName() + " " + spawnHealth);
		isPlaying = false;
		removeCage();
		rep.switchStates( null );
		player.setGameMode( spawnGameMode );
		player.getInventory().setContents( spawnInventory );
		game.alertRemoval( rep );
		player.setMaxHealth( spawnMaxHealth );
		player.setHealth( spawnHealth );
		player.setHealthScale( spawnHealthScale );
		player.setFoodLevel( spawnHunger );
		player.teleport( spawn );
		rep.removeEffects();
	}

	@EventHandler( priority = EventPriority.HIGHEST )
	public void onPlayerPlaceBlock( BlockPlaceEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != player )
			return;
		Block b = e.getBlock();
		// create a bomb when placing tnt
		if ( b.getType() == game.getBombMaterial() && game.state == State.PLAYING ) {
			new Bomb( game, rep, b );
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerBreakBlock( BlockBreakEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != player )
			return;
		e.setCancelled( true );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDropItem( PlayerDropItemEvent e ) {
		if ( e.isCancelled() || !enabled || player != e.getPlayer() )
			return;
		// waiting for game to start
		if ( game.state == State.STARTING )
			e.setCancelled( true );
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerTelepot( PlayerTeleportEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != this.player )
			return;
		if ( !game.box.contains( e.getTo() ) ) {
			Message message = Text.TELEPORT_DENIED.getMessage( player );
			message.put( "game", game );
			Chat.sendMessage( message );
			e.setCancelled( true );
		}
	}

	// an attempt at making potion effects shorter
	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDrinkPotion( PlayerItemConsumeEvent e ) {
		if ( e.isCancelled() || !enabled || e.getPlayer() != player )
			return;

		if ( e.getItem().getType() == Material.POTION ) {
			Potion potion = Potion.fromItemStack( e.getItem() );
			if ( potion.getType() == PotionType.INSTANT_HEAL
					|| potion.getType() == PotionType.INSTANT_DAMAGE ) {
				// instant potions don't need the duration changed
			} else {
				e.setCancelled( true );
				// hack to fix bug that creates a ghost bottle
				plugin.getServer().getScheduler()
						.scheduleSyncDelayedTask( plugin, new Runnable() {
							@SuppressWarnings( "deprecation" )
							@Override
							public void run() {
								player.updateInventory();
							}
						} );

				player.addPotionEffect( new PotionEffect( potion.getType()
						.getEffectType(), 20 * game.getPotionDuration(), 1 ),
						true );
			}

			// remove the bottle
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask( plugin, new Runnable() {
						@Override
						public void run() {
							player.setItemInHand( null );
						}
					} );
		}
	}

	@EventHandler( priority = EventPriority.HIGH )
	public void onPlayerLeave( PlayerQuitEvent e ) {
		if ( e.getPlayer() == player && enabled ) {
			kill();
			rep.switchStates( null );
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerRegen( EntityRegainHealthEvent e ) {
		if ( e.getEntity() == player && game != null ) {
			if ( e.getRegainReason() == RegainReason.MAGIC )
				if ( game.isSuddenDeath() ) {
					Message message = Text.NO_REGEN.getMessage( player );
					message.put( "game", game );
					Chat.sendMessage( message );
				} else
					player.setHealth( Math.min( player.getHealth() + 1,
							player.getMaxHealth() ) );

			e.setCancelled( true );
		}
	}

	@EventHandler( priority = EventPriority.LOWEST )
	public void onPlayerDamaged( EntityDamageEvent e ) {
		if ( e.isCancelled() || !enabled )
			return;
		if ( e.getEntity() instanceof Player && (Player)e.getEntity() == player ) {
			player.setFireTicks( 0 );
			e.setCancelled( true );
		}
	}

	public int bombStrength() {
		int strength = 0;
		if ( game == null )
			return 0;
		for ( ItemStack stack : player.getInventory().getContents() ) {
			if ( stack != null && stack.getType() == game.getPowerMaterial() ) {
				strength += stack.getAmount();
			}
		}
		return Math.max( strength, 1 );
	}
	
	public int bombAmount() {
		int strength = 0;
		if ( game == null )
			return 0;
		for ( ItemStack stack : player.getInventory().getContents() ) {
			if ( stack != null && stack.getType() == game.getBombMaterial() ) {
				strength += stack.getAmount();
			}
		}
		return Math.max( strength, 1 );
	}

	public void damage( PlayerRep attacker ) {
		boolean dead = false;
		if ( immunity > 0 )
			return;
		if ( player.getHealth() > 1 )
			player.damage( 1 );
		else
			dead = true;
		new Immunity();

		Stats playerStats = game.getStats( rep );
		Stats attackerStats = game.getStats( attacker );

		attackerStats.hitsGiven++;
		playerStats.hitsTaken++;

		if ( !dead ) {
			if ( attacker == rep ) {
				Message message = game.getMessage( Text.HIT_SUICIDE, player );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );
			} else {
				Message message = game.getMessage( Text.HIT_BY, player );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );

				message = game.getMessage( Text.HIT_OPPONENT, attacker.getPlayer() );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );
			}
		} else {
			playerStats.deaths++;
			attackerStats.kills++;
			if ( attacker == rep ) {
				Message message = game.getMessage( Text.KILL_SUICIDE, player );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );
				playerStats.suicides++;
			} else {
				Message message = game.getMessage( Text.KILLED_BY, player );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );

				message = game.getMessage( Text.KILL_OPPONENT, attacker.getPlayer() );
				message.put( "attacker", attacker ).put( "defender", rep );
				Chat.sendMessage( message );
			}
		}

		if ( dead )
			kill();
	}

	private class Immunity implements Runnable {

		public Immunity() {
			immunity++;
			plugin.getServer().getScheduler()
					.scheduleSyncDelayedTask( plugin, this, 20 );
		}

		@Override
		public void run() {
			immunity--;
		}
	}
}