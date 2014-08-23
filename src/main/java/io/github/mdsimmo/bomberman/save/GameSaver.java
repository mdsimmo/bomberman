package io.github.mdsimmo.bomberman.save;

import io.github.mdsimmo.bomberman.BoardGenerator;
import io.github.mdsimmo.bomberman.Game;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;


public class GameSaver extends Save {

	private Game game;
	
	public GameSaver(Game game) {
		super(game.name + ".game");
		this.game = game;
	}
	
	public GameSaver(File file) {
		super(file);
	}

	@Override
	public void save() {
		set("name", game.name);
		set("location.world", game.loc.getWorld().getName());
		set("location.x", game.loc.getBlockX());
		set("location.y", game.loc.getBlockY());
		set("location.z", game.loc.getBlockZ());
		set("arena.current", game.board.name);
		set("arena.old", game.oldBoard.name);
		BoardGenerator.saveBoard(game.oldBoard);
		
		super.save();
	}
	
	public static void loadGame(File file) {
		GameSaver save = new GameSaver(file);;
		save.convert(save.getVersion("version"));
		save.set("version", plugin.getDescription().getVersion());
	
		String name = save.getString("name");
		int x = save.getInt("location.x");
		int y = save.getInt("location.y");
		int z = save.getInt("location.z");
		World w = plugin.getServer().getWorld(save.getString("location.world"));
		Game game = new Game(name, new Location(w, x, y, z));
		game.board = BoardGenerator.loadBoard(save.getString("arena.current"));
		game.oldBoard = BoardGenerator.loadBoard(save.getString("arena.old"));
		Game.register(game);
	}
	
	@Override
	public void convert(Version version) {
		switch (version) {
		case PAST:
			convertFromOld();
			break;
		case FUTURE:
			plugin.getLogger().info("Unkowen verion " + version + " in " + file.getName());
			break;
		case V0_0_3:
			break;
		default:
			convertFromOld();
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
