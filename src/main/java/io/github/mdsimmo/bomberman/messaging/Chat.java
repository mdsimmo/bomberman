package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;
import io.github.mdsimmo.bomberman.commands.Command.Permission;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public abstract class Chat {

	public static void sendText(PlayerRep rep, Text text, Object... objs) {
		sendText(rep.player, rep.getLanguage(), text, objs);
	}
	
	public static void sendText(List<PlayerRep> playerList, Text text, Object ... objs) {
		for (PlayerRep rep : playerList)
			sendText(rep, text, objs);
	}
	
	public static void sendText(CommandSender sender, Language lang, Text text, Object ... objs) {
		sendMessage(sender, text.getMessage(lang, sender, objs));
	}
	
	public static void sendText(CommandSender sender, Text text, Object ... objs) {
		sendText(sender, PlayerRep.getLanguage(sender), text, objs);
	}
	
	public static void sendMessage(PlayerRep rep, Message message) {
		sendMessage(rep.player, message);
	}
	
	public static void sendMessage(CommandSender sender, Message message) {
		if (message.isBlank())
			return;
		messageRaw(sender, ChatColor.GREEN + "[BomberMan] " + ChatColor.RESET + message.toString());
	}

	public static void sendMap(CommandSender sender, Map<Message, Message> points) {
		for (Map.Entry<Message, Message> point : points.entrySet()) {
			if (point.getValue().isBlank() || point.getKey().isBlank())
				continue;
			messageRaw(sender, "   " + ChatColor.GOLD + point.getKey() + ": "
					+ ChatColor.RESET + point.getValue());
		}
	}

	public static void sendList(CommandSender sender, List<Message> list) {
		for (Message line : list) {
			if (line.isBlank())
				continue;
			messageRaw(sender, "   " + line);
		}
	}

	private static void messageRaw(CommandSender sender, String message) {
		if (Permission.OBSERVER.isAllowedBy(sender))
			sender.sendMessage(message);
	}

	public static void sendHeading(CommandSender sender, Message message) {
		if (message.isBlank())
			return;
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