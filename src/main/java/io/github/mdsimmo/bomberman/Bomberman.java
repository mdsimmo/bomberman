package io.github.mdsimmo.bomberman;

import io.github.mdsimmo.bomberman.commands.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Bomberman extends JavaPlugin {
	
	public static Bomberman instance;
	
	/* TODO FEATURES
	 * make chests (and other like things) spawn with contents
	 * more styles and underground styles
	*/
	
	/* TODO BUGS
	 */
	
	public static void sendMessage(Player[] playerList, String message) {
        for(Player p : playerList) {
            sendMessage(p, message);
        }
    }
    
    public static void sendMessage(ArrayList<PlayerRep> playerList, String message) {
        for(PlayerRep p : playerList) {
            sendMessage(p, message);
        }
    }
    
    public static void sendMessage(PlayerRep rep, String message) {
        sendMessage(rep.player, message);
    }
    
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "[BomberMan] " + ChatColor.WHITE + message);
    }
    
    public static void sendMessage(CommandSender sender, Map<String, String> points) {
    	for (Map.Entry<String, String> point : points.entrySet()) {
    		sender.sendMessage("   " + ChatColor.GOLD + point.getKey() + ": " + ChatColor.WHITE + point.getValue());
    	}
    }
    
    public static void sendMessage(CommandSender sender, List<String> list) {
    	for (String line : list) {
    		sender.sendMessage("   " + ChatColor.WHITE + line);
    	}
    }
    
	public static String heading (String text) {
		String head = ChatColor.YELLOW + "--------- "
				+ ChatColor.WHITE + text + " " + ChatColor.YELLOW;
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
		new CommandHandler();
		Game.loadGames();
	}
	
	@Override
	public void onDisable() {
		for (String game : Game.allGames()) {
			Game.findGame(game).terminate();
			Game.findGame(game).saveGame();
		}
	}
}
