package io.github.mdsimmo.bomberman.save;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NullArgumentException;

public class CompressedSection {

	private StringBuilder content = new StringBuilder();
	private char separator;
	private boolean dirty = false;
	private List<Object> objects = new ArrayList<Object>();

	/**
	 * Creates a CompressedSection using {@code|separator} to define the
	 * edges between sections
	 * @param separator the separator. Make sure no text added to the list contains
	 * this character.
	 */
	public CompressedSection( char separator ) {
		if ( separator == '!' )
			throw new IllegalArgumentException( "Seperator may not be '!'" );
		this.separator = separator;
	}

	/**
	 * Adds a part to this section.
	 * @param parts the parts to add
	 */
	public void addParts( Object... parts ) {
		dirty = true;
		for ( Object o : parts ) 
			objects.add( o );
	}
	
	/**
	 * Adds all the temporary objects to the built up string
	 */
	private void flush() {
		int consecutiveObjects = 0;
		String lastObject = null;
		for ( Object part : objects ) {
			String s = part.toString();
			if ( s.equals( lastObject ) ) {
				consecutiveObjects++;
				continue;
			} else {
				if ( consecutiveObjects > 0 )
					append( lastObject, consecutiveObjects );
				lastObject = s;
				consecutiveObjects = 1;
			}
		}
		
		// append left over objects
		append( lastObject, consecutiveObjects );
		
		dirty = false;
		objects.clear();
	}
	
	/**
	 * Whites the object to the list using the compressed format. If amount is 0,
	 * then nothing happens, if amount is one, then the string is written as normal,
	 * if amount is greater that one, it will be written using the compressed syntax.
	 * @param object the string to write
	 * @param amount the amount of time it should be in the list
	 * @throws IllegalArgumentException if amount is less than 0
	 */
	private void append( String object, int amount ) {
		if ( amount < 0 )
			throw new IllegalArgumentException( "Attempted to append " + amount + " " + object );
		if ( amount == 0 )
			return;
		if ( amount != 1 )
			content.append( "!" ).append( amount ).append( separator );
		content.append( object ).append( separator );
	}

	/**
	 * reads the next part of the file.
	 * 
	 * @return the read part. null if the end is reached
	 */
	public List<String> readParts() {
		if ( dirty )
			flush();
		List<String> parts = new ArrayList<>();
		StringBuilder part = new StringBuilder();
		int length = content.length();
		int nextAmount = 1;
		for ( int i = 0; i < length; i++ ) {
			char c = content.charAt( i );
			if ( c == separator ) {
				if ( part.charAt( 0 ) == '!' ) {
					nextAmount = Integer.parseInt( part.substring( 1 ) );
				} else {
					String p = part.toString();
					for( int j = 0; j < nextAmount; j++ )
						parts.add( p );
					nextAmount = 1;
				}
				part.setLength( 0 );
			} else {
				part.append( c );
			}
		}
		
		// add the last elements if they exists
		if ( part.length() != 0 ) {
			String p = part.toString();
			for( int j = 0; j < nextAmount; j++ )
				parts.add( p );
		}
		
		return parts;
	}

	/**
	 * Removes everything this list contains
	 */
	public void reset() {
		dirty = false;
		objects.clear();
		content.setLength( 0 );
	}

	/**
	 * Sets what this list contains. This method should be used for loading compressed sections
	 */
	public void setValue( String value ) {
		if ( value == null )
			throw new NullArgumentException( "value cannot be null" );
		if ( value.isEmpty() )
			throw new NullArgumentException( "value cannot be empty" );
		
		// set the value
		reset();
		content.append( value );
		
		// add a ending separator if its not there already
		if ( value.charAt( value.length()-1 ) != separator )
			content.append( separator );
	}

	/**
	 * Turns the file into the savable string
	 */
	@Override
	public String toString() {
		flush();
		// get the string minus the closing separator
		return content.substring( 0, content.length()-1 );
	}
}