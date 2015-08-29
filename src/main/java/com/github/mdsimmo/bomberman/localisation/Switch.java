package com.github.mdsimmo.bomberman.localisation;

import java.util.List;

public class Switch implements Formattable {

    @Override
    public String format( Message message, List<String> args ) {
        final int size = args.size();
        if ( size < 4 )
            throw new RuntimeException( "switch needs at least 4 arguments" );
        if ( size % 2 != 0 )
            throw new RuntimeException( "switch needs an even amount of arguments" );

        String val = args.get( 0 );
        for ( int i = 1; i < size-1; i += 2 ) {
            String test = args.get( i );
            if ( equal( val, test ) )
                return args.get( i+1 );
        }
        // return the default value
        return args.get( size-1 );
    }

    private boolean equal( String start, String arg ) {
        String[] parts = arg.split( "," );
        for ( String part : parts ) {
            if ( part.trim().equalsIgnoreCase( start ) )
                return true;
        }
        return false;
    }

}
