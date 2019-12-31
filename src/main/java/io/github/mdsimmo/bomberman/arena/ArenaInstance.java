package io.github.mdsimmo.bomberman.arena;

import io.github.mdsimmo.bomberman.messaging.Formattable;
import io.github.mdsimmo.bomberman.messaging.Message;
import org.bukkit.Location;

import java.util.List;

public class ArenaInstance implements Formattable {

    private ArenaTemplate base;
    private Location location;

    public ArenaInstance(ArenaTemplate template, Location location) {
        this.base = template;
        this.location = location;
    }

    public ArenaTemplate getBase() {
        return base;
    }

    public void setBase(ArenaTemplate base) {
        this.base = base;
    }

    @Override
    public String format(Message message, List<String> args) {
        if (args.size() == 0)
            return base.name;
        if (args.size() != 1)
            throw new RuntimeException("Arenas can have at most one argument");
        switch (args.remove(0)) {
            case "xsize":
                return Integer.toString(base.size.x);
            case "ysize":
                return Integer.toString(base.size.y);
            case "zsize":
                return Integer.toString(base.size.z);
            default:
                return null;
        }
    }

    private fun findSpareSpawn(): Location? {
        val list = base !!.spawnPoints ?: return null
        for (v in list!!) {
            if (blockEmpty(v))
                return v
        }
        return null
    }

}
