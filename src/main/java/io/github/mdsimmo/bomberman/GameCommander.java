package io.github.mdsimmo.bomberman;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class GameCommander implements CommandExecutor, TabCompleter {
	
	private JavaPlugin plugin = Bomberman.instance;
	public GameCommander() {
//		String[] commands = {
//			"create-game", 
//			"destroy-game",
//			"arena",
//			"lives",
//			"power", 
//			"bombs",
//			"min-players",
//			"create-arena",
//			"reset-game", 
//			"join-game",
//			"leave-game",
//			"start-game",
//			"stop-game",
//			"list-games",
//			"list-arenas",
//			"convert-to-game",
//			"fare",
//			"prize",
//			"info",
//			"autostart",
//			"autostartdelay"
//		};
//		for (String cmd : commands) {
//			plugin.getCommand(cmd).setExecutor(this);
//			plugin.getCommand(cmd).setTabCompleter(this);
//		}
		plugin.getCommand("bm").setExecutor(this);
		plugin.getCommand("bm").setTabCompleter(this);
	}
	
	private void createGame(String name, Location l, Board arena) {
		Game game = new Game(name, l);
		game.board = arena;
		game.oldBoard = BoardGenerator.createArena(name+".old", game.loc, game.board.xSize, game.board.ySize, game.board.zSize);
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
		ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));
		
		if (argsList.size() == 0) {
		    return sendCommandInfo(sender, "bm");
		} else {
		    String arg = argsList.remove(0);
		    String commandInfo = arg;
		    
		    if (argsList.size() == 0) {
		        return sendCommandInfo(sender, commandInfo);
		    }
		    
		    switch (arg) {
		    case "game":
	            arg = argsList.remove(0);
	            commandInfo += "." + arg;
	            
	            switch (arg) {
	            case "join":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
                    return joinCommand(sender, argsList);
	            case "leave":
                    return leaveCommand(sender, argsList);
	            case "info":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                return infoCommand(sender, argsList);
	            case "list":
	                return listCommand(sender, argsList);
	            case "create":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                return createCommand(sender, argsList);
	            case "destroy":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                return destroyCommand(sender, argsList);
	            case "convert":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                return convertCommand(sender, argsList);
	            case "force":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                
	                arg = argsList.remove(0);
	                commandInfo += "." + arg;
	                
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                
                    switch (arg) {
                    case "start":
                        return startGameCommand(sender, argsList);
                    case "stop":
                        return stopGameCommand(sender, argsList);
                    case "reset":
                        return resetGameCommand(sender, argsList);
                    default:
                        return sendCommandInfo(sender, commandInfo);
                    }
	            case "set":
	                if (argsList.size() == 0) {
	                    return sendCommandInfo(sender, commandInfo);
	                }
	                
                    arg = argsList.remove(0);
                    commandInfo += "." + arg;
                    
                    if (argsList.size() == 0) {
                        return sendCommandInfo(sender, commandInfo);
                    }
                    
                    switch (arg) {
                    case "arena":
                        return setArenaCommand(sender, argsList);
                    case "lives":
                    case "bombs":
                    case "power":
                    case "minplayers":
                        return setGameAttributeCommand(sender, argsList, arg);
                    case "autostart":
                        return setAutostartCommand(sender, argsList);
                    case "autostartdelay":
                        return setAutostartDelayCommand(sender, argsList);
                    case "fare":
                    case "prize":
                        return setEconomyAttributeCommand(sender, argsList, arg);
                    default:
                        return sendCommandInfo(sender, commandInfo);
                    }
                default:
                    return sendCommandInfo(sender, commandInfo);
	            }
		    case "arena":
		        arg = argsList.remove(0);
		        commandInfo += "." + arg;
		        
		        if (argsList.size() == 0) {
		            return sendCommandInfo(sender, commandInfo);
		        }
		        
		        switch (arg) {
		        case "create":
		            return createArenaCommand(sender, argsList);
		        case "list":
		            return listArenasCommand(sender, argsList);
	            default:
	                return sendCommandInfo(sender, commandInfo);
		        }
	        default:
	            return sendCommandInfo(sender, commandInfo);
		    }
		}
	}

	private boolean joinCommand(CommandSender sender, ArrayList<String> argsList) {
	    if (argsList.size() != 1)
            return false;
	    
        if (sender instanceof Player) {
            Game game = Game.findGame(argsList.get(0)); 
            if (game == null) {
                Bomberman.sendMessage(sender, "Game not found");
            } else {
                if (game.isPlaying == false) {
                    PlayerRep rep = game.getPlayerRep((Player)sender); 
                    if (rep == null)
                        rep = new PlayerRep((Player)sender, game);
                    for (String name : Game.allGames()) {
                        for (PlayerRep test : Game.findGame(name).players)
                            if (test.player == rep.player) {
                                Bomberman.sendMessage(sender, "You can't join twice!");
                                return true;
                            }
                    }
                    rep.joinGame();
                    
                } else {
                    Bomberman.sendMessage(sender, "Game has already started");
                }
            }
        } else {
            Bomberman.sendMessage(sender, "You must be a player to join");
        }
        return true;
    }

    private boolean leaveCommand(CommandSender sender, ArrayList<String> argsList) {
        if (sender instanceof Player) {
            for (String name : Game.allGames()) {
                Game game = Game.findGame(name);
                PlayerRep rep = game.getPlayerRep((Player)sender);
                if (rep != null) {
                    rep.kill(true);
                    rep.game.observers.remove(rep);
                    return true;
                }
            }
        }
        Bomberman.sendMessage(sender, "You're not part of a game");
        return true;
    }

    private boolean infoCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1)
            return false;
        
        Game game = Game.findGame(argsList.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        String message = "About " + game.name + ":\n";
        message += " * Status: ";
        if (game.isPlaying)
            message += "In progress\n";
        else
            message += "Waiting\n";
        message += " * Players: " + game.players.size() + "\n";
        message += " * Min players: " + game.getMinPlayers()+ "\n";
        message += " * Max players: " + game.board.spawnPoints.size() + "\n";
        message += " * Init bombs: " + game.getBombs() + "\n";
        message += " * Init lives: " + game.getLives() + "\n";
        message += " * Init power: " + game.getPower() + "\n";
        message += " * Autostart: " + game.getAutostart() + "\n";
        message += " * Entry fare: ";
        if (game.getFare() == null)
            message += "no fee \n";
        else
            message += game.getFare().getType() + " x" + game.getFare().getAmount() + "\n";
        message += " * Winner's prize: ";
        if (game.getPot() == true && game.getFare() != null)
            message += "Pot currently at " + game.getFare().getAmount()*game.players.size() + " " + game.getFare().getType() + "\n";
        else {
            if (game.getPrize() == null)
                message += "No prize \n";
            else
                message += game.getPrize().getAmount() + " " + game.getPrize().getType() + "\n";
        }
        message += " * Arena: " + game.board.name + "\n";
        Bomberman.sendMessage(sender, message);
        return true;
    }

    private boolean listCommand(CommandSender sender, ArrayList<String> argsList) {
        List<String> games = Game.allGames();
        if (games.size() == 0) {
            Bomberman.sendMessage(sender, "No games");
        } else {
            Bomberman.sendMessage(sender, "Current games:");
            for (String name : games) {
                Game game = Game.findGame(name);
                String status = " * " + game.name;
                status += " : " + game.players.size() + "/" + game.board.spawnPoints.size() + " : ";
                if (game.isPlaying)
                    status += "playing";
                else
                    status += "waiting  ";
                        
                Bomberman.sendMessage(sender, status);
            }
        }
        return true;
    }

    private boolean createCommand(CommandSender sender, ArrayList<String> argsList) {
        if (!(argsList.size() == 1 || argsList.size() == 2))
            return false;
        if (sender instanceof Player) {
            if (Game.findGame(argsList.get(0)) != null) {
                Bomberman.sendMessage(sender, "Game already exists");
            } else {
                Board arena;
                if (argsList.size() == 2) {
                    arena = BoardGenerator.loadBoard(argsList.get(1));
                } else {
                    arena = BoardGenerator.loadDefault();
                }
                if (arena == null) {
                    Bomberman.sendMessage(sender, "Arena not found");
                    return true;
                }
                // long location getting line to round to integers...
                Location l = ((Player)sender).getLocation().getBlock().getLocation();
                createGame(argsList.get(0), l, arena);
                Bomberman.sendMessage(sender, "Game created");
            }
        } else {
            Bomberman.sendMessage(sender, "You must be a player");
        }
        return true;
    }

    private boolean destroyCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1)
            return false;
        Game game = Game.findGame(argsList.get(0)); 
        if (game != null) {
            destroyGame(game);
            Bomberman.sendMessage(sender, "Game destroyed");
        } else
            Bomberman.sendMessage(sender, "Game not found");
        return true;
    }
    
    private boolean convertCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1)
            return false;
        if (sender instanceof Player) {
            if (Game.findGame(argsList.get(0)) != null) {
                Bomberman.sendMessage(sender, "Game already exists");
            } else {
                Location[] locations = BoardGenerator.getBoundingStructure((Player)sender, argsList.get(0));
                Board board = BoardGenerator.createArena(argsList.get(0), locations[0], locations[1]);
                BoardGenerator.saveBoard(board);
                Game game = new Game(argsList.get(0), locations[0]);
                game.board = board;
                game.oldBoard = board;
                Game.register(game);
                Bomberman.sendMessage(sender, "Game created");
            }
        } else {
            Bomberman.sendMessage(sender, "You must be a player");
        }
        return true;
    }

    private boolean startGameCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1) {
            return false;
        }
        Game game = Game.findGame(argsList.get(0));
        if (game == null)
            Bomberman.sendMessage(sender, "Game not found");
        else if (game.isPlaying)
            Bomberman.sendMessage(sender, "Game already started");
        else {
            if (game.startGame())
                Bomberman.sendMessage(sender, "Game starting");
            else
                Bomberman.sendMessage(sender, "There are not enough players");
        }
        return true;
    }

    private boolean stopGameCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1) {
            return false;
        }
        Game game = Game.findGame(argsList.get(0));
        if (game == null)
            Bomberman.sendMessage(sender, "Game not found");
        else if (!game.isPlaying)
            Bomberman.sendMessage(sender, "Game hasn't started");
        else {
            game.terminate();
            Bomberman.sendMessage(sender, "Game stopped");
        }
        return true;
    }

    private boolean resetGameCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1)
            return false;
        Game game = Game.findGame(argsList.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        game.terminate();
        BoardGenerator.switchBoard(game.board, game.board, game.loc);
        Bomberman.sendMessage(sender, "Game reset");
        return true;
    }

    private boolean setArenaCommand(CommandSender sender, ArrayList<String> argsList) {
        if (!(argsList.size() == 1 || argsList.size() == 2))
            return false;
        Game game = Game.findGame(argsList.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        if (argsList.size() == 1) {
            Bomberman.sendMessage(sender, "Arena: " + game.board.name);
            return true;
        } else {
            if (game.isPlaying) {
                Bomberman.sendMessage(sender, "Game in progress");
                return true;
            }
                
            Board board = BoardGenerator.loadBoard(argsList.get(1));
            if (board == null) {
                Bomberman.sendMessage(sender, "Arena not found");
                return true;
            }
            BoardGenerator.switchBoard(game.board, game.oldBoard, game.loc);
            game.board = board;
            game.oldBoard = BoardGenerator.createArena(game.name+".old", game.loc, board.xSize, board.ySize, board.zSize);
            BoardGenerator.switchBoard(game.oldBoard, board, game.loc);
            Bomberman.sendMessage(sender, "Game arena changed");
            return true;
        }
    }

    private boolean setGameAttributeCommand(CommandSender sender, ArrayList<String> argsList, String attribute) {
        if (!(argsList.size() == 1 || argsList.size() == 2))
            return false;
        
        Game game = Game.findGame(argsList.get(0));
        
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        } else {
            if (argsList.size() == 1) {
                if (attribute.equals("bombs"))
                    Bomberman.sendMessage(sender, "Starting " + attribute + ": " + game.getBombs());
                else if (attribute.equals("power"))
                    Bomberman.sendMessage(sender, "Starting " + attribute + ": " + game.getPower());
                else if (attribute.equals("lives"))
                    Bomberman.sendMessage(sender, "Starting " + attribute + ": " + game.getLives());
                else if (attribute.equals("minplayers"))
                    Bomberman.sendMessage(sender, "Minimum players: " + game.getMinPlayers());
                return true;
            } else {
                int num = 0;
                try {
                     num = Integer.parseInt(argsList.get(1), 10);
                } catch (NumberFormatException e) {
                    return false;
                }

                if (attribute.equals("bombs"))
                    game.setBombs(num);
                else if (attribute.equals("power"))
                    game.setPower(num);
                else if (attribute.equals("lives"))
                    game.setLives(num);
                else if (attribute.equals("minplayers"))
                    game.setMinPlayers(num);

                Bomberman.sendMessage(sender, attribute.toUpperCase() + " set");
                return true;
            }
        }
    }

    private boolean setAutostartCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 2)
            return false;
        Game game = Game.findGame(argsList.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Cannot find game");
            return true;
        }
        if (argsList.get(1).equalsIgnoreCase("true")) {
            game.setAutostart(true);
        } else if (argsList.get(1).equalsIgnoreCase("false"))
            game.setAutostart(false);
        else
            return false;
        Bomberman.sendMessage(sender, "Autostart set to " + game.getAutostart());
        return true;
    }

    private boolean setAutostartDelayCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 2)
            return false;
        
        Game game = Game.findGame(argsList.get(0));
        
        if (game == null) {
            Bomberman.sendMessage(sender, "Cannot find game");
            return true;
        }
        
        try {
            game.setAutostartDelay(Integer.parseInt(argsList.get(1)));
            Bomberman.sendMessage(sender, "Autostart delay set to " + game.getAutostartDelay());
        } catch (NumberFormatException e) {
            Bomberman.sendMessage(sender, "Delay entered is not a valid number");
        }
        return true;
    }

    private boolean setEconomyAttributeCommand(CommandSender sender, ArrayList<String> argsList, String attribute) {
        if (argsList.size() < 1 || argsList.size() > 3)
            return false;
        Game game = Game.findGame(argsList.get(0));
        if (game == null) {
            Bomberman.sendMessage(sender, "Game not found");
            return true;
        }
        if (argsList.size() == 1) {
            if (attribute.equals("fare")) {
                if (game.getFare() != null)
                    Bomberman.sendMessage(sender, "Fare: " + game.getFare().getAmount() + " " + game.getFare().getType());
                else
                    Bomberman.sendMessage(sender, "No fare");
            } else {
                if (game.getPrize() != null)
                    Bomberman.sendMessage(sender, "Prize: " + game.getPrize().getAmount() + " " + game.getPrize().getType());
                else
                    if (game.getPot() == true)
                        Bomberman.sendMessage(sender, "Game has a pot");
                    else
                        Bomberman.sendMessage(sender, "No prize");
            }
            return true;
        } else {
            if (!sender.hasPermission("bomberman.op")) {
                Bomberman.sendMessage(sender, "You may only view what the " + attribute + " is");
                return true;
            }
            if (argsList.size() == 2) {
                if (argsList.get(1).equalsIgnoreCase("pot")) {
                    game.setPrize(null, true);
                    Bomberman.sendMessage(sender, "Prize set");
                    return true;
                } else if (argsList.get(1).equals("none")) {
                    if (attribute.equalsIgnoreCase("fare")) {
                        game.setFare(null);
                        Bomberman.sendMessage(sender, "Fare removed");
                    } else {
                        game.setPrize(null, false);
                        Bomberman.sendMessage(sender, "Prize removed");
                    }
                    return true;
                }
                return false;
            }
            if (argsList.size() == 3) {
                try {
                    Material m = Material.getMaterial(argsList.get(1).toUpperCase());
                    if (m == null)
                        Bomberman.sendMessage(sender, "Unknown material");
                    int amount = Integer.parseInt(argsList.get(2));
                    if (attribute.equalsIgnoreCase("fare")) {
                        game.setFare(new ItemStack(m, amount));
                        Bomberman.sendMessage(sender, "Fare set");
                    } else {
                        game.setPrize(new ItemStack(m, amount), false);
                        Bomberman.sendMessage(sender, "Prize set");
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
        }
        return false;
    }

    private boolean createArenaCommand(CommandSender sender, ArrayList<String> argsList) {
        if (argsList.size() != 1)
            return false;
        if (sender instanceof Player) {
            Location[] locations = BoardGenerator.getBoundingStructure((Player)sender, argsList.get(0));
            Board board2 = BoardGenerator.createArena(argsList.get(0), locations[0], locations[1]);
            BoardGenerator.saveBoard(board2);
            Bomberman.sendMessage(sender, "Arena created");
        }
        return true;
    }

    private boolean listArenasCommand(CommandSender sender, ArrayList<String> argsList) {
        List<String> arenas = BoardGenerator.allBoards();
        if (arenas.size() == 0) {
            Bomberman.sendMessage(sender, "No arenas");
        } else {
            Bomberman.sendMessage(sender, "Current arenas:");
            for (String name : arenas) {
                if (!name.endsWith(".old"))
                    Bomberman.sendMessage(sender, " * " + name);
            }
        }
        return true;
    }

    @Override
	public List<String> onTabComplete(CommandSender sender, Command command,
			String alias, String[] args) {
		String cmd = command.getName().toLowerCase();
		List<String> options = new ArrayList<>();
		switch (cmd) {
		case "create-arena":
			if (args.length == 1)
				addArenas(options, args[0]);
			break;
			
		case "create-game":
			if (args.length == 1)
				addGames(options, args[0]);
			if (args.length == 2)
				addArenas(options, args[1]);
			break;
		
		case "arena":
			if (args.length == 1)
				addGames(options, args[0]);
			else if (args.length == 2)
				addArenas(options, args[2]);
			break;
		
		case "autostart":
			if (args.length == 1)
				addGames(options, args[0]);
			else if (args.length == 2) {
				if (StringUtil.startsWithIgnoreCase("false", args[1]))
					options.add("false");
				if (StringUtil.startsWithIgnoreCase("true", args[1]))
					options.add("true");
			}
			break;

		case "lives":
		case "bombs":
		case "power":
		case "min-players":
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
	
	private boolean sendCommandInfo(CommandSender sender, String command) {
	    switch(command) {
	    case "bm":
            break;
	    case "game":
            break;
	    case "game.join":
            break;
	    case "game.leave":
            break;
	    case "game.info":
            break;
	    case "game.list":
            break;
	    case "game.create":
            break;
	    case "game.destroy":
            break;
	    case "game.convert":
	        break;
	    case "game.force":
            break;
	    case "game.force.start":
            break;
	    case "game.force.stop":
            break;
	    case "game.force.reset":
            break;
	    case "game.set":
            break;
	    case "game.set.arena":
            break;
	    case "game.set.lives":
            break;
	    case "game.set.bombs":
            break;
	    case "game.set.power":
            break;
	    case "game.set.minplayers":
            break;
	    case "game.set.autostart":
            break;
	    case "game.set.autostartdelay":
            break;
	    case "game.set.fare":
            break;
	    case "game.set.prize":
            break;
	    case "arena":
            break;
	    case "arena.create":
            break;
        case "arena.list":
            break;
        default:
            return false;
	    }
	    return true;
	}
	
	private void addGames(List<String> options, String start) {
		for (String name : Game.allGames()) {
			if (name.toLowerCase().startsWith(start.toLowerCase()))
				options.add(name);
		}
	}
	
	private void addArenas(List<String> options, String start) {
		for (String name : BoardGenerator.allBoards()) {
			if (name.toLowerCase().startsWith(start.toLowerCase()))
				options.add(name);
		}
	}
}
