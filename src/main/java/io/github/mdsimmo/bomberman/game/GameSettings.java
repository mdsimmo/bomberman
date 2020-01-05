package io.github.mdsimmo.bomberman.game;

import io.github.mdsimmo.bomberman.Config;
import io.github.mdsimmo.bomberman.prizes.EmptyPayment;
import io.github.mdsimmo.bomberman.prizes.Payment;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class GameSettings implements ConfigurationSerializable {

    public Material bombMaterial;
    public boolean autostart;
    public int autostartDelay;
    public ItemStack bombItem;
    public int bombs;
    public double dropChance;
    public List<ItemStack> drops;
    public Payment fare;
    public int lives;
    public int minPlayers;
    public int power;
    public Material powerMaterial;
    public Payment prize;
    public boolean protection;
    public boolean protectFire;
    public boolean protectPlace;
    public boolean protectBreak;
    public boolean protectExplosion;
    public boolean protectDamage;
    public boolean protectPVP;
    public int potionDuration;
    public int suddenDeath;
    public int timeout;
    public List<ItemStack> initialitems;

    {
        try {
            prize = Config.PRIZE.getValue( save );
        } catch( ClassCastException ignored ) {
        }
        if ( prize == null )
            prize = EmptyPayment.getEmptyPayment();
        try {
            fare = Config.FARE.getValue( save );
        } catch( ClassCastException ignored ) {
        }
        if ( fare == null )
            fare = EmptyPayment.getEmptyPayment();
        bombs = Config.BOMBS.getValue( save );
        power = Config.POWER.getValue( save );
        lives = Config.LIVES.getValue( save );
        minPlayers = Config.MIN_PLAYERS.getValue( save );
        minPlayers = Math.max( 1, minPlayers );
        autostart = Config.AUTOSTART.getValue( save );
        autostartDelay = Config.AUTOSTART_DELAY.getValue( save );
        drops = Config.DROPS_ITEMS.getValue( save );
        dropChance = Config.DROPS_CHANCE.<Number>getValue( save ).doubleValue();
        protection = Config.PROTECT.getValue( save );
        protectBreak = Config.PROTECT_DESTROYING.getValue( save );
        protectPlace = Config.PROTECT_PLACING.getValue( save );
        protectFire = Config.PROTECT_FIRE.getValue( save );
        protectExplosion = Config.PROTECT_EXPLOSIONS.getValue( save );
        protectDamage = Config.PROTECT_DAMAGE.getValue( save );
        protectPVP = Config.PROTECT_PVP.getValue( save );
        suddenDeath = Config.SUDDEN_DEATH.getValue( save );
        timeout = Config.TIME_OUT.getValue( save );
        initialitems = Config.INITIAL_ITEMS.getValue( save );
        potionDuration = Config.POTION_DURATION.getValue( save );
        bombMaterial = Material.getMaterial( (String)Config.BOMB_MATERIAL
                .getValue( save ) );
        powerMaterial = Material.getMaterial( (String)Config.POWER_MATERIAL
                .getValue( save ) );
    }


    public void setAutostart( boolean autostart ) {
        this.autostart = autostart;
        save.set( Config.AUTOSTART.getPath(), autostart );
    }

    public void setAutostartDelay( int delay ) {
        this.autostartDelay = delay;
        save.set( Config.AUTOSTART_DELAY.getPath(), delay );
    }

    public void setBombs( int bombs ) {
        this.bombs = bombs;
        save.set( Config.BOMBS.getPath(), bombs );
    }

    public void setFare( Payment fare ) {
        if ( fare == null )
            throw new NullPointerException( "Cannot set a null fare" );
        this.fare = fare;
        save.set( Config.FARE.getPath(), fare );
    }

    public void setLives( int lives ) {
        this.lives = lives;
        save.set( Config.LIVES.getPath(), lives );
    }

    public void setMinPlayers( int minPlayers ) {
        this.minPlayers = Math.max( 1, minPlayers );
        save.set( Config.MIN_PLAYERS.getPath(), minPlayers );
    }

    public void setPower( int power ) {
        this.power = power;
        save.set( Config.POWER.getPath(), power );
    }

    public void setPrize( Payment prize ) {
        if ( prize == null )
            throw new NullPointerException( "Cannot set a null prize" );
        this.prize = prize;
        save.set( Config.PRIZE.getPath(), prize );
    }

    @Override
    public Map<String, Object> serialize() {
        return null;
    }

    public GameSettings deserialize(Map<String, Object> data) {

    }
}
