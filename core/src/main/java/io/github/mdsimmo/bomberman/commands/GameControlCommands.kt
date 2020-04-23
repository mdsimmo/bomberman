package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.events.BmPlayerJoinGameIntent
import io.github.mdsimmo.bomberman.events.BmPlayerLeaveGameIntent
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.game.Game
import io.github.mdsimmo.bomberman.messaging.*
import io.github.mdsimmo.cmdmsg.Arg
import io.github.mdsimmo.cmdmsg.Msg
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.jetbrains.annotations.NotNull
import java.io.File

class GameControlCommands {

    @Target(AnnotationTarget.FUNCTION)
    annotation class Cmd(val path: String)

    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class Flag(val name: String)

    @Target(AnnotationTarget.VALUE_PARAMETER)
    annotation class Context

    open class CmdResult(val success: Boolean) {
        companion object {
            fun success(msg: Message = Message.empty) = CmdResult(true)
            fun fail() = object : CmdResult(false) {}
        }
    }

    @Cmd("bm create")
    fun create(@Context location: Location,
                name: String,
                @Flag("p") schematic: Plugin,
                @Flag("f") file: File,
                @Flag("s") skipAir: Boolean = false,
                @Flag("v") deleteVoid: Boolean = false): CmdResult {

        val flags = Game.BuildFlags(skipAir = skipAir, deleteVoid = deleteVoid)
        Game.buildGameFromSchema(name, location, file, flags)

        return CmdResult.success()
    }

    @Cmd("bm join")
    fun join(@Context player: Player, game: Game): CmdResult {
        BmPlayerJoinGameIntent.join(game, player)
        return CmdResult.success()
    }

    @Msg("bm.cmd.leave.success", "Sent when a player successfully leaves a game")
    @Arg("player", Player::class, "Hello")
    private val leaveSuccess = "{#gray|{#italic|{player} left}}}"

    @Msg("bm.cmd.leave.not-joined",
            "Sent when a player tries to leave but is not part of any games")
    @Arg("player", Player::class)
    private val notJoined = GameControlCommandsTexts::leaveNotJoined

    @Cmd("bm leave")
    fun leave(@Context player: Player): CmdResult {
        val e = BmPlayerLeaveGameIntent.leave(player)
        return when {
            e.isHandled() -> {
                CmdResult.success()
            }
            else -> CmdResult.fail()
        }
    }

    @Cmd("bm start")
    fun start(game: Game, @Flag("d") delay: Int): CmdResult {
        val e = BmRunStartCountDownIntent.startGame(game, delay)
        return if (e.isCancelled) {
            CmdResult.fail(e.cancelledReason())
        } else {
            CmdResult.success()
        }
    }

    @Cmd("bm stop")
    fun stop(game: Game): CmdResult {
        val e = BmRunStoppedIntent.stopGame(game)
        return if (e.isCancelled) {
            CmdResult.fail(e.cancelledReason())
        } else {
            CmdResult.success()
        }
    }


}