package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public enum Text {

	SPECIFY_GAME (			"join.specify-game",			"You must specify a game to join" ),
	ALREADY_PLAYING (		"join.already-playing",			"You're already part of game {1}" ),
	GAME_FULL (				"join.game-full",				"Game {1} is full" ),
	TOO_POOR (				"join.poor-man",				"You need {2} to join {1}" ),
	PLAYER_JOINED (			"join.player-joined",			"{2} joined game {1}" ),
	
	EDIT_BUILD_DENIED(		"editmode.builddenied",			"Cannot build outside while in editmode"),
	EDIT_DESTROY_DENIED(	"editmode.destroydenied",		"Cannot destroy blocks outside while in editmode"),
	
	TELEPORT_DENIED(		"game-play.teleport-denied",	ChatColor.RED + "Cannot teleport while part of a game"),
	HIT_OPPONENT( 			"game-play.hit-opponent", 		"You hit {2}"),
	HIT_SUICIDE( 			"game-play.hit-suicide", 		"You hit yourself"),
	HIT_BY(					"game-play.hit-by",				"You were hit by {2}"),
	KILL_OPPONENT(			"game-play.kill-opponent", 		ChatColor.YELLOW + "You killed {2}"),
	KILL_SUICIDE(			"game-play.kill-suicide",		ChatColor.RED + "You killed yourself!"),
	KILLED_BY(				"game-play.killed-by",			ChatColor.RED + "Killed by {2}"),
	NO_REGEN(				"game-play.no-regen",			"No regen in sudden death!"),
	
	
	
	protected final String path;
	protected final String message;
	
	Text(String path, String message) {
		this.path = path;
		this.message = message;
	}
	
	public Message getMessage(CommandSender sender, Language lang, Object ... objs) {
		return new Message(sender, lang.translate(this), objs);
	}
}
