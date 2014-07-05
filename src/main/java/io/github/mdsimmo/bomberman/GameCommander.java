package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GameCommander implements CommandExecutor, TabCompleter {
	
	private JavaPlugin plugin = Bomberman.instance;
		
	public GameCommander() {
		String[] commands = {
				"create-game", 
				"destroy-game",
				"set-style",
				"create-style",
				"reset-game", 
				"join-game",
				"start-game",
				"list-games",};
		for (String cmd : commands) {
			plugin.getCommand(cmd).setExecutor(this);
			plugin.getCommand(cmd).setTabCompleter(this);
			
		}
	}
	
	private void createGame(String name, Location l, Board style) {
		Game game = new Game(name, l);
		game.board = style;
		game.oldBoard = BoardGenerator.createStyle(name+".old", game.loc, game.board.xSize, game.board.ySize, game.board.zSize);
		BoardGenerator.switchBoard(game.oldBoard, game.board, game.loc);
		Game.register(game);
	}
	
	private void destroyGame(Game game) {
		Game.deregister(game);
		BoardGenerator.switchBoard(game.board, game.oldBoard, game.loc);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		String cmd = command.getName().toLowerCase();
		Game game;
		switch (cmd) {
		case "create-game":
			if (!(args.length == 1 || args.length == 2))
				return false;
			if (sender instanceof Player) {
				if (Game.findGame(args[0]) != null) {
					sender.sendMessage("Game already exists");
				} else {
					Board style;
					if (args.length == 2) {
						style = BoardGenerator.loadBoard(args[1]);
					} else {
						style = BoardGenerator.loadDefault();
					}
					if (style == null) {
						sender.sendMessage("Style not found");
						return true;
					}
					createGame(args[0], ((Player)sender).getLocation(), style);
					sender.sendMessage("Game created");
				}
			} else {
				sender.sendMessage("You must be a player");
			}
			return true;
		
		case "destroy-game":
			if (args.length != 1)
				return false;
			game = Game.findGame(args[0]); 
			if (game != null) {
				destroyGame(game);
				sender.sendMessage("Game destroyed");
			} else
				sender.sendMessage("Game not found");
			return true;
			
		case "reset-game":
			if (args.length != 1)
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			}
			destroyGame(game);
			createGame(game.name, game.loc, game.board);
			return true;
			
			
		case "join-game":
			if (args.length != 1)
				return false;
			if (sender instanceof Player) {
				game = Game.findGame(args[0]); 
				if (game == null) {
					sender.sendMessage("Game not found");
				} else {
					if (game.isPlaying == false) {
						PlayerRep rep = game.getPlayerRep((Player)sender); 
						if (rep == null) {
							rep = new PlayerRep((Player)sender, game);
							rep.joinGame();
						} else {
							rep.joinGame();
						}
						
					} else {
						sender.sendMessage("Game has already started");
					}
				}
			} else {
				sender.sendMessage("You must be a player to join");
			}
			return true;
			
		case "start-game":
			if (args.length != 1) {
				return false;
			}
			game = Game.findGame(args[0]);
			if (game == null)
				sender.sendMessage("Game not found");
			else if (game.isPlaying)
				sender.sendMessage("Game already started");
			else {
				if (game.startGame())
					sender.sendMessage("Game starting");
				else
					sender.sendMessage("There must be at least one player");
			}
			return true;
		
		case "set-style":
			if (args.length != 2)
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			}
			if (game.isPlaying) {
				sender.sendMessage("Game in progress");
				return true;
			}
			Board board = BoardGenerator.loadBoard(args[1]);
			if (board == null) {
				sender.sendMessage("Style not found");
				return true;
			}
			BoardGenerator.switchBoard(game.board, game.oldBoard, game.loc);
			game.board = board;
			game.oldBoard = BoardGenerator.createStyle(game.name+".old", game.loc, game.board.xSize, game.board.ySize, game.board.zSize);
			BoardGenerator.switchBoard(game.oldBoard, game.board, game.loc);
			sender.sendMessage("Game style changed");
			return true;
			
		case "create-style":
			if (args.length != 1)
				return false;
			if (sender instanceof Player) {
				Board board2 = BoardGenerator.createStyle((Player)sender, args[0]);
				BoardGenerator.saveBoard(board2);
				sender.sendMessage("Style created");
			}
			return true;
		
		case "list-games":
			List<Game> games = Game.allGames();
			if (games.size() == 0) {
				sender.sendMessage("No games");
			} else {
				sender.sendMessage("Current games:");
				for (Game game2 : Game.allGames()) {
					sender.sendMessage("* " + game2.name);
				}
			}
			return true;
		}
			
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		String cmd = command.getName().toLowerCase();
		List<String> options = new ArrayList<>();
		switch (cmd) {
		case "create-style":
			if (args.length == 1) {
				for (Board board : BoardGenerator.allBoards())
					if (board.name.startsWith(args[1]))
						options.add(board.name);
			}
			break;
		case "set-style":
			if (args.length == 2) {
				for (Board board : BoardGenerator.allBoards())
					if (board.name.startsWith(args[1]))
						options.add(board.name);
				break;
			} // else do next
		case "start-game":
		case "join-game":
		case "restart-game":
		case "destroy-game":
			if (args.length == 1) {
				for (Game game : Game.allGames()) {
					if (game.name.startsWith(args[0])) 
						options.add(game.name);
				}
			}
			break;
		}
		return options;
	}
}
