package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.Command;
import io.github.mdsimmo.bomberman.commands.Command.Permission;
import io.github.mdsimmo.bomberman.commands.CommandHandler;
import io.github.mdsimmo.bomberman.save.BoardSaver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/**
	 * Puts the given objects into the string and colors them. <br>
	 * To specify where to put the objects in the string, use '%' followed by:<br>
	 * <b>b:</b> a Board<br>
	 * <b>c:</b> a command<br>
	 * <b>p:</b> for a Player or PlayerRep<br>
	 * <b>g:</b> a Game<br>
	 * <b>i:</b> an ItemStack<br>
	 * <b>other:</b> passed to String.format()<br>
	 * All values can be passed as a string.  
	 * @param s the string to format
	 * @param objects the objects
	 * @return the formated string
	 */
	public static String format(String s, Object ... objects) {
		String formated = "";
		int objectIndex = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
			case '%':
				char lastChar = s.charAt(Math.max(0, i-1));
				if (lastChar == '\\') {
					formated += c;
					break;
				}
				String part;
				Object o = objects[objectIndex];
				char formater = s.charAt(i+1);
				switch (formater) {
				case 'p':
					if (o instanceof Player)
						part = PlayerRep.getPlayerRep((Player)o).getName();
					else if (o instanceof PlayerRep)
						part = ((PlayerRep)o).getName();
					else
						part = (String)o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'g':
					if (o instanceof Game) 
						part = ((Game) o).name;
					else
						part = (String) o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'b':
					if (o instanceof Board) 
						part = ((Board) o).name;
					else
						part = (String) o;
					formated += ChatColor.YELLOW + part + ChatColor.RESET;
					break;
				case 'c':
					if (o instanceof Command) {
						Command cmd = (Command)o;
						part = "/" + cmd.path();
					} else
						part = (String)o;
					formated += ChatColor.AQUA + part + ChatColor.RESET;
					break;
				case 'i':
					if (o instanceof ItemStack) {
						ItemStack stack = (ItemStack)o;
						part = stack.getAmount() + " " + stack.getType().toString().toLowerCase();
					} else
						part = (String)o;
					formated += ChatColor.GREEN + part + ChatColor.RESET;
					break;
				default:
					formated += ChatColor.YELLOW + String.format("%" + formater, o) + ChatColor.RESET;
					break;
				}
				i++;
				objectIndex++;
				break;
			default:
				formated += c;
				break;
			}
		}
		return formated;
		
	}
	
	public static void sendMessage(Player[] playerList, String message, Object ... objs) {
        for(Player p : playerList) {
            sendMessage(p, message, objs);
        }
    }
    
    public static void sendMessage(ArrayList<PlayerRep> playerList, String message, Object ... objs) {
        for(PlayerRep p : playerList) {
            sendMessage(p, message, objs);
        }
    }
    
    public static void sendMessage(PlayerRep rep, String message, Object ... objs) {
        sendMessage(rep.player, message, objs);
    }
    
    public static void sendMessage(CommandSender sender, String message, Object ... objs) {
        message(sender, ChatColor.GREEN + "[BomberMan] " + ChatColor.RESET + message, objs);
    }
    
    public static void sendMessage(CommandSender sender, Map<String, String> points, Object ... objs) {
    	for (Map.Entry<String, String> point : points.entrySet()) {
    		message(sender, "   " + ChatColor.GOLD + point.getKey() + ": " + ChatColor.RESET + point.getValue(), objs);
    	}
    }
    
    public static void sendMessage(CommandSender sender, List<String> list, Object ... objs) {
    	for (String line : list) {
    		message(sender, "   " + line, objs);
    	}
    }
    
    private static void message(CommandSender sender, String message, Object ... objs) {
    	if (Permission.OBSERVER.isAllowedBy(sender))
    		sender.sendMessage(format(message, objs));
    }
    
	public static String heading (String text) {
		String head = ChatColor.YELLOW + "--------- "
				+ ChatColor.RESET + text + " " + ChatColor.YELLOW;
		for (int i = text.length(); i < 38; i++) {
			head += "-";
		}
		return head;
	}
	
	public static void sendHeading(CommandSender sender, String text) {
		sender.sendMessage(heading(text));
	}
	
	@Override
	public void onEnable() {
		instance = this;
		BoardGenerator.copyDefaults();
		BoardSaver.convertArenas();
		new CommandHandler();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).stop();
			Game.findGame(game).saveGame();
		}
	}
}
