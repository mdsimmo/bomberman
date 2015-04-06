package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.PlayerRep;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Message implements Formattable {

	private final String text;
	private Stack<ChatColor> colors = new Stack<>();
	private HashMap<String, Object> values = new HashMap<>();

	public Message( CommandSender sender, String text ) {
		this.text = text;
		values.put( "sender", sender );
		colors.push( ChatColor.RESET );
	}

	private Object get( String key ) {
		Object val = values.get( key );
		if ( val == null )
			try {
				val = ChatColor.valueOf( key.toUpperCase() );
			} catch ( IllegalArgumentException e ) {
				Bomberman.instance.getLogger().info(
						"Key " + key + " has no associated value" );
			}
		return val;
	}

	public CommandSender getSender() {
		return (CommandSender)values.get( "sender" );
	}

	public Message put( Map<String, Object> values ) {
		for ( Entry<String, Object> entry : values.entrySet() ) {
			put( entry.getKey(), entry.getValue() );
		}
		return this;
	}

	public Message put( String key, Object value ) {
		values.put( key, value );
		return this;
	}

	public boolean containsKey( String key ) {
		return get( key ) != null;
	}

	@Override
	public String toString() {
		try {
			return expand( text );
		} catch ( Exception e ) {
			e.printStackTrace();
			Bomberman.instance.getLogger().warning( "Faulty message: " + text );
			return ChatColor.RED + "Internal format error";
		}
	}

	private String expand( String text ) {
		StringBuffer expanded = new StringBuffer();
		for ( int i = 0; i < text.length(); i++ ) {
			char c = text.charAt( i );
			if ( c == '{' ) {
				StringBuffer subText = new StringBuffer();
				int openBracesFound = 0;
				while ( c != '}' || openBracesFound != 0 ) {
					if ( c == '}' )
						openBracesFound--;
					subText.append( c );
					i++;
					c = text.charAt( i );
					if ( c == '{' )
						openBracesFound++;
				}
				subText.append( c );
				expanded.append( expandBrace( subText.toString() ) );
			} else {
				expanded.append( c );
			}
		}
		return expanded.toString();
	}

	private String expandBrace( String text ) {
		if ( text.charAt( 0 ) != '{' || text.charAt( text.length() - 1 ) != '}' )
			throw new RuntimeException(
					"expandBrace() must start and end with a brace" );
		StringBuffer buffer = new StringBuffer();
		int i = 1;
		char c = text.charAt( i );
		// skip whitespace
		while ( Character.isWhitespace( c ) ) {
			i++;
			c = text.charAt( i );
		}
		// get reference
		while ( Character.isJavaIdentifierPart( c ) ) {
			buffer.append( c );
			i++;
			c = text.charAt( i );
		}
		// remove whitespace
		while ( Character.isWhitespace( c ) ) {
			i++;
			c = text.charAt( i );
		}
		String key = buffer.toString();
		buffer.delete( 0, buffer.length() );
		Object value = get( key );
		if ( value instanceof ChatColor )
			colors.push( (ChatColor)value );

		if ( c == '}' ) {
			if ( i == text.length() - 1 ) {
				buffer.append( format( value, null ) );
				return buffer.toString();
			} else
				throw new RuntimeException( "Expected ending brace" );
		} else if ( c == '|' ) {
			String subarg = expand( text.substring( i + 1, text.length() - 1 ) );
			buffer.append( format( value, subarg ) );
			if ( value instanceof ChatColor ) {
				buffer.append( subarg.trim() );
				colors.pop();
				buffer.append( colors.peek().toString() );
			}
		} else {
			throw new RuntimeException( "Expected '|' or '}' " );
		}

		return buffer.toString();

	}

	private String format( Object obj, String value ) {
		if ( value != null )
			value = value.trim();
		if ( obj instanceof CommandSender )
			if ( obj instanceof Player )
				obj = PlayerRep.getPlayerRep( (Player)obj );
			else
				return ( (CommandSender)obj ).getName();
		if ( obj instanceof Formattable )
			return ( (Formattable)obj ).format( this, value );
		if ( obj instanceof ItemStack ) {
			ItemStack stack = (ItemStack)obj;
			int amount = stack.getAmount();
			Material type = stack.getType();
			return amount + " "
					+ type.toString().replace( '_', ' ' ).toLowerCase();
		}
		return String.valueOf( obj );
	}

	public boolean isBlank() {
		return text.isEmpty();
	}

	@Override
	public String format( Message message, String value ) {
		colors.push( message.colors.peek() );
		return this.toString();
	}
}
