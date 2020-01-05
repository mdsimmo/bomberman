package io.github.mdsimmo.bomberman.game.gamestate;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.GameStoppedEvent;
import io.github.mdsimmo.bomberman.events.PlayerLeaveGameEvent;
import io.github.mdsimmo.bomberman.game.Bomb;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GamePlayingState implements GameState {

    private final Game game;
    private final Set<GamePlayer> players;
    private final int runningId;
    HashMap<Location, List<Bomb.DeathBlock>> deathBlocks = new HashMap<>();
    Map<Block, Bomb> explosions = new HashMap<>();

    public GamePlayingState(Game game, Set<GamePlayer> players) {
        this.game = game;
        this.players = players;

        runningId = Bukkit.getScheduler().scheduleSyncRepeatingTask(Bomberman.instance, () -> {
            for (GamePlayer p : players) {
                for (Location d : deathBlocks.keySet()) {
                    if (Bomb.DeathBlock.touching(p.player, d)) {
                        p.damageFrom(deathBlocks.get(d).get(0).cause);
                    }
                }
            }
        }, 1, 1);
    }

    @Override
    public void terminate() {
        while (!players.isEmpty()) {
            players.iterator().next().removeFromGame();
        }
        // The PlayerLeaveGameHandler will notice the last player leaving and stop the game
    }

    @EventHandler
    public void OnPlayerLeave(PlayerLeaveGameEvent e) {
        if (e.getGame() != game)
            return;

        players.removeIf(g -> g.player == e.getPlayer());

        if (players.size() == 1) {
            GamePlayer winner = players.iterator().next();
            winner.removeFromGame();

            Bukkit.getPluginManager().callEvent(new GameStoppedEvent(game));
        }
    }

    @EventHandler
    public void OnGameStopped(GameStoppedEvent e) {
        if (e.getGame() != game)
            return;

        Bukkit.getScheduler().cancelTask(runningId);
    }
}
