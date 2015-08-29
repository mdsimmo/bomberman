package com.github.mdsimmo.bomberman.localisation;

import com.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Language implements Formattable {

    public static class LanguageNotFoundException extends Exception {
        public LanguageNotFoundException( String s, Throwable throwable ) {
            super( s, throwable );
        }

    }

    private static final Map<String, Language> langs = new HashMap<String, Language>();
    private static final Plugin plugin = Bomberman.instance();
    private static final File languagesDir = new File( plugin.getDataFolder(), "languages" );

    static {
        langs.put( "english", EnglishLanguage.instance );
    }

    /**
     * Gets the language with the given name. If the language does not exist, then a FileNotFoundException will be thrown.
     * @param name the name of the language to fetch
     * @return the language to use for that name. Never null.
     * @throws NullPointerException if name is null
     * @throws FileNotFoundException if there is no language file for the requested file
     */
    public static Language getLanguage( String name ) throws FileNotFoundException {
        if ( name == null )
            throw new NullPointerException( "name cannot be null" );

        String matchName = name.toLowerCase();
        // first check in cache
        Language language = langs.get( matchName );
        if ( language != null )
            return language;

        // attempt to get from file
        File file = getFile( name );
        language = loadLanguage( name, file );

        // cache and return the result
        langs.put( matchName, language );
        return language;
    }

    /**
     * Looks up the file that the given language will be stored in.
     * @param name the name of the language to look for
     * @return the languages location
     * @throws FileNotFoundException
     */
    private static File getFile( String name ) throws FileNotFoundException {
        return new File( languagesDir, name );
    }

    /**
     * Loads the given language from a file
     * @param name the name of the language to load
     * @param file the file the languages data is stored in
     * @return the loaded language
     */
    private static Language loadLanguage( String name, File file ) {
        if ( file == null )
            throw new NullPointerException( "file cannot be null" );
        if ( !file.exists() )
            throw new IllegalArgumentException( "File must exist" );
        YamlConfiguration languageConfig = YamlConfiguration.loadConfiguration( file );
        return new Language( name, languageConfig );
    }

    /**
     * Gets the default language to use. This language can be used as a backup to other languages.
     * @return the default language - always a valid language and never null
     */
    public static Language getDefaultLanguage() {
        // todo read default language from config
        return EnglishLanguage.instance;
    }

    private final YamlConfiguration save;
    private final String name;
    private final Language bup;

    private Language( String name, YamlConfiguration save ) {
        this.name = name;
        this.save = save;
        this.bup = getBup();
    }

    Language getBup() {
        String bupName = save.getString( "backup-language" );
        Language bup = null;
        if ( bupName == null ) {
            bup = getDefaultLanguage();
        } else {
            try {
                bup = getLanguage( name );
            } catch ( FileNotFoundException e ) {
                bup = getDefaultLanguage();
                plugin.getLogger().warning( "language " + name + " had an invalid backup language " + bupName + ". Using " + bup.name + "." );
            }
        }
        return bup;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Translates the phrase into this language. The returned string will still need to be passed through the Formatter
     * before being shown to a player.
     * @param phrase the phrase to translate
     * @return the translated phrase.
     */
    public String translate( Phrase phrase ) {
        String t = save.getString( phrase.getKey() );
        if ( t == null )
            return bup.translate( phrase );
        else
            return t;
    }

    @Override
    public String format( Message message, List<String> args ) {
        return name;
    }

    /**
     * The default english language. Simply takes all it's values from the phrases default value
     */
    private static class EnglishLanguage extends Language {

        public static final EnglishLanguage instance = new EnglishLanguage();

        public EnglishLanguage() {
            super( "english", null  );
        }

        @Override
        Language getBup() {
            return null;
        }

        public String translate( Phrase phrase ) {
            return phrase.getDefault();
        }

    }

}