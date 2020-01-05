package io.github.mdsimmo.bomberman.game.gamestate;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.events.PlayerJoinGameEvent;
import io.github.mdsimmo.bomberman.game.Game;
import io.github.mdsimmo.bomberman.game.GamePlayer;
import io.github.mdsimmo.bomberman.messaging.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.*;
import java.util.stream.Collectors;

/**
 * GameState for when the game is about to start or waiting for players to join. This state handles the game count down
 * and starting the actual game
 */
public class GameWaitingState implements GameState {

    private class StartTimer implements Runnable {

        private int time = -1;
        int taskID = -1;

        void start(int time) {
            this.time = time;
            taskID = Bomberman.instance.getServer().getScheduler()
                    .scheduleSyncRepeatingTask(Bomberman.instance, this,0, 1000);
        }

        void skipTo(int time) {
            if (this.time > time) {
                // move the time forwards and restart the task timer so not to get a partial second on the first count
                this.time = time;
                Bomberman.instance.getServer().getScheduler()
                        .cancelTask(taskID);
                taskID = Bomberman.instance.getServer().getScheduler()
                        .scheduleSyncRepeatingTask(Bomberman.instance, this, 0, 1000);
            }
        }

        void stop() {
            Bomberman.instance.getServer().getScheduler()
                    .cancelTask(taskID);
        }

        @Override
        public void run() {
            for (GamePlayer p : players) {
                p.player.sendTitle(
                        game.getMessage(Text.GAME_COUNT, p.player)
                                .put("count", time).toString(),
                        "", 1, 10, 0);
            }
            if (time == 0) {
                stop();
                startGame();
            } else {
                --time;
            }
        }
    }

    private final Game game;
    private final Set<GamePlayer> players = new HashSet<>();

    private final Map<Location, BlockData> cageBlocks;
    private StartTimer timer = null;

    public GameWaitingState(Game game) {
        this.game = game;

        cageBlocks = makeCages();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoinGame(PlayerJoinGameEvent e) {
        if (e.isCancelled() || e.getGame() != game)
            return;

        // Check if there is spare room
        Location gameSpawn = findSpareSpawn().orElse(null);
        if (gameSpawn == null) {
            e.setCancelled(true);
            // TODO message player game is too full
            return;
        }

        GamePlayer player = new GamePlayer(e.getPlayer(), game, gameSpawn);
        players.add(player);

        // Trigger auto-start if needed
        if (game.getSettings().autostart) {
            if (!findSpareSpawn().isPresent()) {
                startTimer(5);
            } else if (players.size() >= game.getSettings().minPlayers) {
                startTimer(game.getSettings().autostartDelay);
            }
        }
    }

    private Optional<Location> findSpareSpawn() {
        ArrayList<Location> spawns = game.getArena().spawns().stream()
                .map(it -> game.getLoc().clone().add(it).add(0, 0.1, 0))
                .collect(Collectors.toCollection(ArrayList::new));

        Collections.shuffle(spawns);

        while (!spawns.isEmpty()) {
            Location spawn = spawns.remove(spawns.size()-1);

            // Check no player is in the location
            for (GamePlayer p : players) {
                Location playerLocation = p.player.getLocation();
                if (spawn.getBlockX() != playerLocation.getBlockX()
                        || spawn.getBlockY() != playerLocation.getBlockY()
                        || spawn.getBlockZ() != playerLocation.getBlockZ()) {
                    return Optional.of(spawn);
                }
            }
        }

        return Optional.empty();
    }

    private void startTimer(int count) {
        if (timer == null) {
            timer = new StartTimer();
            timer.start(count);
        } else {
            timer.skipTo(count);
        }
    }

    private void startGame() {
        removeCages();
    }

    @Override
    public void terminate() {
        // Clear the current players
        for (GamePlayer p : players) {
            p.removeFromGame();
        }
        players.clear();

        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    private void removeCages() {
        cageBlocks.forEach((loc, data) -> loc.getBlock().setBlockData(data));
    }

    private Map<Location, BlockData> makeCages() {
        return game.getArena().spawns().stream()
                .map(v -> game.getLoc().clone().add(v))
                .map(this::makeCage)
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Location, BlockData> makeCage(Location location) {
        Map<Location, BlockData> cage = new HashMap<>();
        for ( int i = -1; i <= 1; i++ ) {
            for ( int j = -1; j <= 2; j++ ) {
                for ( int k = -1; k <= 1; k++ ) {
                    if ( ( j == 0 || j == 1 ) && i == 0 && k == 0 )
                        continue;
                    Location blockLoc = location.clone().add(i, j, k);
                    Block b = blockLoc.getBlock();
                    if ( b.getType().isSolid() )
                        continue;
                    cage.put(blockLoc, b.getBlockData());
                    b.setType(Material.IRON_BARS);
                }
            }
        }
        return cage;
    }

}
