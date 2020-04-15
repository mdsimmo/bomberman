package io.github.mdsimmo.bomberman.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * A list of Contexteds that are defined in english.yml in the resource folder.
 */
public enum Text implements Contexted {

	MESSAGE_FORMAT ( "format.message" ),
	HEADING_FORMAT ( "format.heading" ),
	MAP_FORMAT ( "format.map" ),
	LIST_FORMAT ( "format.list" ),
	ITEM_FORMAT ( "format.item" ),

	GAME ( "word.game" ),
	ARENA ( "word.arena" ),

	TRUE ( "word.true" ),
	FALSE ( "word.false" ),

	EDIT_BUILD_DENIED ( "editmode.builddenied" ),
	EDIT_DESTROY_DENIED ( "editmode.destroydenied" ),

	TELEPORT_DENIED ( "game-play.teleport-denied" ),
	HIT_OPPONENT ( "game-play.hit-opponent" ),
	HIT_SUICIDE ( "game-play.hit-suicide" ),
	HIT_BY ( "game-play.hit-by" ),
	KILL_OPPONENT ( "game-play.kill-opponent" ),
	KILL_SUICIDE ( "game-play.kill-suicide" ),
	KILLED_BY ( "game-play.killed-by" ),
	PLAYER_KILLED( "game-play.player-killed" ),
	PLAYER_WON( "game-play.player-won" ),
	PLAYER_LEFT( "game-play.player-left" ),
	GAME_STARTING ( "game-play.starting" ),
	GAME_COUNT ( "game-play.count" ),
	GAME_STARTED( "game-play.started" ),
	COUNT_STOPPED ( "game-play.stopped-count" ),
	GAME_OVER ( "game-play.game-over" ),

	SUDDENDEATH_COUNT ( "suddendeath.count" ),
	SUDDENDEATH ( "suddendeath.start" ),
	TIMEOUT_COUNT ( "timeout.count" ),
	TIMEOUT ( "timeout.start" ),

	SCORE_ANNOUNCE ( "scores.announce" ),
	WINNERS_LIST ( "scores.winners" ),

	DENY_PERMISSION ( "command.deny-permission" ),
	INCORRECT_USAGE ( "command.incorrect-usage" ),
	UNKNOWN_COMMAND ( "command.unknown-command" ),
	MUST_BE_PLAYER ( "command.must-be-player" ),
	INVALID_NUMBER ( "command.invalid-number" ),
	INVALID_PLAYER ( "command.invalid-player" ),
	INVALID_MATERIAL ( "command.invalid-material" ),
	INVALID_SCHEMA ( "command.invalid-schema" ),
	INVALID_GAME ( "command.invalid-game" ),
	COMMAND_GROUP_USAGE("command.group.usage"),
	COMMAND_GROUP_EXAMPLE("command.group.example"),
	COMMAND_GROUP_EXTRA("command.group.extra"),
	COMMAND_FORMAT("command.format"),

	// no Bomberman name as it must not be changed
	BOMBERMAN_DESCRIPTION ( "command.bomberman.description" ),

	ARENA_CREATE_NAME ( "command.arenacreate.name" ),
	ARENA_CREATE_DESCRIPTION ( "command.arenacreate.description" ),
	ARENA_CREATE_USAGE ( "command.arenacreate.usage" ),
	ARENA_CREATE_EXAMPLE ( "command.arenacreate.example" ),
	ARENA_CREATE_EXTRA ( "command.arenacreate.extra" ),
	ARENA_CREATED ( "command.arenacreate.success" ),
	ARENA_CREATING ( "command.arenacreate.started" ),
	ARENA_NO_TARGET ( "command.arenacreate.notarget" ),
	ARENA_CREATE_TOO_BIG ( "command.arenacreate.too-big" ),
	ARENA_CREATE_VERY_SMALL ( "command.arenacreate.very-small" ),
	ARENA_CREATE_IN_USE ( "command.arenacreate.in-use" ),

	START_NAME ( "command.start.name" ),
	START_DESCRIPTION ( "command.start.description" ),
	START_USAGE ( "command.start.usage" ),
	START_EXAMPLE ( "command.start.example" ),
	START_EXTRA ( "command.start.extra" ),
	GAME_ALREADY_STARTED ( "command.start.already-started" ),
	GAME_START_SUCCESS ( "command.start.success" ),
	GAME_START_CANCELLED ( "command.start.cancelled" ),
	GAME_MORE_PLAYERS ( "command.start.more-players" ),

	STOP_NAME ( "command.stop.name" ),
	STOP_DESCRIPTION ( "command.stop.description" ),
	STOP_USAGE ( "command.stop.usage" ),
	STOP_EXAMPLE ( "command.stop.example" ),
	STOP_EXTRA ( "command.stop.extra" ),
	STOP_NOT_STARTED ( "command.stop.not-started" ),
	STOP_SUCCESS ( "command.stop.success" ),

	SET_SCHEMA_NAME ( "command.set-schema.name" ),
	SET_SCHEMA_DESCRIPTION ( "command.set-schema.description" ),
	SET_SCHEMA_USAGE ( "command.set-schema.usage" ),
	SET_SCHEMA_EXAMPLE ( "command.set-schema.example" ),
	SET_SCHEMA_EXTRA ( "command.set-schema.extra" ),
	SET_SCHEMA_GIP ( "command.set-schema.game-in-progress" ),
	SET_SCHEMA_STARTED ( "command.set-schema.started" ),
	SET_SCHEMA_SUCCESS ( "command.set-schema.success" ),

	AUTOSTART_NAME ( "command.autostart.name" ),
	AUTOSTART_DESCRIPTION ( "command.autostart.description" ),
	AUTOSTART_USAGE ( "command.autostart.usage" ),
	AUTOSTART_EXAMPLE ( "command.autostart.example" ),
	AUTOSTART_EXTRA ( "command.autostart.extra" ),
	AUTOSTART_ENABLED ( "command.autostart.enabled" ),
	AUTOSTART_DISABLED ( "command.autostart.disabled" ),

	STARTDELAY_NAME ( "command.start-delay.name" ),
	STARTDELAY_DESCRIPTION ( "command.start-delay.description" ),
	STARTDELAY_USAGE ( "command.start-delay.usage" ),
	STARTDELAY_EXAMPLE ( "command.start-delay.example" ),
	STARTDELAY_EXTRA ( "command.start-delay.extra" ),
	STARTDELAY_SET ( "command.start-delay.set" ),

	BOMBS_NAME ( "command.bombs.name" ),
	BOMBS_DESCRIPTION ( "command.bombs.description" ),
	BOMBS_USAGE ( "command.bombs.usage" ),
	BOMBS_EXAMPLE ( "command.bombs.example" ),
	BOMBS_EXTRA ( "command.bombs.extra" ),
	BOMBS_SET ( "command.bombs.set" ),

	FARE_NAME ( "command.fare.name" ),
	FARE_DESCRIPTION ( "command.fare.description" ),
	FARE_USAGE ( "command.fare.usage" ),
	FARE_EXAMPLE ( "command.fare.example" ),
	FARE_EXTRA ( "command.fare.extra" ),
	FARE_SET ( "command.fare.set" ),
	FARE_NONE ( "command.fare.none" ),
	FARE_XP ( "command.fare.xp" ),
	FARE_VAULT ( "command.fare.money" ),
	FARE_IN_HAND ( "command.fare.inhand" ),

	HANDICAP_NAME ( "command.handicap.name" ),
	HANDICAP_DESCRIPTION ( "command.handicap.description" ),
	HANDICAP_USAGE ( "command.handicap.usage" ),
	HANDICAP_EXAMPLE ( "command.handicap.example" ),
	HANDICAP_EXTRA ( "command.handicap.extra" ),
	HANDICAP_SET ( "command.handicap.set" ),
	
	LIVES_NAME ( "command.lives.name" ),
	LIVES_DESCRIPTION ( "command.lives.description" ),
	LIVES_USAGE ( "command.lives.usage" ),
	LIVES_EXAMPLE ( "command.lives.example" ),
	LIVES_EXTRA ( "command.lives.extra" ),
	LIVES_SET ( "command.lives.set" ),

	MINPLAYERS_NAME ( "command.minplayers.name" ),
	MINPLAYERS_DESCRIPTION ( "command.minplayers.description" ),
	MINPLAYERS_USAGE ( "command.minplayers.usage" ),
	MINPLAYERS_EXAMPLE ( "command.minplayers.example" ),
	MINPLAYERS_EXTRA ( "command.minplayers.extra" ),
	MINPLAYERS_SET ( "command.minplayers.set" ),
	MINPLAYERS_LESS_THAN_ONE ( "command.minplayers.more-than-zero" ),

	POWER_NAME ( "command.power.name" ),
	POWER_DESCRIPTION ( "command.power.description" ),
	POWERS_USAGE ( "command.power.usage" ),
	POWER_EXAMPLE ( "command.power.example" ),
	POWER_EXTRA ( "command.power.extra" ),
	POWER_SET ( "command.power.set" ),

	PRIZE_NAME ( "command.prize.name" ),
	PRIZE_DESCRIPTION ( "command.prize.description" ),
	PRIZE_USAGE ( "command.prize.usage" ),
	PRIZE_EXAMPLE ( "command.prize.example" ),
	PRIZE_EXTRA ( "command.prize.extra" ),
	PRIZE_POT ( "command.prize.pot" ),
	PRIZE_NONE ( "command.prize.none" ),
	PRIZE_XP ( "command.prize.xp" ),
	PRIZE_IN_HAND ( "command.prize.inhand" ),
	PRIZE_VAULT ( "command.prize.money" ),
	PRIZE_SET ( "command.prize.set" ),
			
	SET_NAME ( "command.set.name" ),
	SET_DESCRIPTION ( "command.set.description" ),

	SUDDENDEATH_NAME ( "command.suddendeath.name" ),
	SUDDENDEATH_DESCRIPTION ( "command.suddendeath.description" ),
	SUDDENDEATH_USAGE ( "command.suddendeath.usage" ),
	SUDDENDEATH_EXAMPLE ( "command.suddendeath.example" ),
	SUDDENDEATH_EXTRA ( "command.suddendeath.extra" ),
	SUDDENDEATH_OFF ( "command.suddendeath.off" ),
	SUDDENDEATH_REMOVED ( "command.suddendeath.removed" ),
	SUDDENDEATH_SET ( "command.suddendeath.set" ),

	TIMEOUT_NAME ( "command.timeout.name" ),
	TIMEOUT_DESCRIPTION ( "command.timeout.description" ),
	TIMEOUT_USAGE ( "command.timeout.usage" ),
	TIMEOUT_EXAMPLE ( "command.timeout.example" ),
	TIMEOUT_EXTRA ( "command.timeout.extra" ),
	TIMEOUT_OFF ( "command.timeout.off" ),
	TIMEOUT_REMOVED ( "command.timeout.removed" ),
	TIMEOUT_SET ( "command.timeout.set" ),

	CONVERT_NAME ( "command.convert.name" ),
	CONVERT_DESCRIPTION ( "command.convert.description" ),
	CONVERT_USAGE ( "command.convert.usage" ),
	CONVERT_EXAMPLE ( "command.convert.example" ),
	CONVERT_EXTRA ( "command.convert.extra" ),
	CONVERT_GAME_EXISTS ( "command.convert.game-exists" ),
	CONVERT_STARTED ( "command.convert.started" ),
	CONVERT_SUCCESS ( "command.convert.success" ),

	CREATE_NAME ( "command.create.name" ),
	CREATE_DESCRIPTION ( "command.create.description" ),
	CREATE_USAGE ( "command.create.usage" ),
	CREATE_EXAMPLE ( "command.create.example" ),
	CREATE_EXTRA ( "command.create.extra" ),
	CREATE_GAME_EXISTS ( "command.create.game-exists" ),
	CREATE_NEED_SELECTION ( "command.create.need-selection" ),
	CREATE_STARTED ( "command.create.started" ),
	CREATE_SUCCESS ( "command.create.success" ),
	CREATE_DEFAULTMISSING ( "command.create.default-missing" ),
	CREATE_SCHEMA_NOT_FOUND ( "command.create.schema-not-found" ),

	DESTROY_NAME ( "command.destroy.name" ),
	DESTROY_DESCRIPTION ( "command.destroy.description" ),
	DESTROY_USAGE ( "command.destroy.usage" ),
	DESTROY_EXAMPLE ( "command.destroy.example" ),
	DESTROY_EXTRA ( "command.destroy.extra" ),
	DESTROY_SUCCESS ( "command.destroy.success" ),

	FORGET_NAME ( "command.forget.name" ),
	FORGET_DESCRIPTION ( "command.forget.description" ),
	FORGET_USAGE ( "command.forget.usage" ),
	FORGET_EXAMPLE ( "command.forget.example" ),
	FORGET_EXTRA ( "command.forget.extra" ),
	FORGET_SUCCESS ( "command.forget.success" ),

	GAME_NAME ( "command.game.name" ),
	GAME_DESCRIPTION ( "command.game.description" ),

	GAMELIST_NAME ( "command.gamelist.name" ),
	GAMELIST_DESCRIPTION ( "command.gamelist.description" ),
	GAMELIST_USAGE ( "command.gamelist.usage" ),
	GAMELIST_EXAMPLE ( "command.gamelist.example" ),
	GAMELIST_EXTRA ( "command.gamelist.extra" ),
	GAMELIST_GAMES ( "command.gamelist.games" ),

	INFO_NAME ( "command.info.name" ),
	INFO_DESCRIPTION ( "command.info.description" ),
	INFO_USAGE ( "command.info.usage" ),
	INFO_EXAMPLE ( "command.info.example" ),
	INFO_EXTRA ( "command.info.extra" ),
	INFO_DETAILS ( "command.info.details" ),


	JOIN_NAME ( "command.join.name" ),
	JOIN_DESCRIPTION ( "command.join.description" ),
	JOIN_USAGE ( "command.join.usage" ),
	JOIN_EXAMPLE ( "command.join.example" ),
	JOIN_EXTRA ( "command.join.extra" ),
	JOIN_GAME_STARTED ( "command.join.game-started" ),
	JOIN_ALREADY_JOINED ( "command.join.already-joined" ),
	GAME_FULL ( "command.join.game-full" ),
	TOO_POOR ( "command.join.poor-man" ),
	CANT_JOIN ( "command.join.cant-join" ),
	PLAYER_JOINED ( "command.join.player-joined" ),

	LEAVE_NAME ( "command.leave.name" ),
	LEAVE_DESCRIPTION ( "command.leave.description" ),
	LEAVE_USAGE ( "command.leave.usage" ),
	LEAVE_EXAMPLE ( "command.leave.example" ),
	LEAVE_EXTRA ( "command.leave.extra" ),
	LEAVE_SUCCESS ( "command.leave.success" ),
	LEAVE_NOT_JOINED ( "command.leave.not-joined" );

	private final String text;
	
	Text( String path ) {
		this.text = YAMLLanguage.lang.getString( path );
		
		// any error's here should always get caught during development
		if ( text == null )
			throw new RuntimeException( "No default message for text: " + path  );
	}

	@Override
	public Contexted with(String key, Formattable thing) {
		return new Contexted() {
			Map<String, Formattable> things = new HashMap<>();

			@Override
			public Contexted with(String key, Formattable arg) {
				things.put(key, arg);
				return this;
			}

			@NotNull
			@Override
			public Message format() {
				return Expander.expand(text, things);
			}
		}.with(key, thing);
	}

	@Override
	public Message format() {
		return Expander.expand(text, Map.of());
	}

	// class to read the English language text from
	static class YAMLLanguage {
		static final YamlConfiguration lang;
		static {
			InputStream in = Text.class.getClassLoader().getResourceAsStream("english.yml");
			Reader reader = new BufferedReader( new InputStreamReader( in ) );
			lang = new YamlConfiguration();
			try {
				lang.load( reader );
			} catch ( IOException | InvalidConfigurationException e ) {
				e.printStackTrace();
			}
		}
	}

}
