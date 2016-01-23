package io.github.mdsimmo.bomberman.messaging;

import io.github.mdsimmo.bomberman.PlayerRep;

import org.bukkit.command.CommandSender;

public enum Text implements Phrase {

	MESSAGE_FORMAT(
			"format.message",
			"{green|[Bomberman]} {message}" ),
	HEADING_FORMAT(
			"format.heading",
			"{yellow|--------} {type}: {title} {yellow|---------------}" ),
	MAP_FORMAT(
			"format.map",
			"{gold|{title}:} {value}" ),
	LIST_FORMAT(
			"format.list",
			" {gold|*} {value}" ),

	GAME(
			"word.game",
			"Game" ),
	ARENA(
			"word.arena",
			"Arena" ),

	HELP(
			"word.help",
			"Help" ),
	LIST(
			"word.list",
			"List" ),
	INFO(
			"word.info",
			"Info" ),
	DESCTIPTION(
			"word.description",
			"Description" ),
	USAGE(
			"word.usage",
			"Usage" ),
	EXTRA(
			"word.extra",
			"Extra" ),
	COMMANDS(
			"word.commands",
			"Commands" ),
	EXAMPLE(
			"word.example",
			"Example" ),
	TRUE(
			"word.true",
			"true" ),
	FALSE(
			"word.false",
			"false" ),

	SPECIFY_GAME(
			"join.specify-game",
			"You must specify a game to join" ),
	GAME_FULL(
			"join.game-full",
			"Game {game} is full" ),
	TOO_POOR(
			"join.poor-man",
			"You need {game|fare} to join {game}" ),
	PLAYER_JOINED(
			"join.player-joined",
			"{player} joined game {game}" ),

	PLAYER_BUSY(
			"player.busy",
			"You're already doing something in game {game}" ),

	EDIT_BUILD_DENIED(
			"editmode.builddenied",
			"Cannot build outside while in editmode" ),
	EDIT_DESTROY_DENIED(
			"editmode.destroydenied",
			"Cannot destroy blocks outside while in editmode" ),

	TELEPORT_DENIED(
			"game-play.teleport-denied",
			"{RED|Cannot teleport while part of a game}" ),
	HIT_OPPONENT(
			"game-play.hit-opponent",
			"You hit {defender}" ),
	HIT_SUICIDE(
			"game-play.hit-suicide",
			"You hit yourself" ),
	HIT_BY(
			"game-play.hit-by",
			"You were hit by {attacker}" ),
	KILL_OPPONENT(
			"game-play.kill-opponent",
			"{YELLOW|You killed {defender}}" ),
	KILL_SUICIDE(
			"game-play.kill-suicide",
			"{RED|You killed yourself!}" ),
	KILLED_BY(
			"game-play.killed-by",
			"{RED|Killed by {attacker}}" ),
	PLAYER_KILLED_PLAYERS(
			"game-play.player-killed.players",
			"{player} is out!" ),
	PLAYER_KILLED_OBSERVERS(
			"game-play.player-killed.observers",
			"{yellow|{player} died in game {game}!}" ),
	PLAYER_KILLED_ALL(
			"game-play.player-killed.all",
			"" ),
	PLAYER_LEFT_PLAYERS(
			"game-play.player-left.players",
			"{player} left" ),
	PLAYER_LEFT_OBSERVERS(
			"game-play.player-left.observers",
			"{player} left game {game}!" ),
	PLAYER_LEFT_ALL(
			"game-play.player-left.all",
			"" ),
	NO_REGEN(
			"game-play.no-regen",
			"No regen in sudden death!" ),
	GAME_STARTING_PLAYERS(
			"game-play.starting.players",
			"Game {game} is starting soon" ),
	GAME_STARTING_OBSERVERS(
			"game-play.starting.observers",
			"Game {game} is starting soon. Type {aqua|/bm game join {game}} to play!" ),
	GAME_STARTING_ALL(
			"game-play.starting.all",
			"Game {game} is starting soon. Type {aqua|/bm game join {game}} to play!" ),
	GAME_COUNT_PLAYERS(
			"game-play.count.players",
			"Game starting in {time} seconds..." ),
	GAME_COUNT_OBSERVERS(
			"game-play.count.observers",
			"Game {game} starting in {time} seconds..." ),
	GAME_COUNT_ALL(
			"game-play.count.all",
			"" ),
	GAME_STARTED_PLAYERS(
			"game-play.started.players",
			"{YELLOW|Game started!}" ),
	GAME_STARTED_OBSERVERS(
			"game-play.started.observers",
			"Game {game} started!" ),
	GAME_STARTED_ALL(
			"game-play.started.all",
			"Game {game} started!" ),
	COUNT_STOPPED_PLAYERS(
			"game-play.stopped-count.players",
			"Not enough players remaining. The countdown timer has been stopped" ),
	COUNT_STOPPED_OBSERVERS(
			"game-play.stopped-count.observers",
			"" ),
	COUNT_STOPPED_ALL(
			"game-play.stopped-count.all",
			"" ),
	GAME_OVER_PLAYERS(
			"game-play.game-over.players",
			"{YELLOW|The game is over!}" ),
	GAME_OVER_OBSERVERS(
			"game-play.game-over.observers",
			"Game {game} is over!" ),
	GAME_OVER_ALL(
			"game-play.game-over.all",
			"" ),

	SUDDENDEATH_COUNT_P(
			"suddendeath.count.players",
			"Sudden death in {time} seconds..." ),
	SUDDENDEATH_COUNT_O(
			"suddendeath.count.observers",
			"" ),
	SUDDENDEATH_COUNT_A(
			"suddendeath.count.all",
			"" ),
	SUDDENDEATH_P(
			"suddendeath.start.players",
			"{RED|Sudden death started!}" ),
	SUDDENDEATH_O(
			"suddendeath.start.observers",
			"Sudden death started in {game}" ),
	SUDDENDEATH_A(
			"suddendeath.start.all",
			"" ),
	TIMEOUT_COUNT_P(
			"timeout.count.players",
			"Time out in {time} seconds..." ),
	TIMEOUT_COUNT_O(
			"timeout.count.observers",
			"" ),
	TIMEOUT_COUNT_A(
			"timeout.count.all",
			"" ),
	TIMEOUT_P(
			"timeout.start.players",
			"Game timed out!" ),
	TIMEOUT_O(
			"timeout.start.observers",
			"Game {game} timed out!" ),
	TIMEOUT_A(
			"timeout.start.all",
			"" ),

	SCORE_DISPLAY(
			"score.display",
			" {player}: {stats|kills} kills, {stats|deaths} deaths" ),
	SCORE_ANNOUNCE(
			"scores.announce",
			"The winners are:" ),
	WINNERS_LIST(
			"scores.winners",
			" {gold|{switch|{place}|1|1st|2|2nd|3|3rd|{place}th}:} {player}" ),
	SCORE_SEE_SCORES(
			"score.see-score",
			"" ),

	DENY_PERMISSION(
			"command.deny-permission",
			"{RED|You do not have permission to use /{command}!}" ),
	INCORRECT_USAGE(
			"command.incorrect-usage",
			"{RED|Incorrect usage: {command|usage}}" ),
	UNKNOWN_COMMAND(
			"command.unknown-command",
			"{RED|Unknown command: {attempt}}" ),
	MUST_BE_PLAYER(
			"command.must-be-player",
			"You must be a player" ),
	INVALID_NUMBER(
			"command.invalid-number",
			"{number} is not a valid number" ),
	INVALID_PLAYER(
			"command.invalid-player",
			"Cannot find {player}" ),
	INVALID_MATERIAL(
			"command.invalid-material",
			"Unknown material {material}" ),
	INVALID_ARENA(
			"command.invalid-arena",
			"Arena {arena} does not exist" ),
	INVALID_GAME(
			"command.invalid-game",
			"Game {game} not found" ),

	// no Bomberman name as it must not be changed
	BOMBERMAN_DESCRIPTION(
			"command.bomberman.description",
			"Main command for Bomberman" ),

	ARENA_NAME(
			"command.arena.name",
			"arena" ),
	ARENA_DESCRIPTION(
			"command.arena.description",
			"Arena management commands" ),

	ARENA_LIST_NAME(
			"command.arenalist.name",
			"list" ),
	ARENA_LIST_DESCRIPTION(
			"command.arenalist.description",
			"List available arena types" ),
	ARENA_LIST_USAGE(
			"command.arenalist.usage",
			"/{command|path}" ),
	ARENA_LIST_EXAMPLE(
			"command.arenalist.example",
			"" ),
	ARENA_LIST_EXTRA(
			"command.arenalist.extra",
			"" ),
	ARENA_LIST_NO_ARENA(
			"command.arenalist.no-arena",
			"No arenas" ),

	ARENA_CREATE_NAME(
			"command.arenacreate.name",
			"create" ),
	ARENA_CREATE_DESCRIPTION(
			"command.arenacreate.description",
			"Create a new arena type for games to use" ),
	ARENA_CREATE_USAGE(
			"command.arenacreate.usage",
			"/{command|path} <arena>" ),
	ARENA_CREATE_EXAMPLE(
			"command.arenacreate.example",
			"/{command|path} redPuddingArena" ),
	ARENA_CREATE_EXTRA(
			"command.arenacreate.extra",
			"Look at the arena when using. Natural blocks are ignored when detecting structures" ),
	ARENA_CREATED(
			"command.arenacreate.success",
			"Arena {arena} created" ),
	ARENA_CREATING(
			"command.arenacreate.started",
			"Arena {arena} is being created... (it may take a while)" ),
	ARENA_NO_TARGET(
			"command.arenacreate.notarget",
			"You must look at a block" ),
	ARENA_CREATE_TOO_BIG(
			"command.arenacreate.too-big",
			"Max build size exceeded! {maxstructuresize} blocks maximum. Creation cancelled" ),
	ARENA_CREATE_VERY_SMALL(
			"command.arenacreate.very-small",
			"Structure is only a single block! Were you looking at a natural block?" ),
	ARENA_CREATE_IN_USE(
			"command.arenacreate.in-use",
			"Cannot change an arena that is being used by a game" ),

	DELETE_NAME(
			"command.arenadelete.name",
			"delete" ),
	DELETE_DESCRIPTION(
			"command.arenadelete.description",
			"Deletes an arena permanently" ),
	DELETE_USAGE(
			"command.arenadelete.usage",
			"/{command|path} <arena>" ),
	DELETE_EXAMPLE(
			"command.arenadelete.example",
			"" ),
	DELETE_EXTRA(
			"command.arenadelete.extra",
			"Only works if arena is not used by a game" ),
	DELETE_SUCCESSFUL(
			"command.arenadelete.success",
			"Arena {arena} successfully deleted" ),
	DELETE_ARENA_USED(
			"command.arenadelete.arena-used",
			"Cannot delete arena {arena} since it is being used by game {game}" ),
	DELETE_TROUBLE(
			"command.arenadelete.error",
			"Error deleting file {file}" ),

	EDIT_NAME(
			"command.arenaedit.name",
			"edit" ),
	EDIT_DESCRIPTION(
			"command.arenaedit.description",
			"Edit a game's arena" ),
	EDIT_USAGE(
			"command.arenaedit.usage",
			"/{command|path} <game> <save|discard|ignore> (leave blank to start edit mode)" ),
	EDIT_EXAMPLE(
			"command.arenaedit.example",
			"" ),
	EDIT_EXTRA(
			"command.arenaedit.extra",
			"Editing a game's arena effects {RED|all} games using the same arena" ),
	EDIT_SAVE(
			"command.arenaedit.save",
			"save" ),
	EDIT_DISCARD(
			"command.arenaedit.discard",
			"discard" ),
	EDIT_IGNORE(
			"command.arenaedit.ignore",
			"ignore" ),
	EDIT_STARTED(
			"command.arenaedit.started",
			"Editing arena {arena} through game {game}" ),
	EDIT_ALREADY_STARTED(
			"command.arenaedit.already-started",
			"You're already editing {game}" ),
	EDIT_CHANGES_SAVED(
			"command.arenaedit.changes-saved",
			"Changes saved" ),
	EDIT_PROMPT_START(
			"command.arenaedit.prompt-start",
			"Edit mode needs to be started first" ),
	EDIT_CANGES_REMOVED(
			"command.arenaedit.changes-removed",
			"Changes removed" ),
	EDIT_MODE_QUIT(
			"command.arenaedit.changes-ignored",
			"Edit mode quit" ),

	FORCE_NAME(
			"command.force.name",
			"force" ),
	FORCE_DESCRIPTION(
			"command.force.description",
			"Force actions on a game" ),

	RESET_NAME(
			"command.reset.name",
			"reset" ),
	RESET_DESCRIPTION(
			"command.reset.description",
			"Forcibly reset a game to its starting point" ),
	RESET_USAGE(
			"command.reset.usage",
			"/{command|path} <game>" ),
	RESET_EXAMPLE(
			"command.reset.example",
			"" ),
	RESET_EXTRA(
			"command.reset.extra",
			"" ),
	RESET_STARTED(
			"command.reset.started",
			"Game {game} resetting" ),
	RESET_FINISHED(
			"command.reset.finished",
			"Game {game} reset" ),

	START_NAME(
			"command.start.name",
			"start" ),
	START_DESCRIPTION(
			"command.start.description",
			"Forcibly start a game" ),
	START_USAGE(
			"command.start.usage",
			"/{command|path} <game>" ),
	START_EXAMPLE(
			"command.start.example",
			"" ),
	START_EXTRA(
			"command.start.extra",
			"" ),
	GAME_ALREADY_STARTED(
			"command.start.already-started",
			"Game {game} already started" ),
	GAME_START_SUCCESS(
			"command.start.success",
			"Game {game} starting..." ),
	GAME_MORE_PLAYERS(
			"command.start.more-players",
			"There needs to be {game|minplayers} players" ),

	STOP_NAME(
			"command.stop.name",
			"stop" ),
	STOP_DESCRIPTION(
			"command.stop.description",
			"Forcibly stop a game" ),
	STOP_USAGE(
			"command.stop.usage",
			"/{command|path} <game>" ),
	STOP_EXAMPLE(
			"command.stop.example",
			"" ),
	STOP_EXTRA(
			"command.stop.extra",
			"" ),
	STOP_NOT_STARTED(
			"command.stop.not-started",
			"Game {game} hasn't started" ),
	STOP_SUCCESS(
			"command.stop.success",
			"Game {game} stopped" ),

	SETARENA_NAME(
			"command.setarena.name",
			"arena" ),
	SETARENA_DESCRIPTION(
			"command.setarena.description",
			"Change a game's arena" ),
	SETARENA_USAGE(
			"command.setarena.usage",
			"/{command|path} <game> <arena>" ),
	SETARENA_EXAMPLE(
			"command.setarena.example",
			"" ),
	SETARENA_EXTRA(
			"command.setarena.extra",
			"" ),
	SETARENA_GIP(
			"command.setarena.game-in-progress",
			"Game {game} in progress. Cannot change arena" ),
	SETARENA_STARTED(
			"command.setarena.started",
			"{game}'s arena is switching from {arena1} to {arena2} " ),
	SETARENA_SUCCESS(
			"command.setarena.success",
			"{game}'s arena changed" ),

	AUTOSTART_NAME(
			"command.autostart.name",
			"autostart" ),
	AUTOSTART_DESCRIPTION(
			"command.autostart.description",
			"Set if the game should autostart" ),
	AUTOSTART_USAGE(
			"command.autostart.usage",
			"/{command|path} <game> <true|false>" ),
	AUTOSTART_EXAMPLE(
			"command.autostart.example",
			"" ),
	AUTOSTART_EXTRA(
			"command.autostart.extra",
			"" ),
	AUTOSTART_ENABLED(
			"command.autostart.enabled",
			"Autostart enabled in game {game}" ),
	AUTOSTART_DISABLED(
			"command.autostart.disabled",
			"Autostart disabled in game {game}" ),

	STARTDELAY_NAME(
			"command.start-delay.name",
			"autostartdelay" ),
	STARTDELAY_DESCRIPTION(
			"command.start-delay.description",
			"Change the delay on a game's automated start" ),
	STARTDELAY_USAGE(
			"command.start-delay.usage",
			"/{command|path} <game> <seconds>" ),
	STARTDELAY_EXAMPLE(
			"command.start-delay.example",
			"" ),
	STARTDELAY_EXTRA(
			"command.start-delay.extra",
			"" ),
	STARTDELAY_SET(
			"command.start-delay.set",
			"Autostart delay set to {game|autostart} seconds" ),

	BOMBS_NAME(
			"command.bombs.name",
			"bombs" ),
	BOMBS_DESCRIPTION(
			"command.bombs.description",
			"Sets players' initial bombs" ),
	BOMBS_USAGE(
			"command.bombs.usage",
			"/{command|path} <game> <number>" ),
	BOMBS_EXAMPLE(
			"command.bombs.example",
			"" ),
	BOMBS_EXTRA(
			"command.bombs.extra",
			"" ),
	BOMBS_SET(
			"command.bombs.set",
			"Bombs set to {game|bombs}" ),

	FARE_NAME(
			"command.fare.name",
			"fare" ),
	FARE_DESCRIPTION(
			"command.fare.description",
			"Change a game's fare" ),
	FARE_USAGE(
			"command.fare.usage",
			"/{command|path} <game> <material|xp|none> [amount]" ),
	FARE_EXAMPLE(
			"command.fare.example",
			"" ),
	FARE_EXTRA(
			"command.fare.extra",
			"material must be the name in bukkit's code" ),
	FARE_SET(
			"command.fare.set",
			"Fare set to {game|fare}" ),
	FARE_NONE(
			"command.fare.none",
			"none"),
	FARE_XP(
			"command.fare.xp",
			"xp" ),

	HANDICAP_NAME(
			"command.handicap.name",
			"handicap" ),
	HANDICAP_DESCRIPTION(
			"command.handicap.description",
			"Gives a handicap/advantage to a player" ),
	HANDICAP_USAGE(
			"command.handicap.usage",
			"/{command|path} <game> <player> <level>" ),
	HANDICAP_EXAMPLE(
			"command.handicap.example",
			"" ),
	HANDICAP_EXTRA(
			"command.handicap.extra",
			"Negative levels give an advantage" ),
	HANDICAP_SET(
			"command.handicap.set",
			"{switch|{=|sign({player|handicap})}|0|Handicap removed from {player}|-1|{player}'s advantage set to level {=|abs({player|handicap})}|{player}'s handicap set to level {player|handicap}}" ),
	
	LIVES_NAME(
			"command.lives.name",
			"lives" ),
	LIVES_DESCRIPTION(
			"command.lives.description",
			"Sets players' initial lives" ),
	LIVES_USAGE(
			"command.lives.usage",
			"/{command|path} <game> <number>" ),
	LIVES_EXAMPLE(
			"command.lives.example",
			"" ),
	LIVES_EXTRA(
			"command.lives.extra",
			"" ),
	LIVES_SET(
			"command.lives.set",
			"Lives set to {game|lives}" ),

	MINPLAYERS_NAME(
			"command.minplayers.name",
			"minplayers" ),
	MINPLAYERS_DESCRIPTION(
			"command.minplayers.description",
			"Sets the min players before game can start" ),
	MINPLAYERS_USAGE(
			"command.minplayers.usage",
			"/{command|path} <game> <number>" ),
	MINPLAYERS_EXAMPLE(
			"command.minplayers.example",
			"" ),
	MINPLAYERS_EXTRA(
			"command.minplayers.extra",
			"" ),
	MINPLAYERS_SET(
			"command.minplayers.set",
			"Min players set to {game|minplayers}" ),
	MINPLAYERS_LESS_THAN_ONE(
			"command.minplayers.more-than-zero",
			"Min players cannot be less than one" ),

	POWER_NAME(
			"command.power.name",
			"power" ),
	POWER_DESCRIPTION(
			"command.power.description",
			"Sets players' initial power" ),
	POWERS_USAGE(
			"command.power.usage",
			"/{command|path} <game> <number>" ),
	POWER_EXAMPLE(
			"command.power.example",
			"" ),
	POWER_EXTRA(
			"command.power.extra",
			"" ),
	POWER_SET(
			"command.power.set",
			"Power set to {game|power}" ),

	PRIZE_NAME(
			"command.prize.name",
			"prize" ),
	PRIZE_DESCRIPTION(
			"command.prize.description",
			"Change a game's prize" ),
	PRIZE_USAGE(
			"command.prize.usage",
			"/{command|path} <game> <material|xp|none> [amount]" ),
	PRIZE_EXAMPLE(
			"command.prize.example",
			"" ),
	PRIZE_EXTRA(
			"command.prize.extra",
			"" ),
	PRIZE_POT(
			"command.prize.pot",
			"pot" ),
	PRIZE_NONE(
			"command.prize.none",
			"none" ),
	PRIZE_XP(
			"command.prize.xp",
			"xp" ),
	PRIZE_SET(
			"command.prize.set",
			"Prize set to {game|prize}" ),
			
	SET_NAME(
			"command.set.name",
			"set" ),
	SET_DESCRIPTION(
			"command.set.description",
			"Change a game's settings" ),

	SUDDENDEATH_NAME(
			"command.suddendeath.name",
			"suddendeath" ),
	SUDDENDEATH_DESCRIPTION(
			"command.suddendeath.description",
			"Sets when sudden death should happen" ),
	SUDDENDEATH_USAGE(
			"command.suddendeath.usage",
			"/{command|path} <game> <seconds>" ),
	SUDDENDEATH_EXAMPLE(
			"command.suddendeath.example",
			"" ),
	SUDDENDEATH_EXTRA(
			"command.suddendeath.extra",
			"" ),
	SUDDENDEATH_OFF(
			"command.suddendeath.off",
			"off" ),
	SUDDENDEATH_REMOVED(
			"command.suddendeath.removed",
			"Sudden death removed from {game}" ),
	SUDDENDEATH_SET(
			"command.suddendeath.set",
			"Sudden death set to {game|suddendeath} seconds" ),

	TIMEOUT_NAME(
			"command.timeout.name",
			"timeout" ),
	TIMEOUT_DESCRIPTION(
			"command.timeout.description",
			"Sets a maximum game time" ),
	TIMEOUT_USAGE(
			"command.timeout.usage",
			"/{command|path} <game> <seconds>" ),
	TIMEOUT_EXAMPLE(
			"command.timeout.example",
			"" ),
	TIMEOUT_EXTRA(
			"command.timeout.extra",
			"" ),
	TIMEOUT_OFF(
			"command.timeout.off",
			"off" ),
	TIMEOUT_REMOVED(
			"command.timeout.removed",
			"Time out removed for game {game}" ),
	TIMEOUT_SET(
			"command.timeout.set",
			"Time out set to {game|timeout} seconds" ),

	CONVERT_NAME(
			"command.convert.name",
			"convert" ),
	CONVERT_DESCRIPTION(
			"command.convert.description",
			"Converts the structure under the cursor into a Bomberman game" ),
	CONVERT_USAGE(
			"command.convert.usage",
			"/{command|path} <game>" ),
	CONVERT_EXAMPLE(
			"command.convert.example",
			"" ),
	CONVERT_EXTRA(
			"command.convert.extra",
			"Natural blocks are ignored when detecting structures" ),
	CONVERT_GAME_EXISTS(
			"command.convert.game-exists",
			"Game {game} already exists" ),
	CONVERT_STARTED(
			"command.convert.started",
			"Conversion in progress..." ),
	CONVERT_SUCCESS(
			"command.convert.success",
			"Game {game} created" ),

	CREATE_NAME(
			"command.create.name",
			"create" ),
	CREATE_DESCRIPTION(
			"command.create.description",
			"Builds a Bomberman game" ),
	CREATE_USAGE(
			"command.create.usage",
			"/{command|path} <game> [arena]" ),
	CREATE_EXAMPLE(
			"command.create.example",
			"" ),
	CREATE_EXTRA(
			"command.create.extra",
			"" ),
	CREATE_GAME_EXISTS(
			"command.create.game-exists",
			"Game {game} already exists" ),
	CREATE_STARTED(
			"command.create.started",
			"Creation started..." ),
	CREATE_SUCCESS(
			"command.create.success",
			"Game {game} created" ),
	CREATE_DEFAULTMISSING(
			"command.create.default-missing",
			"The default arena {arena} is missing!" ),

	DESTROY_NAME(
			"command.destroy.name",
			"destroy" ),
	DESTROY_DESCRIPTION(
			"command.destroy.description",
			"Destroy a game and revert the land to its previous state" ),
	DESTROY_USAGE(
			"command.destroy.usage",
			"/{command|path} <game>" ),
	DESTROY_EXAMPLE(
			"command.destroy.example",
			"" ),
	DESTROY_EXTRA(
			"command.destroy.extra",
			"See also: {aqua|/bm game forget <game>}" ),
	DESTROY_SUCCESS(
			"command.destroy.success",
			"Game {game} destroyed" ),

	FORGET_NAME(
			"command.forget.name",
			"forget" ),
	FORGET_DESCRIPTION(
			"command.forget.description",
			"Forgets about a game but does not destroy/reset the arena" ),
	FORGET_USAGE(
			"command.forget.usage",
			"/{command|path} <game>" ),
	FORGET_EXAMPLE(
			"command.forget.example",
			"" ),
	FORGET_EXTRA(
			"command.forget.extra",
			"See also: {aqua|/bm game destroy <game>}" ),
	FORGET_SUCCESS(
			"command.forget.success",
			"Game {game} forgotten" ),

	GAME_NAME(
			"command.game.name",
			"game" ),
	GAME_DESCRIPTION(
			"command.game.description",
			"All commands related to game configuration" ),

	GAMELIST_NAME(
			"command.gamelist.name",
			"list" ),
	GAMELIST_DESCRIPTION(
			"command.gamelist.description",
			"Shows all existing games" ),
	GAMELIST_USAGE(
			"command.gamelist.usage",
			"/{command|path}" ),
	GAMELIST_EXAMPLE(
			"command.gamelist.example",
			"" ),
	GAMELIST_EXTRA(
			"command.gamelist.extra",
			"" ),
	GAMELIST_NO_GAMES(
			"command.gamelist.no-games",
			"No games" ),
	GAMELIST_KEY(
			"command.gamelist.key",
			"{game}" ),
	GAMELIST_VALUE(
			"command.gamelist.value",
			"{game|status}" ),

	IGNORE_NAME(
			"command.ignore.name",
			"ignore" ),
	IGNORE_DESCRIPTION(
			"command.ignore.description",
			"Ignore all further messages from a game" ),
	IGNORE_USAGE(
			"command.ignore.usage",
			"/{command|path} <game>" ),
	IGNORE_EXAMPLE(
			"command.ignore.example",
			"" ),
	IGNORE_EXTRA(
			"command.ignore.extra",
			"" ),
	IGNORE_SUCCESS(
			"command.ignore.success",
			"Game {game} ignored" ),
	IGNORE_NOT_WATCHED(
			"command.ignore.not-watched",
			"You where not observing {game}" ),

	INFO_NAME(
			"command.info.name",
			"info" ),
	INFO_DESCRIPTION(
			"command.info.description",
			"Show information about a game" ),
	INFO_USAGE(
			"command.info.usage",
			"/{command|path} <game>" ),
	INFO_EXAMPLE(
			"command.info.example",
			"" ),
	INFO_EXTRA(
			"command.info.extra",
			"" ),
	INFO_1(
			"command.info.1-key",
			"Status" ),
	INFO_1_RESULT(
			"command.info.1-value",
			"{switch|{game|state}|waiting|Waiting|playing|In progress|starting|Starting|ending|Ending|unknown}" ),
	INFO_2(
			"command.info.2-key",
			"Players" ),
	INFO_2_RESULT(
			"command.info.2-value",
			"{game|players}"),
	INFO_3(
			"command.info.3-key",
			"Min players" ),
	INFO_3_RESULT(
			"command.info.3-value",
			"{game|minplayers}" ),
	INFO_4(
			"command.info.4-key",
			"Max players" ),
	INFO_4_RESULT(
			"command.info.4-value",
			"{game|maxplayers}" ),
	INFO_5(
			"command.info.5-key",
			"Init bombs" ),
	INFO_5_RESULT(
			"command.info.5-value",
			"{game|bombs}" ),
	INFO_6 (
			"command.info.6-key",
			"Init lives" ),
	INFO_6_RESULT(
			"command.info.6-value",
			"{game|lives}" ),
	INFO_7(
			"command.info.7-key",
			"Init power" ),
	INFO_7_RESULT(
			"command.info.7-value",
			"{game|power}" ),
	INFO_8(
			"command.info.8-key",
			"Entry fee" ),
	INFO_8_RESULT(
			"command.info.8-value",
			"{game|fare}" ),
	INFO_9(
			"command.info.9-key",
			"Prize" ),
	INFO_9_RESULT(
			"command.info.9-value",
			"{game|prize}" ),
	INFO_10(
			"command.info.10-key",
			"Sudden death" ),
	INFO_10_RESULT(
			"command.info.10-value",
			"{switch|{game|suddendeath}|0,-1|Off|{game|suddendeath} seconds}" ),
	INFO_11(
			"command.info.11-key",
			"Timeout" ),
	INFO_11_RESULT(
			"command.info.11-value",
			"{switch|{game|timeout}|0,-1|Off|{game|timeout} seconds}"),
	INFO_12(
			"command.info.12-key",
			"Arena" ),
	INFO_12_RESULT(
			"command.info.12-value",
			"{game|arena}"),
	INFO_13( "command.info.13-key", "" ),
	INFO_13_RESULT("command.info.13-value",""),
	INFO_14("command.info.14-key",""),
	INFO_14_RESULT("command.info.14-value",""),
	INFO_15("command.info.15-key",""),
	INFO_15_RESULT("command.info.15-value",""),

	JOIN_NAME(
			"command.join.name",
			"join" ),
	JOIN_DESCRIPTION(
			"command.join.description",
			"Join a game" ),
	JOIN_USAGE(
			"command.join.usage",
			"/{command|path} <game>" ),
	JOIN_EXAMPLE(
			"command.join.example",
			"" ),
	JOIN_EXTRA(
			"command.join.extra",
			"" ),
	JOIN_GAME_STARTED(
			"command.join.game-started",
			"Game has already started" ),

	LEAVE_NAME(
			"command.leave.name",
			"leave" ),
	LEAVE_DESCRIPTION(
			"command.leave.description",
			"Leave the game" ),
	LEAVE_USAGE(
			"command.leave.usage",
			"/{command|path}" ),
	LEAVE_EXAMPLE(
			"command.leave.example",
			"" ),
	LEAVE_EXTRA(
			"command.leave.extra",
			"" ),
	LEAVE_NOT_JOINED(
			"command.leave.not-joined",
			"You're not part of a game" ),

	PROTECT_NAME(
			"command.protect.name",
			"protect" ),
	PROTECT_DESCRIPTION(
			"command.protect.description",
			"Protects the arena from griefing" ),
	PROTECT_USAGE(
			"command.protect.usage",
			"/{command|path} <game> [protection-type] <on|off>" ),
	PROTECT_EXAMPLE(
			"command.protect.example",
			"" ),
	PROTECT_EXTRA(
			"command.protect.extra",
			"" ),
	PROTECT_ENABLED(
			"command.protect.enabled",
			"enabled" ),
	PROTECT_PLACEING(
			"command.protect.placing",
			"placing" ),
	PROTECT_PVP(
			"command.protect.pvp",
			"pvp" ),
	PROTECT_DESTROYING(
			"command.protect.destroying",
			"destroy" ),
	PROTECT_DAMAGE(
			"command.protect.damage",
			"damage" ),
	PROTECT_FIRE(
			"command.protect.fire",
			"fire" ),
	PROTECT_EXPLOSIONS(
			"command.protect.explosion",
			"explosion" ),
	PROTECT_ON(
			"command.protect.on",
			"Game {game} enabled {protection} protection" ),
	PROTECT_OFF(
			"command.protect.off",
			"Game {game} removed {protection} protection" ),

	SCORES_NAME(
			"command.scores.name",
			"scores" ),
	SCORES_DESCRIPTION(
			"command.scores.description",
			"Displays the games scores" ),
	SCORES_USAGE(
			"command.scores.usage",
			"/{command|path} <game>" ),
	SCORES_EXAMPLE(
			"command.scores.example",
			"" ),
	SCORES_EXTRA(
			"command.scores.extra",
			"" ),

	LANGUAGE_GROUP_NAME(
			"command.language.name",
			"language" ),
	LANGUAGE_GROUP_DESCRIPTION(
			"command.language.description",
			"The language commands" ),
	LANGUAGE_UNKNOWN(
			"command.language.invalid",
			"Unknown language {lang}" ),
			
	LANGUAGE_NAME(
			"command.languageset.name",
			"set" ),
	LANGUAGE_DESCRIPTION(
			"command.languageset.description",
			"Sets what language to use" ),
	LANGUAGE_USAGE(
			"command.languageset.usage",
			"/{command|path} <lang>" ),
	LANGUAGE_EXAMPLE(
			"command.languageset.example",
			"" ),
	LANGUAGE_EXTRA(
			"command.languageset.extra",
			"" ),
	LANGUAGE_SUCCESS(
			"command.languageset.success",
			"Language set to {lang}" ),
	
	
	TOLUA_NAME(
			"command.tolua.name",
			"tolua" ),
	TOLUA_DESCRIPTION(
			"command.tolua.description",
			"Converts a luguage to a lua file (for uploading to the bukkit repo)" ),
	TOLUA_USAGE(
			"command.tolua.usage",
			"/{command|path} <lang> <exportfile>" ),
	TOLUA_EXAMPLE(
			"command.tolua.example",
			"" ),
	TOLUA_EXTRA(
			"command.tolua.extra",
			"Exported file will be in 'global strings' format" ),
	TOLUA_SUCCESS(
			"command.tolua.success",
			"lua file created" ),
	TOLUA_FAILED(
			"command.tolua.failed",
			"Failed to create file {aqua|{file}}. Was the filename legal?"),
	
	FROMLUA_NAME(
			"command.fromlua.name",
			"fromlua" ),
	FROMLUA_DESCRIPTION(
			"command.fromlua.description",
			"Converts a lua file into a yaml file" ),
	FROMLUA_USAGE(
			"command.fromlua.usage",
			"/{command|path} <importfile> <langname>" ),
	FROMLUA_EXAMPLE(
			"command.fromlua.example",
			"" ),
	FROMLUA_EXTRA(
			"command.fromlua.extra",
			"Lua languages must be in 'global strings' format" ),
	FROMLUA_SUCCESS(
			"command.fromlua.success",
			"language {lang} created" ),
	FROMLUA_FILE_NOT_FOUND(
			"command.fromlua.file-not-found",
			"File {file} was not found"),
	FROMLUA_FAILED(
			"command.fromlua.failed",
			"Failed to import language {lang}"),
			
	SIGN_CANT_BREAK(
			"command.signs.cant_break",
			"Command blocks cannot be broken. Use {aqua|/bm sign remove}" ),

	SIGN_NAME(
			"command.signs.sign.name",
			"sign" ),
	SIGN_DESCRIPTION(
			"command.signs.sign.description",
			"Configures sign behaviour" ),

	SIGN_ADD_NAME(
			"command.signs.add.name",
			"add" ),
	SIGN_ADD_DESCRIPTION(
			"command.signs.add.description",
			"Adds a command to a sign" ),
	SIGN_ADD_USAGE(
			"command.signs.add.usage",
			"/{command|path} <command>" ),
	SIGN_ADD_EXAMPLE(
			"command.signs.add.example",
			"" ),
	SIGN_ADD_EXTRA(
			"command.signs.add.extra",
			"" ),
	SIGN_ADD_PROMT_CLICK(
			"command.signs.add.prompt-click",
			"Right click on the block to add the command to" ),
	SIGN_ADD_ADDED(
			"command.signs.add.added",
			"Command added" ),

	SIGN_REMOVE_NAME(
			"command.signs.remove.name",
			"remove" ),
	SIGN_REMOVE_DESCRIPTION(
			"command.signs.remove.description",
			"Removes all commands from a sign" ),
	SIGN_REMOVE_USAGE(
			"command.signs.remove.usage",
			"/{command|path}" ),
	SIGN_REMOVE_EXAMPLE(
			"command.signs.remove.example",
			"" ),
	SIGN_REMOVE_EXTRA(
			"command.signs.remove.extra",
			"" ),
	SIGN_REMOVE_PROMT_CLICK(
			"command.signs.remove.prompt-click",
			"Right click on the block to remove commands from" ),
	SIGN_REMOVE_SUCCESS(
			"command.signs.remove.success",
			"Commands removed" ),
	SIGN_REMOVE_NO_COMMANDS(
			"command.signs.remove.no-commands",
			"No commands found on this block" ),
			
	SET_HEALTH_NAME(
			"command.sethealth.name",
			"resethealth" ),
	SET_HEALTH_DESCRIPTION(
			"command.sethealth.description",
			"Resets the players health to the default health forever" ),
	SET_HEALTH_USAGE(
			"command.sethealth.usage",
			"/{command|path} <player> [health]" ),
	SET_HEALTH_EXAMPLE(
			"command.signs.remove.example",
			"" ),
	SET_HEALTH_EXTRA(
			"command.sethealth.extra",
			"This command is here due to a bug where bomberman doesn't reset a players health to the normal 20. If this happens to you, just run this command." );

	private final String path;
	private final String message;

	Text( String path, String message ) {
		this.path = path;
		this.message = message;
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
