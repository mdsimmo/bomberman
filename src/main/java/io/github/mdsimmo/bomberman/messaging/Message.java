package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.Bomberman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
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
				String subtext = toNext( text, '}', i );
				expanded.append( expandBrace( subtext ) );
				i += subtext.length()-1; // -1 because starting brace was already counted
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

		List<String> args = new ArrayList<String>();
		if ( text.charAt( i ) != '}' ) {
			try {
				while( true ) {
					String subArg = toNext( text, '|', i );
					args.add( expand( subArg.substring( 1, subArg.length() - 1 ) ) );
					i += subArg.length()-1; // -1 because first '|' would get counted twice
				}
			} catch ( Exception e ) { // happens when cannot find any more '|'
				String subArg = toNext( text, '}', i );
				args.add( expand( subArg.substring( 1, subArg.length() - 1 ) ) );
				i += subArg.length();
			}
		}

		buffer.append( format( value, args ) );

		return buffer.toString();

	}

	/**
	 * Gets the substring of sequence from index to the next endingChar but
	 * takes into account brace skipping. The returned string will include both
	 * the start and end characters.
	 */
	private String toNext( String sequence, char endingChar, int index ) {
		int size = sequence.length();
		int openBracesfound = 0;
		for ( int i = index + 1; i < size; i++ ) {
			char c = sequence.charAt( i );
			if ( c == endingChar && openBracesfound == 0 )
				return sequence.substring( index, i + 1 );
			if ( c == '{' )
				openBracesfound++;
			if ( c == '}' )
				openBracesfound--;
		}
		throw new RuntimeException( "Couldn't find any '" + endingChar
				+ "' after index " + index + " in string " + sequence );
	}

	private String format( Object obj, List<String> values ) {
		String value = values.size() > 0 ? values.remove( 0 ).trim() : null;
		if ( obj instanceof ChatColor ) {
			colors.pop();
			return obj.toString() + value + colors.peek();
		}
		if ( obj instanceof CommandSender )
			return format( new SenderWrapper( (CommandSender)obj ), values );
		if ( obj instanceof Formattable )
			return format( ( (Formattable)obj ).format( this, value ), values );
		if ( obj instanceof ItemStack )
			return format( new ItemWrapper( (ItemStack)obj ), values );
		return String.valueOf( obj );
	}

	public boolean isBlank() {
		return text.isEmpty();
	}

	@Override
	public Object format( Message message, String value ) {
		colors.push( message.colors.peek() );
		return this.toString();
	}
}
