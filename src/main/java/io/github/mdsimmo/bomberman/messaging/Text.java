package io.github.mdsimmo.bomberman.messaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import io.github.mdsimmo.bomberman.Bomberman;
import io.github.mdsimmo.bomberman.PlayerRep;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Text implements Phrase {

	MESSAGE_FORMAT ( "format.message" ),
	HEADING_FORMAT ( "format.heading" ),
	MAP_FORMAT ( "format.map" ),
	LIST_FORMAT ( "format.list" ),

	GAME ( "word.game" ),
	ARENA ( "word.arena" ),

	HELP ( "word.help" ),
	LIST ( "word.list" ),
	INFO ( "word.info" ),
	DESCTIPTION ( "word.description" ),
	USAGE ( "word.usage" ),
	EXTRA ( "word.extra" ),
	COMMANDS ( "word.commands" ),
	EXAMPLE ( "word.example" ),
	TRUE ( "word.true" ),
	FALSE ( "word.false" ),

	SPECIFY_GAME ( "join.specify-game" ),
	GAME_FULL ( "join.game-full" ),
	TOO_POOR ( "join.poor-man" ),
	PLAYER_JOINED ( "join.player-joined" ),

	PLAYER_BUSY ( "player.busy" ),

	EDIT_BUILD_DENIED ( "editmode.builddenied" ),
	EDIT_DESTROY_DENIED ( "editmode.destroydenied" ),

	TELEPORT_DENIED ( "game-play.teleport-denied" ),
	HIT_OPPONENT ( "game-play.hit-opponent" ),
	HIT_SUICIDE ( "game-play.hit-suicide" ),
	HIT_BY ( "game-play.hit-by" ),
	KILL_OPPONENT ( "game-play.kill-opponent" ),
	KILL_SUICIDE ( "game-play.kill-suicide" ),
	KILLED_BY ( "game-play.killed-by" ),
	PLAYER_KILLED_PLAYERS ( "game-play.player-killed.players" ),
	PLAYER_KILLED_OBSERVERS ( "game-play.player-killed.observers" ),
	PLAYER_KILLED_ALL ( "game-play.player-killed.all" ),
	PLAYER_LEFT_PLAYERS ( "game-play.player-left.players" ),
	PLAYER_LEFT_OBSERVERS ( "game-play.player-left.observers" ),
	PLAYER_LEFT_ALL ( "game-play.player-left.all" ),
	NO_REGEN ( "game-play.no-regen" ),
	GAME_STARTING_PLAYERS ( "game-play.starting.players" ),
	GAME_STARTING_OBSERVERS ( "game-play.starting.observers" ),
	GAME_STARTING_ALL ( "game-play.starting.all" ),
	GAME_COUNT_PLAYERS ( "game-play.count.players" ),
	GAME_COUNT_OBSERVERS ( "game-play.count.observers" ),
	GAME_COUNT_ALL ( "game-play.count.all" ),
	GAME_STARTED_PLAYERS ( "game-play.started.players" ),
	GAME_STARTED_OBSERVERS ( "game-play.started.observers" ),
	GAME_STARTED_ALL ( "game-play.started.all" ),
	COUNT_STOPPED_PLAYERS ( "game-play.stopped-count.players" ),
	COUNT_STOPPED_OBSERVERS ( "game-play.stopped-count.observers" ),
	COUNT_STOPPED_ALL ( "game-play.stopped-count.all" ),
	GAME_OVER_PLAYERS ( "game-play.game-over.players" ),
	GAME_OVER_OBSERVERS ( "game-play.game-over.observers" ),
	GAME_OVER_ALL ( "game-play.game-over.all" ),

	SUDDENDEATH_COUNT_P ( "suddendeath.count.players" ),
	SUDDENDEATH_COUNT_O ( "suddendeath.count.observers" ),
	SUDDENDEATH_COUNT_A ( "suddendeath.count.all" ),
	SUDDENDEATH_P ( "suddendeath.start.players" ),
	SUDDENDEATH_O ( "suddendeath.start.observers" ),
	SUDDENDEATH_A ( "suddendeath.start.all" ),
	TIMEOUT_COUNT_P ( "timeout.count.players" ),
	TIMEOUT_COUNT_O ( "timeout.count.observers" ),
	TIMEOUT_COUNT_A ( "timeout.count.all" ),
	TIMEOUT_P ( "timeout.start.players" ),
	TIMEOUT_O ( "timeout.start.observers" ),
	TIMEOUT_A ( "timeout.start.all" ),

	SCORE_ANNOUNCE ( "scores.announce" ),
	WINNERS_LIST ( "scores.winners" ),

	DENY_PERMISSION ( "command.deny-permission" ),
	INCORRECT_USAGE ( "command.incorrect-usage" ),
	UNKNOWN_COMMAND ( "command.unknown-command" ),
	MUST_BE_PLAYER ( "command.must-be-player" ),
	INVALID_NUMBER ( "command.invalid-number" ),
	INVALID_PLAYER ( "command.invalid-player" ),
	INVALID_MATERIAL ( "command.invalid-material" ),
	INVALID_ARENA ( "command.invalid-arena" ),
	INVALID_GAME ( "command.invalid-game" ),

	// no Bomberman name as it must not be changed
	BOMBERMAN_DESCRIPTION ( "command.bomberman.description" ),

	ARENA_NAME ( "command.arena.name" ),
	ARENA_DESCRIPTION ( "command.arena.description" ),

	ARENA_LIST_NAME ( "command.arenalist.name" ),
	ARENA_LIST_DESCRIPTION ( "command.arenalist.description" ),
	ARENA_LIST_USAGE ( "command.arenalist.usage" ),
	ARENA_LIST_EXAMPLE ( "command.arenalist.example" ),
	ARENA_LIST_EXTRA ( "command.arenalist.extra" ),
	ARENA_LIST_NO_ARENA ( "command.arenalist.no-arena" ),

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

	DELETE_NAME ( "command.arenadelete.name" ),
	DELETE_DESCRIPTION ( "command.arenadelete.description" ),
	DELETE_USAGE ( "command.arenadelete.usage" ),
	DELETE_EXAMPLE ( "command.arenadelete.example" ),
	DELETE_EXTRA ( "command.arenadelete.extra" ),
	DELETE_SUCCESSFUL ( "command.arenadelete.success" ),
	DELETE_ARENA_USED ( "command.arenadelete.arena-used" ),
	DELETE_TROUBLE ( "command.arenadelete.error" ),

	EDIT_NAME ( "command.arenaedit.name" ),
	EDIT_DESCRIPTION ( "command.arenaedit.description" ),
	EDIT_USAGE ( "command.arenaedit.usage" ),
	EDIT_EXAMPLE ( "command.arenaedit.example" ),
	EDIT_EXTRA ( "command.arenaedit.extra" ),
	EDIT_SAVE ( "command.arenaedit.save" ),
	EDIT_DISCARD ( "command.arenaedit.discard" ),
	EDIT_IGNORE ( "command.arenaedit.ignore" ),
	EDIT_STARTED ( "command.arenaedit.started" ),
	EDIT_ALREADY_STARTED ( "command.arenaedit.already-started" ),
	EDIT_CHANGES_SAVED ( "command.arenaedit.changes-saved" ),
	EDIT_PROMPT_START ( "command.arenaedit.prompt-start" ),
	EDIT_CANGES_REMOVED ( "command.arenaedit.changes-removed" ),
	EDIT_MODE_QUIT ( "command.arenaedit.changes-ignored" ),

	FORCE_NAME ( "command.force.name" ),
	FORCE_DESCRIPTION ( "command.force.description" ),

	RESET_NAME ( "command.reset.name" ),
	RESET_DESCRIPTION ( "command.reset.description" ),
	RESET_USAGE ( "command.reset.usage" ),
	RESET_EXAMPLE ( "command.reset.example" ),
	RESET_EXTRA ( "command.reset.extra" ),
	RESET_STARTED ( "command.reset.started" ),
	RESET_FINISHED ( "command.reset.finished" ),

	START_NAME ( "command.start.name" ),
	START_DESCRIPTION ( "command.start.description" ),
	START_USAGE ( "command.start.usage" ),
	START_EXAMPLE ( "command.start.example" ),
	START_EXTRA ( "command.start.extra" ),
	GAME_ALREADY_STARTED ( "command.start.already-started" ),
	GAME_START_SUCCESS ( "command.start.success" ),
	GAME_MORE_PLAYERS ( "command.start.more-players" ),

	STOP_NAME ( "command.stop.name" ),
	STOP_DESCRIPTION ( "command.stop.description" ),
	STOP_USAGE ( "command.stop.usage" ),
	STOP_EXAMPLE ( "command.stop.example" ),
	STOP_EXTRA ( "command.stop.extra" ),
	STOP_NOT_STARTED ( "command.stop.not-started" ),
	STOP_SUCCESS ( "command.stop.success" ),

	SETARENA_NAME ( "command.setarena.name" ),
	SETARENA_DESCRIPTION ( "command.setarena.description" ),
	SETARENA_USAGE ( "command.setarena.usage" ),
	SETARENA_EXAMPLE ( "command.setarena.example" ),
	SETARENA_EXTRA ( "command.setarena.extra" ),
	SETARENA_GIP ( "command.setarena.game-in-progress" ),
	SETARENA_STARTED ( "command.setarena.started" ),
	SETARENA_SUCCESS ( "command.setarena.success" ),

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
	CREATE_STARTED ( "command.create.started" ),
	CREATE_SUCCESS ( "command.create.success" ),
	CREATE_DEFAULTMISSING ( "command.create.default-missing" ),

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
	GAMELIST_NO_GAMES ( "command.gamelist.no-games" ),
	GAMELIST_KEY ( "command.gamelist.key" ),
	GAMELIST_VALUE ( "command.gamelist.value" ),

	IGNORE_NAME ( "command.ignore.name" ),
	IGNORE_DESCRIPTION ( "command.ignore.description" ),
	IGNORE_USAGE ( "command.ignore.usage" ),
	IGNORE_EXAMPLE ( "command.ignore.example" ),
	IGNORE_EXTRA ( "command.ignore.extra" ),
	IGNORE_SUCCESS ( "command.ignore.success" ),
	IGNORE_NOT_WATCHED ( "command.ignore.not-watched" ),

	INFO_NAME ( "command.info.name" ),
	INFO_DESCRIPTION ( "command.info.description" ),
	INFO_USAGE ( "command.info.usage" ),
	INFO_EXAMPLE ( "command.info.example" ),
	INFO_EXTRA ( "command.info.extra" ),
	INFO_1 ( "command.info.1-key" ),
	INFO_1_RESULT ( "command.info.1-value" ),
	INFO_2 ( "command.info.2-key" ),
	INFO_2_RESULT ( "command.info.2-value" ),
	INFO_3 ( "command.info.3-key" ),
	INFO_3_RESULT ( "command.info.3-value" ),
	INFO_4 ( "command.info.4-key" ),
	INFO_4_RESULT ( "command.info.4-value" ),
	INFO_5 ( "command.info.5-key" ),
	INFO_5_RESULT ( "command.info.5-value" ),
	INFO_6  ( "command.info.6-key" ),
	INFO_6_RESULT ( "command.info.6-value" ),
	INFO_7 ( "command.info.7-key" ),
	INFO_7_RESULT ( "command.info.7-value" ),
	INFO_8 ( "command.info.8-key" ),
	INFO_8_RESULT ( "command.info.8-value" ),
	INFO_9 ( "command.info.9-key" ),
	INFO_9_RESULT ( "command.info.9-value" ),
	INFO_10 ( "command.info.10-key" ),
	INFO_10_RESULT ( "command.info.10-value" ),
	INFO_11 ( "command.info.11-key" ),
	INFO_11_RESULT ( "command.info.11-value" ),
	INFO_12 ( "command.info.12-key" ),
	INFO_12_RESULT ( "command.info.12-value" ),
	INFO_13 ( "command.info.13-key" ),
	INFO_13_RESULT ("command.info.13-value" ),
	INFO_14 ( "command.info.14-key" ),
	INFO_14_RESULT ( "command.info.14-value" ),
	INFO_15 ( "command.info.15-key" ),
	INFO_15_RESULT ( "command.info.15-value" ),

	JOIN_NAME ( "command.join.name" ),
	JOIN_DESCRIPTION ( "command.join.description" ),
	JOIN_USAGE ( "command.join.usage" ),
	JOIN_EXAMPLE ( "command.join.example" ),
	JOIN_EXTRA ( "command.join.extra" ),
	JOIN_GAME_STARTED ( "command.join.game-started" ),

	LEAVE_NAME ( "command.leave.name" ),
	LEAVE_DESCRIPTION ( "command.leave.description" ),
	LEAVE_USAGE ( "command.leave.usage" ),
	LEAVE_EXAMPLE ( "command.leave.example" ),
	LEAVE_EXTRA ( "command.leave.extra" ),
	LEAVE_NOT_JOINED ( "command.leave.not-joined" ),

	PROTECT_NAME ( "command.protect.name" ),
	PROTECT_DESCRIPTION ( "command.protect.description" ),
	PROTECT_USAGE ( "command.protect.usage" ),
	PROTECT_EXAMPLE ( "command.protect.example" ),
	PROTECT_EXTRA ( "command.protect.extra" ),
	PROTECT_ENABLED ( "command.protect.enabled" ),
	PROTECT_PLACEING ( "command.protect.placing" ),
	PROTECT_PVP ( "command.protect.pvp" ),
	PROTECT_DESTROYING ( "command.protect.destroying" ),
	PROTECT_DAMAGE ( "command.protect.damage" ),
	PROTECT_FIRE ( "command.protect.fire" ),
	PROTECT_EXPLOSIONS ( "command.protect.explosion" ),
	PROTECT_ON ( "command.protect.on" ),
	PROTECT_OFF ( "command.protect.off" ),

	LANGUAGE_GROUP_NAME ( "command.language.name" ),
	LANGUAGE_GROUP_DESCRIPTION ( "command.language.description" ),
	LANGUAGE_UNKNOWN ( "command.language.invalid" ),
			
	LANGUAGE_NAME ( "command.languageset.name" ),
	LANGUAGE_DESCRIPTION ( "command.languageset.description" ),
	LANGUAGE_USAGE ( "command.languageset.usage" ),
	LANGUAGE_EXAMPLE ( "command.languageset.example" ),
	LANGUAGE_EXTRA ( "command.languageset.extra" ),
	LANGUAGE_SUCCESS ( "command.languageset.success" ),
	
	LANG_RELOAD_NAME( "command.langreload.name" ),
	LANG_RELOAD_DESCRIPTION( "command.langreload.description" ),
	LANG_RELOAD_USAGE( "command.langreload.usage" ),
	LANG_RELOAD_EXAMPLE( "command.langreload.example" ),
	LANG_RELOAD_EXTRA( "command.langreload.extra" ),
	LANG_RELOAD_SUCCESS( "command.langreload.success" ),
	
	NEW_LANG_NAME( "command.newlang.name" ),
	NEW_LANG_DESCRIPTION( "command.newlang.description" ),
	NEW_LANG_USAGE( "command.newlang.usage" ),
	NEW_LANG_EXAMPLE( "command.newlang.example" ),
	NEW_LANG_EXTRA( "command.newlang.extra" ),
	NEW_LANG_SUCCESS( "command.newlang.success" ),
	NEW_LANG_FAIL( "command.newlang.fail" ),
	
	TOLUA_NAME ( "command.tolua.name" ),
	TOLUA_DESCRIPTION ( "command.tolua.description" ),
	TOLUA_USAGE ( "command.tolua.usage" ),
	TOLUA_EXAMPLE ( "command.tolua.example" ),
	TOLUA_EXTRA ( "command.tolua.extra" ),
	TOLUA_SUCCESS ( "command.tolua.success" ),
	TOLUA_FAILED ( "command.tolua.failed" ),
	
	FROMLUA_NAME ( "command.fromlua.name" ),
	FROMLUA_DESCRIPTION ( "command.fromlua.description" ),
	FROMLUA_USAGE ( "command.fromlua.usage" ),
	FROMLUA_EXAMPLE ( "command.fromlua.example" ),
	FROMLUA_EXTRA ( "command.fromlua.extra" ),
	FROMLUA_SUCCESS ( "command.fromlua.success" ),
	FROMLUA_FILE_NOT_FOUND ( "command.fromlua.file-not-found" ),
	FROMLUA_FAILED ( "command.fromlua.failed" ),
			
	SIGN_CANT_BREAK ( "command.signs.cant_break" ),

	SIGN_NAME ( "command.signs.sign.name" ),
	SIGN_DESCRIPTION ( "command.signs.sign.description" ),

	SIGN_ADD_NAME ( "command.signs.add.name" ),
	SIGN_ADD_DESCRIPTION ( "command.signs.add.description" ),
	SIGN_ADD_USAGE ( "command.signs.add.usage" ),
	SIGN_ADD_EXAMPLE ( "command.signs.add.example" ),
	SIGN_ADD_EXTRA ( "command.signs.add.extra" ),
	SIGN_ADD_PROMT_CLICK ( "command.signs.add.prompt-click" ),
	SIGN_ADD_ADDED ( "command.signs.add.added" ),

	SIGN_REMOVE_NAME ( "command.signs.remove.name" ),
	SIGN_REMOVE_DESCRIPTION ( "command.signs.remove.description" ),
	SIGN_REMOVE_USAGE ( "command.signs.remove.usage" ),
	SIGN_REMOVE_EXAMPLE ( "command.signs.remove.example" ),
	SIGN_REMOVE_EXTRA ( "command.signs.remove.extra" ),
	SIGN_REMOVE_PROMT_CLICK ( "command.signs.remove.prompt-click" ),
	SIGN_REMOVE_SUCCESS ( "command.signs.remove.success" ),
	SIGN_REMOVE_NO_COMMANDS ( "command.signs.remove.no-commands" ),
			
	SET_HEALTH_NAME ( "command.sethealth.name" ),
	SET_HEALTH_DESCRIPTION ( "command.sethealth.description" ),
	SET_HEALTH_USAGE ( "command.sethealth.usage" ),
	SET_HEALTH_EXAMPLE ( "command.signs.remove.example" ),
	SET_HEALTH_EXTRA ( "command.sethealth.extra" );

	private final String path;
	private final String message;
	
	
	Text( String path ) {
		this.path = path;
		this.message = YAMLLanguage.lang.getString( path );
		
		// any error's here should always get caught during development
		if ( message == null )
			throw new RuntimeException( "No default message for text: " + path  );
	}

	// class to read the English language text from
	static class YAMLLanguage {
		static final YamlConfiguration lang;
		static {
			InputStream in = Bomberman.instance.getResource( "english.lang" );
			Reader reader = new BufferedReader( new InputStreamReader( in ) );
			lang = new YamlConfiguration();
			try {
				lang.load( reader );
			} catch ( IOException | InvalidConfigurationException e ) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getDefault() {
		return message;
	}

	@Override
	public String getPath() {
		return path;
	}

	/**
	 * Convenience method. {@code|Exact same as Chat.getMessage( this, rep )}
	 * 
	 * @see {@link Chat#getMessage(Phrase, PlayerRep)}
	 */
	public Message getMessage( PlayerRep rep ) {
		return Chat.getMessage( this, rep );
	}

	/**
	 * Convenience method. {@code|Exact same as Chat.getMessage( this, lang,
	 * sender )}
	 * 
	 * @see {@link Chat#getMessage(Phrase, Language, CommandSender)}
	 */
	public Message getMessage( Language lang, CommandSender sender ) {
		return Chat.getMessage( this, lang, sender );
	}

	/**
	 * Convenience method. {@code|Exact same as Chat.getMessage( this, sender )}
	 * 
	 * @see {@link Chat#getMessage(Phrase, CommandSender)}
	 */
	public Message getMessage( CommandSender sender ) {
		return Chat.getMessage( this, sender );
	}

}
