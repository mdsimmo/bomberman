package io.github.mdsimmo.bomberman;

import java.util.ArrayList;

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
            sendMessage(p.player, message);
        }
    }
    
    public static void sendMessage(Player p, String message) {
        p.sendMessage(ChatColor.GREEN + "[BomberMan] " + ChatColor.WHITE + message);
    }
    
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "[BomberMan] " + ChatColor.WHITE + message);
    }
	
	@Override
	public void onEnable() {
		instance = this;
		new GameCommander();
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
