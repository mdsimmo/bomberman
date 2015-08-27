package com.github.mdsimmo.bomberman.localisation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import com.github.mdsimmo.bomberman.Bomberman;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Language implements Formattable {

    private static Map<String, Language> langs = new HashMap<String, Language>();
    private static Plugin plugin = Bomberman.instance();
    private static File langsDir = new File( plugin.getDataFolder(), "languages" );

    /**
     * Gets the language with the given name. If the language does not exist, then the english language will be
     * returned. Thus, this method will always return a valid language.
     * @param lang the language name to get
     * @return the language to use for that name. Never null
     */
    public static Language getLanguage( String lang ) {
        if ( lang == null )
            return null;
        lang = lang.toLowerCase();
        Language language = langs.get( lang );
        if ( language != null ) {
            return language;
        } else {
            if ( getFile( lang ).exists() ) {
                language = new Language( lang );
                langs.put( lang, language );
                return language;
            } else {
                if ( lang.equalsIgnoreCase( "english" ) ) {
                    language = new EnglishLanguage();
                    langs.put( "english", language );
                    return language;
                } else {
                    return null;
                }
            }
        }
    }

    /**
     * Tests if the language with the given name exists.
     * @param lang the languages name. "English" or an empty string for english
     * @return true if the language exists
     */
    public static boolean hasLanguage( String lang ) {
        lang = lang.toLowerCase();
        return lang.equals("english") || lang.isEmpty() || lang.contains(lang);
    }

    private static File getFile( String lang ) {
        return new File( plugin.getDataFolder(), lang.toLowerCase() + ".lang" );
    }

    public static List<String> allLanguages() {
        File dir = plugin.getDataFolder();
        File[] files = dir.listFiles( new FilenameFilter() {
            @Override
            public boolean accept( File file, String name ) {
                return name.endsWith( ".lang" );
            }
        } );
        List<String> langs = new ArrayList<String>( files.length );
        for ( File f : files ) {
            langs.add( f.getName() );
        }
        if ( !langs.contains( "english" ) )
            langs.add( "english" );
        return langs;
    }

    private final YamlConfiguration save;
    private final String name;
    private final Language bup;

    private Language( String lang ) {
        File f = getFile( lang );
        save = YamlConfiguration.loadConfiguration( f );
        name = lang;
        String bupLang = save.getString( "language-bup" );
        if ( bupLang != null )
            bup = getLanguage( bupLang );
        else
            bup = getLanguage( "english" );
    }

    /**
     * Translates the phrase into this language. The returned string will still need to be passed through the Formatter
     * before being shown to a player.
     * @param phrase the phrase to translate
     * @return the translated phrase.
     */
    public String translate( Phrase phrase ) {
        String t = save.getString(phrase.getKey());
        if ( t == null )
            Language bup = getLanguage( (String)Config.LANGUAGE.getValue( save ) );
            if ( bup == null )
                return phrase.getDefault();
            else
                return bup.translate( phrase );
        } else
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
        public EnglishLanguage() {
            super( "english" );
        }

        public String translate( Phrase phrase ) {
            return phrase.getDefault();
        }

    }

}