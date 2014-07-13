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
				"leave-game",
				"start-game",
				"stop-game",
				"list-games",
				"list-styles",
				"convert-to-game"};
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
		game.deregister();
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
			
		case "convert-to-game":
			if (args.length != 1)
				return false;
			if (sender instanceof Player) {
				if (Game.findGame(args[0]) != null) {
					sender.sendMessage("Game already exists");
				} else {
					Location[] locations = BoardGenerator.getBoundingStructure((Player)sender, args[0]);
					Board board = BoardGenerator.createStyle(args[0], locations[0], locations[1]);
					BoardGenerator.saveBoard(board);
					game = new Game(args[0], locations[0]);
					game.board = board;
					game.oldBoard = board;
					Game.register(game);
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
			game.terminate();
			BoardGenerator.switchBoard(game.board, game.board, game.loc);
			sender.sendMessage("Game reset");
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
						if (rep == null)
							rep = new PlayerRep((Player)sender, game);
						for (String name : Game.allGames()) {
							for (PlayerRep test : Game.findGame(name).players)
								if (test.player == rep.player) {
									sender.sendMessage("You can't join twice!");
									return true;
								}
						}
						rep.joinGame();
						
					} else {
						sender.sendMessage("Game has already started");
					}
				}
			} else {
				sender.sendMessage("You must be a player to join");
			}
			return true;
		
		case "leave-game":
			if (sender instanceof Player) {
				for (String name : Game.allGames()) {
					game = Game.findGame(name);
					PlayerRep rep = game.getPlayerRep((Player)sender);
					if (rep != null) {
						rep.kill(true);
						return true;
					}
					
				}
			}
			sender.sendMessage("You're not part of a game");
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
			
		case "stop-game":
			if (args.length != 1) {
				return false;
			}
			game = Game.findGame(args[0]);
			if (game == null)
				sender.sendMessage("Game not found");
			else if (!game.isPlaying)
				sender.sendMessage("Game hasn't started");
			else {
				game.terminate();
				sender.sendMessage("Game stopped");
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
				Location[] locations = BoardGenerator.getBoundingStructure((Player)sender, args[0]);
				Board board2 = BoardGenerator.createStyle(args[0], locations[0], locations[1]);
				BoardGenerator.saveBoard(board2);
				sender.sendMessage("Style created");
			}
			return true;
			
		case "list-games":
			List<String> games = Game.allGames();
			if (games.size() == 0) {
				sender.sendMessage("No games");
			} else {
				sender.sendMessage("Current games:");
				for (String name : games) {
					sender.sendMessage("* " + name);
				}
			}
			return true;
		
		case "list-styles":
			List<String> styles = BoardGenerator.allBoards();
			if (styles.size() == 0) {
				sender.sendMessage("No styles");
			} else {
				sender.sendMessage("Current games:");
				for (String name : styles) {
					sender.sendMessage("* " + name);
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
				String start = args[0];
				for (String name : BoardGenerator.allBoards()) {
					if (name.startsWith(start))
						options.add(name);
				}
			}
			break;
		case "create-game":
		case "set-style":
			if (args.length == 2) {
				for (String name : BoardGenerator.allBoards()) {
					if (name.startsWith(args[1]))
						options.add(name);
				}
				break;
			} // else do next
		case "start-game":
		case "stop-game":
		case "join-game":
		case "reset-game":
		case "destroy-game":
			if (args.length == 1) {
				for (String name : Game.allGames()) {
					if (name.startsWith(args[0])) 
						options.add(name);
				}
			}
			break;
		}
		return options;
	}
}
