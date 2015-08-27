package com.github.mdsimmo.bomberman.utils;

/**
 * The Files class provides some helper methods for files
 */
public final class Files {

    /**
     * This removes all "special" characters from a string so that is is a valid
     * string on all operating systems. More precisely, it converts the filename
     * to lowercase and converts anything that is not a letter, number or period
     * to an underscore character.
     * @param filename the filename to standardise
     * @return the standardised filename
     */
    public static String standardise( String filename ) {
        return filename.toLowerCase().replaceAll( "[^a-z0-9.]", "_" );
    }

}
