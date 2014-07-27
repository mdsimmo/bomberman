package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class GameCommander implements CommandExecutor, TabCompleter {
	
	private JavaPlugin plugin = Bomberman.instance;
	public GameCommander() {
		String[] commands = {
				"create-game", 
				"destroy-game",
				"style",
				"lives",
				"power", 
				"bombs",
				"min-players",
				"create-style",
				"reset-game", 
				"join-game",
				"leave-game",
				"start-game",
				"stop-game",
				"list-games",
				"list-styles",
				"convert-to-game",
				"fare",
				"prize",
				"info"};
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
					sender.sendMessage("There are not enough players");
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
		
		case "style":
			if (!(args.length == 1 || args.length == 2))
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage("Style: " + game.board.name);
				return true;
			} else {
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
				game.oldBoard = BoardGenerator.createStyle(game.name+".old", game.loc, board.xSize, board.ySize, board.zSize);
				BoardGenerator.switchBoard(game.oldBoard, board, game.loc);
				sender.sendMessage("Game style changed");
				return true;
			}
			
		case "power":
		case "bombs":
		case "lives":
		case "min-players":
			if (!(args.length == 1 || args.length == 2))
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			} else {
				if (args.length == 1) {
					if (cmd.equals("bombs"))
						sender.sendMessage("Starting bombs: " + game.bombs);
					else if (cmd.equals("power"))
						sender.sendMessage("Starting power: " + game.power);
					else if (cmd.equals("lives"))
						sender.sendMessage("Starting lives: " + game.lives);
					else if (cmd.equals("min-players"))
						sender.sendMessage("Min players: " + game.minPlayers);
					return true;
				} else {
					int num = 0;
					try {
						 num = Integer.parseInt(args[1], 10);
					} catch (NumberFormatException e) {
						return false;
					}
					if (cmd.equals("bombs"))
						game.bombs = num;
					else if (cmd.equals("power"))
						game.power = num;
					else if (cmd.equals("lives"))
						game.lives = num;
					else if (cmd.equals("min-players"))
						game.minPlayers = num;
					sender.sendMessage(StringUtils.capitalize(cmd) + " set");
					return true;
				}
			}
		
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
					game = Game.findGame(name);
					String status = " * " + game.name;
					status += " : " + game.players.size() + "/" + game.board.spawnPoints.size() + " : ";
					if (game.isPlaying)
						status += "playing";
					else
						status += "waiting  ";
							
					sender.sendMessage(status);
				}
			}
			return true;
		
		case "list-styles":
			List<String> styles = BoardGenerator.allBoards();
			if (styles.size() == 0) {
				sender.sendMessage("No styles");
			} else {
				sender.sendMessage("Current styles:");
				for (String name : styles) {
					if (!name.endsWith(".old"))
						sender.sendMessage(" * " + name);
				}
			}
			return true;
			
		case "prize":
		case "fare":
			if (args.length < 1 || args.length > 3)
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			}
			if (args.length == 1) {
				if (cmd.equals("fare")) {
					if (game.fare != null)
						sender.sendMessage("Fare: " + game.fare.getAmount() + " " + game.fare.getType());
					else
						sender.sendMessage("No fare");
				} else {
					if (game.prize != null)
						sender.sendMessage("Prize: " + game.prize.getAmount() + " " + game.prize.getType());
					else
						if (game.pot == true)
							sender.sendMessage("Game has a pot");
						else
							sender.sendMessage("No prize");
				}
				return true;
			} else {
				if (!sender.hasPermission("bomberman.op")) {
					sender.sendMessage("You may only view what the " + cmd + " is");
					return true;
				}
				if (args.length == 2) {
					if (args[1].equalsIgnoreCase("pot")) {
						game.prize = null;
						game.pot = true;
						sender.sendMessage("Prize set");
						return true;
					} else if (args[2].equals("none")) {
						if (cmd.equalsIgnoreCase("fare")) {
							game.fare = null;
							sender.sendMessage("Fare removed");
						} else {
							game.prize = null;
							game.pot = false;
							sender.sendMessage("Prize removed");
						}
						return true;
					}
					return false;
				}
				if (args.length == 3) {
					try {
						Material m = Material.getMaterial(args[2]);
						if (m == null)
							sender.sendMessage("Unknown material");
						int amount = Integer.parseInt(args[3]);
						if (cmd.equalsIgnoreCase("fare")) {
							game.fare = new ItemStack(m, amount);
							sender.sendMessage("Fare set");
						} else {
							game.prize = new ItemStack(m, amount);
							game.pot = false;
							sender.sendMessage("Prize set");
						}
						return true;
					} catch (Exception e) {
						return false;
					}
				}
			}
			return false;
					
		case "info":
			if (args.length != 1)
				return false;
			game = Game.findGame(args[0]);
			if (game == null) {
				sender.sendMessage("Game not found");
				return true;
			}
			String message = "About " + game.name + ":\n";
			message += " * Status: ";
			if (game.isPlaying)
				message += "In progress\n";
			else
				message += "Waiting\n";
			message += " * Players: " + game.players.size() + "\n";
			message += " * Min players: " + game.minPlayers+ "\n";
			message += " * Max players: " + game.board.spawnPoints.size() + "\n";
			message += " * Init bombs: " + game.bombs + "\n";
			message += " * Init lives: " + game.lives + "\n";
			message += " * Init power: " + game.power + "\n";
			message += " * Entry fare: ";
			if (game.fare == null)
				message += "no fee \n";
			else
				message += game.fare.getType() + " x" + game.fare.getAmount() + "\n";
			message += " * Winner's prize: ";
			if (game.pot == true && game.fare != null)
				message += "Pot currently at " + game.fare.getAmount()*game.players.size() + " " + game.fare.getType() + "\n";
			else {
				if (game.prize == null)
					message += "No prize \n";
				else
					message += game.prize.getAmount() + " " + game.prize.getType() + "\n";
			}
			message += " * Style: " + game.board.name + "\n";
			sender.sendMessage(message);
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
			if (args.length == 1)
				addStyles(options, args[0]);
			break;
			
		case "create-game":
			if (args.length == 1)
				addGames(options, args[0]);
			if (args.length == 2)
				addStyles(options, args[1]);
			break;
		
		case "style":
			if (args.length == 1)
				addGames(options, args[0]);
			else if (args.length == 2)
				addStyles(options, args[2]);
			break;
		
		case "lives":
		case "bombs":
		case "power":
		case "min-players":
			if (args.length == 1)
				addGames(options, args[0]);
			
		case "info":
		case "start-game":
		case "stop-game":
		case "join-game":
		case "reset-game":
		case "destroy-game":
			if (args.length == 1)
				addGames(options, args[0]);
			break;
		
		case "fare":
		case "prize":
			if (args.length == 1) {
				if (sender.hasPermission("bomberman.op"))
					if ("set".startsWith(args[0].toLowerCase()))
					options.add("set");
				addGames(options, args[0]);
			} else if (args.length > 1 ) {
				if (args[0].equalsIgnoreCase("set")) {
					if (sender.hasPermission("bomberman.op")) {
						if (args.length == 2)
							addGames(options, args[1]);
						else if (args.length == 3) {
							if ("none".startsWith(args[2].toLowerCase()))
								options.add("none");
							if ("pot".startsWith(args[2].toLowerCase()))
								options.add("pot");
							for (Material m : Material.values()) {
								if (m.toString().toLowerCase().startsWith(args[2].toLowerCase()))
									options.add(m.toString());
							}
						}
					}
				}
			}
			break;
		}
		return options;
	}
	
	private void addGames(List<String> options, String start) {
		for (String name : Game.allGames()) {
			if (name.toLowerCase().startsWith(start.toLowerCase()))
				options.add(name);
		}
	}
	
	private void addStyles(List<String> options, String start) {
		for (String name : BoardGenerator.allBoards()) {
			if (name.toLowerCase().startsWith(start.toLowerCase()))
				options.add(name);
		}
	}
}
