package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class Message {

	private class InvalidMessageException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public InvalidMessageException(String reason, int location) {
			this(reason + " in message '" + text + "' at position " + location);
		}

		public InvalidMessageException(String reason) {
			super(reason);
		}

	}

	private final String text;
	private final Object[] objects;

	public Message(CommandSender sender, String text, Object... objects) {
		this.text = text;
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
				formated.append(c);
			} else {
				// Get data stuff
				do {
					i++;
					c = text.charAt(i);
				} while (c == ' ');

				// get the reference
				if (!Character.isDigit(c))
					throw new InvalidMessageException("Expected digit", i);
				String ref = "";
				do {
					ref += c;
					i++;
					c = text.charAt(i);
				} while (Character.isDigit(c));
				int reference;
				try {
					reference = Integer.parseInt(ref);
				} catch (NumberFormatException e) {
					throw new InvalidMessageException("Funny looking numbers",
							i);
				}

				while (c == ' ') {
					i++;
					c = text.charAt(i);
				}

				if (c != '}') {
					throw new InvalidMessageException("Expected '}'", i);
				}

				// append the formatted object
				try {
					formated.append(format(objects[reference]));
				} catch (IndexOutOfBoundsException e) {
					throw new InvalidMessageException("Index out of bounds");
				}
			}
		}

		return formated.toString();
	}
	
	private String format(Object obj) {
		if (obj instanceof Message)
			return obj.toString();
		if (obj instanceof PlayerRep)
			return ((PlayerRep)obj).player.getName();
		if (obj instanceof Player)
			return ((Player)obj).getName();
		if (obj instanceof ItemStack) {
			ItemStack stack = (ItemStack)obj;
			int amount = stack.getAmount();
			Material type = stack.getType();
			return amount + " " + type.toString().replace('_', ' ').toLowerCase();
		}
		return obj.toString();
	}
}
