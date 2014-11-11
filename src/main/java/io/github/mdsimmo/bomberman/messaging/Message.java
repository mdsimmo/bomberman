package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;


public class Message implements Formattable {

	private class InvalidMessageException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidMessageException(String reason, int location) {
			super(reason + " in message '" + text + "' at position " + location);
		}

		public InvalidMessageException(String reason) {
			super(reason + " in message '" + text + "'");
		}

	}

	private final String text;
	private final Object[] objects;
	private ChatColor normal = null;
	private final CommandSender sender;

	public Message(CommandSender sender, String text, Object... objects) {
		this.text = text;
		this.sender = sender;
		Object[] objs = new Object[objects.length+1];
		objs[0] = sender;
		for (int i = 0; i < objects.length; i++)
			objs[i+1] = objects[i];
		this.objects = objs;
	}
	
	@Override
	public String toString() {
		StringBuffer formated = new StringBuffer();
		int length = text.length();
		for (int i = 0; i < length; i++) {
			char c = text.charAt(i);
			if (c != '{') {
				if (c == '}' && normal != null) {
					normal = null;
					formated.append(ChatColor.RESET);
					continue;
				}
				formated.append(c);
			} else {
				// Get data stuff
				do {
					i++;
					c = text.charAt(i);
				} while (c == ' ');

				// get the value
				String val = "";
				do {
					val += c;
					i++;
					c = text.charAt(i);
				} while (Character.isJavaIdentifierPart(c));
				
				while (c == ' ') {
					i++;
					c = text.charAt(i);
				}
				
				int reference = 0;
				try {
					reference = Integer.parseInt(val);
				} catch (NumberFormatException e) {
					try {
						normal = ChatColor.valueOf(val);
						formated.append(normal);
						if (c != '|')
							throw new InvalidMessageException("Expected '|'", i);
						continue;
					} catch (IllegalArgumentException e2) {
						throw new InvalidMessageException("Expected number or ChatColor", i);
					}
				}

				if (c != '}') {
					throw new InvalidMessageException("Expected '}'", i);
				}

				// append the formatted object
				try {
					formated.append(format(objects[reference], sender));
				} catch (IndexOutOfBoundsException e) {
					throw new InvalidMessageException("Index out of bounds");
				}
			}
		}

		return formated.toString();
	}
	
	private String format(Object obj, CommandSender sender) {
		if (obj instanceof Formattable)
			return ((Formattable)obj).format(sender);
		if (obj instanceof CommandSender)
			return ((CommandSender)obj).getName();
		if (obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			int amount = stack.getAmount();
			Material type = stack.getType();
			return amount + " " + type.toString().replace('_', ' ').toLowerCase();
		}
		return obj.toString();
	}
	
	public boolean isBlank() {
		return text.isEmpty();
	}

	@Override
	public String format(CommandSender sender) {
		return toString();
	}
}
