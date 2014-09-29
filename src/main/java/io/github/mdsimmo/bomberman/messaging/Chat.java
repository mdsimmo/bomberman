package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command.Permission;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class Chat {

	public static void sendMessage(PlayerRep rep, Text text, Object... objs) {
		sendMessage(rep.player, text, rep.getLanguage(), objs);
	}
	
	public static void sendMessage(List<PlayerRep> playerList, Text text, Object ... objs) {
		for (PlayerRep rep : playerList)
			sendMessage(rep, text, objs);
	}
	
	public static void sendMessage(CommandSender sender, Text text, Language lang, Object ... objs) {
		sendMessage(sender, text.getMessage(sender, lang, objs));
	}
	
	public static void sendMessage(ArrayList<PlayerRep> playerList, Message message) {
		for (PlayerRep rep : playerList) {
			sendMessage(rep, message);
		}
	}

	public static void sendMessage(PlayerRep rep, Message message) {
		sendMessage(rep.player, message);
	}
	
	public static void sendMessage(CommandSender sender, Message message) {
		messageRaw(sender, ChatColor.GREEN + "[BomberMan] " + ChatColor.RESET + message);
	}

	public static void sendMessage(CommandSender sender, Map<Message, Message> points) {
		for (Map.Entry<Message, Message> point : points.entrySet()) {
			messageRaw(sender, "   " + ChatColor.GOLD + point.getKey() + ": "
					+ ChatColor.RESET + point.getValue());
		}
	}

	public static void sendMessage(CommandSender sender, List<Message> list) {
		for (Message line : list) {
			messageRaw(sender, "   " + line);
		}
	}

	private static void messageRaw(CommandSender sender, String message) {
		if (Permission.OBSERVER.isAllowedBy(sender))
			sender.sendMessage(message);
	}

	public static void sendHeading(CommandSender sender, Message message) {
		sender.sendMessage(heading(message.toString()));
	}
	
	private static String heading(String text) {
		String head = ChatColor.YELLOW + "--------- " + ChatColor.RESET + text
				+ " " + ChatColor.YELLOW;
		for (int i = text.length(); i < 38; i++) {
			head += "-";
		}
		return head;
	}
}