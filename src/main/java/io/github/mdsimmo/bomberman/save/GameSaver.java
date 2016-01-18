package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.Board;
import io.github.mdsimmo.bomberman.Game;
import io.github.mdsimmo.bomberman.arenabuilder.ArenaGenerator;
import io.github.mdsimmo.bomberman.utils.Box;

import java.io.File;

import org.bukkit.World;

public class GameSaver extends Save {

	private Game game;
	
	public GameSaver(Game game) {
		super(game.name + ".game");
		this.game = game;
	}
	
	private GameSaver(File file) {
		super(file);
	}

	@Override
	public void save() {
		set("name", game.name);
		set("location.world", game.box.world.getName());
		set("location.x", (int)game.box.x);
		set("location.y", (int)game.box.y);
		set("location.z", (int)game.box.z);
		set("arena.current", game.board.name);
		set("arena.old", game.oldBoard.name);
		ArenaGenerator.saveBoard(game.oldBoard);
		
		super.save();
	}
	
	public static void loadGame(File file) {
		plugin.getLogger().info("Loading game '" + file.getName() + "'");
		GameSaver save = new GameSaver(file);
		save.convert();
		String name = save.getString("name");
		int x = save.getInt("location.x");
		int y = save.getInt("location.y");
		int z = save.getInt("location.z");
		World w = plugin.getServer().getWorld(save.getString("location.world"));
		Board gameBoard = ArenaGenerator.loadBoard(save.getString("arena.current"));
		Game game = new Game(name, new Box(w, x, y, z, gameBoard.xSize, gameBoard.ySize, gameBoard.zSize));
		game.board = gameBoard;
		game.oldBoard = ArenaGenerator.loadBoard(save.getString("arena.old"));
		Game.register(game);
	}
	
	@Override
	public void convert(Version version ) {
		switch (version) {
		case PAST:
		case V0_0_1:
		case V0_0_2:
		case V0_0_2a:
		case V0_0_3_SNAPSHOT:
			convertFromOld();
			break;
		case V0_0_3:
		case V0_0_3a:
		case V0_0_3b:
		case V0_0_3c:
		case V0_0_3d:
			break;
		case FUTURE:
			plugin.getLogger().info("Unknown version '" + getVersionRaw() + "' in " + file.getName());
			break;
		default:
			break;
		}
	}
	
	public void convertFromOld() {
		String temp;
		if (contains("arena.current")) {
			set("style.current", null);
		} else if (contains("style.current")) {
			temp = getString("style.current");
			set("arena.current", temp);
			set("style.current", null);
		}
		
		if (contains("arena.old")) {
			set("style.old", null);
		} else if (contains("style.old")) {
			temp = getString("style.old");
			set("arena.old", temp);
			set("style.old", null);
		}
	}
}
