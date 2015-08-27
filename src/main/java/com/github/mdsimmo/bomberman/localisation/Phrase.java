package com.github.mdsimmo.bomberman.localisation;

/**
 * All user messages shown to players should be defined as a phrase. A phrase allows the player to select a language to
 * use and then receive all messages in that language.
 */
public class Phrase {

    private final String bup, key;

    /**
     * Creates a phrase from the key and the default text
     * @param key the key to use
     * @param bup the default text
     * @throws NullPointerException if key or bup is null
     */
    public Phrase( String key, String bup ) {
        if ( key == null )
            throw new NullPointerException( "key cannot be null" );
        if ( bup == null )
            throw new NullPointerException( "default value cannot be null" );
        this.key = key;
        this.bup = bup;
    }

    /**
     * The default english message to use when the user defined message is either faulty or missing.
     * @return the default message
     */
    public String getDefault() {
        return bup;
    }

    /**
     * Gets the key that should be used with this message
     * @return the key
     */
    public String getKey() {
        return key;
    }
}
