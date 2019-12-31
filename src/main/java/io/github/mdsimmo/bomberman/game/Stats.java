package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;

import java.util.List;

public class Stats implements Formattable {

    public int deaths = 0;
    public int kills = 0;
    public int hitsTaken = 0;
    public int hitsGiven = 0;
    public int suicides = 0;

    @Override
    public String format(Message message, List<String> args ) {
        switch ( args.remove( 0 ) ) {
        case "kills":
            return Integer.toString( kills );
        case "deaths":
            return Integer.toString( deaths );
        case "hitsgiven":
            return Integer.toString( hitsGiven );
        case "hitstaken":
            return Integer.toString( hitsTaken );
        case "suicides":
            return Integer.toString( suicides );
        }
        return "INVALID REFERENCE";
    }
}
