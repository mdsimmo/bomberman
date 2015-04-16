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
			"Game starting in {time} seconds..." ),
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
			"score.anounce",
			"The winners are:" ),
	WINNERS_LIST(
			"scores.winners",
			" {gold|{place}:} {player}"),
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
			"command.invalid-matierial",
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

	HELP_NAME(
			"command.help.name",
			"help" ),
	HELP_DESCRIPTION(
			"command.help.descripion",
			"Help for the selected command" ),
	HELP_USAGE(
			"command.help.usage",
			"/{command|path} <command>" ),
	HELP_EXAMPLE(
			"command.help.example",
			"/{command|path} game set fare" ),
	HELP_EXTRA(
			"command.help.extra",
			"Shortcut is to put '?' after a command" ),

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
			"command.areacreate.success",
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
			"Editting a game's arena effects {RED|all} games using the same arena" ),
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
			"Editting arena {arena} through game {game}" ),
	EDIT_ALREADY_STARTED(
			"command.arenaedit.already-started",
			"You're already editting {game}" ),
	EDIT_CHANGES_SAVED(
			"command.arenaedit.changes-saved",
			"Changes saved" ),
	EDIT_SPECIFY_GAME(
			"command.arenaedit.specify-game",
			"You must specify a game" ),
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
	RESET_SUCCESS(
			"command.reset.success",
			"Game {game} reset" ),
	RESET_SUCCESS_P(
			"command.reset.success-players",
			"Game {game} resetting" ),

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
	SETARENA_SUCCESS(
			"command.setarena.success",
			"Game {game} arena's changed" ),

	AUTOSTART_NAME(
			"command.autostart.name",
			"autostart" ),
	AUTOSTART_DESCRIPTION(
			"command.autostart.description",
			"Set if the game should autostart" ),
	AUTOSTART_USAGE(
			"command.autostart.usage",
			"/{game} <game> <true|false>" ),
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
			"/{command|path} <game> <material|none> [amount]" ),
	FARE_EXAMPLE(
			"command.fare.example",
			"" ),
	FARE_EXTRA(
			"command.fare.extra",
			"matirial must be the name in bukkit's code" ),
	FARE_NONE(
			"command.fare.none",
			"none" ),
	FARE_REMOVED(
			"command.fare.removed",
			"Fare removed" ),
	FARE_SET(
			"command.fare.set",
			"Fare set to {game|fare}" ),

	HANDICAP_NAME(
			"command.handicap.name",
			"handicap" ),
	HANDICAP_DESCRIPTION(
			"command.handicap.description",
			"Gives a hanicap/advantage to a player" ),
	HANDICAP_USAGE(
			"command.handicap.usage",
			"/{command|path} <game> <player> <level>" ),
	HANDICAP_EXAMPLE(
			"command.handicap.example",
			"" ),
	HANDICAP_EXTRA(
			"command.handicap.extra",
			"Negitive levels give an advantage" ),
	HANDICAP_HANDYCAPPED(
			"command.handicap.handicapped",
			"{player} handicap set to {player|handicap}" ),
	HANDICAP_REMOVED(
			"command.handicap.removed",
			"Handicap removed from {player}" ),
	HANDICAP_ADVANTAGE(
			"command.handicap.advantage",
			"{player} advantaged at {player|handicap}" ),
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
			"/{command|path} <game> <material|pot|none> [amount]" ),
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
	PRIZE_SET(
			"command.prize.set",
			"Prize set to {game|prize}" ),
	PRIZE_REMOVED(
			"command.prize.removed",
			"Prize removed" ),
	PRIZE_POT_SET(
			"command.prize.pot-set",
			"Pot set" ),

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
			"/" ),
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
			"All commands related to game configuation" ),

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
	GAMELIST_PLAYING(
			"command.gamelist.playing",
			"Playing" ),
	GAMELIST_WAITING(
			"command.gamelist.waiting",
			"Waiting" ),
	GAMELIST_GAME_FORMAT(
			"command.gamelist.game-format",
			"" ),

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
	INFO_STATUS(
			"command.info.status",
			"Status" ),
	INFO_IN_PROGRESS(
			"command.info.in-progress",
			"In progress" ),
	INFO_WAITING(
			"command.info.waiting",
			"Waiting" ),
	INFO_PLAYERS(
			"command.info.players",
			"Players" ),
	INFO_MIN_PLAYERS(
			"command.info.min-players",
			"Min players" ),
	INFO_MAX_PLAYERS(
			"command.info.max-players",
			"Max players" ),
	INFO_INIT_BOMBS(
			"command.info.initial-bombs",
			"Init bombs" ),
	INFO_INIT_LIVES(
			"command.info.initial-lives",
			"Init lives" ),
	INFO_INIT_POWER(
			"command.info.initial-power",
			"Init power" ),
	INFO_FARE(
			"command.info.fare",
			"Entry fee" ),
	INFO_NO_FARE(
			"command.info.no-fare",
			"No fee" ),
	INFO_PRIZE(
			"command.info.prize",
			"Prize" ),
	INFO_NO_PRIZE(
			"command.info.no-prize",
			"No prize" ),
	INFO_POT_AT(
			"command.info.pot-at",
			"Pot at {game|prize}" ),
	INFO_SUDDENDEATH(
			"command.info.suddendeath",
			"Sudden death" ),
	INFO_OFF(
			"command.info.sd-off",
			"Off" ),
	INFO_TIME(
			"command.info.sd-time",
			"{game|suddendeath} seconds" ),
	INFO_TIMEOUT(
			"command.info.timeout",
			"Timeout" ),

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
	PROTECT_NOT_JOINED(
			"command.protect.not-joined",
			"You're not part of a game" ),
	PROTECT_FAILED(
			"command.protect.failed",
			"Couldn't remove you" ),
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

	LANGUAGE_NAME(
			"command.language.name",
			"language" ),
	LANGUAGE_DESCRIPTION(
			"command.language.description",
			"Sets what language to use" ),
	LANGUAGE_USAGE(
			"command.language.usage",
			"/{command|path} <lang>" ),
	LANGUAGE_EXAMPLE(
			"command.language.example",
			"" ),
	LANGUAGE_EXTRA(
			"command.language.extra",
			"" ),
	LANGUAGE_UNKNOWN(
			"command.language.invalid",
			"Unknown language {lang}" ),
	LANGUAGE_SUCCESS(
			"command.language.success",
			"Language set to {lang}" );

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
	 * @see {@link Chat#getMessage(Phrase, PlayerRep)}
	 */
	public Message getMessage( PlayerRep rep ) {
		return Chat.getMessage( this, rep );
	}

	/**
	 * Convenience method. {@code|Exact same as Chat.getMessage( this, lang, sender )}
	 * @see {@link Chat#getMessage(Phrase, Language, CommandSender)}
	 */
	public Message getMessage( Language lang, CommandSender sender ) {
		return Chat.getMessage( this, lang, sender );
	}

	/**
	 * Convenience method. {@code|Exact same as Chat.getMessage( this, sender )}
	 * @see {@link Chat#getMessage(Phrase, CommandSender)}
	 */
	public Message getMessage( CommandSender sender ) {
		return Chat.getMessage( this, sender );
	}
	
}
