package io.github.mdsimmo.bomberman.messaging;

import java.math.BigDecimal;
import java.util.List;

public class Switch implements Formattable {

	@Override
	public String format( Message message, List<String> args ) {
		BigDecimal value = new BigDecimal( args.get( 0 ) );
		final int size = args.size()-1;
		for ( int i = 1; i < size; i += 2 ) {
			String test = args.get( i );
			if ( equal( value, test ) )
				return args.get( i+1 );
		}
		// return the default value
		return args.get( size );
	}
	
	private boolean equal( BigDecimal number, String arg ) {
		// TODO switch equaling is very heavy
		String[] parts = arg.split( "," );
		for ( String part : parts ) {
			String[] subParts = part.split( "-" );
			if ( subParts.length == 1 ) {
				if ( new BigDecimal( part.trim() ).equals( number ) )
					return true;
			} else if ( subParts.length == 2 ) {
				BigDecimal min = new BigDecimal( subParts[0].trim() );
				BigDecimal max = new BigDecimal( subParts[1].trim() );
				if (min.compareTo( number ) <= 0 && max.compareTo( number ) >= 0 )
					return true;
			}
		}
		return false;
	}

}
