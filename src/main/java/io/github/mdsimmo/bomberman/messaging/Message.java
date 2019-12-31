package io.github.mdsimmo.bomberman.messaging;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class Message implements Formattable {

	private final String text;
	private Deque<ChatColor> colors = new ArrayDeque<>( 2 );
	private HashMap<String, Object> values = new HashMap<>();
	private static final Equation equationExpander = new Equation();
	private static final Switch switchExpander = new Switch();

	public Message(CommandSender sender, String text ) {
		this.text = text;
		values.put( "sender", sender );
		values.put( "=", equationExpander );
		values.put( "switch", switchExpander );
		colors.push( ChatColor.RESET );
	}
	
	private Object get( String key ) {
		Object val = values.get( key );
		if ( val == null )
			try {
				val = ChatColor.valueOf( key.toUpperCase() );
			} catch ( IllegalArgumentException e ) {
				//Bomberman.instance.getLogger().info( "Key " + key + " has no associated value" );
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
			return ChatColor.RED + "Formatting error!";
		}
	}
	
	public void validate() {
		expand( text );
	}

	private String expand( String text ) {
		StringBuilder expanded = new StringBuilder();
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
		StringBuilder buffer = new StringBuilder();
		int i = 1;
		char c = text.charAt( i );
		// skip whitespace
		while ( Character.isWhitespace( c ) ) {
			i++;
			c = text.charAt( i );
		}
		
		// get reference
		while ( !Character.isWhitespace( c ) && c != '}' && c != '{' && c != '|' ) {
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
		if ( value instanceof ChatColor ) {
			colors.addLast( (ChatColor)value );
		}

		List<String> args = new ArrayList<String>();
		if ( text.charAt( i ) != '}' ) {
			try {
				while( true ) {
					String subArg = toNext( text, '|', i );
					args.add( expand( subArg.substring( 1, subArg.length() - 1 ) ) );
					i += subArg.length()-1; // -1 because the first '|' would get counted twice
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

	private String format( Object obj, List<String> args ) {
		if ( obj instanceof Formattable )
			return ( (Formattable)obj ).format( this, args );
		if ( obj instanceof ChatColor ) {
			if ( args.size() != 1 )
				throw new RuntimeException( "Colors must have exactly one argument" );
			ChatColor color = colors.removeLast(); // == obj;
			
			String startText; // colors/formats before message
			String endText;   // colors/formats after message
			
			String formats = ""; // the formats that were applied
			ChatColor lastColor = ChatColor.RESET; // the previous color
			for ( ChatColor c : colors ) {
				if ( c.isColor() )
					lastColor = c;
				if ( c.isFormat() )
					formats += c;
			}
			
			if ( color.isFormat() ) {
				// formats will keep things before it active. So start is simple
				startText = color.toString();
				// to remove a format, we need to reset and re-do everything else
				endText = lastColor + formats;
			} else if ( color.isColor() ) {
				// colors destroy format codes so we need to re apply them
				startText = color + formats;
				
				// to end, we switch across to the previous color and re-apply formats
				endText = lastColor + formats;
			} else {
				// color = Color.RESET
				
				// override everything
				startText = ChatColor.RESET.toString();
				
				// re-apply everything
				endText = lastColor + formats;
			}
			return startText + args.get( 0 ) + endText;
		}
		if ( obj instanceof CommandSender )
			return new SenderWrapper( (CommandSender)obj ).format( this, args );
		if ( obj instanceof ItemStack )
			return new ItemWrapper( (ItemStack)obj ).format( this, args );
		return String.valueOf( obj );
	}

	boolean isBlank() {
		return text.isEmpty();
	}

	@Override
	public String format( Message message, List<String> args ) {
		colors.addAll( message.colors );
		return this.toString();
	}
}
